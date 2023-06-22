package src;

import src.Entities.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

import java.io.FileWriter;
import java.io.IOException;
import static src.App.initLeaderboardWindow;

/**
 * @brief Board class serves as the central point in the programm. It stores all present entities,
 * calculates entity collisions, draws the background, etc...
 */
public class Board extends JPanel implements ActionListener, KeyListener {

    // controls the delay between each tick in ms
    public static final int DELAY = 50;
    // controls the size of the board
    public static final int TILE_SIZE = 25;

    // 0 - path, 1 - wall
    public static final int MAP [] [] = {{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 1, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0},
            {0, 0, 1, 0, 0, 0, 0, 1, 0, 1, 0, 0, 1, 0, 0},
            {0, 0, 1, 0, 0, 0, 0, 1, 0, 1, 0, 0, 1, 0, 0},
            {0, 0, 1, 0, 0, 0, 0, 1, 0, 1, 0, 0, 1, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0},
            {0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0},
            {0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 1, 0, 0, 1, 0, 1, 0, 1, 0, 0, 0, 0, 0},
            {0, 0, 1, 0, 0, 1, 0, 1, 0, 1, 0, 0, 0, 0, 0},
            {0, 0, 1, 0, 0, 1, 0, 1, 0, 1, 0, 0, 0, 0, 0},
            {0, 0, 1, 0, 0, 1, 0, 1, 0, 1, 0, 1, 1, 1, 1},
            {0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}};


    public static final int ROWS = MAP.length;
    public static final int COLUMNS = MAP[0].length;;

    // controls how many coins appear on the board
    public static final int NUM_COINS = 5;
    public static final int REVIVAL_COST = 500;
    // suppress serialization warning
    private static final long serialVersionUID = 490905409104883233L;

    private Timer timer;
    private static Player player;
    private ArrayList<Coin> coins;
    private ArrayList<MovingEntity> ghosts;
    private boolean is_gameOver;

    private String leaderboardPath;

    /**
     * @brief Constructs a new Board object.
     *
     * This constructor initializes the game board by performing the following tasks:
     *  - Sets the preferred size of the board
     *  - Creates a player object
     *  - Initializes a list of ghosts
     *  - Loads the leaderboard path
     *  - Populates the board with coins
     *  - Starts a timer to trigger action events
     */
    public Board() {
        setPreferredSize(new Dimension(TILE_SIZE * COLUMNS, TILE_SIZE * ROWS));
        // initialize the game state
        player = new Player(100.0f, new Point(0,0));
        ghosts = new ArrayList<MovingEntity>();

        ghosts.add(new WallhuggerGhost("assets/ghost_yellow.png", 200.0f, new Point(4, 4)));
        ghosts.add(new WallhuggerGhost("assets/ghost_yellow.png", 200.0f, new Point(8, 8)));
        ghosts.add(new DijkstraGhost("assets/ghost_green.png", 200.0f, new Point(8, 8)));
        ghosts.add(new RandomGhost("assets/ghost_pink.png", 100.0f, new Point(8, 8)));

        leaderboardPath = "src/leaderboard.txt";

        coins = populateCoins();

        timer = new Timer(DELAY, this); // call the actionPerformed() method every DELAY ms
        timer.start();
    }

    /** @brief get player object
     *
     * @return player - Board's player instance
     */
    public static Player getPlayer() {
        return player;
    }

