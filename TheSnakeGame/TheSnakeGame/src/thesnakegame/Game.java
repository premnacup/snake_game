package thesnakegame;

import javax.swing.*;

public class Game extends JFrame {

    public Game() {
        add(new MainMenu(this));
        this.setTitle("The Snake Game");
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();
        this.setVisible(true);
        this.setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        new Game();
    }
}
