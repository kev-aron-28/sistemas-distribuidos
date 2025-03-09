abstract class Figura implements Desplazable {
  protected Coordenada centro;

  public Figura(double x, double y) {
      this.centro = new Coordenada(x, y);
  }

  public abstract double area();

  @Override
  public void desplazar(double dx, double dy) {
      centro.mover(dx, dy);
  }

  public Coordenada getCentro() {
      return centro;
  }
}
