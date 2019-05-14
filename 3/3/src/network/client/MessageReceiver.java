package network.client;

import interactor.Interactor;
import network.ServerClient;

import java.io.BufferedReader;

public class MessageReceiver extends Thread {
    private BufferedReader in;

    public MessageReceiver(BufferedReader in) {
        this.in = in;
    }

    private static boolean isAMessage(String command) {
        return command.charAt(command.length() - 1) == '$';
    }

    private static String getMessage(String message) {
        return message.substring(0, message.length() - 1);
    }

    @Override
    public void run() {
        try {
            String response = in.readLine();
            while (response != null) {
                if (isAMessage(response))
                    System.out.println(getMessage(response));
                else
                    System.out.println(response);
                response = in.readLine();
            }
        } catch (Exception ignored) {
        }
    }
}