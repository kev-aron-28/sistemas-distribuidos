// PROYECTO 2
// KEVIN ARON TAPIA CRUZ
// 7CM3

import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;

public class Main extends JPanel {
    private final int PANEL_WIDTH = 1280;
    
    private final int PANEL_HEIGHT = 720;
    
    private boolean running = true;
    
    private long startTime;

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
        
            mainPlain.addPursuer(chaser);
        
            planes.add(chaser);
        }

        JFrame frame = new JFrame("Cosa");
        
        frame.add(this);
        
        frame.pack();
        
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        
        frame.setVisible(true);

        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                System.out.println("El avión principal recorrió " + mainPlain.getDistanceTraveled() + " unidades.");
                
                System.exit(0);
            }
        });

        startTime = System.currentTimeMillis();

        Timer timer = new Timer(20, e -> updatePlaneMovements());

        timer.start();

        new Timer(120000, e -> endGame()).start();
    }

    private void updatePlaneMovements() {
        if (!running) return;

        for (Airplane plane : planes) {
            plane.updateMovement();
        }

        for (Airplane chaser : planes) {
            if (chaser != mainPlain && chaser.hasCollided(mainPlain)) {
                System.out.println("Colision detectada en (" + (int) chaser.planeX + ", " + (int) chaser.planeY + ")");
                
                System.out.println("El avion principal recorrio " + mainPlain.getDistanceTraveled() + " unidades");
                
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

    private void endGame() {
        System.out.println("Tiempo de ejecucion de 2 minutos alcanzado");
        
        System.out.println("El avion principal recorrio " + mainPlain.getDistanceTraveled() + " unidades");
        
        running = false;

        System.exit(0);
    }

    public static void main(String[] args) {
        int numerOfPlanes = Integer.parseInt(args[0]);

        double speed = Double.parseDouble(args[1]);

        SwingUtilities.invokeLater(() -> {
            Main game = new Main(numerOfPlanes, speed);
        });
    }
}
