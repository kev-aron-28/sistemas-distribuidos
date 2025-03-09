// PROYECTO 2
// KEVIN ARON TAPIA CRUZ
// 7CM3

import java.awt.Color;
import java.awt.Point;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class Airplane {

    private double speed;

    public double planeX;

    public double planeY;

    private double angle = 0;

    private final double turnAngle = Math.toRadians(5);

    private final List<Point> trail = new LinkedList<>();

    private final Random random = new Random();

    private int PANEL_WIDTH;

    private int PANEL_HEIGHT;

    public Color color;

    private Airplane target;

    private List<Airplane> pursuers = new LinkedList<>();

    private double distanceTraveled = 0;

    public Airplane(double speed, int width, int height, Color color) {
        this.speed = speed;

        this.PANEL_HEIGHT = height;

        this.PANEL_WIDTH = width;

        this.planeX = random.nextInt(PANEL_WIDTH);

        this.planeY = random.nextInt(PANEL_HEIGHT);

        this.color = color;
    }

    public void setTarget(Airplane target) {
        this.target = target;
    }

    public void addPursuer(Airplane pursuer) {
        this.pursuers.add(pursuer);
    }

    public List<Point> getTrail() {
        return this.trail;
    }

    public void updateMovement() {
        double prevX = planeX; // Guarda la posición anterior

        double prevY = planeY;

        // Si hay un objetivo, lo persigue
        if (target != null) {
            pursueTarget();
        } // Si hay perseguidores, intenta evadirlos
        else if (!pursuers.isEmpty()) {
            evadePursuers();
        }

        distanceTraveled += Math.hypot(planeX - prevX, planeY - prevY);

        if (planeX > PANEL_WIDTH) {
            planeX = 0;
        }

        if (planeX < 0) {
            planeX = PANEL_WIDTH;
        }

        if (planeY > PANEL_HEIGHT) {
            planeY = 0;
        }

        if (planeY < 0) {
            planeY = PANEL_HEIGHT;
        }

        // Agrega la posicion actual a la estela del avión
        trail.add(new Point((int) planeX, (int) planeY));

        // Mantiene la estela con un maximo de 100 puntos
        if (trail.size() > 100) {
            trail.remove(0);
        }
    }

    private void pursueTarget() {
        if (target == null) {
            return;
        }
    
        // Calcula la distancia al objetivo
        double distance = Math.hypot(target.planeX - planeX, target.planeY - planeY);
        
        // Si esta muy cerca, detenerse para evitar giros innecesarios
        if (distance < 100) return;
    
        // Angulo hacia lo que persigue
        double targetAngle = Math.atan2(target.planeY - planeY, target.planeX - planeX);
        
        // Define un margen de error
        double angleMargin = Math.toRadians(2); // 2 grados de tolerancia
    
        // Calcula la diferencia entre angulos
        double angleDifference = (targetAngle - angle + Math.PI * 2) % (Math.PI * 2);
    
        // Ajusta el giro para evitar oscilaciones infinitas
        if (Math.abs(angleDifference) > angleMargin) {
            if (angleDifference < Math.PI) {
                angle += turnAngle; // derecha
            } else {
                angle -= turnAngle; // izquierda
            }
        } else {
            angle = targetAngle; // Corrige el angulo si la diferencia es pequena
        }
    
        //  mantenerlo en 0 a 2π
        angle = (angle + Math.PI * 2) % (Math.PI * 2);
    
        //  avanza correctamente hacia el objetivo
        planeX += Math.cos(angle) * speed;

        planeY += Math.sin(angle) * speed;
    }
    
    
    private void evadePursuers() {
        // Obtiene la direccion
        double threatAngle = getThreatDirection();
        // el avión se mueve en la direccion opuesta
        if (!Double.isNaN(threatAngle)) {
            angle = (threatAngle + Math.PI) % (2 * Math.PI);
        }

        // Mueve el avion en la nueva dirección
        planeX += Math.cos(angle) * speed;

        planeY += Math.sin(angle) * speed;
    }

    private double getThreatDirection() {
        if (pursuers.isEmpty()) {
            return Double.NaN;
        }

        double sumX = 0, sumY = 0;
        int count = 0;

        //  promedio de los perseguidores cercanos
        for (Airplane pursuer : pursuers) {
            double distance = Math.sqrt(Math.pow(this.planeX - pursuer.planeX, 2) + Math.pow(this.planeY - pursuer.planeY, 2));

            // dentro de un radio de 100 unidades
            if (distance < 100) {
                sumX += pursuer.planeX;
                sumY += pursuer.planeY;
                count++;
            }
        }

        // Si no hay perseguidores cercanos, no hay amenaza
        if (count == 0) {
            return Double.NaN;
        }

        // promedio de la amenaza
        double avgX = sumX / count;
        double avgY = sumY / count;

        return Math.atan2(avgY - planeY, avgX - planeX);
    }

    public double getDistanceTraveled() {
        return this.distanceTraveled;
    }

    public boolean hasCollided(Airplane other) {
        double distance = Math.sqrt(Math.pow(this.planeX - other.planeX, 2) + Math.pow(this.planeY - other.planeY, 2));
        
        return distance < 10;
    }
}
