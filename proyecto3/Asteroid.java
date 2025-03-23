import java.util.Random;

public class Asteroid extends PoligonoIrreg implements Runnable {
    private Coordenada position;

    private double speed;
    
    private double directionX, directionY;
    
    private int size;
    
    private boolean active;
    
    private static final int SCREEN_WIDTH = 800;
    
    private static final int SCREEN_HEIGHT = 600;
    
    public Asteroid() {
        super(new Random().nextInt(5) + 3);

        Random rand = new Random();
        
        
        size = rand.nextInt(40) + 10;
        
        position = new Coordenada(rand.nextInt(SCREEN_WIDTH), rand.nextInt(SCREEN_HEIGHT));
        
        speed = 200.0 / size;
        
        directionX = rand.nextDouble() * 2 - 1;
        
        directionY = rand.nextDouble() * 2 - 1;
        
        active = true;
    }
    
    public Coordenada getPosition() {
        return position;
    }
    
    public int getSize() {
        return size;
    }
    
    public boolean isActive() {
        return active;
    }
    
    public synchronized void move() {
        position.setX((position.abcisa() + directionX * speed + SCREEN_WIDTH) % SCREEN_WIDTH);

        position.setY((position.ordenada() + directionY * speed + SCREEN_HEIGHT) % SCREEN_HEIGHT);
    }
    
    public synchronized boolean checkCollision(Asteroid other) {
        double dx = position.abcisa() - other.getPosition().abcisa();

        
        double dy = position.ordenada() - other.getPosition().ordenada();
        
        double distance = Math.sqrt(dx * dx + dy * dy);
        
        return distance < (size / 2.0 + other.getSize() / 2.0);
    }
    
    public synchronized void split() {
        if (size > 10) {
            size /= 2;
            speed *= 1.5;
        } else {
            active = false;
        }
    }
    
    @Override
    public void run() {
        while (active) {
            move();
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                break;
            }
        }
    }
}
