package network.server;

import network.ServerClient;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

import static network.ServerClient.*;

public class ClientHandler extends Thread {
    private String username;
    private Socket clientSocket;
    private PrintStream out;
    private Scanner scanner;

    public ClientHandler(Socket clientSocket, String username) {
        try {
            this.username = username;
            this.clientSocket = clientSocket;
            out = new PrintStream(clientSocket.getOutputStream(), true);
            scanner = new Scanner(clientSocket.getInputStream());
        } catch (Exception ignored) {
        }
    }

    private void tell(String message) {
        out.println(message + MESSAGE_SIGN);
    }

    @Override
    public void run() {
        try {
            while (scanner.hasNextLine()) {
                String command = scanner.nextLine();
                if (isFromSomeone(command)) {
                    String destinationUsername = ServerClient.getUsername(command);
                    if (destinationUsername.equals(username))
                        tell("No such user");
                    else {
                        ClientHandler destination = Acceptor.getClientHandler(destinationUsername);
                        if (destination == null)
                            tell("No such user");
                        else
                            destination.out.println(ServerClient.tell(username, command.split(DESCRIPTION_MARKER)[1]));
                    }
                } else
                    out.println(command);
            }
            finish();
        } catch (Exception ignored) {
        }
    }

    private void finish() throws IOException {
        scanner.close();
        out.close();
        clientSocket.close();
        Acceptor.removeClientHandler(this);
    }

    public String getUsername() {
        return username;
    }
}