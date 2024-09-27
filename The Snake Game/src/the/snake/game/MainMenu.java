package the.snake.game;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class MainMenu extends JPanel {

    static final int SCREEN_WIDTH = 720;
    static final int SCREEN_HEIGHT = 720;
    private ImageComponent background;

    public MainMenu(JFrame frame) {
        background = new ImageComponent("The Snake Game\\src\\the\\snake\\game\\Assets\\background.png", SCREEN_WIDTH,
                SCREEN_HEIGHT);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));

        JButton startButton = new JButton("Start Game");
        JLabel title = new JLabel("The Snake Game", JLabel.CENTER);

        title.setFont(new Font("FC Candy", Font.BOLD, 72));
        startButton.setFont(new Font("FC Candy", Font.BOLD, 48));
        startButton.setContentAreaFilled(false);
        startButton.setBorderPainted(false);
        startButton.setFocusPainted(false);
        startButton.setOpaque(false);

        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        startButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        add(Box.createVerticalGlue());
        add(title);
        add(Box.createRigidArea(new Dimension(0, 20)));
        add(startButton);
        add(Box.createVerticalGlue());

        startButton.addActionListener((ActionEvent e) -> {
            frame.getContentPane().removeAll();
            MainGameScreen mainGameScreen = new MainGameScreen();
            frame.add(mainGameScreen);
            frame.revalidate();
            frame.repaint();
            mainGameScreen.requestFocusInWindow();
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        background.paintComponent(g);
    }
}
