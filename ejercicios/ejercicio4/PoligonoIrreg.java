import java.util.ArrayList;
import java.util.Collections;

public class PoligonoIrreg {
    private ArrayList<Coordenada> vertices;

    public PoligonoIrreg() {
        vertices = new ArrayList<>();
    }

    public void anadeVertice(Coordenada v) {
        System.out.println("VERTICE NUEVO: " + v);

        vertices.add(v);
    }

    public int size() {
        return this.vertices.size();
    }

    public void modificaVertice(int index, Coordenada nuevaCoordenada) {
        if (index < 0 || index >= vertices.size()) {
            throw new IndexOutOfBoundsException("Indice fuera de rango.");
        }
        vertices.set(index, nuevaCoordenada);
    }

    public void ordenaVertices() {
        Collections.sort(vertices, new ComparadorPorMagnitud());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Poliigono Irregular con vertices:\n");
        int i = 0;
        for (Coordenada v : vertices) {
            sb.append("Vertice ").append(i++).append(": ").append(v).append("\n");
        }
        return sb.toString();
    }
}
