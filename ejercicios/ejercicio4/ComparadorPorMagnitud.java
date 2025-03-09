import java.util.Comparator;

public class ComparadorPorMagnitud implements Comparator<Coordenada> {
  @Override
  public int compare(Coordenada c1, Coordenada c2) {
      return Double.compare(c1.getMagnitud(), c2.getMagnitud());
  }
}
