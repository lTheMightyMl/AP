package network.client;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Scanner;
import java.util.concurrent.BlockingDeque;

public class MessageSender extends Thread {
    PrintStream out;
    Scanner scanner;
    BlockingDeque<String> output;

    public MessageSender(OutputStream outputStream, BlockingDeque<String> output) {
        scanner = new Scanner(System.in);
        this.out = new PrintStream(outputStream, true);
        this.output = output;
    }

    @Override
    public void run() {
        try {
            while (scanner.hasNextLine()) {
                String message = scanner.nextLine();
                message = message.trim();
                output.add(message);
            }
        } catch (Exception ignored) {
        }
    }
}