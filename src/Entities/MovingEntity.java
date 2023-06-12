package src.Entities;

import src.Board;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;

public abstract class MovingEntity {
    protected BufferedImage sprite;
    protected Point pos;
    protected int tickCounter; //counts the number of ticks since the last move.
    //it the number of ticks is at least equal to set speed, make a move and reset the counter.
    protected float speed; //specifies how fast the ghost will move.
    //LOWER speed - ghost moves FASTER
    protected Point direction;

    public MovingEntity(float speed, Point startPos){
        this.speed = speed;
        this.tickCounter = 0;
        this.pos = startPos;
        this.direction = new Point();
    }
    protected abstract void Move();
    protected void setSprite(String imagePath){
        try {
            sprite = ImageIO.read(new File(imagePath));
        }
        catch (IOException exc) {
            System.out.println("Error opening image file: " + exc.getMessage());
        }
    }
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

    //takes a set of coordinates and check if theres a wall/border there.
    protected  boolean checkWallCollisionAbsolute(Point position){
        boolean colided = false;

        if (position.x < 0) colided = true;
        else if (position.x >= Board.COLUMNS) colided = true;

        if (position.y < 0) colided = true;
        else if (position.y >= Board.ROWS) colided = true;

        if (Board.MAP[position.y][position.x] == 1) colided = true;

        return colided;
    }
    public void draw(Graphics g, ImageObserver observer){
        if (tickCounter*Board.DELAY >= speed){
            Move();
            tickCounter = 1;
        }
        else{
            ++tickCounter;
        }
        //we want to draw the ghost every frame, just not move it.
        g.drawImage(sprite, pos.x*Board.TILE_SIZE, pos.y*Board.TILE_SIZE, observer);
    }
    public Point getPos(){
        return pos;
    }


}
