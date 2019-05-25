package network.client;

import network.ServerClient;

import java.io.InputStream;
import java.util.Scanner;
import java.util.concurrent.BlockingDeque;

public class MessageReceiver extends Thread {
    private Scanner scanner;
    private BlockingDeque<String> input;

    public MessageReceiver(InputStream in, BlockingDeque<String> input) {
        this.scanner = new Scanner(in);
        this.input = input;
    }

    @Override
    public void run() {
        try {
            while (scanner.hasNextLine()) {
                String response = scanner.nextLine();
                input.add(response);
            }
        } catch (Exception ignored) {
        }
    }
}