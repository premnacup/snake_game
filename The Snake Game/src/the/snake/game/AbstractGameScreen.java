package the.snake.game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public abstract class AbstractGameScreen extends JPanel implements ActionListener {
    // Add JFrame field
    protected final JFrame frame;
    // Constants
    protected static final int SCREEN_WIDTH = 720;
    protected static final int SCREEN_HEIGHT = 720;
    protected static final int UNIT_SIZE = 40;
    protected static final int DEFAULT_SNAKE_SIZE = 4;
    protected static final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT) / UNIT_SIZE;
    protected static final int MAX_SNAKE_SIZE = GAME_UNITS / UNIT_SIZE;
    protected static final int DELAY = 150;

    // Game state
    protected int countdown;
    protected final int[] x = new int[GAME_UNITS];
    protected final int[] y = new int[GAME_UNITS];
    protected int snakeBody;
    protected int foodCoorX;
    protected int foodCoorY;

    // Images
    protected Image backgroundImage;
    protected Image foodImage;
    protected Image snakeHead;
    protected Image PowerUpImage;

    // Game control
    protected char direction;
    protected boolean isRunning;
    protected boolean gameWin;
    protected boolean showGameOverOverlay;
    protected boolean showGameWinOverlay;
    protected boolean directionChanged;

    // Utilities
    protected Timer countdownTimer;
    protected Timer timer;
    protected Random random;
    protected JButton retryButton;
    protected JButton mainMenuButton;
    protected PowerUP powerUp;
    protected KeyChecker keyboard;

    protected AbstractGameScreen(JFrame frame) {
        this.frame = frame;
        initializeComponents();
        setupPanel();
        loadImages();
        startGame();
    }

    protected abstract void loadImages();

    protected void initializeComponents() {
        random = new Random();
        keyboard = new KeyChecker();
        powerUp = new PowerUP();
        countdown = 60;
        snakeBody = DEFAULT_SNAKE_SIZE;
        direction = 'R';
    }

    protected void setupPanel() {
        setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        setBackground(Color.BLACK);
        setVisible(true);
        setFocusable(true);
        addKeyListener(keyboard);
    }

    protected void startGame() {
        spawnPowerUp();
        spawnFood();
        isRunning = true;
        setupTimers();
    }

    protected void setupTimers() {
        timer = new Timer(DELAY, this);
        countdownTimer = new Timer(1000, e -> {
            countdown--;
            if (countdown <= 0) {
                countdown = 0;
                isRunning = false;
                showGameWinOverlay = true;
                timer.stop();
                countdownTimer.stop();
            }
            repaint();
        });
        countdownTimer.start();
        timer.start();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    protected abstract void draw(Graphics g);

    protected void drawSnakeHead(Graphics2D g2d) {
        g2d.translate(x[0] + UNIT_SIZE / 2, y[0] + UNIT_SIZE / 2);

        switch (direction) {
            case 'U' -> g2d.rotate(Math.toRadians(0));
            case 'D' -> g2d.rotate(Math.toRadians(180));
            case 'L' -> g2d.rotate(Math.toRadians(270));
            case 'R' -> g2d.rotate(Math.toRadians(90));
        }

        g2d.drawImage(snakeHead, -UNIT_SIZE / 2, -UNIT_SIZE / 2, UNIT_SIZE, UNIT_SIZE, this);
        g2d.dispose();
    }

    protected void spawnFood() {
        int attempts = 0;
        boolean validPositionFound = false;

        while (!validPositionFound && attempts < 100) {
            foodCoorX = random.nextInt((SCREEN_WIDTH / UNIT_SIZE)) * UNIT_SIZE;
            foodCoorY = random.nextInt((SCREEN_HEIGHT / UNIT_SIZE)) * UNIT_SIZE;
            validPositionFound = true;

            for (int i = 0; i < snakeBody; i++) {
                if (x[i] == foodCoorX && y[i] == foodCoorY) {
                    validPositionFound = false;
                    break;
                }
            }
            attempts++;
        }
    }

    protected void spawnPowerUp() {
        int attempts = 0;
        if (snakeBody >= MAX_SNAKE_SIZE) {
            return;
        }

        double usedSpace = snakeBody;
        double gameSpace = GAME_UNITS / UNIT_SIZE;
        double availableSpace = gameSpace - usedSpace;

        if (availableSpace <= gameSpace * 0.5) {
            powerUp.deactivatePowerUps();
            return;
        }

        boolean validPositionFound = false;
        while (!validPositionFound && attempts < 100) {
            powerUp.powerUpCoorX = random.nextInt((SCREEN_WIDTH / UNIT_SIZE)) * UNIT_SIZE;
            powerUp.powerUpCoorY = random.nextInt((SCREEN_HEIGHT / UNIT_SIZE)) * UNIT_SIZE;
            validPositionFound = true;

            for (int i = 0; i < snakeBody; i++) {
                if ((powerUp.powerUpCoorX == x[i]) && (powerUp.powerUpCoorY == y[i])) {
                    validPositionFound = false;
                    break;
                }
            }

            if ((powerUp.powerUpCoorX == foodCoorX) && (powerUp.powerUpCoorY == foodCoorY)) {
                validPositionFound = false;
            }

            attempts++;
        }
    }

    protected void checkPowerUp() {
        if (powerUp.isMagnetActive() || powerUp.isDoubleLengthActive()) {
            return;
        }

        if (x[0] == powerUp.powerUpCoorX && y[0] == powerUp.powerUpCoorY) {
            if (random.nextBoolean()) {
                powerUp.activateMagnet();
            } else {
                powerUp.activateDoubleLength();
            }
            spawnPowerUp();
        }
    }

    protected void checkFood() {
        if (powerUp.isMagnetActive()) {
            if (Math.abs(x[0] - foodCoorX) <= UNIT_SIZE * 2 && Math.abs(y[0] - foodCoorY) <= UNIT_SIZE * 2) {
                growSnake(1);
                spawnFood();
            }
        } else if (x[0] == foodCoorX && y[0] == foodCoorY) {
            growSnake(powerUp.isDoubleLengthActive() ? 2 : 1);
            spawnFood();
        }
    }

    protected void growSnake(int length) {
        for (int i = 0; i < length; i++) {
            x[snakeBody + i] = x[snakeBody - 1];
            y[snakeBody + i] = y[snakeBody - 1];
        }
        snakeBody += length;
    }

    protected void move() {
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

    protected void checkCollision() {
        for (int i = 1; i < snakeBody; i++) {
            if ((x[0] == x[i]) && (y[0] == y[i])) {
                isRunning = false;
                showGameOverOverlay = true;
                break;
            }
        }

        if (x[0] < 0 || x[0] > SCREEN_WIDTH - UNIT_SIZE || y[0] < 0 || y[0] > SCREEN_HEIGHT - UNIT_SIZE) {
            isRunning = false;
            showGameOverOverlay = true;
        }

        if (!isRunning) {
            timer.stop();
        }
    }

    protected void gameOver(Graphics g) {
        timer.stop();
        countdownTimer.stop();
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

        createButton();
    }

    protected void gameWin(Graphics g) {
        timer.stop();
        countdownTimer.stop();
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

        createButton();
    }

    private void createButton() {
        retryButton = new JButton("Retry");
        retryButton.setFont(new Font("Tahoma", Font.BOLD, 30));
        retryButton.setFocusPainted(false);
        retryButton.setContentAreaFilled(false);
        retryButton.setBorderPainted(false);
        retryButton.setOpaque(false);
        retryButton.setBounds(SCREEN_WIDTH / 2 - 100, SCREEN_HEIGHT / 2 + 50, 200, 50);
        retryButton.addActionListener(e -> resetGame());

        mainMenuButton = new JButton("Main Menu");
        mainMenuButton.setFont(new Font("Tahoma", Font.BOLD, 30));
        mainMenuButton.setFocusPainted(false);
        mainMenuButton.setContentAreaFilled(false);
        mainMenuButton.setBorderPainted(false);
        mainMenuButton.setOpaque(false);
        mainMenuButton.setBounds(SCREEN_WIDTH / 2 - 100, SCREEN_HEIGHT / 2 + 120, 200, 50);
        mainMenuButton.addActionListener(e -> {
            switchToGameScreen(frame, new MainMenu(frame));
        });

        setLayout(null);
        add(retryButton);
        add(mainMenuButton);
        repaint();
    }

    protected void switchToGameScreen(JFrame frame, MainMenu gameScreen) {
        frame.getContentPane().removeAll();
        frame.add(gameScreen);
        frame.revalidate();
        frame.repaint();
        gameScreen.requestFocusInWindow();
    }

    protected void resetGame() {
        gameWin = false;
        snakeBody = DEFAULT_SNAKE_SIZE;
        direction = 'R';
        isRunning = true;
        showGameOverOverlay = false;
        showGameWinOverlay = false;
        countdown = 60;
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
        countdownTimer.restart();
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

    protected class KeyChecker extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            if (!directionChanged) {
                int key = e.getKeyCode();
                switch (key) {
                    case KeyEvent.VK_LEFT -> {
                        if (direction != 'R')
                            direction = 'L';
                    }
                    case KeyEvent.VK_RIGHT -> {
                        if (direction != 'L')
                            direction = 'R';
                    }
                    case KeyEvent.VK_UP -> {
                        if (direction != 'D')
                            direction = 'U';
                    }
                    case KeyEvent.VK_DOWN -> {
                        if (direction != 'U')
                            direction = 'D';
                    }
                }
            }
            directionChanged = true;
        }
    }
}
