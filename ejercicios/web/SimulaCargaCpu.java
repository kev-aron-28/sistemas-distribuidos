import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class SimulaCargaCpu implements Runnable {
    private final int porcentaje;
    private final int duracion;

    public SimulaCargaCpu(int porcentaje, int duracion) {
        this.porcentaje = porcentaje;
        this.duracion = duracion;
    }

    @Override
    public void run() {
        Random ran = new Random();
        for (int segundo = 0; segundo < duracion; segundo++) {
            ArrayList<Boolean> milisegundos = new ArrayList<>(1000);
            for (int i = 0; i < porcentaje; i++) milisegundos.add(true);
            for (int i = porcentaje; i < 1000; i++) milisegundos.add(false);
            Collections.shuffle(milisegundos);
            for (int i = 0; i < 1000; i++) {
                long inicio = System.currentTimeMillis();
                if (milisegundos.get(i)) {
                    while (System.currentTimeMillis() - inicio < 1) {
                        Math.sqrt(ran.nextInt(Integer.MAX_VALUE));
                    }
                } else {
                    while (System.currentTimeMillis() - inicio < 1) {
                        Thread.yield();
                    }
                }
            }
        }
    }

    public static void ejecutarSimulacion(int porcentaje, int segundos, int hilos) {
        for (int i = 0; i < hilos; i++) {
            new Thread(new SimulaCargaCpu(porcentaje, segundos)).start();
        }
    }
}
