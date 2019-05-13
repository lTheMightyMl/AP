package network;

import interactor.Interactor;

import java.io.BufferedReader;

public class MessageReceiver extends Thread {
    private BufferedReader in;
    private Interactor interactor;

    public MessageReceiver(BufferedReader in, Interactor interactor) {
        this.in = in;
        this.interactor = interactor;
    }

    private static boolean isAMessage(String command) {
        if (command.charAt(command.length() - 1) == '$')
            return true;
        return false;
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
                    interactor.act(response);
                response = in.readLine();
            }
        } catch (Exception ignored) {
        }
    }
}
