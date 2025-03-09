class TrianguloEq extends Figura {
  private double lado;

  public TrianguloEq(double x, double y, double lado) {
      super(x, y);
      this.lado = lado;
  }

  @Override
  public double area() {
      return (Math.sqrt(3) / 4) * lado * lado;
  }

  public Coordenada[] obtenerVertices() {
      double h = (Math.sqrt(3) / 2) * lado;
      return new Coordenada[]{
          new Coordenada(centro.getX(), centro.getY() + 2 * h / 3),
          new Coordenada(centro.getX() - lado / 2, centro.getY() - h / 3),
          new Coordenada(centro.getX() + lado / 2, centro.getY() - h / 3)
      };
  }
}
