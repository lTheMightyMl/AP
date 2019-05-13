package network.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class Acceptor extends Thread {
    private static final String NONUNIQUE_USERNAME = "NONUNIQUE_USERNAME";
    private static final String UNIQUE_USERNAME = "UNIQUE_USERNAME";

    private static final String PROMPT_USERNAME = "Enter your username";

    private static HashMap<String, ClientHandler> clientHandlers = new HashMap<>();

    private ServerSocket serverSocket;

    public Acceptor(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public static ClientHandler getClientHandler(String username) {
        return clientHandlers.get(username);
    }

    private static ClientHandler initiateClient(BufferedReader in, PrintWriter out, Socket clientSocket) throws IOException {
        String username = getUsername(in, out);
        ClientHandler clientHandler = new ClientHandler(clientSocket, username);
        clientHandlers.put(username, clientHandler);
        return clientHandler;
    }

    private static String getUsername(BufferedReader in, PrintWriter out) throws IOException {
        String username = in.readLine();
        while (clientHandlers.containsKey(username)) {
            out.println(NONUNIQUE_USERNAME);
            username = in.readLine();
        }
        out.println(UNIQUE_USERNAME);
        return username;
    }


    @Override
    public void run() {
        BufferedReader in;
        PrintWriter out;
        try {
            while (!interrupted()) {
                Socket clientSocket = serverSocket.accept();
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                ClientHandler clientHandler = initiateClient(in, out, clientSocket);
                clientHandler.start();
            }
        } catch (Exception ignored) {
        }
    }
}
