import java.util.Random;

public class CharGenerator1 {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Uso: java CharGenerator <times>");
            return;
        }

        int times = Integer.parseInt(args[0]);

        int counter = 0;

        Random random = new Random();
        char[] buffer = new char[times * 4];
        int bufferIndex = 0;

        for (int i = 0; i < times; i++) {
            char[] word = randomCharArray(3, random);
            
            if (word[0] == 'I' && word[1] == 'P' && word[2] == 'N') { 
                counter++;
                System.out.println("IPN At: " + bufferIndex);
            }

            for (char c : word) {
                buffer[bufferIndex++] = c;
            }
            buffer[bufferIndex++] = ' ';
        }

        System.out.println("IPN: " + counter + " times");
    }

    public static char[] randomCharArray(int len, Random random) {
        char[] result = new char[len];
        for (int i = 0; i < len; i++) {
            result[i] = (char) (random.nextInt(26) + 'A');
        }
        return result;
    }
}
