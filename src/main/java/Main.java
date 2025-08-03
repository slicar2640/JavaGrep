import java.util.ArrayList;
import java.util.Scanner;

public class Main {
  public static void main(String[] args) {
    if (args.length != 2 || !args[0].equals("-E")) {
      System.out.println("Usage: ./your_program.sh -E <pattern>");
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
    ArrayList<RegexMatcher> regex = new ArrayList<>();
    boolean startAtStart = false;
    boolean endAtEnd = false;
    for (int i = 0; i < pattern.length(); i++) {
      if (pattern.charAt(i) == '\\') {
        i++;
        switch (pattern.charAt(i)) {
          case 'd':
            regex.add(new RangeMatcher("0-9"));
            break;
          case 'w':
            regex.add(new RangeMatcher("a-z", "A-Z", "0-9", "_"));
            break;
          default:
            break;
        }
      } else if (pattern.charAt(i) == '[') {
        int endIndex = pattern.indexOf(']', i);
        if (pattern.charAt(i + 1) == '^') {
          String sub = pattern.substring(i + 2, endIndex);
          regex.add(new NegativeRangeMatcher(sub));
        } else {
          String sub = pattern.substring(i + 1, endIndex);
          regex.add(new RangeMatcher(sub)); // Change to handle a-c, \d, etc.
        }
        i = endIndex;
      } else if(pattern.charAt(i) == '^') {
        startAtStart = true;
      } else if(pattern.charAt(i) == '$') {
        endAtEnd = true;
      } else {
        regex.add(new CharacterMatcher(pattern.charAt(i)));
      }
    }
    int startMatchIndex = -1;
    while (true) {
      int testIndex = startAtStart ? 0 : regex.get(0).match(inputLine, startMatchIndex + 1);
      startMatchIndex = testIndex;
      boolean matches = true;
      for (RegexMatcher matcher : regex) {
        if (testIndex == -1 || testIndex >= inputLine.length() || !matcher.test(inputLine.charAt(testIndex))) {
          matches = false;
          break;
        }
        testIndex++;
      }
      if (matches) {
        if(endAtEnd) {
          return testIndex == inputLine.length();
        } else {
          return true;
        }
      } else {
        if (testIndex == -1 || testIndex >= inputLine.length() || startAtStart) {
          return false;
        }
      }
    }
  }
}
