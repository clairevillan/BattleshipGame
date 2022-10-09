package rst;

import static org.junit.Assert.*;

import org.junit.Test;

public class BattleshipTest {

    @Test
    // testing the display arrays method in BattleshipGame class since it is a
    // public method
    public void testDisplayArray() {
        BattleshipGame bg = new BattleshipGame();
        String[] names = { "PlayerOne", "PlayerTwo", "PlayerThree" };
        int[] scores = { 24, 12, 6 };
        String expectedOut = bg.display(names, scores);

        String testOutput = "";
        for (int i = 0; i < names.length; i++) {
            testOutput += (i + 1) + ". " + names[i] + ": " + scores[i] + " misses\n";
        }

        assertEquals(expectedOut, testOutput);
    }

}