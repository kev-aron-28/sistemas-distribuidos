import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class Main {
    static int num = 3;

    public static void main(String[] args) {
        int times;
        try {
            times = Integer.parseInt(args[0]);
            if (times % 5 != 0) {
                System.err.println("Error: El numero debe ser multiplo de 5.");
                return;
            }
        } catch (NumberFormatException e) {
            System.err.println("Error: El primer argumento debe ser un numero entero.");
            return;
        }

        ArrayList<String> aleatorios = new ArrayList<>();
        for (int i = 0; i < times; i++) {
            aleatorios.add(getCURP());
        }

        System.out.println("LISTA DE CURPS ALEATORIOS: ");
        System.out.println(aleatorios);

        ExecutorService pool = Executors.newFixedThreadPool(5);
        int range = times / 5;

        for (int i = 0; i < 5; i++) {
            pool.execute(new Task(i + 1, aleatorios, i * range, (i + 1) * range));
        }

        pool.shutdown();
    }

    static String getCURP() {
        String Letra = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String Numero = "0123456789";
        String Sexo = "HM";
        String Entidad[] = {"AS", "BC", "BS", "CC", "CS", "CH", "CL", "CM", "DF", "DG", "GT", "GR", "HG", "JC", "MC", "MN", "MS", "NT", "NL", "OC", "PL", "QT", "QR", "SP", "SL", "SR", "TC", "TL", "TS", "VZ", "YN", "ZS"};
        int indice;

        StringBuilder sb = new StringBuilder(18);

        for (int i = 1; i < 5; i++) {
            indice = (int) (Letra.length() * Math.random());
            sb.append(Letra.charAt(indice));
        }

        for (int i = 5; i < 11; i++) {
            indice = (int) (Numero.length() * Math.random());
            sb.append(Numero.charAt(indice));
        }

        indice = (int) (Sexo.length() * Math.random());
        sb.append(Sexo.charAt(indice));

        sb.append(Entidad[(int) (Math.random() * 32)]);

        for (int i = 14; i < 17; i++) {
            indice = (int) (Letra.length() * Math.random());
            sb.append(Letra.charAt(indice));
        }

        for (int i = 17; i < 19; i++) {
            indice = (int) (Numero.length() * Math.random());
            sb.append(Numero.charAt(indice));
        }

        return sb.toString();
    }
}

class Task implements Runnable {
    private int taskId;
    private ArrayList<String> aleatorios;
    private int start;
    private int end;

    public Task(int taskId, ArrayList<String> aleatorios, int start, int end) {
        this.taskId = taskId;
        this.aleatorios = aleatorios;
        this.start = start;
        this.end = end;
    }

    @Override
    public void run() {
        ArrayList<String> ordenados = new ArrayList<>(aleatorios.subList(start, end));
        ArrayList<String> resultado = new ArrayList<>();

        for (String curp : ordenados) {
            if (resultado.isEmpty() || curp.compareTo(resultado.get(resultado.size() - 1)) >= 0) {
                resultado.add(curp);
            } else {
                for (int j = 0; j < resultado.size(); j++) {
                    if (curp.compareTo(resultado.get(j)) < 0) {
                        resultado.add(j, curp);
                        break;
                    }
                }
            }
        }
        System.out.println("Tarea " + taskId + " CURPs ordenados: " + resultado);
    }
}
