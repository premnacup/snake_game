package the.snake.game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class MainGameScreen extends JPanel implements ActionListener {

    static final int SCREEN_WIDTH = 720;
    static final int SCREEN_HEIGHT = 720;
    static final int UNIT_SIZE = 40;
    static final int DEFAULT_SNAKE_SIZE = 4;
    static final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT) / UNIT_SIZE;
    static final int MAX_SNAKE_SIZE = GAME_UNITS / UNIT_SIZE;
    static final int DELAY = 150;

    final int x[] = new int[GAME_UNITS];
    final int y[] = new int[GAME_UNITS];

    int snakeBody = DEFAULT_SNAKE_SIZE;
    int foodCoorX;
    int foodCoorY;

    Image backgroundImage;
    Image foodImage;
    Image snakeHead;
    Image PowerUpImage;

    char direction = 'R';
    boolean isRunning = false;
    boolean gameWin = false;
    boolean showGameOverOverlay = false;
    boolean showGameWinOverlay = false;
    boolean directionChanged = false;

    Timer timer;
    Random random;
    JButton retryButton;
    PowerUP powerUp;
    KeyChecker keyboard = new KeyChecker();

    public MainGameScreen() {
        random = new Random();
        setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        setBackground(Color.BLACK);
        setVisible(true);
        setFocusable(true);
        addKeyListener(keyboard);

        powerUp = new PowerUP();
        PowerUpImage = new ImageIcon("The Snake Game\\src\\the\\snake\\game\\Assets\\powerUp.png").getImage();
        backgroundImage = new ImageIcon("The Snake Game\\src\\the\\snake\\game\\Assets\\background.png").getImage();
        foodImage = new ImageIcon("The Snake Game\\src\\the\\snake\\game\\Assets\\apple.png").getImage();
        snakeHead = new ImageIcon("The Snake Game\\src\\the\\snake\\game\\Assets\\snake.png").getImage();

        startGame();
    }

    private final void startGame() {
        spawnPowerUp();
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

    private void draw(Graphics g) {

        Graphics2D g2d = (Graphics2D) g.create();

        // Draw background
        g.drawImage(backgroundImage, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT, this);

        // Draw food
        g.drawImage(foodImage, foodCoorX, foodCoorY, UNIT_SIZE, UNIT_SIZE, this);

        // Draw PowerUp (if any powerUp isn't active)
        if (!powerUp.isDoubleLengthActive() && !powerUp.isMagnetActive()) {
            g.drawImage(PowerUpImage, powerUp.powerUpCoorX, powerUp.powerUpCoorY, UNIT_SIZE, UNIT_SIZE, this);
        }

        // Draw snake's shadow
        for (int i = 0; i < snakeBody; i++) {
            g.setColor(new Color(0, 0, 0, 100));
            g.fillRect(x[i] + 5, y[i] + 5, UNIT_SIZE, UNIT_SIZE);
        }

        // Draw Snake Head
        if (snakeHead != null && !powerUp.isDoubleLengthActive()) {
            snakeHead = new ImageIcon("The Snake Game\\src\\the\\snake\\game\\Assets\\snake.png").getImage();
            drawSnakeHead(g2d);
        }

        // Draw Snake with conditions
        if (powerUp.isMagnetActive()) {
            // Draw Snake with MagnetActive
            g.setColor(Color.CYAN);
            for (int i = 0; i < 3; i++) {
                g.drawArc(x[0] - i * 10, y[0] - i * 10, UNIT_SIZE + i * 20, UNIT_SIZE + i * 20, 0, 360);
            }
            for (int i = 1; i < snakeBody; i++) {
                g.setColor(new Color(0, 191, 99));
                g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
            }

        } else if (powerUp.isDoubleLengthActive()) {
            // Draw Snake with DoubleLengthActive
            for (int i = 0; i < snakeBody; i++) {
                float hue = (float) i / snakeBody;
                g.setColor(Color.getHSBColor(hue, 1.0f, 1.0f));
                g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
            }

            snakeHead = new ImageIcon("The Snake Game\\src\\the\\snake\\game\\Assets\\snakeGlasses.png").getImage();
            drawSnakeHead(g2d);

        } else {
            // Draw Snake
            for (int i = 1; i < snakeBody; i++) {
                g.setColor(new Color(0, 191, 99));
                g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
            }
        }

        // Check win condition
        if (snakeBody >= MAX_SNAKE_SIZE) {
            showGameWinOverlay = true;
            isRunning = false;
        }

        // Draw Game Over and Game Win overlays
        if (showGameOverOverlay) {
            snakeHead = new ImageIcon("The Snake Game\\src\\the\\snake\\game\\Assets\\dedSnake.png").getImage();
            gameOver(g);
        } else if (showGameWinOverlay) {
            gameWin(g);
        }
    }

    // drawSnakeHead manager (Rotate depending on direction)
    private void drawSnakeHead(Graphics2D g2d) {
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

    private void spawnFood() {
        int attempts = 0;
        boolean validPositionFound = false;

        while (!validPositionFound && attempts < 100) {
            foodCoorX = random.nextInt((SCREEN_WIDTH / UNIT_SIZE)) * UNIT_SIZE;
            foodCoorY = random.nextInt((SCREEN_HEIGHT / UNIT_SIZE)) * UNIT_SIZE;
            validPositionFound = true;

            // Check if food overlaps with snake
            for (int i = 0; i < snakeBody; i++) {
                if (x[i] == foodCoorX && y[i] == foodCoorY) {
                    validPositionFound = false;
                    break;
                }
            }
            attempts++;
        }
    }

    private void spawnPowerUp() {
        int attempts = 0;
        // Check if the snake is already at max size
        if (snakeBody >= MAX_SNAKE_SIZE) {
            return;
        }

        // Calculate available space
        double usedSpace = snakeBody; // occupied by the snake
        double gameSpace = GAME_UNITS / UNIT_SIZE;
        double availableSpace = gameSpace - usedSpace;

        // Check if available space is less than 50% of the total
        if (availableSpace <= gameSpace * 0.5) {
            powerUp.deactivatePowerUps();
            return; // Don't spawn power-up
        } else {
            boolean validPositionFound = false;
            while (!validPositionFound && attempts < 100) {
                // Generate random position
                powerUp.powerUpCoorX = random.nextInt((SCREEN_WIDTH / UNIT_SIZE)) * UNIT_SIZE;
                powerUp.powerUpCoorY = random.nextInt((SCREEN_HEIGHT / UNIT_SIZE)) * UNIT_SIZE;
                validPositionFound = true;

                // Check overlap with snake
                for (int i = 0; i < snakeBody; i++) {
                    if ((powerUp.powerUpCoorX == x[i]) && (powerUp.powerUpCoorY == y[i])) {
                        validPositionFound = false; // Overlaps with snake
                        break;
                    }
                }

                // Check overlap with food
                if ((powerUp.powerUpCoorX == foodCoorX) && (powerUp.powerUpCoorY == foodCoorY)) {
                    validPositionFound = false;
                }

                attempts++;
            }
        }
    }

    private void checkPowerUp() {
        // Check if powerUp is active then return
        if (powerUp.isMagnetActive() || powerUp.isDoubleLengthActive()) {
            return;
        } else {
            // Check if the snake head touches powerUp
            if (x[0] == powerUp.powerUpCoorX && y[0] == powerUp.powerUpCoorY) {
                // Random powerUp
                if (random.nextBoolean()) {
                    powerUp.activateMagnet();
                } else {
                    powerUp.activateDoubleLength();
                }
                spawnPowerUp();
            }
        }
    }

    private void checkFood() {
        // Check if magnetActive
        if (powerUp.isMagnetActive()) {
            // if food is in magnet's range of the snake head
            if (Math.abs(x[0] - foodCoorX) <= UNIT_SIZE * 2 && Math.abs(y[0] - foodCoorY) <= UNIT_SIZE * 2) {
                growSnake(1);
                spawnFood();
            }
        } else if (x[0] == foodCoorX && y[0] == foodCoorY) {
            if (powerUp.isDoubleLengthActive()) {
                growSnake(2);
            } else {
                growSnake(1);
            }
            spawnFood();
        }
    }

    /**
     * Increases the size of the snake by the given length.
     *
     * @param length the number of units to grow the snake by
     */
    private void growSnake(int length) {
        for (int i = 0; i < length; i++) {
            x[snakeBody + i] = x[snakeBody - 1];
            y[snakeBody + i] = y[snakeBody - 1];
        }
        snakeBody += length;
    }

    private void move() {
        for (int i = snakeBody; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }

        switch (direction) {
            case 'U' -> y[0] -= UNIT_SIZE;
            case 'D' -> y[0] += UNIT_SIZE;
            case 'L' -> x[0] -= UNIT_SIZE;
            case 'R' -> x[0] += UNIT_SIZE;
        }

    }

    private void checkCollision() {
        // Check collision with itself
        for (int i = 1; i < snakeBody; i++) {
            if ((x[0] == x[i]) && (y[0] == y[i])) {
                isRunning = false;
                showGameOverOverlay = true;
                break;
            }
        }

        // Check collision with border
        if (x[0] < 0 || x[0] > SCREEN_WIDTH - UNIT_SIZE || y[0] < 0 || y[0] > SCREEN_HEIGHT - UNIT_SIZE) {
            isRunning = false;
            showGameOverOverlay = true;
        }

        if (!isRunning) {
            timer.stop();
        }
    }

    private void gameOver(Graphics g) {
        timer.stop();
        gameWin = false;
        removeKeyListener(keyboard);

        // Background rectangle
        Color transparentColor = new Color(255, 255, 255, 128);
        g.setColor(transparentColor);
        g.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);

        // Text
        g.setColor(Color.red);
        g.setFont(new Font("Tahoma", Font.BOLD, 75));
        FontMetrics metrics = getFontMetrics(g.getFont());
        g.drawString("Game Over", (SCREEN_WIDTH - metrics.stringWidth("Game Over")) / 2, SCREEN_HEIGHT / 2);

        // Transparent Button
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

    private void gameWin(Graphics g) {
        timer.stop();
        gameWin = true;
        removeKeyListener(keyboard);

        // Background rectangle
        Color transparentColor = new Color(255, 255, 255, 128);
        g.setColor(transparentColor);
        g.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);

        // Text
        g.setColor(Color.GREEN);
        g.setFont(new Font("Tahoma", Font.BOLD, 75));
        FontMetrics metrics = getFontMetrics(g.getFont());
        g.drawString("You Win", (SCREEN_WIDTH - metrics.stringWidth("You Win")) / 2, SCREEN_HEIGHT / 2);

        // Transparent button
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

    private void resetGame() {
        gameWin = false;
        snakeBody = DEFAULT_SNAKE_SIZE;
        direction = 'R';
        isRunning = true;
        showGameOverOverlay = false;
        showGameWinOverlay = false;
        powerUp.deactivatePowerUps();

        for (int i = 0; i < snakeBody; i++) {
            x[i] = 0;
            y[i] = 0;
        }

        removeAll();
        addKeyListener(keyboard);
        spawnFood();
        spawnPowerUp();
        timer.restart();
        repaint();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (isRunning) {
            move();
            directionChanged = false;
            checkPowerUp();
            checkFood();
            checkCollision();
        }
        repaint();
    }

    public class KeyChecker extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            if (!directionChanged) {

                int key = e.getKeyCode();
                switch (key) {
                    case KeyEvent.VK_LEFT:
                        if (direction != 'R') {
                            direction = 'L';
                        }
                        break;
                    case KeyEvent.VK_RIGHT:
                        if (direction != 'L') {
                            direction = 'R';
                        }
                        break;
                    case KeyEvent.VK_UP:
                        if (direction != 'D') {
                            direction = 'U';
                        }
                        break;
                    case KeyEvent.VK_DOWN:
                        if (direction != 'U') {
                            direction = 'D';
                        }
                        break;
                }
            }
            directionChanged = true;
        }
    }

}
