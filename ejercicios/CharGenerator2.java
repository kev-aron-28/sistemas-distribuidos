import java.util.Random;

public class CharGenerator2 {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Uso: java CharGenerator <times>");
            return;
        }

        int times = Integer.parseInt(args[0]);

        int counter = 0;

        Random random = new Random();
        StringBuilder buffer = new StringBuilder();
        int bufferPosition = 0;
        

        for (int i = 0; i < times; i++) {
            String word = randomString(3, random);
            
            if(word.contains("IPN")) {
                counter++;
                System.out.println("IPN At: " + bufferPosition);
            }

            buffer.append(" ").append(word);
            bufferPosition += word.length() + 1;
        }

        System.out.println("IPN: " + counter + " times");
    }

    public static String randomString(int len, Random random) {
        StringBuilder result = new StringBuilder();
        
        for (int i = 0; i < len; i++) {
            char character = (char) (random.nextInt(26) + 'A');

            result.append(character);
        }

        return result.toString();
    }
}
