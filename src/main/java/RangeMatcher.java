import java.util.HashSet;

public class RangeMatcher implements RegexMatcher {
  static final String lowercaseAlphabet = "abcdefghijklmnopqrstuvwxyz";
  static final String uppercaseAlphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
  static final String digits = "0123456789";
  HashSet<Character> matchedChars = new HashSet<>();

  public RangeMatcher(String... ranges) {
    String finalRange = "";
    for (String range : ranges) {
      if (range.contains("-")) {
        String start = String.valueOf(range.charAt(0));
        String end = String.valueOf(range.charAt(2));
        String correctRange;
        if (lowercaseAlphabet.contains(start)) {
          correctRange = lowercaseAlphabet;
        } else if (uppercaseAlphabet.contains(start)) {
          correctRange = uppercaseAlphabet;
        } else if (digits.contains(start)) {
          correctRange = digits;
        } else {
          throw new IllegalArgumentException("Invalid Range: " + range);
        }
        if (!correctRange.contains(end)) {
          throw new IllegalArgumentException("Invalid Range: " + range);
        }
        finalRange += correctRange.substring(correctRange.indexOf(start),correctRange.indexOf(end) + 1);
      } else {
        finalRange += range;
      }
    }
    char[] charArray = finalRange.toCharArray();
    for(char c : charArray) {
      matchedChars.add(c);
    }
  }

  public boolean test(char input) {
    return matchedChars.contains(input);
  }

  public int match(String input) {
    for(int i = 0; i < input.length(); i++) {
      if(matchedChars.contains(input.charAt(i))) {
        return i;
      }
    }
    return -1;
  }

  public int match(String input, int startIndex) {
    for(int i = startIndex; i < input.length(); i++) {
      if(matchedChars.contains(input.charAt(i))) {
        return i;
      }
    }
    return -1;
  }
}
