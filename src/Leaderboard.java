package src;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static src.App.initBoardWindow;

/**
 * @class Leaderboard
 *
 * @brief The Leaderboard class represents the leaderboard panel in the game.
 */
public class Leaderboard extends JPanel implements ActionListener {

    private int startY;
    private int leaderboardHeight;
    private int leaderboardSize;
    private List<String> leaderboardLines;

    private final String leaderboardPath;

    /**
     * @brief Constructs a Leaderboard object.
     */
    public Leaderboard(){
        setLayout(null);

        leaderboardPath = "src/leaderboard.txt";
        setPreferredSize(new Dimension(Board.COLUMNS*Board.TILE_SIZE, Board.ROWS*Board.TILE_SIZE));
        readLeaderboard();

        JButton startAgainButton = new JButton("Play");
        startAgainButton.setActionCommand("play_again");
        startAgainButton.addActionListener(this);

        startAgainButton.setForeground(new Color(173, 33, 33));
        startAgainButton.setBackground(new Color(255, 255, 255));
        startAgainButton.setFocusPainted(false);
        startAgainButton.setFont(new Font("Lato", Font.BOLD, 16));
        startAgainButton.setBorder(BorderFactory.createLineBorder(new Color(173, 33, 33), 2));
        startAgainButton.setBounds((Board.COLUMNS * Board.TILE_SIZE - 120) / 2,
                startY + leaderboardHeight + 20, 120, 30);
        startAgainButton.setVisible(true);

        add(startAgainButton);
    }

    /**
     * @brief Reads the leaderboard data from the file.
     */
    private void readLeaderboard(){
        try {
            BufferedReader leaderboardReader = new BufferedReader(new FileReader(leaderboardPath));
            leaderboardLines = new ArrayList<>();
            String line;
            while ((line = leaderboardReader.readLine()) != null) {
                leaderboardLines.add(line);
            }
            leaderboardReader.close();

            leaderboardLines.sort((s1, s2) -> {
                int score1 = Integer.parseInt(s1.split(": ")[1]);
                int score2 = Integer.parseInt(s2.split(": ")[1]);
                return Integer.compare(score2, score1);
            });

            leaderboardSize = Math.min(5, leaderboardLines.size());
            leaderboardHeight = leaderboardSize * Board.TILE_SIZE;
            startY = (Board.ROWS * Board.TILE_SIZE - leaderboardHeight) / 2;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @brief Handles the button click event.
     *
     * @param e The action event object.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if ("play_again".equals(e.getActionCommand())){
            JFrame leaderboardFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            leaderboardFrame.dispose();

            initBoardWindow();
        }
    }

    /**
     * @brief Paints the leaderboard panel.
     *
     * @param g The graphics context.
     */
    @Override
    public void paintComponent(Graphics g){
//        super.paintComponent(g);
//        drawLeaderboard(g);

        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(new Color(0, 0, 0));
        g2d.fillRect(0, 0, Board.TILE_SIZE * Board.COLUMNS, Board.TILE_SIZE * Board.ROWS);
        g2d.setColor(new Color(173, 33, 33));
        g2d.setFont(new Font("Lato", Font.BOLD, 40));

        String gameOverText = "GAME OVER";
        FontMetrics fontMetrics = g2d.getFontMetrics();
        int gameOverWidth = fontMetrics.stringWidth(gameOverText);
        int gameOverX = (Board.COLUMNS * Board.TILE_SIZE - gameOverWidth) / 2;
        int gameOverY = startY - Board.TILE_SIZE;
        g2d.drawString(gameOverText, gameOverX, gameOverY);

        // Draw leaderboard entries
        g2d.setFont(new Font("Lato", Font.BOLD, 20));
        int position = 1;
        FontMetrics recordMetrics = g2d.getFontMetrics();
        for (int i = 0; i < leaderboardSize; i++) {
            String entry = leaderboardLines.get(i);
            String record = String.format("%d. %s", position, entry);
            int recordWidth = recordMetrics.stringWidth(record);
            int recordX = (Board.COLUMNS * Board.TILE_SIZE) / 2 - (recordWidth / 2); //
            // Calculate x-coordinate
            int recordY = startY + (i + 1) * Board.TILE_SIZE;
            g2d.drawString(record, recordX, recordY);
            position++;
        }
    }
}