    /**
     * @brief Handles the action event for a specific action.
     *
     * This method is called when an action event occurs, such as a button click.
     * It performs the following tasks:
     *  - Collects coins
     *  - Repaints the graphical user interface
     *  - Checks for entity collision
     *
     * @param e The action event that occurred.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        // give the player points for collecting coins
        collectCoins();
        // calling repaint() will trigger paintComponent() to run again,
        // which will refresh/redraw the graphics.
        repaint();
        // this method is called by the timer every DELAY ms.
        // use this space to update the state of your game or animation
        checkEntityCollision();
    }

    /**
     * @brief Overrides the paintComponent method of JPanel to customize the rendering of the game graphics.
     *
     * This method is responsible for painting the game components on the screen. It performs the following tasks:
     *  - If the game is over, it draws the "Game Over" screen and returns.
     *  - Calls the superclass's paintComponent method to perform default painting operations.
     *  - Draws the background.
     *  - Draws the score.
     *  - Draws the coins.
     *  - Draws the ghosts.
     *  - Draws the player.
     *  - Syncs the graphics to smooth out animations on some systems.
     *
     * @param g The Graphics object used for rendering.
     */
    @Override
    public void paintComponent(Graphics g) {

        if (is_gameOver){
            drawGameOver(g, player.getScore());
            return;
        }

        super.paintComponent(g);
        // when calling g.drawImage() we can use "this" for the ImageObserver
        // because Component implements the ImageObserver interface, and JPanel
        // extends from Component. So "this" Board instance, as a Component, can
        // react to imageUpdate() events triggered by g.drawImage()

        // draw our graphics.
        drawBackground(g);
        drawScore(g);
        for (Coin coin : coins) {
            coin.draw(g, this);
        }
        for (MovingEntity ghost : ghosts){
            ghost.draw(g, this);
        }
        player.draw(g, this);

        // this smooths out animations on some systems
        Toolkit.getDefaultToolkit().sync();
    }

    /**
     * @brief handles keys typed - this mehtod is overriden only because we must implement
     * all methods being part of the KeyListener interface
     * @param e the event to be processed
     */
    @Override
    public void keyTyped(KeyEvent e) {
        // this is not used but must be defined as part of the KeyListener interface
    }

    /**
     * @brief delegate key presses to the player instance wherein they are handled
     * @param e the event to be processed
     */
    @Override
    public void keyPressed(KeyEvent e) {
        // react to key down events

        player.keyPressed(e);
    }

    /**
     * @brief handles keys released - this mehtod is overriden only because we must implement
     * all methods being part of the KeyListener interface
     * @param e the event to be processed
     */
    @Override
    public void keyReleased(KeyEvent e) {
        // react to key up events
    }

