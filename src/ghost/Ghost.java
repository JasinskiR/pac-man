package src.ghost;

import src.Board;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;

public abstract class Ghost {

    private BufferedImage sprite;
    protected Point pos;
    protected int tickCounter; //counts the number of ticks since the last move.
    //it the number of ticks is at least equal to set speed, make a move and reset the counter.
    protected int speed; //specifies how fast the ghost will move.
    //LOWER speed - ghost moves FASTER

    protected static Point moveVector;

    public Ghost(String imagePath, int speed, Point startPos){
        setSprite(imagePath);
        setSpeed(speed);
        pos = startPos;
        tickCounter = 0;
        moveVector = new Point();
    }

    abstract void Move();

    private void setSprite(String imagePath){
        try {
            sprite = ImageIO.read(new File(imagePath));
        }
        catch (IOException exc) {
            System.out.println("Error opening image file: " + exc.getMessage());
        }
    }

    protected void setSpeed(int speed) {
        this.speed = speed;
    }

    public Point getPos(){
        return pos;
    }

    /*
    * Since Player has the same collision logic, either make a parent class for both player and ghost, or instead of
    * extension compose those classes out of different objects.
    * */
    protected void wallCollision(Point moveVector){
        if (pos.x < 0) pos.x = 0;
        else if (pos.x >= Board.COLUMNS) pos.x = Board.COLUMNS - 1;

        if (pos.y < 0) pos.y = 0;
        else if (pos.y >= Board.ROWS) pos.y = Board.ROWS - 1;

        if (Board.MAP[pos.y][pos.x] == 1){
            pos.x = pos.x - moveVector.x;
            pos.y = pos.y - moveVector.y;
        }
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

}
