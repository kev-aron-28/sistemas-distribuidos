import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.swing.*;

public class Main {

    static class Screen extends JPanel {
        private final List<Asteroid> asteroids;

        public Screen(List<Asteroid> asteroids) {
            this.asteroids = asteroids;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, getWidth(), getHeight());

            g.setColor(Color.WHITE);
            synchronized (asteroids) {
                for (Asteroid a : asteroids) {
                    if (a.isActive()) {
                        Coordenada pos = a.getPosition();
                        int size = a.getSize();
                        g.fillOval((int) pos.abcisa(), (int) pos.ordenada(), size, size);
                    }
                }
            }
        }

        public void updateScreen() {
            SwingUtilities.invokeLater(this::repaint);
        }
    }

    public static void main(String[] args) {
        int numAsteroids = args.length > 0 ? Integer.parseInt(args[0]) : 5;

        List<Asteroid> asteroids = new ArrayList<>();
        for (int i = 0; i < numAsteroids; i++) {
            Asteroid a = new Asteroid();
            asteroids.add(a);
            new Thread(a).start();
        }

        JFrame window = new JFrame("Asteroides");
        Screen screen = new Screen(asteroids);
        
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setSize(800, 600);
        window.setResizable(false);
        window.add(screen);
        window.setVisible(true);

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(() -> {
            synchronized (asteroids) {
                for (int i = 0; i < asteroids.size(); i++) {
                    for (int j = i + 1; j < asteroids.size(); j++) {
                        if (asteroids.get(i).isActive() && asteroids.get(j).isActive() &&
                                asteroids.get(i).checkCollision(asteroids.get(j))) {
                            asteroids.get(i).split();
                            asteroids.get(j).split();
                        }
                    }
                }
            }
            screen.updateScreen();
        }, 0, 16, TimeUnit.MILLISECONDS);
    }
}
