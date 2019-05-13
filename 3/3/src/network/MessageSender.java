package network;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class MessageSender extends Thread {
    PrintWriter out;
    BufferedReader scanner;

    public MessageSender(PrintWriter out) {
        scanner = new BufferedReader(new InputStreamReader(System.in));
        this.out = out;
    }

    @Override
    public void run() {
        try {
            String response = scanner.readLine();
            while (response != null) {
                out.println(response);
                response = scanner.readLine();
            }
        } catch (Exception ignored) {
        }
    }
}
