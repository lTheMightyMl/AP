package sample;

import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;
import sample.classes.Account;
import sample.classes.Coordinates;
import sample.classes.Game;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Random;

public class Main extends Application {
    private static final double WINDOW_WIDTH_ASPECT_RATIO = 3;
    private static final double WINDOW_HEIGHT_ASPECT_RATIO = 4;
    private static final double WINDOW_SCALE = 150;
    private static final double WINDOW_WIDTH = WINDOW_WIDTH_ASPECT_RATIO * WINDOW_SCALE;
    private static final double WINDOW_HEIGHT = WINDOW_HEIGHT_ASPECT_RATIO * WINDOW_SCALE;
    private static final double BUTTON_WIDTH = 60;
    private static final double BUTTON_HEIGHT = 30;
    private static final double LABEL_HEIGHT = 50;
    private static final double TABLE_PADDING = 50;
    private static final double TABLE_DIMENSIONS = WINDOW_WIDTH - 2 * TABLE_PADDING;
    private static final double CELL_PADDING = 40;
    private static final int ANIMATION_TIME = 250;

    private static final String POSITIVE_INTEGER = "\\d+";

    private static final Random random = new Random();

    private static Account account;
    private static Game currentGame;
    private static ArrayList<ArrayList<Label>> cellLabels;
    private static ArrayList<HBox> rows;
    private static ArrayList<ArrayList<Coordinates>> destinations;

    public static void main(String[] args) {
        launch(args);
    }

    public static void setDestinations(ArrayList<ArrayList<Coordinates>> destinations) {
        Main.destinations = destinations;
    }

