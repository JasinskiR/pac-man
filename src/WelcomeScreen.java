package src;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static src.App.initBoardWindow;
import static src.App.initLeaderboardWindow;

class WelcomeScreen extends JPanel implements ActionListener {
  private JButton startButton;
  private JButton leaderboardButton;
  private JButton exitButton;
  private BufferedImage backgroundImage;

  public WelcomeScreen() {
    setLayout(new BorderLayout());

    // Load the background image
    try {
      backgroundImage = ImageIO.read(new File("assets/menu.png"));
    } catch (IOException exc) {
      System.out.println("Error opening image file: " + exc.getMessage());
    }

    // Create buttons
    startButton = createButton("Start Game");
    leaderboardButton = createButton("Leaderboard");
    exitButton = createButton("Exit");

    // Create button container panel
    JPanel buttonContainer = new JPanel();
    buttonContainer.setOpaque(false);
    buttonContainer.setLayout(new FlowLayout(FlowLayout.CENTER, 100, 10)); // Set
    // horizontal alignment and spacing
    buttonContainer.setBorder(new EmptyBorder(0, -40, 50, 0)); // Add space at the bottom
    buttonContainer.add(startButton);
    buttonContainer.add(leaderboardButton);
    buttonContainer.add(exitButton);

    add(buttonContainer, BorderLayout.SOUTH);
    setPreferredSize(new Dimension(backgroundImage.getWidth(), backgroundImage.getHeight()));
  }
  private JButton createButton(String text) {
    JButton button = new JButton(text);
    button.setFont(button.getFont().deriveFont(Font.BOLD, 24f));
    button.setForeground(Color.WHITE);
    button.setOpaque(false);
    button.setBorder(null);
    button.setContentAreaFilled(false);
    button.setBorderPainted(false);
    button.setBorder(new EmptyBorder(0, 0, 0, 0));
    button.setBorderPainted(false);
    button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    button.addActionListener(this);
    button.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        button.setText("<html><u>" + text + "</u></html>");
      }

      public void mouseExited(java.awt.event.MouseEvent evt) {
        button.setText(text);
      }
    });
    return button;
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    if (backgroundImage != null) {
      g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
    }
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if (e.getSource() == startButton) {
      JFrame welcomeFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
      welcomeFrame.dispose();

      // Open the board window
      initBoardWindow();
    } else if (e.getSource() == leaderboardButton) {
      JFrame welcomeFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
      welcomeFrame.dispose();

      initLeaderboardWindow();

    } else if (e.getSource() == exitButton) {
      System.exit(0);
    }
  }
}
