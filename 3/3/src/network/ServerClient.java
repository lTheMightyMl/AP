package network;

import interactor.Interactor;
import network.client.MessageReceiver;
import network.client.MessageSender;
import network.server.Acceptor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class ServerClient {
    private static final String HOST = "127.0.0.1";
    private static final int PORT = 9999;

    private static final String NONUNIQUE_USERNAME = "NONUNIQUE_USERNAME";
    private static final String UNIQUE_USERNAME = "UNIQUE_USERNAME";

    private static final String PROMPT_USERNAME = "Enter your username";

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = null;
            try {
                serverSocket = new ServerSocket(PORT);
            } finally {
                if (serverSocket != null)
                    new Acceptor(serverSocket).start();
                Interactor interactor = new Interactor();
                Socket clientSocket = new Socket(HOST, PORT);
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                sendUsername(in, out);
                new MessageSender(out).start();
                new MessageReceiver(in).start();
            }
        } catch (Exception ignored) {
        }
    }

    private static void sendUsername(BufferedReader in, PrintWriter out) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println(PROMPT_USERNAME);
        String username = scanner.nextLine();
        out.println(username);
        String response = in.readLine();
        while (response.equals(NONUNIQUE_USERNAME)) {
            System.out.println(PROMPT_USERNAME);
            username = scanner.nextLine();
            out.println(username);
            response = in.readLine();
        }
        if (response.equals(UNIQUE_USERNAME))
            System.out.println("Successfully logged into the network.");
    }
}