package src.ghost;

import java.awt.*;
import java.util.Random;

public class RandomGhost extends Ghost {
    private static int moveDuration;
    public RandomGhost(String imagePath, float speed, Point startPos) {
        super(imagePath, speed, startPos);
    }

    @Override
    void Move() {
        if (moveDuration > 0){
            --moveDuration;
        }
        else {
            Random rand = new Random();
            int moveDirection = rand.nextInt(4); //0-up, 1-right, 2-down, 3-left

            switch (moveDirection) {
                case 0 -> {
                    moveVector.x = 0;
                    moveVector.y = -1;
                }
                case 1 -> {
                    moveVector.x = 1;
                    moveVector.y = 0;
                }
                case 2 -> {
                    moveVector.x = 0;
                    moveVector.y = 1;
                }
                case 3 -> {
                    moveVector.x = -1;
                    moveVector.y = 0;
                }
            }
            moveDuration = 3;
        }
        pos.translate(moveVector.x, moveVector.y);
        wallCollision(moveVector);
    }
}
