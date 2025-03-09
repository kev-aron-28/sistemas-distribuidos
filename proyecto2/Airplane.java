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

    public List<Point> getTrail() {
        return this.trail;
    }

    public void updateMovement() {
        if (target != null) {
            pursueTarget();
        } else {
            randomMovement();
        }

        if (planeX > PANEL_WIDTH) planeX = 0;

        if (planeX < 0) planeX = PANEL_WIDTH;

        if (planeY > PANEL_HEIGHT) planeY = 0;

        if (planeY < 0) planeY = PANEL_HEIGHT;

        trail.add(new Point((int) planeX, (int) planeY));

        if (trail.size() > 100) trail.remove(0);
    }

    private void randomMovement() {
        planeX += Math.cos(angle) * speed;

        planeY += Math.sin(angle) * speed;

        int rand = random.nextInt(100);
        
        if (rand < 10) {
        
          if (random.nextBoolean()) {
                angle += turnAngle;
        
              } else {
        
                angle -= turnAngle;
            }
        }
    }

    private void pursueTarget() {
        if (target == null) return;

        double targetAngle = Math.atan2(target.planeY - planeY, target.planeX - planeX);

        if (Math.abs(targetAngle - angle) > turnAngle) {
           
          if ((targetAngle - angle + Math.PI * 2) % (Math.PI * 2) < Math.PI) {
                angle += turnAngle;
          
              } else {
          
                angle -= turnAngle;
            }
        } else {
            angle = targetAngle;
        
          }

        planeX += Math.cos(angle) * speed;
        
        planeY += Math.sin(angle) * speed;
    }

    public boolean hasCollided(Airplane other) {
        double distance = Math.sqrt(Math.pow(this.planeX - other.planeX, 2) + Math.pow(this.planeY - other.planeY, 2));

        return distance < 10;
    }
}
