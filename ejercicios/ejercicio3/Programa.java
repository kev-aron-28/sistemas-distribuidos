public class Programa {
  public static void main(String[] args) {
      TrianguloEq t = new TrianguloEq(0, 0, 4);
      Rectangulo r = new Rectangulo(5, 5, 6, 3);

      // Area del triangulo

      System.out.println("Triangulo - area: " + t.area());
      System.out.println("Vertices iniciales: ");
      for (Coordenada v : t.obtenerVertices()) {
          System.out.println(v);
      }

      // Area del rectangulo

      System.out.println("\nRectangulo - area: " + r.area());
      System.out.println("Vertices iniciales: ");
      for (Coordenada v : r.obtenerVertices()) {
          System.out.println(v);
      }

      // Desplazar figuras
      t.desplazar(3, 3);
      r.desplazar(-2, -1);

      System.out.println("\nDespues de desplazar:");
      System.out.println("Triángulo - Nueva posicion y vertices:");
      System.out.println("Triangulo - area: " + t.area());
      for (Coordenada v : t.obtenerVertices()) {
          System.out.println(v);
      }

      System.out.println("\nRectangulo - Nueva posicion y vertices:");
      System.out.println("\nRectangulo - area: " + r.area());
      for (Coordenada v : r.obtenerVertices()) {
          System.out.println(v);
      }
  }
}

