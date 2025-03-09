import java.util.ArrayList;

class Main
{
    static int num = 3;
    public static void main(String[] args)
    {                 
      int times;
      try {
          times = Integer.parseInt(args[0]);
      } catch (NumberFormatException e) {
          System.err.println("Error: El primer argumento debe ser un número entero.");
          return;
      }

      ArrayList<String> list = new ArrayList<>();

      String currentCurp = getCURP();
      System.out.println("NUEVA CURP GENERADA= " + currentCurp);

      list.add(currentCurp);

      System.err.println("LA LISTA DE CURPS ES: ");
      System.err.println(list);

      for (int i = 1; i < times; i++) {
          String lastCurp = list.get(list.size() - 1);
          String tempCurp = getCURP();

          System.out.println("NUEVA CURP GENERADA= " + tempCurp);

          if (tempCurp.compareTo(lastCurp) < 0) {
              list.add(list.size() - 1, tempCurp);
          } else {
              list.add(tempCurp);
          }
          System.err.println("LA LISTA DE CURPS ES: ");
          System.out.println(list);
      }
    }
    static String getCURP()
    {
        String Letra = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String Numero = "0123456789";
        String Sexo = "HM";
        String Entidad[] = {"AS", "BC", "BS", "CC", "CS", "CH", "CL", "CM", "DF", "DG", "GT", "GR", "HG", "JC", "MC", "MN", "MS", "NT", "NL", "OC", "PL", "QT", "QR", "SP", "SL", "SR", "TC", "TL", "TS", "VZ", "YN", "ZS"};
        int indice;
        
        StringBuilder sb = new StringBuilder(18);
        
        for (int i = 1; i < 5; i++) {
            indice = (int) (Letra.length()* Math.random());
            sb.append(Letra.charAt(indice));        
        }
        
        for (int i = 5; i < 11; i++) {
            indice = (int) (Numero.length()* Math.random());
            sb.append(Numero.charAt(indice));        
        }
        indice = (int) (Sexo.length()* Math.random());
        sb.append(Sexo.charAt(indice));        
        
        sb.append(Entidad[(int)(Math.random()*32)]);
        for (int i = 14; i < 17; i++) {
            indice = (int) (Letra.length()* Math.random());
            sb.append(Letra.charAt(indice));        
        }
        for (int i = 17; i < 19; i++) {
            indice = (int) (Numero.length()* Math.random());
            sb.append(Numero.charAt(indice));        
        }
        
        return sb.toString();
    }           
}
