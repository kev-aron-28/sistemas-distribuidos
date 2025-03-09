class Coordenada {
  private double x, y;

  public Coordenada(double x, double y) {
      this.x = x;
      this.y = y;
  }

  public double getX() { return x; }
  public double getY() { return y; }

  public void setX(double x) { this.x = x; }
  public void setY(double y) { this.y = y; }

  public void mover(double dx, double dy) {
      this.x += dx;
      this.y += dy;
  }

  @Override
  public String toString() {
      return "(" + x + ", " + y + ")";
  }
}
