package src.Entities;

import src.Entities.MovingEntity;

import java.awt.*;
import java.util.Random;

public class RandomGhost extends MovingEntity {
    private static int moveDuration;
    public RandomGhost(String imagePath, float speed, Point startPos) {
        super(speed, startPos);
        setSprite(imagePath);
    }

    @Override
    protected void Move() {
        if (moveDuration > 0){
            --moveDuration;
        }
        else {
            Random rand = new Random();
            int moveDirection = rand.nextInt(4); //0-up, 1-right, 2-down, 3-left

            switch (moveDirection) {
                case 0 -> {
                    direction.x = 0;
                    direction.y = -1;
                }
                case 1 -> {
                    direction.x = 1;
                    direction.y = 0;
                }
                case 2 -> {
                    direction.x = 0;
                    direction.y = 1;
                }
                case 3 -> {
                    direction.x = -1;
                    direction.y = 0;
                }
            }
            moveDuration = 3;
        }
        pos.translate(direction.x, direction.y);
        checkWallCollision(direction);
    }
}
