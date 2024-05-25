import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Random;

public class GuessTheNumberGame {
    private JLabel msgLbl2;
    private JFrame frm;
    private JTextField guessFld;
    private JLabel msgLbl;
    private JButton guessBtn;
    private JTextArea prevGuessArea;
    private int randNum;
    private int minRange = 1;
    private int maxRange = 100;
    private int closer = 10;
    private int attempts = 0;
    private int maxAttempts = 15;
    private int score = 0;
    private ArrayList<Integer> prevGuesses;

    public GuessTheNumberGame() {
        frm = new JFrame("Guess The Number");
        frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frm.setSize(500, 400);
        frm.setLayout(null);

        msgLbl = new JLabel("Guess a number between " + minRange + " and " + maxRange + ". Attempts left: " + (maxAttempts - attempts));
        msgLbl.setBounds(30, 20, 400, 30);
        msgLbl.setForeground(Color.BLUE);
        frm.add(msgLbl);

        msgLbl2 = new JLabel("");
        msgLbl2.setBounds(50, 270, 400, 40); // Adjusted y-coordinate
        msgLbl2.setForeground(Color.BLACK);
        msgLbl2.setFont(new Font("Arial Black", Font.PLAIN, 18));
        frm.add(msgLbl2);


        guessFld = new JTextField();
        guessFld.setBounds(30, 60, 150, 30);
        frm.add(guessFld);

        guessBtn = new JButton("Guess");
        guessBtn.setBounds(190, 60, 100, 30);
        guessBtn.setBackground(Color.GREEN);
        frm.add(guessBtn);

        prevGuessArea = new JTextArea();
        prevGuessArea.setBounds(30, 100, 400, 150);
        prevGuessArea.setEditable(false);
        prevGuessArea.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        frm.add(prevGuessArea);

        guessBtn.addActionListener(new GuessBtnListener());

        resetGame();

        frm.setVisible(true);
    }

    private void resetGame() {
        Random rand = new Random();
        randNum = rand.nextInt(maxRange - minRange + 1) + minRange;
        prevGuesses = new ArrayList<>();
        attempts = 0;
        score = 0;
        msgLbl.setText("Guess a number between " + minRange + " and " + maxRange + ". Attempts left: " + (maxAttempts - attempts));
        guessFld.setText("");
        prevGuessArea.setText("");
        guessBtn.setEnabled(true);
    }

    private class GuessBtnListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            try {
                int userGuess = Integer.parseInt(guessFld.getText());
                prevGuesses.add(userGuess);
                updatePrevGuesses();

                attempts++;
                if (randNum == userGuess) {
                    score = (maxAttempts - attempts) * 10;
                    msgLbl.setText("Correct! The number was: " + randNum + ". Your score: " + score);
                    msgLbl.setForeground(Color.BLACK);
                    msgLbl2.setText("CONGRATULATIONS!!!! YOU WON");
                    guessBtn.setEnabled(false);
                    playCelebrationSound();
                    displayCelebrationAnimation();
                    guessFld.setText("");
                } else {
                    int difference = Math.abs(randNum - userGuess);
                    if (difference <= closer) {
                        msgLbl.setText("You are too close! ");
                        if (userGuess < randNum) {
                            msgLbl.setText(msgLbl.getText() + "Low!");
                        } else if (userGuess > randNum) {
                            msgLbl.setText(msgLbl.getText() + "High!");
                        }
                    } else {
                        msgLbl.setText("");
                        if (userGuess < randNum) {
                            msgLbl.setText("Too low!");
                        } else if (userGuess > randNum) {
                            msgLbl.setText("Too high!");
                        }
                    }
                    guessFld.setText("");
                    try {
                        File file = new File("E:\\IdeaProjects\\Cipherbyte\\src\\lose.wav");
                        AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);
                        Clip clip = AudioSystem.getClip();
                        clip.open(audioStream);
                        clip.start();
                    } catch (Exception ex) {
                        System.out.println(ex.getMessage());
                    }
                }

                if (attempts >= maxAttempts) {
                    msgLbl.setText("Game Over! The number was: " + randNum);
                    guessBtn.setEnabled(false);
                } else {
                    msgLbl.setText(msgLbl.getText() + " Attempts left: " + (maxAttempts - attempts));
                }

            } catch (NumberFormatException ex) {
                msgLbl.setText("Please enter a valid number.");
            }
        }

        private void updatePrevGuesses() {
            StringBuilder sb = new StringBuilder("Previous guesses: ");
            for (int guess : prevGuesses) {
                sb.append(guess).append(" ");
            }
            prevGuessArea.setText(sb.toString());
        }

        private void playCelebrationSound() {
            try {
                File file = new File("E:\\IdeaProjects\\Cipherbyte\\src\\win.wav");
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);
                Clip clip = AudioSystem.getClip();
                clip.open(audioStream);
                clip.start();
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }

        private void displayCelebrationAnimation() {
            Timer timer = new Timer(100, new ActionListener() {
                int frame = 0;

                @Override
                public void actionPerformed(ActionEvent e)
                {
                    if (frame == 10)
                    {
                        ((Timer) e.getSource()).stop();
                        return;
                    }
                    frm.getContentPane().setBackground(Color.getHSBColor((float) Math.random(), 1.0f, 1.0f));
                    frame++;
                }
            });
            timer.start();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GuessTheNumberGame());
    }
}
