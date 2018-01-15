import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

/**
 * Creates a Whack-a-Mole game.
 * @author Rui Yang (Andrew id: ryang2)
 *
 */

public class Game {
    /**
     * The string when a mole is down.
     */
    private static final String OFF_STRING = "   ";
    /**
     * The string when a mole is up.
     */
    private static final String UP_STRING = "(^-^)";
    /**
     * The string when a mole is being hit.
     */
    private static final String HIT_STRING = "(XoX)";
    /**
     *  The color when a mole is down.
     */
    private static final Color OFF_COLOR = Color.LIGHT_GRAY;
    /**
     * The color when a mole is up.
     */
    private static final Color UP_COLOR = new Color((float) 0.118, (float) 0.565, (float) 1.000);
    /**
     * The color when a mole is being hit.
     */
    private static final Color HIT_COLOR = new Color((float) 1, (float) 0.388, (float) 0.278);
    /**
     * The number of moles.
     */
    private static final int BUTTON_NUM = 49;
    /**
     * The int of score and time count.
     */
    private int count, score;
    /**
     * The array of mole JButtons.
     */
    private JButton[] buttons;
    /**
     * The array of mole Threads.
     */
    private Thread[] moleThreads = new Thread[BUTTON_NUM];
    /**
     * The area show timer.
     */
    private JTextField timeText = new JTextField(10);
    /**
     * The area shows score.
     */
    private JTextField scoreText = new JTextField(10);
    /**
     * The JFrame of the Swing GUI.
     */
    private JFrame window;
    /**
     * The content panel of Swing GUI.
     */
    private JPanel pane = new JPanel();
    /**
     * The panel holds timer, scoreboard and start button.
     */
    private JPanel title = new JPanel();
    /**
     * The panel holds mole buttons.
     */
    private JPanel buttonsPane = new JPanel();
    /**
     * The start button.
     */
    private JButton start = new JButton();
    /**
     * The label of start button.
     */
    private JLabel timeLeft = new JLabel();
    /**
     * The label of score board.
     */
    private JLabel scoreBoard = new JLabel();
    /**
     * Random will be used to generate random numbers.
     */
    private Random random = new Random();
    /**
     * Generates the game GUI.
     */
    public Game() {
        //font
        Font font = new Font(Font.MONOSPACED, Font.BOLD, 14);
        //pane
        window = new JFrame("Whack a Mole");
        window.setSize(800, 400);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pane.setLayout(new BoxLayout(pane, BoxLayout.PAGE_AXIS));
        buttonsPane.setLayout(new GridLayout(7, 7));
        pane.add(title);
        pane.add(buttonsPane);
        window.setContentPane(pane);
        title.setBorder(new EmptyBorder(20, 10, 0, 10));
        buttonsPane.setBorder(new EmptyBorder(0, 20, 20, 20));
        //title start
        start = new JButton("start");
        start.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                start.setEnabled(false);
                Thread timer = new TimerThread();
                timer.start();
                for (int i = 0; i < BUTTON_NUM; i++) {
                    moleThreads[i] = new HelperThread(buttons[i]);
                    moleThreads[i].start();
                }
            }
        });
        title.add(start);
        //title time
        timeLeft = new JLabel("          Time Left: ");
        title.add(timeLeft);
        timeText.setEditable(false);
        title.add(timeText);
        //title score
        scoreBoard = new JLabel("          Score: ");
        title.add(scoreBoard);
        scoreText.setEditable(false);
        title.add(scoreText);
        //buttons
        buttons = new JButton[BUTTON_NUM];
        for (int i = 0; i < buttons.length; i++) {
            buttons[i] = new JButton(OFF_STRING);
            buttons[i].setBackground(OFF_COLOR);
            buttons[i].setFont(font);
            buttons[i].setOpaque(true);
            buttonsPane.add(buttons[i]);
            JButton moleButton = buttons[i];
            moleButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (count > 0) {
                        if (moleButton.getText().equals(UP_STRING)) {
                            score++;
                            scoreText.setText("" + score);
                            moleButton.setText(HIT_STRING);
                            moleButton.setBackground(HIT_COLOR);
                        }
                    }
                }
            });
        }
        window.setVisible(true);
    }
    /**
     * Helper method to generate mole threads.
     * @author Rui Yang (Andrew id: ryang2)
     *
     */
    private class HelperThread extends Thread {
        /**
         * The private button that will be manipulated with threads.
         */
        private JButton button;
        /**
         * The constructor that set the button to the passed parameter.
         * @param inputButton the button that passed in.
         */
        HelperThread(JButton inputButton) {
            button = inputButton;
            if (count > 0) {
                if (button.getText().equals(OFF_STRING)) {
                    button.setText(UP_STRING);
                    button.setBackground(UP_COLOR);
                } else {
                    button.setText(OFF_STRING);
                    button.setBackground(OFF_COLOR);
                }
            }
        }
        @Override
        public void run() {
            while (count > 0) {
                int randomUpNum = random.nextInt(3000);
                int randomDownNum = random.nextInt(8000);
                synchronized (button) {
                    if (!start.isEnabled()) {
                        if (button.getText().equals(OFF_STRING)) {
                            button.setText(UP_STRING);
                            button.setBackground(UP_COLOR);
                            try {
                                Thread.sleep(1000 + randomUpNum);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        } else {
                            button.setText(OFF_STRING);
                            button.setBackground(OFF_COLOR);
                            try {
                                Thread.sleep(2000);
                                Thread.sleep(randomDownNum);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
            synchronized (button) {
                if (count < 1) {
                    button.setText(OFF_STRING);
                    button.setBackground(OFF_COLOR);
                }
            }
        }
    }
    /**
     * Helper method to generate the timer thread.
     * @author Rui Yang (Andrew id: ryang2)
     *
     */
    private class TimerThread extends Thread {
        @Override
        public void run() {
            synchronized (scoreText) {
                count = 20;
                score = 0;
                while (count >= 0) {
                    timeText.setText(String.valueOf(count));
                    scoreText.setText(String.valueOf(score));
                    count--;
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                count = 20;
                score = 0;
                scoreText.setText("" + score);
                start.setEnabled(true);
            }
        }
    }
    /**
     * Main method that runs the game.
     * @param args the parameter of main method
     */
    public static void main(String[] args) {
        new Game();
    }
}
