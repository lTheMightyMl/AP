package network;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler extends Thread {
    private static final String REQUEST = "new game \\w+";

    private String name;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    public ClientHandler(Socket clientSocket, String name) {
        try {
            this.name = name;
            this.clientSocket = clientSocket;
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (Exception ignored) {
        }
    }

    public void tell(String sentence) {
        out.println(sentence + "$");
    }

    @Override
    public void run() {
        try {
            String command = in.readLine();
            while (command != null) {
                if (command.matches(REQUEST)) {
                    ClientHandler destination = Acceptor.getClientHandler(command.split(" ")[2]);
                    if (destination == null || destination == this)
                        tell("No such user");
                    else
                        destination.tell(name + " says hello.");
                } else
                    out.println(command);
                command = in.readLine();
            }
            in.close();
            out.close();
            clientSocket.close();
        } catch (Exception ignored) {
        }
    }
}
