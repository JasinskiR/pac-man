package src.Entities;

import src.Entities.MovingEntity;

import java.awt.*;
import java.util.Random;

/**
 * @class RandomGhost
 *
 * @brief The RandomGhost class represents a random moving ghost entity in a game.
 *        It inherits from the MovingEntity class.
 */
public class RandomGhost extends MovingEntity {
    private static int moveDuration;

    /**
     * Constructs a RandomGhost object with the specified image path, speed, and starting position.
     *
     * @param imagePath The path to the image representing the ghost.
     * @param speed     The speed at which the ghost moves.
     * @param startPos  The starting position of the ghost.
     */
    public RandomGhost(String imagePath, float speed, Point startPos) {
        super(speed, startPos);
        setSprite(imagePath);
    }

    /**
     * Moves the ghost in a random direction.
     * If the move duration is greater than 0, it decreases the duration by 1.
     * If the move duration is 0, it randomly selects a move direction (up, right, down, or left) and updates the ghost's direction accordingly.
     * After moving, it updates the ghost's position and checks for wall collisions.
     */
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
