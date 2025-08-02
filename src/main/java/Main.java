import java.util.ArrayList;
import java.util.Scanner;

public class Main {
  public static void main(String[] args){
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
    for(int i = 0; i < pattern.length(); i++) {
      if(pattern.charAt(i) == '\\') {
        i++;
        switch (pattern.charAt(i)) {
          case 'd':
            regex.add(RangeMatcher.fromRanges("0-9"));
            break;
          case 'w':
            regex.add(RangeMatcher.fromRanges("a-z", "A-Z", "0-9", "_"));
          break;
          default:
            break;
        }
      } else {
        regex.add(new CharacterMatcher(pattern.charAt(i)));
      }
    }
    for(RegexMatcher matcher : regex) {
      if(matcher.match(inputLine) >= 0) {
        return true;
      }
    }
    return false;
  }
}
