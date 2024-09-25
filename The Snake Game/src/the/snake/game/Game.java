package the.snake.game;

import javax.swing.*;

public class Game extends JFrame{

    static final int SCREEN_WIDTH = 720;
    static final int SCREEN_HEIGHT = 720;

    public Game() {
        this.setTitle("The Snake Game");
        this.setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
        this.setResizable(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        
        add(new MainMenu(this));
    }

    public static void main(String[] args) {
        new Game();
    }
}
