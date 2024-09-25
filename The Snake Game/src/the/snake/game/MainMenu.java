package the.snake.game;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class MainMenu extends JPanel {

    public MainMenu(JFrame frame) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

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

        panel.add(Box.createVerticalGlue());
        panel.add(title);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(startButton);
        panel.add(Box.createVerticalGlue());

        setLayout(new BorderLayout());
        add(panel, BorderLayout.CENTER);

        startButton.addActionListener((ActionEvent e) -> {
            frame.getContentPane().removeAll();
            MainGameScreen mainGameScreen = new MainGameScreen();
            frame.add(mainGameScreen);
            frame.revalidate();
            frame.repaint();
            mainGameScreen.requestFocusInWindow();
        });
    }
}
