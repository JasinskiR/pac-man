package src.Entities;

import src.Board;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;

/**
 * @brief serves as a parent class for all "moving" entities - player and ghosts
 */
public abstract class MovingEntity {
    protected BufferedImage sprite;
    protected Point pos;
    protected int tickCounter; //counts the number of ticks since the last move.
    //it the number of ticks is at least equal to set speed, make a move and reset the counter.
    protected float speed; //specifies how fast the ghost will move.
    //LOWER speed - ghost moves FASTER
    protected Point direction;

    /**
     * @breif class constructor
     * @param speed - speed of the entity- lower values result in higher speeds
     * @param startPos - starting position of the entity
     */
    public MovingEntity(float speed, Point startPos){
        this.speed = speed;
        this.tickCounter = 0;
        this.pos = startPos;
        this.direction = new Point();
    }

    /**
     * @breif abstract method that handles how each entity moves across the board.
     * Each child class can override this method differently
     */
    protected abstract void Move();

    /**
     * @brief Thread wrapper for the Move method
     */
    public class moveThreaded extends Thread{
        public void run(){ Move();}
    }

    /**
     * @breif sets entity sprite
     * @param imagePath path to the sprite
     */
    protected void setSprite(String imagePath){
        try {
            sprite = ImageIO.read(new File(imagePath));
        }
        catch (IOException exc) {
            System.out.println("Error opening image file: " + exc.getMessage());
        }
    }

    /**
     * @breif checks if an entity collides with the walls
     * @param moveVector - the vector which will result in the entity's new possition
     * @return colided - whether the entity will collide with a wall
     */
    protected boolean checkWallCollision(Point moveVector){
        boolean colided = false;
        if (pos.x < 0) {
            pos.x = 0; colided = true;
        }
        else if (pos.x >= Board.COLUMNS) {
            pos.x = Board.COLUMNS - 1; colided = true;
        }

        if (pos.y < 0) {
            pos.y = 0; colided = true;
        }
        else if (pos.y >= Board.ROWS) {
            pos.y = Board.ROWS - 1; colided = true;
        }

        if (Board.MAP[pos.y][pos.x] == 1){
            pos.x = pos.x - moveVector.x;
            pos.y = pos.y - moveVector.y;
            colided = true;
        }
        return colided;
    }

    /**
     * @breif Same as checkWallCollision, but position is absolute
     * @param position possition at which to check for collision
     * @return collided whether the position results in a collision
     */
    protected boolean checkWallCollisionAbsolute(Point position){
        boolean collided = false;

        if (position.x < 0) collided = true;
        else if (position.x >= Board.COLUMNS) collided = true;

        if (position.y < 0) collided = true;
        else if (position.y >= Board.ROWS) collided = true;

        if (Board.MAP[position.y][position.x] == 1) collided = true;

        return collided;
    }

    /**
     * @brief Draws the entoty on the screen.
     *
     * This method is responsible for drawing the entity on the screen using the provided Graphics object.
     * It also checks the tickCounter to determine if the ghost should move.
     * If the tickCounter multiplied by the Board's DELAY is greater than or equal to the speed of the ghost,
     * a new thread is created to handle the movement of the ghost.
     * The tickCounter is reset to 1 in this case.
     * Otherwise, the tickCounter is incremented.
     * The ghost's sprite is then drawn at the current position on the screen using the provided ImageObserver.
     *
     * @param g The Graphics object used for rendering.
     * @param observer The ImageObserver used to observe image updates.
     */
    public void draw(Graphics g, ImageObserver observer){
        if (tickCounter*Board.DELAY >= speed){
//            Move();
            moveThreaded moveTh = new moveThreaded();
            moveTh.start();
            tickCounter = 1;
        }
        else{
            ++tickCounter;
        }
        //we want to draw the ghost every frame, just not move it.
        g.drawImage(sprite, pos.x*Board.TILE_SIZE, pos.y*Board.TILE_SIZE, observer);
    }

    /**
     * @brief get entity position
     * @return entity position
     */
    public Point getPos(){
        return pos;
    }

}

/**
 * How to thread
 * Dorob synchronizacje wątków
 * Zsynchronizuj pozycje bytów
 * Rozdrobnij kod - DRY
 * */
