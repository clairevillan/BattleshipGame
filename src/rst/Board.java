package rst;

import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Board extends Button {

    public static final char USER = 'U';
    public static final char COM = 'C';
    public static final char HIT = 'H';
    public static final char MISS = 'M';
    public static final char BLANK = ' ';

    private char value;

    private static final Image imgDeploy = new Image(Board.class.getResource("/images/deployed.png").toString());
    private static final Image imgHit = new Image(Board.class.getResource("/images/hit.png").toString());
    private static final Image imgMiss = new Image(Board.class.getResource("/images/miss.png").toString());
    private static final Image imgBlank = new Image(Board.class.getResource("/images/ocean.png").toString());

    /*
     * Constructor
     */
    public Board() {
        super();
        value = BLANK;
        setPrefSize(50, 50);
        ImageView view = new ImageView(imgBlank);

        setGraphic(view);

    }

    /*
     * This method changes the image of the button corresponding to its set action
     * which can be changed by both the user and the AI
     *
     * @param a char that holds the value of an image for a button
     */
    public void playShip(char val) {
        value = val;

        switch (value) {
            case USER:
                setGraphic(new ImageView(imgDeploy));
                break;

            // com has to remain blank so the user does not see their ships
            case COM:
                setGraphic(new ImageView(imgBlank));
                break;

            case HIT:
                setGraphic(new ImageView(imgHit));
                break;

            case MISS:
                setGraphic(new ImageView(imgMiss));
                break;

            case BLANK:
                setGraphic(new ImageView(imgBlank));
                break;
        }
    }

    /*
     * This getter method obtains the value of an action executed on a button grid
     * board
     *
     * @return a char containing the value of the user's or AI's actions on button
     * grid
     */
    public char getValue() {
        return value;
    }
}