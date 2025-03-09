class Rectangulo extends Figura {
  private double base, altura;

  public Rectangulo(double x, double y, double base, double altura) {
      super(x, y);
      this.base = base;
      this.altura = altura;
  }

  @Override
  public double area() {
      return base * altura;
  }

  public Coordenada[] obtenerVertices() {
      return new Coordenada[]{
          new Coordenada(centro.getX() - base / 2, centro.getY() - altura / 2),
          new Coordenada(centro.getX() + base / 2, centro.getY() - altura / 2),
          new Coordenada(centro.getX() + base / 2, centro.getY() + altura / 2),
          new Coordenada(centro.getX() - base / 2, centro.getY() + altura / 2)
      };
  }
}