    @Override
    public void start(Stage primaryStage) {
        Account.load();
        Group mainMenu = new Group();
        Group gameMenu = new Group();
        Scene rootScene = getScene(mainMenu);
        Scene gameScene = getScene(gameMenu);
        Button play = getButton("Play");
        Button leaderboard = getButton("Leaderboard");
        Button changeUsername = getButton("Change username");
        Button quit = getButton("Quit");
        Button back = getButton("Back");
        setButtonWidth(leaderboard, 100);
        setButtonWidth(changeUsername, 140);
        addButtons(mainMenu, new Button[]{play, leaderboard, changeUsername, quit});
        addButtons(gameMenu, new Button[]{back});
        play.setOnMouseClicked(event1 -> {
            if (getDimensions())
                showTable(primaryStage, gameMenu, gameScene, back);
        });
        leaderboard.setOnMouseClicked(event -> {
            ArrayList<Label> entries = getLeaderboardLabels();
            showLeaderboard(primaryStage, back, entries);

        });
        changeUsername.setOnMouseClicked(event -> changeUsername());
        quit.setOnMouseClicked(event -> {
            account.save();
            System.exit(0);
        });
        back.setOnMouseClicked(event -> primaryStage.setScene(rootScene));
        gameMenu.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case W:
                    currentGame.moveUp();
                    break;
                case S:
                    currentGame.moveDown();
                    break;
                case D:
                    currentGame.moveRight();
                    break;
                case A:
                    currentGame.moveLeft();
                    break;
                default:
                    return;
            }
            account.setHighscore(currentGame.getScore());
            animate(primaryStage, gameMenu, gameScene, back, currentGame.getPreviousCells());
            if (currentGame.hasEnded())
                endGame(primaryStage, rootScene);
        });
        getAccount();
        primaryStage.setTitle("2×1024");
        primaryStage.setScene(rootScene);
        primaryStage.show();
        primaryStage.setOnCloseRequest(event -> account.save());

        AnimationTimer animationTimer = new AnimationTimer() {
            private static final long DECISECOND = 100 * 1000 * 1000;
            private static final int TILE_DIMENSIONS = 40;
            private final Random random = new Random();

            long lastTime = 0;

            @Override
            public void handle(long now) {
                if (now - lastTime > DECISECOND) {
                    lastTime = now;
                    Rectangle rectangle = new Rectangle(random.nextInt((int) WINDOW_WIDTH - TILE_DIMENSIONS),
                            random.nextInt((int) WINDOW_HEIGHT - TILE_DIMENSIONS), TILE_DIMENSIONS,
                            TILE_DIMENSIONS);
                    rectangle.setSmooth(true);
                    rectangle.setFill(getRandomColor());
                    rectangle.setMouseTransparent(true);
                    mainMenu.getChildren().add(rectangle);
                    rectangle.toBack();
                }
            }
        };
        animationTimer.start();
    }

    private void endGame(Stage primaryStage, Scene rootScene) {
        Alert gameOver = new Alert(Alert.AlertType.WARNING, "You scored " + currentGame.getScore() +
                " points.");
        gameOver.setTitle("Game over");
        gameOver.setHeaderText("Game over");
        gameOver.showAndWait();
        primaryStage.setScene(rootScene);
    }

    private void animate(Stage primaryStage, Group gameMenu, Scene gameScene, Button back, int[][] cells) {
        ArrayList<KeyValue> keyValues = new ArrayList<>();
        double cellDimensions = TABLE_DIMENSIONS / destinations.size() - CELL_PADDING / destinations.size();
        clearTable(cellDimensions);
        for (int i = 0; i < destinations.size(); i++) {
            ArrayList<Coordinates> row = destinations.get(i);
            for (int j = 0; j < row.size(); j++) {
                if (cells[i][j] == 0)
                    continue;
                Label movingLabel = getCellLabel(cells[i][j], cellDimensions);
                gameMenu.getChildren().add(movingLabel);
                movingLabel.toFront();
                double rowHeight = TABLE_DIMENSIONS / destinations.size();
                double cellPadding = CELL_PADDING / (2 * destinations.size());
                movingLabel.relocate(TABLE_PADDING + j * rowHeight + cellPadding, 2 * LABEL_HEIGHT + i *
                        rowHeight + cellPadding);
                Coordinates destination = destinations.get(i).get(j);
                keyValues.add(new KeyValue(movingLabel.translateXProperty(), cellLabels.get(destination.getX()).
                        get(destination.getY()).getLayoutX() - movingLabel.getLayoutX()));
                keyValues.add(new KeyValue(movingLabel.translateYProperty(), (destination.getX() - i) * rowHeight));
            }
        }
        KeyValue[] keyValuesArray = new KeyValue[keyValues.size()];
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(ANIMATION_TIME), keyValues.toArray(keyValuesArray)));
        timeline.setOnFinished(e -> showTable(primaryStage, gameMenu, gameScene, back));
        timeline.play();
    }

    private void clearTable(double cellDimensions) {
        for (HBox row : rows)
            for (int i = 0; i < row.getChildren().size(); i++)
                row.getChildren().set(i, getCellLabel(0, cellDimensions));
    }

    private Color getRandomColor() {
        return Color.color(getColorRandom(), getColorRandom(), getColorRandom());
    }

    private double getColorRandom() {
        return Double.min(Double.max(random.nextDouble(), 0), 1);
    }

    private void showTable(Stage primaryStage, Group gameMenu, Scene gameScene, Button back) {
        VBox vBox = getGameVBox();
        vBox.getChildren().add(getScoreHBox());
        vBox.getChildren().add(getEmptyHBox());
        cellLabels = new ArrayList<>();
        rows = new ArrayList<>();
        for (int[] row : currentGame.getCells()) {
            double rowLength = row.length;
            HBox rowHBox = getRowHBox(rowLength);
            double rowHeight = TABLE_DIMENSIONS / rowLength;
            double cellDimensions = rowHeight - CELL_PADDING / rowLength;
            setHBoxHeight(rowHBox, rowHeight);
            ArrayList<Label> rowLabels = new ArrayList<>();
            for (int cell : row) {
                Label cellLabel = getCellLabel(cell, cellDimensions);
                rowLabels.add(cellLabel);
                rowHBox.getChildren().add(cellLabel);
            }
            vBox.getChildren().add(rowHBox);
            cellLabels.add(rowLabels);
            rows.add(rowHBox);
        }
        vBox.getChildren().add(getEmptyHBox());
        vBox.getChildren().add(back);
        HBox emptyHBox;
        emptyHBox = new HBox();
        setHBoxHeight(emptyHBox, WINDOW_HEIGHT);
        vBox.getChildren().add(emptyHBox);
        gameMenu.getChildren().clear();
        gameMenu.getChildren().add(vBox);
        primaryStage.setScene(gameScene);
    }

    private Label getCellLabel(int cell, double cellDimensions) {
        Label cellLabel;
        if (cell == 0)
            cellLabel = new Label("");
        else
            cellLabel = new Label(Integer.toString(cell));
        setLabelHeight(cellLabel, cellDimensions);
        setLabelWidth(cellLabel, cellDimensions);
        cellLabel.setAlignment(Pos.CENTER);
        cellLabel.setBackground(new Background(new BackgroundFill(getColorByCell(cell), new CornerRadii(
                10), new Insets(0, 0, 0, 0))));
        cellLabel.setFont(Font.font(Font.getFamilies().get(random.nextInt(Font.getFamilies().size())), FontWeight.
                EXTRA_BOLD, FontPosture.ITALIC, 15));
        return cellLabel;
    }

    private HBox getEmptyHBox() {
        HBox emptyHBox = new HBox();
        setHBoxHeight(emptyHBox, LABEL_HEIGHT);
        return emptyHBox;
    }

    private HBox getRowHBox(double rowLength) {
        HBox hBox = new HBox();
        hBox.setSpacing(CELL_PADDING / rowLength);
        hBox.setBackground(new Background(new BackgroundFill(Color.color(0.8, 0.1, 0.3),
                new CornerRadii(0), new Insets(0, TABLE_PADDING, 0, TABLE_PADDING))));
        hBox.setAlignment(Pos.CENTER);
        setHBoxWidth(hBox, WINDOW_WIDTH);
        return hBox;
    }

    private void setHBoxHeight(HBox hBox, double hBoxHeight) {
        hBox.setMinHeight(hBoxHeight);
        hBox.setPrefHeight(hBoxHeight);
        hBox.setMaxHeight(hBoxHeight);
    }

    private void setHBoxWidth(HBox hBox, double hBoxWidth) {
        hBox.setMaxWidth(hBoxWidth);
        hBox.setMinWidth(hBoxWidth);
        hBox.setPrefWidth(hBoxWidth);
    }

    private void setVBoxWidth(VBox vBox, double vBoxWidth) {
        vBox.setMaxWidth(vBoxWidth);
        vBox.setMinWidth(vBoxWidth);
        vBox.setPrefWidth(vBoxWidth);
    }

    private VBox getGameVBox() {
        VBox vBox = new VBox();
        vBox.setFillWidth(true);
        vBox.setAlignment(Pos.CENTER);
        setVBoxWidth(vBox, WINDOW_WIDTH);
        vBox.setBackground(new Background(new BackgroundFill(Color.color(0.1, 0.4, 0.7),
                new CornerRadii(2), new Insets(0, 0, 0, 0))));
        return vBox;
    }

    private HBox getScoreHBox() {
        HBox score = new HBox();
        score.getChildren().add(getLabel("Score: " + currentGame.getScore()));
        score.setBackground(new Background(new BackgroundFill(Color.color(0.8, 0.5, 0),
                new CornerRadii(2), new Insets(0, 0, 0, 0))));
        return score;
    }

    private boolean getDimensions() {
        TextInputDialog dimensionsInputDialogue = new TextInputDialog("4");
        dimensionsInputDialogue.setTitle("Dimensions");
        dimensionsInputDialogue.setResizable(false);
        dimensionsInputDialogue.setHeaderText("Please enter your desired dimensions.");
        Optional<String> result = dimensionsInputDialogue.showAndWait();
        if (result.isPresent() && getString(result).matches(POSITIVE_INTEGER)) {
            currentGame = new Game(Integer.parseInt(getString(result)));
            return true;
        }
        return false;
    }

    private void changeUsername() {
        TextInputDialog newUsernameInputDialogue = new TextInputDialog();
        newUsernameInputDialogue.setTitle("New username");
        newUsernameInputDialogue.setResizable(false);
        newUsernameInputDialogue.setHeaderText("Please enter your new username.");
        Optional<String> username = newUsernameInputDialogue.showAndWait();
        while (true) {
            if (username.isPresent()) {
                String usernameString = getString(username);
                if (!usernameString.isEmpty()) {
                    if (!Account.exists(usernameString)) {
                        account.changeUsername(usernameString);
                        break;
                    } else {
                        new Alert(Alert.AlertType.ERROR, "Username already taken").showAndWait();
                        username = newUsernameInputDialogue.showAndWait();
                    }
                }
            } else
                break;
        }
    }

    private void showLeaderboard(Stage primaryStage, Button back, ArrayList<Label> entries) {
        VBox vBox = new VBox();
        vBox.setSpacing(0);
        vBox.setFillWidth(true);
        vBox.setAlignment(Pos.CENTER);
        vBox.setBackground(new Background(new BackgroundFill(getRandomColor(), new CornerRadii(0), new Insets(
                0))));
        vBox.getChildren().add(getLabel("Leaderboard"));
        vBox.getChildren().addAll(entries);
        vBox.getChildren().add(getEmptyHBox());
        vBox.getChildren().add(back);
        HBox emptyHBox = new HBox();
        setHBoxHeight(emptyHBox, Double.max(WINDOW_HEIGHT - (entries.size() + 2) * LABEL_HEIGHT - BUTTON_HEIGHT,
                LABEL_HEIGHT));
        vBox.getChildren().add(emptyHBox);
        setVBoxWidth(vBox, WINDOW_WIDTH);
        ScrollPane scrollPane = getScrollPane(vBox);
        primaryStage.setScene(new Scene(scrollPane, WINDOW_WIDTH, WINDOW_HEIGHT));
    }

    private ArrayList<Label> getLeaderboardLabels() {
        int lastHighscore = Account.getAccounts().get(0).getHighscore();
        int index = 1;
        ArrayList<Label> entries = new ArrayList<>();
        for (int i = 0; i < Account.getAccounts().size(); i++) {
            Account account = Account.getAccounts().get(i);
            int highscore = account.getHighscore();
            if (highscore != lastHighscore) {
                index++;
                lastHighscore = highscore;
            }
            entries.add(getLabel(index + "." + account.getName() + " : " + highscore));
        }
        return entries;
    }

    private void getAccount() {
        TextInputDialog usernameInputDialogue = new TextInputDialog();
        usernameInputDialogue.setTitle("Username");
        usernameInputDialogue.setResizable(false);
        usernameInputDialogue.setHeaderText("Please enter your username.");
        Optional<String> username = usernameInputDialogue.showAndWait();
        while (true) {
            if (username.isPresent()) {
                String usernameString = getString(username);
                if (!usernameString.isEmpty()) {
                    account = Account.getAccount(usernameString);
                    break;
                }
            } else
                System.exit(0);
            username = usernameInputDialogue.showAndWait();
        }
    }

    private Color getColorByCell(int cell) {
        if (cell == 0)
            return Color.BLANCHEDALMOND;
        double logarithm = Math.log(cell) / Math.log(2);
        return Color.color(((logarithm % 2) + 1) / 3, Double.max(1 - logarithm / 12, 0), Double.min(logarithm / 12,
                1));
    }

    private ScrollPane getScrollPane(VBox vBox) {
        ScrollPane scrollPane = new ScrollPane();
        setScrollPaneSize(scrollPane, WINDOW_WIDTH, WINDOW_HEIGHT);
        setScrollPaneViewportSize(scrollPane, WINDOW_WIDTH, WINDOW_HEIGHT);
        fixHbar(scrollPane);
        scrollPane.setFitToWidth(true);
        scrollPane.setContent(vBox);
        scrollPane.setVvalue(scrollPane.getVmax());
        return scrollPane;
    }

    private void fixHbar(ScrollPane scrollPane) {
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setHmax(0);
        scrollPane.setHvalue(0);
    }

    private void setScrollPaneViewportSize(ScrollPane scrollPane, double scrollPaneViewportWidth, double
            scrollPaneViewportHeight) {
        scrollPane.setMinViewportHeight(scrollPaneViewportHeight);
        scrollPane.setPrefViewportHeight(scrollPaneViewportHeight);
        scrollPane.setMinViewportHeight(scrollPaneViewportWidth);
        scrollPane.setPrefViewportWidth(scrollPaneViewportWidth);
    }

    private void setScrollPaneSize(ScrollPane scrollPane, double scrollPaneWidth, double scrollPaneHeight) {
        scrollPane.setPrefSize(scrollPaneWidth, scrollPaneHeight);
        scrollPane.setMinSize(scrollPaneWidth, scrollPaneHeight);
        scrollPane.setMaxSize(scrollPaneWidth, scrollPaneHeight);
    }

    private Label getLabel(String text) {
        Label label = new Label(text);
        setLabelHeight(label, LABEL_HEIGHT);
        setLabelWidth(label, WINDOW_WIDTH);
        label.setAlignment(Pos.CENTER);
        label.setTextFill(getRandomColor());
        label.setBackground(new Background(new BackgroundFill(getRandomColor(), new CornerRadii(0), new Insets(
                0))));
        label.setFont(Font.font(Font.getFamilies().get(random.nextInt(Font.getFamilies().size())), FontWeight.EXTRA_BOLD
                , FontPosture.ITALIC, 15));
        return label;
    }

    private void setLabelWidth(Label label, double labelWidth) {
        label.setMaxWidth(labelWidth);
        label.setMinWidth(labelWidth);
        label.setPrefWidth(labelWidth);
    }

    private void setLabelHeight(Label label, double labelHeight) {
        label.setMaxHeight(labelHeight);
        label.setMinHeight(labelHeight);
        label.setPrefHeight(labelHeight);
    }

    private String getString(Optional<String> result) {
        String[] resultSplit = result.toString().split("\\[")[1].split("]");
        return (resultSplit.length > 0 ? resultSplit[0] : "");
    }

    private Scene getScene(Group group) {
        return new Scene(group, WINDOW_WIDTH, WINDOW_HEIGHT);
    }

    private void addButtons(Group root, Button[] buttons) {
        for (int i = 0; i < buttons.length; i++) {
            Button button = buttons[i];
            button.relocate(WINDOW_WIDTH / 2 - button.getMaxWidth() / 2, (i + 1) * WINDOW_HEIGHT / (buttons.
                    length + 1) - button.getMaxHeight() / 2);
            root.getChildren().add(button);
        }
    }

    private Button getButton(String text) {
        Button button = new Button(text);
        setButtonWidth(button, BUTTON_WIDTH);
        setButtonHeight(button, BUTTON_HEIGHT);
        return button;
    }

    private void setButtonWidth(Button changeUsername, double buttonWidth) {
        changeUsername.setMaxWidth(buttonWidth);
        changeUsername.setMinWidth(buttonWidth);
        changeUsername.setPrefWidth(buttonWidth);
    }

    private void setButtonHeight(Button button, double buttonHeight) {
        button.setMaxHeight(buttonHeight);
        button.setMinHeight(buttonHeight);
        button.setPrefHeight(buttonHeight);
    }
}