    /**
     * @brief Draws the background of the game board.
     *
     * This method is responsible for drawing a checkered background on the game board.
     * It iterates through each row and column of the board and determines the color of each tile based on the value in the MAP array.
     * It then fills a square tile at the current row/column position with the determined color.
     *
     * @param g The Graphics object used for rendering.
     */
    private void drawBackground(Graphics g) {
        // draw a checkered background

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLUMNS; col++) {

                if (MAP[row][col] == 0) g.setColor(new Color(10, 10, 35));
                else if (MAP[row][col] == 1) g.setColor(new Color(36, 75, 161));
                // draw a square tile at the current row/column position
                g.fillRect(
                        col * TILE_SIZE,
                        row * TILE_SIZE,
                        TILE_SIZE,
                        TILE_SIZE
                );
            }
        }
    }

    /**
     * @brief draws user score on the screen
     * @param g The Graphics object used for rendering.
     */
    private void drawScore(Graphics g) {
        // set the text to be displayed
        String text = String.format("$%d", player.getScore());
        // we need to cast the Graphics to Graphics2D to draw nicer text
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setRenderingHint(
                RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(
                RenderingHints.KEY_FRACTIONALMETRICS,
                RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        // set the text color and font
        g2d.setColor(new Color(30, 201, 139));
        g2d.setFont(new Font("Lato", Font.BOLD, 25));
        // draw the score in the bottom center of the screen
        // https://stackoverflow.com/a/27740330/4655368
        FontMetrics metrics = g2d.getFontMetrics(g2d.getFont());
        // the text will be contained within this rectangle.
        // here I've sized it to be the entire bottom row of board tiles
        Rectangle rect = new Rectangle(0, TILE_SIZE * (ROWS - 1), TILE_SIZE * COLUMNS, TILE_SIZE);
        // determine the x coordinate for the text
        int x = rect.x + (rect.width - metrics.stringWidth(text)) / 2;
        // determine the y coordinate for the text
        // (note we add the ascent, as in java 2d 0 is top of the screen)
        int y = rect.y + ((rect.height - metrics.getHeight()) / 2) + metrics.getAscent();
        // draw the string
        g2d.drawString(text, x, y);
    }

    /**
     * @breif draws the game over screen by destroying the current window and initialising the leaderboard
     * @param g The Graphics object used for rendering.
     * @param score player score
     */
    private void drawGameOver(Graphics g, int score) {
        showLeaderboardForm(score);
//        displayLeaderboard(g);
        JFrame boardFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        boardFrame.dispose();
        initLeaderboardWindow();
    }

    /**
     * @brief renders a small form allowing the user to enter their username
     * @param score user score
     */
    private void showLeaderboardForm(int score) {
        JDialog dialog = new JDialog();
        dialog.setTitle("Leaderboard");
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setModal(true);

        JTextField playerNameField = new JTextField();
        JButton okButton = new JButton("OK");
        okButton.addActionListener(e -> {
            String playerName = playerNameField.getText();
            if (!playerName.isEmpty()) {
                if (isPlayerInLeaderboard(playerName)) {
                    JDialog error = new JDialog();
                    // Player already exists in the leaderboard
                    JOptionPane.showMessageDialog(error, "Username already exists. \n" +
                            "Please choose a different username.", "Leaderboard", JOptionPane.ERROR_MESSAGE);
                    error.dispose();
                } else {
                    updateLeaderboard(playerName, score);
                    dialog.dispose();
                }
            }
        });

        JPanel contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(new JLabel("Enter your name:"), BorderLayout.NORTH);
        contentPane.add(playerNameField, BorderLayout.CENTER);
        contentPane.add(okButton, BorderLayout.SOUTH);

        dialog.setContentPane(contentPane);
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    /**
     * @brief checks if the username provided by the player is alredy in the leaderboard
     * @param playerName player name
     * @return boolean whether the player is in the leaderboard
     */
    private boolean isPlayerInLeaderboard(String playerName) {
        try {
            BufferedReader leaderboardReader = new BufferedReader(new FileReader(leaderboardPath));
            String line;
            while ((line = leaderboardReader.readLine()) != null) {
                String storedPlayerName = line.split(": ")[0];
                if (storedPlayerName.equals(playerName)) {
                    leaderboardReader.close();
                    return true;
                }
            }
            leaderboardReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * @brief updates the leaderboard with new player score
     * @param playerName name of the player
     * @param score player score
     */
    private void updateLeaderboard(String playerName, int score) {
        List<String> leaderboard = new ArrayList<>();

        try {
            // Read the current leaderboard
            BufferedReader leaderboardReader = new BufferedReader(new FileReader(leaderboardPath));
            String line;
            while ((line = leaderboardReader.readLine()) != null) {
                leaderboard.add(line);
            }
            leaderboardReader.close();

            // Add the new score
            leaderboard.add(playerName + ": " + score);

            // Sort the leaderboard in descending order by score
            leaderboard.sort((s1, s2) -> {
                int score1 = Integer.parseInt(s1.split(": ")[1]);
                int score2 = Integer.parseInt(s2.split(": ")[1]);
                return Integer.compare(score2, score1);
            });

            // Update the leaderboard file
            FileWriter leaderboardWriter = new FileWriter(leaderboardPath);
            for (String entry : leaderboard) {
                leaderboardWriter.write(entry + "\n");
            }
            leaderboardWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * Leaderboard display is now handled by initLeaderboardWindow
     */
    private void displayLeaderboard(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(new Color(0, 0, 0));
        g2d.fillRect(0, 0, TILE_SIZE * COLUMNS, TILE_SIZE * ROWS);
        g2d.setColor(new Color(173, 33, 33));
        g2d.setFont(new Font("Lato", Font.BOLD, 20));

        try {
            BufferedReader leaderboardReader = new BufferedReader(new FileReader(leaderboardPath));
            List<String> leaderboardLines = new ArrayList<>();
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

            int leaderboardSize = Math.min(5, leaderboardLines.size());
            int leaderboardHeight = leaderboardSize * TILE_SIZE;
            int startY = (ROWS * TILE_SIZE - leaderboardHeight) / 2;

            // Draw "GAME OVER" title
            String gameOverText = "GAME OVER";
            g2d.setFont(new Font("Lato", Font.BOLD, 40));
            FontMetrics fontMetrics = g2d.getFontMetrics();
            int gameOverWidth = fontMetrics.stringWidth(gameOverText);
            int gameOverX = (COLUMNS * TILE_SIZE - gameOverWidth) / 2;
            int gameOverY = startY - TILE_SIZE;
            g2d.drawString(gameOverText, gameOverX, gameOverY);

            // Draw leaderboard entries
            g2d.setFont(new Font("Lato", Font.BOLD, 20));
            int position = 1;
            FontMetrics recordMetrics = g2d.getFontMetrics();
            for (int i = 0; i < leaderboardSize; i++) {
                String entry = leaderboardLines.get(i);
                String record = String.format("%d. %s", position, entry);
                int recordWidth = recordMetrics.stringWidth(record);
                int recordX = (COLUMNS * TILE_SIZE) / 2 - (recordWidth / 2); //
                // Calculate x-coordinate
                int recordY = startY + (i + 1) * TILE_SIZE;
                g2d.drawString(record, recordX, recordY);
                position++;
            }

            // Add a button for starting again
            JButton startAgainButton = new JButton("Start Again");
//            startAgainButton.addActionListener(this);
//            startAgainButton.setActionCommand("start_over");

            startAgainButton.setForeground(new Color(173, 33, 33));
            startAgainButton.setBackground(new Color(255, 255, 255));
            startAgainButton.setFocusPainted(false);
            startAgainButton.setFont(new Font("Lato", Font.BOLD, 16));
            startAgainButton.setBorder(BorderFactory.createLineBorder(new Color(173, 33, 33), 2));
            startAgainButton.setBounds((COLUMNS * TILE_SIZE - 120) / 2, startY + leaderboardHeight + 20, 120, 30);
            //startAgainButton.addActionListener(e -> startGameAgain());
            startAgainButton.setVisible(true);
            add(startAgainButton);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * @brief place coins on the board at random
     *
     * @return list of coins
     */
    private ArrayList<Coin> populateCoins() {
        ArrayList<Coin> coinList = new ArrayList<Coin>();
        Random rand = new Random();
        int coinsToPlace = NUM_COINS;

        while (coinsToPlace > 0){
            int coinX = rand.nextInt(COLUMNS);
            int coinY = rand.nextInt(ROWS);
            if (Board.MAP[coinY][coinX] == 1) continue;
            else coinList.add(new Coin(coinX, coinY, 100));
            --coinsToPlace;
        }
        return coinList;
    }

    /**
     * @brief Allows the player to collect coins.
     *
     * This method checks if the player's position matches the position of any coins on the board.
     * If a match is found, the player's score is increased by the value of the collected coin,
     * and the coin is added to a list of collected coins.
     * After collecting the coins, they are removed from the board.
     * If there are no more coins remaining on the board, new coins are populated.
     */
    private void collectCoins() {
        // allow player to pickup coins
        ArrayList<Coin> collectedCoins = new ArrayList<>();
        for (Coin coin : coins) {
            if (player.getPos().equals(coin.getPos())) {
                player.addScore(coin.getValue());
                collectedCoins.add(coin);
            }
        }
        // remove collected coins from the board
        coins.removeAll(collectedCoins);

        if (coins.size() < 1) coins = populateCoins();

    }


    /**
     * @brief Checks for collisions between the player and ghosts.
     *
     * This method iterates through each ghost in the list of ghosts.
     * It compares the position of each ghost with the position of the player.
     * If a collision is detected, the player's position is set to (0, 0) and the game is marked as over.
     * This method is typically called to check for collisions between the player and ghosts during gameplay.
     */
    private void checkEntityCollision(){
        for (MovingEntity ghost : ghosts){
            if (ghost.getPos().equals(player.getPos())){
//                player.setPos(new Point(0, 0));
                gameOver();
            }
        }
    }

    /**
     * @breif signal that the game has finished and stop the timer
     */
    private void gameOver(){
        timer.stop();
        is_gameOver = true;
        repaint();
    }
}