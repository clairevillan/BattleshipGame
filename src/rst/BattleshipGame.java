package rst;

import rst.Board;
import simpleIO.Console;
import simpleIO.FXDialog;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class BattleshipGame extends Application {

    private static final int GAP = 0;
    private static final int SMALL_FONT = 18;
    private static final int FONT = 25;
    private static final int MEDIUM_FONT = 30;

    private Button btnScore;

    private Board[][] boxUser;
    private Board[][] boxCom;

    private Label lblUserFleet, lblComFleet, lblHits, lblMisses, lblAction;

    private int userShips = 5;
    private int comShips = 5;
    private int userShipsDep = 5;

    int comShipsDep = 5;
    int hits = 0;
    int miss = 0;

    String playerName;

    ArrayList<String> players = new ArrayList<String>();
    ArrayList<Integer> scores = new ArrayList<Integer>();

    public void start(Stage myStage) throws Exception {

        GridPane root = new GridPane();

        root.setHgap(GAP);
        root.setVgap(GAP);
        root.setPadding(new Insets(GAP, GAP, GAP, GAP));

        boxUser = new Board[6][6];
        boxCom = new Board[6][6];

        // user deploys their ships on this board
        for (int row = 0; row <= 5; row++) {
            for (int col = 0; col <= 5; col++) {
                boxUser[row][col] = new Board();
                boxCom[row][col] = new Board();
                boxCom[row][col].setDisable(true);
                boxUser[row][col].setOnAction(event -> deployUserShips(event));
                root.add(boxUser[row][col], col, row + 15);
            }
        }

        // AI deploys its five ships
        deployComShips();

        // displays firing status for user - user plays on this board
        for (int row = 0; row <= 5; row++) {
            for (int col = 0; col <= 5; col++) {
                boxCom[row][col].setOnAction(event -> userAttack(event));
                root.add(boxCom[row][col], col + 9, row + 15);

            }
        }

        ImageView imgTitle = new ImageView(getClass().getResource("/images/title.jpg").toString());
        root.add(imgTitle, 8, 1, 1, 5);

        Label lblUserBase = new Label("\t\tYOUR BASE");
        lblUserBase.setFont(new Font("Impact", MEDIUM_FONT));
        lblUserBase.setTextFill(Color.WHITE);
        root.add(lblUserBase, 1, 14, 10, 1);

        Label lblFire = new Label("\t    FIRING STATUS");
        lblFire.setFont(new Font("Impact", MEDIUM_FONT));
        lblFire.setTextFill(Color.WHITE);
        root.add(lblFire, 10, 14, 40, 1);

        btnScore = new Button("LEADERBOARD");
        btnScore.setOnAction(event -> loadScore());
        btnScore.setDisable(true);
        btnScore.setFont(new Font("Impact", SMALL_FONT));
        root.add(btnScore, 12, 5, 5, 1);

        lblUserFleet = new Label();
        lblUserFleet.setFont(new Font("Impact", FONT));
        lblUserFleet.setTextFill(Color.WHITE);
        root.add(lblUserFleet, 2, 23, 3, 1);

        lblComFleet = new Label();
        lblComFleet.setFont(new Font("Impact", FONT));
        lblComFleet.setTextFill(Color.WHITE);
        root.add(lblComFleet, 2, 26, 3, 1);

        lblHits = new Label();
        lblHits.setFont(new Font("Impact", FONT));
        lblHits.setTextFill(Color.WHITE);
        root.add(lblHits, 11, 23, 3, 1);

        lblMisses = new Label();
        lblMisses.setFont(new Font("Impact", FONT));
        lblMisses.setTextFill(Color.WHITE);
        root.add(lblMisses, 11, 26, 3, 1);

        lblAction = new Label("   >> DEPLOY YOUR FIVE\n   SHIPS ON YOUR BASE!");
        lblAction.setFont(new Font("Impact", MEDIUM_FONT));
        lblAction.setTextFill(Color.WHITE);
        root.add(lblAction, 7, 17, 30, 1);

        // displays the actions of the user or the AI
        status();

        root.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));
        Scene scene = new Scene(root);
        myStage.setTitle("Battleship");
        myStage.setScene(scene);
        myStage.show();

    }

    /*
     * This method displays to the user how many ships each player has on their
     * board and the amount of hits and misses the user executes
     */
    private void status() {
        lblUserFleet.setText("YOUR FLEET: " + userShips);
        lblComFleet.setText("ENEMY FLEET: " + comShips);

        lblHits.setText("HITS: " + hits);
        lblMisses.setText("MISSES: " + miss);
    }

    /*
     * This method allows the user to deploy their five ships using a button click
     * (on the user's base), once all five ships have been deployed, a new status
     * will be prompted to the user and the user's base will be disabled while the
     * AI's base will be enabled to indicate to the user that it is their turn to
     * fire at the status. The user's base will also become null so that the user
     * does not place more than five ships on their base.
     *
     * @param an ActionEvent that will obtain the user's clicks on the button grid
     */
    private void deployUserShips(ActionEvent event) {
        Board temp = (Board) event.getSource();

        if (temp.getValue() == Board.BLANK) {
            temp.playShip(Board.USER);

            if (--userShipsDep == 0) {
                lblAction.setText("  >> ENEMY DEPLOYED!\n\t  FIRE A TARGET!");
                for (int row = 0; row <= 5; row++) {
                    for (int col = 0; col <= 5; col++) {
                        boxUser[row][col].setDisable(true);
                        boxCom[row][col].setDisable(false);
                        boxUser[row][col].setOnAction(null);

                    }
                }
            }
        }

    }

    /*
     * This method deploys all five of the AI's ships on the its board (Firing
     * Status) by generating a new random coordinate five times using a for loop.
     * Before allowing the AI to deploy its ships, the method checks if the new
     * generated coordinate matches a blank image button so that there is no overlap
     * of ships
     */
    private void deployComShips() {

        for (int i = 0; i < comShipsDep; i++) {
            int x = (int) (Math.random() * comShipsDep);
            int y = (int) (Math.random() * comShipsDep);

            if (boxCom[x][y].getValue() == Board.BLANK) {
                boxCom[x][y].playShip(Board.COM);

            }
        }

    }

    /*
     * This method allows the user to attack once on the firing status. The method
     * checks if the user's attack location matches the AI's deployed ships'
     * location. If the location does match, the button image will change to
     * indicate a hit, the number of hits will increment, and the number of AI's
     * ships will decrease; otherwise, the button image will change to indicate a
     * miss and the number of misses will increment. Once the user has fired at a
     * location, their base board will be enabled to show that the AI will fire and
     * the chosen button location will become null so that the user cannot choose
     * that location again.
     *
     * @param an ActionEvent that will obtain the user's click on the button grid
     */
    private void userAttack(ActionEvent event) {
        Board temp = (Board) event.getSource();

        if (temp.getValue() == Board.COM) {
            hits++;
            comShips--;
            temp.playShip(Board.HIT);

        } else {
            miss++;
            temp.playShip(Board.MISS);
        }
        for (int row = 0; row <= 5; row++) {
            for (int col = 0; col <= 5; col++) {
                boxUser[row][col].setDisable(false);
                temp.setOnAction(null);
            }
        }
        status();
        comAttack();

        if (comShips == 0 || userShips == 0) {
            gameOver();
        }
    }

    /*
     * This method generates a new random coordinate for the AI to attack on the
     * user's base. It checks if the new coordinate is valid, if it is, then that
     * location will be fired at. Once an attack has been executed, status will be
     * updated to prompt the user on the AI's actions.
     */
    private void comAttack() {

        int x = (int) (Math.random() * 6);
        int y = (int) (Math.random() * 6);

        while (boxUser[x][y].getValue() != Board.HIT && boxUser[x][y].getValue() != Board.MISS) {

            if (boxUser[x][y].getValue() == Board.USER) {
                boxUser[x][y].playShip(Board.HIT);
                userShips--;
                lblAction.setText("   ENEMY HIT YOUR SHIP!\n   >> FIRE A TARGET");

            } else {
                boxUser[x][y].playShip(Board.MISS);
                lblAction.setText("\t  ENEMY MISSED!\n     >> FIRE A TARGET");

            }
            break;
        }
        status();

        if (comShips == 0 || userShips == 0) {
            gameOver();

        }
    }

    /*
     * This method outputs the final status of the game to the user and disables
     * both boards so there is no additional playing on the finished game. If the
     * user has won the game, the leaderboard button will be enabled for the user to
     * press.
     */
    private void gameOver() {
        for (int row = 0; row <= 5; row++) {
            for (int col = 0; col <= 5; col++) {
                boxCom[row][col].setDisable(true);
                boxUser[row][col].setDisable(true);

                if (comShips == 0) {
                    lblAction.setText("\t\tYOU WON!\n   CHECK LEADERBOARD!");
                    btnScore.setDisable(false);
                } else {
                    lblAction.setText("\t\tYOU LOST!\n\t       TRY AGAIN!");
                }
            }
        }
    }

    /*
     * This method prompts the user for its username and gets their total amount of
     * misses. Then, these values are added to two separate ArrayLists to be sorted
     * and organized into a leaderboard layout. The sorted data will be written and
     * saved to the file.
     */

    private void saveScore() {
        BattleshipGame demo = new BattleshipGame();

        String playerName;
        playerName = FXDialog.readString("Enter Username: ");
        int num;
        num = FXDialog.readInt("Enter amount of misses: ");

        players.add(playerName);
        scores.add(num);

        try {

            PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter("data/scores.txt")));

            String[] names = new String[players.size()];
            int[] scoreNum = new int[scores.size()];
            int counter = 0;

            for (int i = 0; i < players.size(); i++) {
                names[counter] = players.get(i);
                scoreNum[counter] = scores.get(i);
                counter++;
            }

            // sorts the added names and scores in ascending order (the less misses the
            // better the score)
            demo.sortScore(names, scoreNum);
            pw.write(demo.display(names, scoreNum));

            pw.close();

        } catch (FileNotFoundException e) {
            Console.print("File not found");

        } catch (IOException e) {
            System.err.println("Exception: " + e);
            System.exit(0);
        }
    }

    /*
     * This method loads the sorted scores from the saved text file and outputs to
     * the user through a FXDialog
     */
    private void loadScore() {

        saveScore();

        try {
            BufferedReader br = new BufferedReader(new FileReader("data/scores.txt"));
            String line;
            String output = "";

            while ((line = br.readLine()) != null) {
                output += line + "\n";

            }
            FXDialog.print("LEADERBOARD\n" + output);
            br.close();
        } catch (FileNotFoundException e) {
            Console.print("File not found");

        } catch (IOException e) {
            System.err.println("Exception: " + e);
            System.exit(0);
        }
    }

    /*
     * This method combines the string and int array together in sorted order and
     * displays the final sorted list as a string
     *
     * @return a String containing both the sorted String and int arrays to output.
     */
    public String display(String[] names, int[] score) {
        String output = "";
        for (int i = 0; i < names.length; i++) {
            output += (i + 1) + ". " + names[i] + ": " + score[i] + " misses\n";
        }
        return output;
    }

    /*
     * This method uses selection sort to arrange the data numerically in ascending
     * order so that the least amount of misses will be at the top of the list
     *
     * @param a String array containing the list of player names and an int array
     * containing the list of corresponding scores
     */
    private void sortScore(String[] names, int[] score) {

        int numItems = names.length;
        int currentMin = 0;

        for (int i = 0; i < numItems - 1; i++) {
            currentMin = i;
            for (int j = i + 1; j < numItems; j++) {

                if (score[j] < score[currentMin]) {
                    currentMin = j;
                }

            }
            // swapping names
            String temp = names[i];
            names[i] = names[currentMin];
            names[currentMin] = temp;

            // swapping marks
            int tempInt = score[i];
            score[i] = score[currentMin];
            score[currentMin] = tempInt;

        }

    }

    public static void main(String[] args) {
        launch();
    }
}