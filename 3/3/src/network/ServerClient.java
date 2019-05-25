package network;

import interactor.Interactor;
import interactor.classes.Game;
import interactor.classes.Player;
import network.client.MessageReceiver;
import network.client.MessageSender;
import network.server.Acceptor;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;


public class ServerClient {
    public static final String MESSAGE_SIGN = "$";
    public static final String USERNAME_MARKER = "@";
    public static final String NON_UNIQUE_USERNAME = "NON_UNIQUE_USERNAME";
    public static final String UNIQUE_USERNAME = "UNIQUE_USERNAME";
    public static final String DESCRIPTION_MARKER = ":";
    private static final String HOST = "127.0.0.1";
    private static final int PORT = 9999;
    private static final String NEW_GAME_REQUEST = "new game \\w+";
    private static final String PROMPT_USERNAME = "Enter your username";
    private static final Interactor interactor = new Interactor();
    private static final String IS_IN_GAME = "is in game";
    private static final String PLAYER_IN_GAME = "The player is playing a game right now.";
    private static final String ORDER_MARKER = "!";
    private static final String X = "X";
    private static final String NEW_GAME = "new game \\w+ \\w+";
    private static final String NUMBER = "\\d+";

    private static final BlockingDeque<String> input = new LinkedBlockingDeque<>();
    private static final BlockingDeque<String> output = new LinkedBlockingDeque<>();

    private static String username;

    private static Socket clientSocket;
    private static Scanner scanner;
    private static PrintStream out;

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = null;
            try {
                serverSocket = new ServerSocket(PORT);
            } finally {
                if (serverSocket != null)
                    new Acceptor(serverSocket).start();
                clientSocket = new Socket(HOST, PORT);
                InputStream inputStream = clientSocket.getInputStream();
                scanner = new Scanner(inputStream);
                OutputStream outputStream = clientSocket.getOutputStream();
                out = new PrintStream(outputStream, true);
                sendUsername(inputStream, outputStream);
                new MessageSender(outputStream, output).start();
                new MessageReceiver(inputStream, input).start();
                new InputProcessor(input).start();
                sendMessage();
            }
        } catch (Exception ignored) {
        }
    }

    private static void sendUsername(InputStream inputStream, OutputStream outputStream) {
        Scanner scanner = new Scanner(System.in);
        PrintStream out = new PrintStream(outputStream, true);
        System.out.println(PROMPT_USERNAME);
        username = scanner.nextLine();
        out.println(username);
        Scanner in = new Scanner(inputStream);
        String response = in.nextLine();
        while (response.equals(NON_UNIQUE_USERNAME)) {
            System.out.println(PROMPT_USERNAME);
            username = scanner.nextLine();
            out.println(username);
            response = in.nextLine();
        }
        if (response.equals(UNIQUE_USERNAME))
            System.out.println("Successfully logged into the network.");
    }

    private static String getOrder(String description) {
        return description.substring(0, description.length() - 1);
    }

    private static boolean isAnOrder(String description) {
        return description.endsWith(ORDER_MARKER);
    }

    public static String getUsername(String message) {
        return message.split(DESCRIPTION_MARKER)[0].substring(1);
    }

    private static String getDescription(String message) {
        return message.split(DESCRIPTION_MARKER)[1];
    }

    public static boolean isFromSomeone(String message) {
        return USERNAME_MARKER.equals(String.valueOf(message.charAt(0)));
    }

    private static void finish() throws IOException {
        scanner.close();
        out.close();
        clientSocket.close();
    }

    private static boolean isMessage(String command) {
        return command.endsWith(MESSAGE_SIGN);
    }

    private static String getMessage(String message) {
        return message.substring(0, message.length() - 1);
    }

    public static void sendMessage() {
        while (true) {
            if (!output.isEmpty()) {
                String message = null;
                try {
                    message = output.take();
                } catch (InterruptedException ignored) {
                } finally {
                    if (message.matches(NEW_GAME_REQUEST)) {
                        if (interactor.getIsInGame())
                            out.println(X);
                        else
                            out.println(tell(message.split(" ")[2], IS_IN_GAME));
                    } else if (interactor.isMultiplayer()) {
                        if (username.trim().equals(interactor.getOtherPlayer().trim())) {
                            System.err.println("NOT YOUR TURN");
                            out.println(X);
                            continue;
                        }
                        out.println(message);
                        out.println(tell(interactor.getOtherPlayer(), message + ORDER_MARKER));
                    } else
                        out.println(message);
                }
            }
        }
    }

    public static String tell(String username, String description) {
        return USERNAME_MARKER + username + DESCRIPTION_MARKER + description;
    }

    static class InputProcessor extends Thread {
        private static final String SET_MULTIPLAYER = "set multiplayer";
        private static final String TABLE = "table";
        private static final String DIMENSIONS = "\\d+\\*\\d+";
        private static int previousN = interactor.getN();
        private static int previousM = interactor.getM();

        private BlockingDeque<String> input;

        public InputProcessor(BlockingDeque<String> input) {
            this.input = input;
        }

        @Override
        public void run() {
            while (true) {
                if (!input.isEmpty()) {
                    String message = null;
                    try {
                        message = input.take();
                    } catch (InterruptedException ignored) {
                    } finally {
                        if (isMessage(message))
                            System.out.println(getMessage(message));
                        else if (isFromSomeone(message)) {
                            String description = getDescription(message);
                            if (description.matches(IS_IN_GAME)) {
                                String username = getUsername(message);
                                if (interactor.getIsInGame())
                                    out.println(tell(username, PLAYER_IN_GAME));
                                else {
                                    interactor.reset();
                                    out.println(tell(username, TABLE));
                                    out.println(tell(username, "new game " + username + " " + ServerClient.
                                            username + ORDER_MARKER));
                                }
                            } else if (description.equals(TABLE)) {
                                out.println(tell(getUsername(message), interactor.getN() + "*" + interactor.getM()));
                            } else if (description.matches(DIMENSIONS)) {
                                String[] descriptionSplit = description.split("\\*");
                                previousN = interactor.getN();
                                previousM = interactor.getM();
                                interactor.setDimensions(Integer.parseInt(descriptionSplit[0]), Integer.parseInt(
                                        descriptionSplit[1]));
                                out.println("new game " + getUsername(message) + " " + ServerClient.username);
                                out.println(SET_MULTIPLAYER);
                            } else if (isAnOrder(description)) {
                                String order = getOrder(description);
                                out.println(order.trim());
                                if (order.matches(NEW_GAME))
                                    out.println(SET_MULTIPLAYER);
                            } else
                                System.out.println(description);
                        } else if (message.matches(SET_MULTIPLAYER)) {
                            interactor.setDimensions(previousN, previousM);
                            interactor.setMultiplayer(true);
                        } else {
                            if (message.matches(NUMBER)) {
                                int number = Integer.parseInt(message);
                                if (number > 0 && number <= Game.numberOfGamesInProgress()) {
                                    Game game = Game.getGame(Integer.parseInt(message));
                                    if (game.isMultiplayer()) {
                                        Player players = Game.getGame(number).getOtherPlayer(username);
                                        tell();
                                    }
                                }
                            }
                            boolean isRunning = interactor.act(message);
                            if (!message.startsWith("new game")) {
                                previousN = interactor.getN();
                                previousM = interactor.getM();
                            }
                            if (!isRunning) {
                                try {
                                    finish();
                                } catch (IOException ignored) {
                                } finally {
                                    System.exit(0);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}