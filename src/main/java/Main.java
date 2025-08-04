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
      char thisChar = pattern.charAt(i);
      if (thisChar == '\\') {
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
      } else if (thisChar == '[') {
        int endIndex = pattern.indexOf(']', i);
        if (pattern.charAt(i + 1) == '^') {
          String sub = pattern.substring(i + 2, endIndex);
          regex.add(new NegativeRangeMatcher(sub));
        } else {
          String sub = pattern.substring(i + 1, endIndex);
          regex.add(new RangeMatcher(sub)); // Change to handle a-c, \d, etc.
        }
        i = endIndex;
      } else if (thisChar == '^') {
        startAtStart = true;
      } else if (thisChar == '$') {
        endAtEnd = true;
      } else if (thisChar == '.') {
        if (i + 1 < pattern.length() && pattern.charAt(i + 1) == '+') {
          regex.add(new RegexMatcher(RegexMatcher.MatchRepeat.ONEORMORE));
          i++;
        } else if (i + 1 < pattern.length() && pattern.charAt(i + 1) == '?') {
          regex.add(new RegexMatcher(RegexMatcher.MatchRepeat.ZEROORONE));
          i++;
        } else {
          regex.add(new RegexMatcher());
        }
      } else {
        if (i + 1 < pattern.length() && pattern.charAt(i + 1) == '+') {
          regex.add(new CharacterMatcher(thisChar, RegexMatcher.MatchRepeat.ONEORMORE));
          i++;
        } else if (i + 1 < pattern.length() && pattern.charAt(i + 1) == '?') {
          regex.add(new CharacterMatcher(thisChar, RegexMatcher.MatchRepeat.ZEROORONE));
          i++;
        } else {
          regex.add(new CharacterMatcher(thisChar));
        }
      }
    }
    return checkMatch(inputLine, regex, 0, 0, startAtStart, endAtEnd);
  }

  private static boolean checkMatch(String inputLine, ArrayList<RegexMatcher> regex, int startPosition,
      int startMatcher, boolean startAtStart, boolean endAtEnd) {
    int startMatchIndex = startPosition - 1;
    while (true) {
      int testIndex = startAtStart ? startPosition : regex.get(startMatcher).match(inputLine, startMatchIndex + 1);
      startMatchIndex = testIndex;
      boolean matches = true;
      matcherLoop: for (int i = startMatcher; i < regex.size(); i++) {
        RegexMatcher matcher = regex.get(i);
        switch (matcher.repeat) {
          case ONE:
            if (testIndex == -1 || testIndex >= inputLine.length() || !matcher.test(inputLine.charAt(testIndex))) {
              matches = false;
              break matcherLoop;
            }
            testIndex++;
            break;
          case ONEORMORE:
            if (testIndex == -1) { // Should only happen if this is first and doesn't match anywhere
              matches = false;
              break matcherLoop;
            }
            int startOfMatch = testIndex;
            for (int j = startOfMatch; j < inputLine.length(); j++) {
              if (matcher.test(inputLine.charAt(j))) {
                if (checkMatch(inputLine, regex, j + 1, i + 1, true, endAtEnd)) {
                  testIndex = j + 1;
                  continue matcherLoop;
                }
              } else {
                if (j - startOfMatch == 0) {
                  matches = false;
                  break matcherLoop;
                }
              }
            }
            break;
          case ZEROORONE:
            boolean worksWithOne = testIndex != -1 && matcher.test(inputLine.charAt(i));
            if (worksWithOne && checkMatch(inputLine, regex, testIndex + 1, startMatcher + 1, true, endAtEnd)) {
              testIndex++;
            }
            break;
        }
      }
      if (matches) {
        if (endAtEnd) {
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
