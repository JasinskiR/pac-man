package src.Entities;

import src.Entities.MovingEntity;

import java.awt.event.KeyEvent;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import javax.imageio.ImageIO;

/**
 * @class Player
 *
 * @brief The Player class represents the player entity in a game. It inherits from the MovingEntity class.
 */
public class Player extends MovingEntity {

    // keep track of the player's score
    private int score;

    /**
     * Constructs a Player object with the specified speed and starting position.
     *
     * @param speed    The speed at which the player moves.
     * @param startPos The starting position of the player.
     */
    public Player(float speed, Point startPos) { //what if player had a list of ghosts to detect?
        super(speed, startPos);
        loadImage();
        score = 0;
    }

    /**
     * Moves the player based on the current direction. It updates the player's position and checks for wall collisions.
     */
    @Override
    protected void Move() {

        pos.translate(direction.x, direction.y);

        checkWallCollision(direction);
    }

    /**
     * Loads the player's image from a file.
     */
    private void loadImage() {
        try {
            // you can use just the filename if the image file is in your
            // project folder, otherwise you need to provide the file path.
            sprite = ImageIO.read(new File("assets/pacman.png"));
        } catch (IOException exc) {
            System.out.println("Error opening image file: " + exc.getMessage());
        }
    }

    /**
     * Handles the player's keyboard input.
     *
     * @param e The KeyEvent representing the key press event.
     */
    public void keyPressed(KeyEvent e) {
        // every keyboard get has a certain code. get the value of that code from the
        // keyboard event so that we can compare it to KeyEvent constants
        int key = e.getKeyCode();
        Point lastMove = new Point(0 ,0);

        // depending on which arrow key was pressed, we're going to move the player by
        // one whole tile for this input
        if (key == KeyEvent.VK_UP) {
            lastMove.y = -1;
        }
        if (key == KeyEvent.VK_RIGHT) {
            lastMove.x = 1;
        }
        if (key == KeyEvent.VK_DOWN) {
            lastMove.y = 1;
        }
        if (key == KeyEvent.VK_LEFT) {
            lastMove.x = -1;
        }
        direction = lastMove;
    }
    public void tick() {
        // this gets called once every tick, before the repainting process happens.
    }

    /**
     * Returns the score of the player.
     *
     * @return The player's score.
     */
    public int getScore() {
        return score;
    }

    /**
     * Increases the player's score by the specified amount.
     *
     * @param amount The amount to increase the player's score by.
     */
    public void addScore(int amount) {
        score += amount;
    }

    /**
     * Returns the position of the player.
     *
     * @return The player's position as a Point object.
     */
    public Point getPos() {
        return pos;
    }

    /**
     * Sets the position of the player.
     *
     * @param newPos The new position of the player as a Point object.
     */
    public void setPos(Point newPos) {
        pos = newPos;
    }

}
