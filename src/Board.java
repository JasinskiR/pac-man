package src;

import src.ghost.Ghost;
import src.ghost.RandomGhost;
import src.ghost.DijkstraGhost;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.swing.*;

public class Board extends JPanel implements ActionListener, KeyListener {

    // controls the delay between each tick in ms
    public static final int DELAY = 50;
    // controls the size of the board
    public static final int TILE_SIZE = 25;
    public static final int ROWS = 15;
    public static final int COLUMNS = 15;

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

    // controls how many coins appear on the board
    public static final int NUM_COINS = 5;
    public static final int REVIVAL_COST = 500;
    // suppress serialization warning
    private static final long serialVersionUID = 490905409104883233L;

    private Timer timer;
    private static Player player;
    private ArrayList<Coin> coins;
    private ArrayList<Ghost> ghosts;
    private boolean is_gameOver;

    public Board() {
        setPreferredSize(new Dimension(TILE_SIZE * COLUMNS, TILE_SIZE * ROWS));
        // initialize the game state
        player = new Player();

        ghosts = new ArrayList<Ghost>();
        ghosts.add(new DijkstraGhost("assets/ghost_pink.png", 0.01f, new Point(8, 8)));
        ghosts.add(new RandomGhost("assets/ghost_pink.png", 0.01f, new Point(8, 8)));

        coins = populateCoins();

        timer = new Timer(DELAY, this); // call the actionPerformed() method every DELAY ms
        timer.start();
    }
    public static Player getPlayer() {
        return player;
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        // this method is called by the timer every DELAY ms.
        // use this space to update the state of your game or animation
        // before the graphics are redrawn.

        checkEntityCollision();

        // give the player points for collecting coins
        collectCoins();

        // calling repaint() will trigger paintComponent() to run again,
        // which will refresh/redraw the graphics.
        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {

        if (is_gameOver){
            drawGameOver(g);
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
        for (Ghost ghost : ghosts){
            ghost.draw(g, this);
        }
        player.draw(g, this);

        // this smooths out animations on some systems
        Toolkit.getDefaultToolkit().sync();
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // this is not used but must be defined as part of the KeyListener interface
    }

    @Override
    public void keyPressed(KeyEvent e) {
        // react to key down events

        player.keyPressed(e);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // react to key up events
    }

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

    private void drawGameOver(Graphics g){
        Graphics2D g2d = (Graphics2D) g;
        String text = String.format("GAME OVER\n final score: %d", player.getScore());
        g.setColor(new Color(0, 0, 0));
        g.fillRect(0, 0, TILE_SIZE*COLUMNS, TILE_SIZE*ROWS);
        g.setColor(new Color(173, 33, 33));
        g.setFont(new Font("Lato", Font.BOLD, 20));
        g.drawString(text, TILE_SIZE*1 ,TILE_SIZE*1);
    }

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

    //Collisions between entities (ghost and player) are handled by the board because it has the access
    //to both player and ghost pos.
    private void checkEntityCollision(){
        for (Ghost ghost : ghosts){
            if (ghost.getPos().equals(player.getPos())){
                player.setPos(new Point(0, 0));
                player.addScore(-REVIVAL_COST);
                if (player.getScore() < 0) gameOver();
            }
        }
    }

    private void gameOver(){
        timer.stop();
        is_gameOver = true;
        repaint();
    }

}