import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;

public class Main extends JPanel {
    private final int PANEL_WIDTH = 1280;
    
    private final int PANEL_HEIGHT = 720;
    
    private boolean running = true;

    Airplane mainPlain = new Airplane(2.0, PANEL_WIDTH, PANEL_HEIGHT, Color.RED);

    ArrayList<Airplane> planes = new ArrayList<>();

    public Main(int numberOfPlanes, double speedPlanes) {
        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        
        setBackground(Color.BLACK);

        setFocusable(true);

        if (numberOfPlanes > 7) numberOfPlanes = 7;

        planes.add(mainPlain);

        for (int i = 0; i < numberOfPlanes; i++) {
            Airplane chaser = new Airplane(2.0 * speedPlanes, PANEL_WIDTH, PANEL_HEIGHT, Color.blue);
        
            chaser.setTarget(mainPlain);
        
            planes.add(chaser);
        }

        Timer timer = new Timer(20, e -> updatePlaneMovements());
        
        timer.start();
    }

    private void updatePlaneMovements() {
        if (!running) return;

        for (Airplane plane : planes) {
            plane.updateMovement();
        }

        for (Airplane chaser : planes) {
            if (chaser != mainPlain && chaser.hasCollided(mainPlain)) {
                
                System.out.println("¡Colisión detectada en (" + (int) chaser.planeX + ", " + (int) chaser.planeY + ")!");
                
                running = false;
                
                System.exit(0);
            }
        }

        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        for (Airplane plane : planes) {
            g.setColor(Color.WHITE);

            for (Point p : plane.getTrail()) {
                g.fillRect(p.x, p.y, 5, 5);
            }

            g.setColor(plane.color);
            g.fillOval((int) plane.planeX - 5, (int) plane.planeY - 5, 20, 20);
        }
    }

    public static void main(String[] args) {
        int numerOfPlanes = Integer.parseInt(args[0]);

        double speed = Double.parseDouble(args[1]);

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Cosa");
        
            Main game = new Main(numerOfPlanes, speed);
        
            frame.add(game);
        
            frame.pack();
        
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
            frame.setVisible(true);
        });
    }
}
