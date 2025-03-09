public class Rectangulo {
    private Coordenada superiorIzq, inferiorDer;

    public Rectangulo() {
        superiorIzq = new Coordenada(0, 0);
        inferiorDer = new Coordenada(0, 0);
    }

    public Rectangulo(double xSupIzq, double ySupIzq, double xInfDer, double yInfDer) {
        if (xSupIzq >= xInfDer || ySupIzq <= yInfDer) {
            throw new IllegalArgumentException("Coordenadas inválidas: la superior izquierda debe estar arriba y a la izquierda.");
        }
        superiorIzq = new Coordenada(xSupIzq, ySupIzq);
        inferiorDer = new Coordenada(xInfDer, yInfDer);
    }

    public Rectangulo(Coordenada supIzq, Coordenada infDer) {
        if (supIzq.abcisa() >= infDer.abcisa() || supIzq.ordenada() <= infDer.ordenada()) {
            throw new IllegalArgumentException("Coordenadas inválidas: la superior izquierda debe estar arriba y a la izquierda.");
        }
        this.superiorIzq = supIzq;
        this.inferiorDer = infDer;
    }

    public Coordenada superiorIzquierda() {
        return superiorIzq;
    }

    public Coordenada inferiorDerecha() {
        return inferiorDer;
    }

    @Override
    public String toString() {
        return "Esquina superior izquierda: " + superiorIzq + "\tEsquina inferior derecha: " + inferiorDer + "\n";
    }
}
