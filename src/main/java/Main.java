import java.util.Scanner;

import grep.Pattern;
import grep.PatternCompiler;

public class Main {
  public static void main(String[] args) {
    if (args.length != 2 || !args[0].equals("-E")) {
      System.err.println("Usage: ./your_program.sh -E <pattern>");
      System.exit(1);
    }

    String pattern = args[1];
    Scanner scanner = new Scanner(System.in);
    String inputLine = scanner.nextLine();

    if (matchPattern(inputLine, pattern)) {
      System.exit(0);
    } else {
      System.exit(1);
    }
    scanner.close();
  }

  public static boolean matchPattern(String inputLine, String pattern) {
    try {
      Pattern regex = new PatternCompiler(pattern).compile();
      return regex.matches(inputLine);
    } catch (StackOverflowError e) {
      System.err.println(e.getClass().getName());
      System.err.println(e.getStackTrace()[0]);
      return false;
    }
  }
}
