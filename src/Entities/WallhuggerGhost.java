package src.Entities;

import src.Board;

import java.awt.*;
import java.util.ArrayList;

/**
 * @class WallhuggerGhost
 *
 * @brief The WallhuggerGhost class represents a ghost that follows a wall-hugging movement logic.
 */

public class WallhuggerGhost extends MovingEntity{

    private Point lastMove;

    /**
     * @brief Constructs a WallhuggerGhost object with the specified image path, speed, and starting position.
     *
     * @param imagePath The path to the image representing the ghost.
     * @param speed     The speed of the ghost.
     * @param startPos  The starting position of the ghost.
     */
    public WallhuggerGhost(String imagePath, float speed, Point startPos) {
        super(speed, startPos);
        setSprite(imagePath);
    }

    /**
     * @brief The Move() method determines the movement logic for the Wallhugger ghost.
     */
    @Override
    protected void Move() {
        ArrayList<Point> possibleRelDirection = new ArrayList<Point>(); //possible relative directions
        possibleRelDirection.add(new Point(0, -1)); //up
        possibleRelDirection.add(new Point(1, 0)); //right
        possibleRelDirection.add(new Point(0, 1)); //down
        possibleRelDirection.add(new Point(-1 ,0)); //left
        ArrayList<Point> dirsToRemove = new ArrayList<Point>(); //possible relative directions

        if (pos.y - 1 < 0 || Board.MAP[pos.y-1][pos.x] == 1) { //if world border or wall above
            dirsToRemove.add(possibleRelDirection.get(0));
//            System.out.println("removed: up");
        }
        if (pos.x + 1 >= Board.COLUMNS || Board.MAP[pos.y][pos.x+1] == 1){
            dirsToRemove.add(possibleRelDirection.get(1));
//            System.out.println("removed: right");
        }
        if (pos.y + 1 >= Board.ROWS || Board.MAP[pos.y+1][pos.x] == 1){
            dirsToRemove.add(possibleRelDirection.get(2));
//            System.out.println("removed: down");
        }
        if (pos.x - 1 < 0 || Board.MAP[pos.y][pos.x-1] == 1){
            dirsToRemove.add(possibleRelDirection.get(3));
//            System.out.println("removed: left");
        }
        possibleRelDirection.removeAll(dirsToRemove);

        //make the next move the same as the last one unless the next move will result in a collision
        //if it results in a collision, get another move
        if (lastMove != null){
            pos.translate(lastMove.x, lastMove.y);
            if (checkWallCollision(lastMove)){ //if this move will result in a collision
                direction = possibleRelDirection.get(0); //get the first available direction
                if (-lastMove.x == direction.x && -lastMove.y == direction.y && possibleRelDirection.size() > 1)
                    direction = possibleRelDirection.get(1); //get another to avoid infinite backtracking
                lastMove = direction;
            }
            else{
                pos.translate(-lastMove.x, -lastMove.y);
                direction = lastMove;
            }
        }
        else {
            direction = possibleRelDirection.get(0);
            lastMove = direction;
        }
        /**
         * Comment to the above: This took far longer than i anticipated.
         * First, the ghost would enter a loop where it would either go in a 2x2 circle
         * or move up and down ad infinitum. So i had to fix that.
         * Then it would move two tiles at a time sometimes, so that had to be taken care of as well.
         * Then i wasted like an hour or two because i forgot that .remove() will shift indexes to the left.
         * The above solution is far from optimal - at one point im moving the pacman to see if it collides with
         * anything and if it does, it backtracks instead of checking if the filed at a specified index is blocked.
         * I even modified an existing method to do just that, but it kept throwing my indexes out of range and i couldnt
         * bother to fix that.
         */

        pos.translate(direction.x, direction.y);
        checkWallCollision(direction);
    }
}
