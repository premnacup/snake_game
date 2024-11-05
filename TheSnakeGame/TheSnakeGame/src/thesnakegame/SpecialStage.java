package thesnakegame;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class SpecialStage extends AbstractGameScreen {
    private List<Rectangle> dangerZones;
    private static final int MIN_DANGER_ZONES = 3;
    private static final int MAX_DANGER_ZONES = 5;
    private static final int MIN_ZONE_SIZE = 2;
    private static final int MAX_ZONE_SIZE = 4;
    private Color dangerZoneColor;

    public SpecialStage(JFrame frame) {
        super(frame);
    }

    @Override
    protected void initializeComponents() {
        super.initializeComponents();
        dangerZones = new ArrayList<>();
        dangerZoneColor = new Color(255, 0, 0, 128);
        generateDangerZones();
    }

    @Override
    protected void loadImages() {
        PowerUpImage = new ImageIcon("..\\Assets\\powerUp.png").getImage();
        backgroundImage = new ImageIcon("..\\Assets\\special_background.jpg")
                .getImage();
        foodImage = new ImageIcon("..\\Assets\\apple.png").getImage();
        snakeHead = new ImageIcon("..\\Assets\\snake.png").getImage();
    }

    private void generateDangerZones() {
        dangerZones.clear();
        int numZones = random.nextInt(MAX_DANGER_ZONES - MIN_DANGER_ZONES + 1) + MIN_DANGER_ZONES;

        for (int i = 0; i < numZones; i++) {
            int zoneWidth = (random.nextInt(MAX_ZONE_SIZE - MIN_ZONE_SIZE + 1) + MIN_ZONE_SIZE) * UNIT_SIZE;
            int zoneHeight = (random.nextInt(MAX_ZONE_SIZE - MIN_ZONE_SIZE + 1) + MIN_ZONE_SIZE) * UNIT_SIZE;

            int x = random.nextInt((SCREEN_WIDTH - zoneWidth) / UNIT_SIZE) * UNIT_SIZE;
            int y = random.nextInt((SCREEN_HEIGHT - zoneHeight) / UNIT_SIZE) * UNIT_SIZE;

            // Avoid placing zones in the starting area
            if (x < UNIT_SIZE * 4 && y < UNIT_SIZE * 4) {
                continue;
            }

            Rectangle newZone = new Rectangle(x, y, zoneWidth, zoneHeight);

            // Check for overlap with other zones
            boolean overlaps = false;
            for (Rectangle existing : dangerZones) {
                if (newZone.intersects(existing)) {
                    overlaps = true;
                    break;
                }
            }

            if (!overlaps) {
                dangerZones.add(newZone);
            } else {
                i--; // Try again if there was an overlap
            }
        }
    }

    @Override
    protected void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();

        // Draw background
        g.drawImage(backgroundImage, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT, this);

        // Draw danger zones with pulsing effect
        drawDangerZones(g2d);

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

        g2d.dispose();
    }

    private void drawDangerZones(Graphics2D g2d) {
        g2d.setColor(dangerZoneColor);
        for (Rectangle zone : dangerZones) {
            g2d.fillRect(zone.x, zone.y, zone.width, zone.height);
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
        g.setColor(new Color(255, 215, 0)); // Gold color
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

    @Override
    protected void checkCollision() {
        // Check collision with danger zones
        Rectangle snakeHead = new Rectangle(x[0], y[0], UNIT_SIZE, UNIT_SIZE);
        for (Rectangle zone : dangerZones) {
            if (zone.intersects(snakeHead)) {
                isRunning = false;
                showGameOverOverlay = true;
                return;
            }
        }

        // Check normal collisions
        super.checkCollision();
    }

    @Override
    protected void spawnFood() {
        int attempts = 0;
        boolean validPositionFound = false;

        while (!validPositionFound && attempts < 100) {
            foodCoorX = random.nextInt((SCREEN_WIDTH / UNIT_SIZE)) * UNIT_SIZE;
            foodCoorY = random.nextInt((SCREEN_HEIGHT / UNIT_SIZE)) * UNIT_SIZE;
            validPositionFound = true;

            // Check collision with snake
            for (int i = 0; i < snakeBody; i++) {
                if (x[i] == foodCoorX && y[i] == foodCoorY) {
                    validPositionFound = false;
                    break;
                }
            }

            // Check collision with danger zones
            Rectangle foodSpot = new Rectangle(foodCoorX, foodCoorY, UNIT_SIZE, UNIT_SIZE);
            for (Rectangle zone : dangerZones) {
                if (zone.intersects(foodSpot)) {
                    validPositionFound = false;
                    break;
                }
            }

            attempts++;
        }
    }

    @Override
    protected void spawnPowerUp() {
        int attempts = 0;
        boolean validPositionFound = false;

        while (!validPositionFound && attempts < 100) {
            powerUp.powerUpCoorX = random.nextInt((SCREEN_WIDTH / UNIT_SIZE)) * UNIT_SIZE;
            powerUp.powerUpCoorY = random.nextInt((SCREEN_HEIGHT / UNIT_SIZE)) * UNIT_SIZE;
            validPositionFound = true;

            // Check collision with snake
            for (int i = 0; i < snakeBody; i++) {
                if (x[i] == powerUp.powerUpCoorX && y[i] == powerUp.powerUpCoorY) {
                    validPositionFound = false;
                    break;
                }
            }

            // Check collision with danger zones
            Rectangle powerUpSpot = new Rectangle(powerUp.powerUpCoorX, powerUp.powerUpCoorY, UNIT_SIZE, UNIT_SIZE);
            for (Rectangle zone : dangerZones) {
                if (zone.intersects(powerUpSpot)) {
                    validPositionFound = false;
                    break;
                }
            }

            attempts++;
        }
    }

    @Override
    protected void resetGame() {
        dangerZones.clear();
        generateDangerZones();
        super.resetGame();
    }
}