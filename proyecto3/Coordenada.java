public class Coordenada {
  private double x, y;
  public Coordenada(double x, double y) {
      this.x = x;
      this.y = y;
  }

  public double abcisa( ) { return x; }
  
  public double ordenada( ) { return y; }

  public void setX(double x) { this.x = x; }
  
  public void setY(double y) { this.y = y; }
  
  @Override
  public String toString( ) {
      return "[" + x + "," + y + "]";
  }
}


