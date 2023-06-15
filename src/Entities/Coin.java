package src.Entities;

import src.Board;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.Point;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * It would make a lot of sense to create a base abstract class StaticEntity, but since Coin would be its only child,
 * this seems redundant
 */

/**
 * @class Coin
 *
 * @brief The Coin class represents a coin object in the game.
 */
public class Coin {

    // image that represents the coin's position on the board
    private BufferedImage image;
    // current position of the coin on the board grid
    private Point pos;
    private int value;

    /**
     * @brief Constructs a Coin object with the specified position and value.
     *
     * @param x     The x-coordinate of the coin's position.
     * @param y     The y-coordinate of the coin's position.
     * @param value The value of the coin.
     */
    public Coin(int x, int y, int value) {
        // load the assets
        loadImage();
        // initialize the state
        pos = new Point(x, y);
        this.value = value;
    }

    /**
     * @brief Loads the image of the coin.
     */
    private void loadImage() {
        try {
            // you can use just the filename if the image file is in your
            // project folder, otherwise you need to provide the file path.
            image = ImageIO.read(new File("assets/monee.png"));
        } catch (IOException exc) {
            System.out.println("Error opening image file: " + exc.getMessage());
        }
    }

    /**
     * @brief Draws the coin on the specified graphics context.
     *
     * @param g        The graphics context to draw on.
     * @param observer The image observer object.
     */
    public void draw(Graphics g, ImageObserver observer) {
        // with the Point class, note that pos.getX() returns a double, but
        // pos.x reliably returns an int. https://stackoverflow.com/a/30220114/4655368
        // this is also where we translate board grid position into a canvas pixel
        // position by multiplying by the tile size.
        g.drawImage(
                image,
                pos.x * Board.TILE_SIZE,
                pos.y * Board.TILE_SIZE,
                observer
        );
    }

    /**
     * @brief Returns the position of the coin.
     *
     * @return The position of the coin.
     */
    public Point getPos() {
        return pos;
    }

    /**
     * @brief Returns the value of the coin.
     *
     * @return The value of the coin.
     */
    public int getValue() { return value; }

}