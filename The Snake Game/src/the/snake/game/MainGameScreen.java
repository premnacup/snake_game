package the.snake.game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class MainGameScreen extends JPanel implements ActionListener {

    static final int SCREEN_WIDTH = 720;
    static final int SCREEN_HEIGHT = 720;
    static final int UNIT_SIZE = 40;
    static final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT) / UNIT_SIZE;
    static final int DELAY = 100;

    final int x[] = new int[GAME_UNITS];
    final int y[] = new int[GAME_UNITS];

    int snakeBody = 4;
    int foodEaten;
    int foodCoorX;
    int foodCoorY;

    Image foodImage;
    Image snakeHead;

    char direction = 'R';
    boolean isRunning = false;
    Timer timer;
    Random random;
    JButton retryButton;

    public MainGameScreen() {
        random = new Random();
        setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        setBackground(Color.BLACK);
        setVisible(true);
        setFocusable(true);
        addKeyListener(new KeyChecker());

        foodImage = new ImageIcon("The Snake Game\\src\\the\\snake\\game\\Assets\\apple.png").getImage();
        snakeHead = new ImageIcon("The Snake Game\\src\\the\\snake\\game\\Assets\\snake.png").getImage();

        startGame();
    }

    public final void startGame() {
        spawnFood();
        isRunning = true;
        timer = new Timer(DELAY, this);
        timer.start();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        if (isRunning) {
            Graphics2D g2d = (Graphics2D) g.create();

            for (int i = 0; i <= SCREEN_WIDTH / UNIT_SIZE; i++) {
                g.drawLine(i * UNIT_SIZE, 0, i * UNIT_SIZE, SCREEN_HEIGHT);
            }
            for (int i = 0; i <= SCREEN_HEIGHT / UNIT_SIZE; i++) {
                g.drawLine(0, i * UNIT_SIZE, SCREEN_WIDTH, i * UNIT_SIZE);
            }

            g.drawImage(foodImage, foodCoorX, foodCoorY, UNIT_SIZE, UNIT_SIZE, this);

            if (snakeHead != null) {
                g2d.translate(x[0] + UNIT_SIZE / 2, y[0] + UNIT_SIZE / 2);

                switch (direction) {
                    case 'U' -> g2d.rotate(Math.toRadians(0));
                    case 'D' -> g2d.rotate(Math.toRadians(90 * 2));
                    case 'L' -> g2d.rotate(Math.toRadians(90 * 3));
                    case 'R' -> g2d.rotate(Math.toRadians(90));
                }

                g2d.drawImage(snakeHead, -UNIT_SIZE / 2, -UNIT_SIZE / 2, UNIT_SIZE, UNIT_SIZE, this);
                g2d.dispose();
            }

            for (int i = 1; i < snakeBody; i++) {
                g.setColor(new Color(0, 191, 99));
                g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
            }
        } else {
            gameOver(g);
        }
    }

    public void spawnFood() {
        foodCoorX = random.nextInt((int) (SCREEN_WIDTH / UNIT_SIZE) - 1) * UNIT_SIZE;
        foodCoorY = random.nextInt((int) (SCREEN_HEIGHT / UNIT_SIZE) - 1) * UNIT_SIZE;

        for (int i = 0; i < snakeBody; i++) {
            if ((foodCoorX == x[i]) && (foodCoorY == y[i])) {
                spawnFood();
            }
        }
    }

    public void checkFood() {
        if ((x[0] == foodCoorX) && (y[0] == foodCoorY)) {
            snakeBody++;
            foodEaten++;
            spawnFood();
        }
    }

    public void move() {
        for (int i = snakeBody; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }
        switch (direction) {
            case 'U' ->
                y[0] = y[0] - UNIT_SIZE;
            case 'D' ->
                y[0] = y[0] + UNIT_SIZE;
            case 'L' ->
                x[0] = x[0] - UNIT_SIZE;
            case 'R' ->
                x[0] = x[0] + UNIT_SIZE;

        }
    }

    public void checkCollision() {
        for (int i = snakeBody; i > 0; i--) {
            if ((x[0] == x[i]) && (y[0] == y[i])) {
                isRunning = false;
            }
        }

        if (x[0] < 0) {
            isRunning = false;
        }
        if (x[0] > SCREEN_WIDTH - UNIT_SIZE) {
            isRunning = false;
        }
        if (y[0] > SCREEN_HEIGHT - UNIT_SIZE) {
            isRunning = false;
        }
        if (y[0] < 0) {
            isRunning = false;
        }

        if (!isRunning) {
            timer.stop();
        }
    }

    public void gameOver(Graphics g) {
        g.setColor(Color.red);
        g.setFont(new Font("Tahoma", Font.BOLD, 75));
        FontMetrics metrics = getFontMetrics(g.getFont());
        g.drawString("Game Over", (SCREEN_WIDTH - metrics.stringWidth("Game Over")) / 2, SCREEN_HEIGHT / 2);

        retryButton = new JButton("Retry");
        retryButton.setFont(new Font("Tahoma", Font.BOLD, 30));
        retryButton.setFocusPainted(false);
        retryButton.setContentAreaFilled(false);
        retryButton.setBorderPainted(false);
        retryButton.setFocusPainted(false);
        retryButton.setOpaque(false);
        retryButton.setBounds(SCREEN_WIDTH / 2 - 100, SCREEN_HEIGHT / 2 + 50, 200, 50);

        retryButton.addActionListener(e -> resetGame());

        setLayout(null);
        add(retryButton);
        repaint();
    }

    public void resetGame() {
        new MainGameScreen();
        snakeBody = 4;
        foodEaten = 0;
        direction = 'R';
        isRunning = true;

        for (int i = 0; i < snakeBody; i++) {
            x[i] = 0;
            y[i] = 0;
        }

        spawnFood();
        timer.restart();
        removeAll();
        repaint();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (isRunning) {
            move();
            checkFood();
            checkCollision();
        }
        repaint();
    }

    public class KeyChecker extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_A:
                    if (direction != 'R') {
                        direction = 'L';
                    }
                    break;
                case KeyEvent.VK_D:
                    if (direction != 'L') {
                        direction = 'R';
                    }
                    break;
                case KeyEvent.VK_W:
                    if (direction != 'D') {
                        direction = 'U';
                    }
                    break;
                case KeyEvent.VK_S:
                    if (direction != 'U') {
                        direction = 'D';
                    }
                    break;
            }
        }
    }
}
