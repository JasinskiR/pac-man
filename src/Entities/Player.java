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
import javax.imageio.ImageIO;

public class Player extends MovingEntity {

    // keep track of the player's score
    private int score;

    public Player(float speed, Point startPos) { //what if player had a list of ghosts to detect?
        super(speed, startPos);
        loadImage();
        score = 0;
    }

    @Override
    protected void Move() {
        pos.translate(direction.x, direction.y);
        checkWallCollision(direction);
    }

    private void loadImage() {
        try {
            // you can use just the filename if the image file is in your
            // project folder, otherwise you need to provide the file path.
            sprite = ImageIO.read(new File("assets/pacman.png"));
        } catch (IOException exc) {
            System.out.println("Error opening image file: " + exc.getMessage());
        }
    }

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

    public int getScore() {
        return score;
    }

    public void addScore(int amount) {
        score += amount;
    }

    public Point getPos() {
        return pos;
    }

    public void setPos(Point newPos) {
        pos = newPos;
    }

}
