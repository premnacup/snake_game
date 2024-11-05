package thesnakegame;

import javax.swing.*;
import java.awt.*;

public class NormalStage extends AbstractGameScreen {

    public NormalStage(JFrame frame) {
        super(frame);
    }

    @Override
    protected void loadImages() {
        PowerUpImage = new ImageIcon("..\\Assets\\powerUp.png").getImage();
        backgroundImage = new ImageIcon("..\\Assets\\background.png").getImage();
        foodImage = new ImageIcon("..\\Assets\\apple.png").getImage();
        snakeHead = new ImageIcon("..\\Assets\\snake.png").getImage();
    }

    @Override
    protected void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();

        // Draw background
        g.drawImage(backgroundImage, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT, this);

        // Draw food
        g.drawImage(foodImage, foodCoorX, foodCoorY, UNIT_SIZE, UNIT_SIZE, this);

        // Draw PowerUp
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
            snakeHead = new ImageIcon("..\\Assets\\snake.png").getImage();
            drawSnakeHead(g2d);
        }

        // Draw Snake with conditions
        if (powerUp.isMagnetActive()) {
            drawMagnetActiveSnake(g);
        } else if (powerUp.isDoubleLengthActive()) {
            drawDoubleLengthActiveSnake(g);
        } else {
            drawNormalSnake(g);
        }

        // Draw countdown timer
        drawCountdownTimer(g);

        // Draw score
        drawScore(g);

        // Check win condition
        if (snakeBody >= MAX_SNAKE_SIZE) {
            showGameWinOverlay = true;
            isRunning = false;
        }

        // Draw overlays
        if (showGameOverOverlay) {
            snakeHead = new ImageIcon("..\\Assets\\dedSnake.png").getImage();
            gameOver(g);
        } else if (showGameWinOverlay) {
            gameWin(g);
        }
    }

    private void drawMagnetActiveSnake(Graphics g) {
        g.setColor(Color.CYAN);
        for (int i = 0; i < 3; i++) {
            g.drawArc(x[0] - i * 10, y[0] - i * 10, UNIT_SIZE + i * 20, UNIT_SIZE + i * 20, 0, 360);
        }
        for (int i = 1; i < snakeBody; i++) {
            g.setColor(new Color(0, 191, 99));
            g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
        }
    }

    private void drawDoubleLengthActiveSnake(Graphics g) {
        for (int i = 0; i < snakeBody; i++) {
            float hue = (float) i / snakeBody;
            g.setColor(Color.getHSBColor(hue, 1.0f, 1.0f));
            g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
        }
        snakeHead = new ImageIcon("..\\Assets\\snakeGlasses.png").getImage();
        Graphics2D g2d = (Graphics2D) g.create();
        drawSnakeHead(g2d);
    }

    private void drawNormalSnake(Graphics g) {
        for (int i = 1; i < snakeBody; i++) {
            g.setColor(new Color(0, 191, 99));
            g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
        }
    }

    private void drawCountdownTimer(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Tahoma", Font.BOLD, 30));
        String timerText = "Time Left: " + countdown;
        FontMetrics timerMetrics = getFontMetrics(g.getFont());
        g.drawString(timerText, (SCREEN_WIDTH - timerMetrics.stringWidth(timerText)) / 2, 50);
    }

    private void drawScore(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Tahoma", Font.BOLD, 30));
        String scoreText = "Score: " + snakeBody;
        FontMetrics scoreMetrics = getFontMetrics(g.getFont());
        g.drawString(scoreText, (SCREEN_WIDTH - scoreMetrics.stringWidth(scoreText)) / 2, 100);
    }
}