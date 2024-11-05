package the.snake.game;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class MainMenu extends JPanel {
    static final int SCREEN_WIDTH = 720;
    static final int SCREEN_HEIGHT = 720;
    private ImageComponent background;
    private Font TitleFont = FontLoader.loadFont("The Snake Game\\src\\the\\snake\\game\\Font\\FC Candy.ttf", 72);
    private Font ButtonFont = FontLoader.loadFont("The Snake Game\\src\\the\\snake\\game\\Font\\FC Candy.ttf", 48);

    public MainMenu(JFrame frame) {
        background = new ImageComponent("The Snake Game\\src\\the\\snake\\game\\Assets\\background.png", SCREEN_WIDTH,
                SCREEN_HEIGHT);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));

        // Create title and buttons
        JLabel title = new JLabel("The Snake Game", JLabel.CENTER);
        JButton normalStageButton = new JButton("Normal Stage");
        JButton specialStageButton = new JButton("Special Stage");

        // Style title
        title.setFont(TitleFont);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Style buttons
        styleButton(normalStageButton);
        styleButton(specialStageButton);

        // Add action listeners
        normalStageButton.addActionListener((ActionEvent e) -> {
            switchToGameScreen(frame, new NormalStage(frame));
        });

        specialStageButton.addActionListener((ActionEvent e) -> {
            switchToGameScreen(frame, new SpecialStage(frame));
        });

        // Add components with spacing
        add(Box.createVerticalGlue());
        add(title);
        add(Box.createRigidArea(new Dimension(0, 50)));
        add(normalStageButton);
        add(Box.createRigidArea(new Dimension(0, 20)));
        add(specialStageButton);
        add(Box.createVerticalGlue());
    }

    private void styleButton(JButton button) {
        button.setFont(ButtonFont);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setOpaque(false);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Add hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setForeground(Color.GREEN);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setForeground(Color.BLACK);
            }
        });
    }

    private void switchToGameScreen(JFrame frame, AbstractGameScreen gameScreen) {
        frame.getContentPane().removeAll();
        frame.add(gameScreen);
        frame.revalidate();
        frame.repaint();
        gameScreen.requestFocusInWindow();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        background.paintComponent(g);
    }
}