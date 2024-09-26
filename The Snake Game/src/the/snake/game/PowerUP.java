package the.snake.game;

import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PowerUP {
    int powerUpCoorX;
    int powerUpCoorY;
    boolean magnetActive = false;
    boolean doubleLengthActive = false;

    private static final int POWERUP_DURATION = 10000;
    private Timer powerUpTimer;

    public PowerUP() {
        magnetActive = false;
        doubleLengthActive = false;
    }

    public void activateMagnet() {
        magnetActive = true;
        startPowerUpTimer();
    }

    public void activateDoubleLength() {
        doubleLengthActive = true;
        startPowerUpTimer();
    }

    private void startPowerUpTimer() {

        System.out.println("Start PowerUp Timer");
        powerUpTimer = new Timer(POWERUP_DURATION, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Deactivating PowerUp");
                deactivatePowerUps();
            }
        });
        powerUpTimer.setRepeats(false);
        powerUpTimer.start();
    }

    public void deactivatePowerUps() {
        System.out.println("PowerUp deactivated");
        magnetActive = false;
        doubleLengthActive = false;
    }

    public boolean isMagnetActive() {
        return magnetActive;
    }

    public boolean isDoubleLengthActive() {
        return doubleLengthActive;
    }

}
