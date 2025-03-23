import java.util.Random;

public class PoligonoIrreg {
  private Coordenada[] vertices;

  public PoligonoIrreg(int numVertices) {
      if (numVertices <= 0) {
          throw new IllegalArgumentException("El número de vértices debe ser mayor que 0.");
      }

      vertices = new Coordenada[numVertices];
      Random rand = new Random();

      for (int i = 0; i < numVertices; i++) {
          double x = -10 + (20 * rand.nextDouble());
          double y = -10 + (20 * rand.nextDouble());
          vertices[i] = new Coordenada(x, y);
      }
  }

  public void modificaVertice(int index, Coordenada nuevaCoordenada) {
      if (index < 0 || index >= vertices.length) {
          throw new IndexOutOfBoundsException("Índice fuera de rango.");
      }
      vertices[index] = nuevaCoordenada;
  }

  @Override
  public String toString() {
      StringBuilder sb = new StringBuilder("Polígono Irregular con vértices:\n");
      for (int i = 0; i < vertices.length; i++) {
          sb.append("Vértice ").append(i).append(": ").append(vertices[i]).append("\n");
      }
      return sb.toString();
  }
}
