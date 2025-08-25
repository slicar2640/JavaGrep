import java.io.File;
import java.util.Scanner;

import grep.Pattern;
import grep.PatternCompiler;

public class Main {
  public static void main(String[] args) {
    String patternString = args[1];
    Pattern pattern = new PatternCompiler(patternString).compile();
    if (args.length == 2) {
      Scanner scanner = new Scanner(System.in);
      String inputLine = scanner.nextLine();

      if (pattern.matches(inputLine)) {
        System.exit(0);
      } else {
        System.exit(1);
      }
      scanner.close();
    } else {
      boolean foundOne = false;
      String filepath = args[2];
      try (Scanner scanner = new Scanner(new File(filepath))) {
        while (scanner.hasNextLine()) {
          String inputLine = scanner.nextLine();
          if (pattern.matches(inputLine)) {
            foundOne = true;
            System.out.println(inputLine);
          }
        }
      } catch (Exception e) {
        e.printStackTrace();
        System.exit(1);
      }
      if (foundOne) {
        System.exit(0);
      } else {
        System.exit(1);
      }
    }
  }
}
