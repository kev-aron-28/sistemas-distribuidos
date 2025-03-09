import java.util.Random;

class Programa {
  public static void main(String[] args) {
    PoligonoIrreg poligono = new PoligonoIrreg();
    Random rand = new Random();

    for (int i = 0; i < 7; i++) {
        double x = -100 + (100 * rand.nextDouble());
        double y = -100 + (100 * rand.nextDouble());
        poligono.anadeVertice(new Coordenada(x, y));
    }

    System.out.println("Antes de ordenar:");
    System.out.println(poligono);

    poligono.ordenaVertices();

    System.out.println("Después de ordenar:");
    System.out.println(poligono);
  }
}
