public class RangeMatcher extends RegexMatcher {
  static final String lowercaseAlphabet = "abcdefghijklmnopqrstuvwxyz";
  static final String uppercaseAlphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
  static final String digits = "0123456789";
  String matchedChars;

  public RangeMatcher(String chars) {
    matchedChars = chars;
  }

  public boolean test(char input) {
    return matchedChars.contains("" + input);
  }

  public int match(String input) {
    int firstIndex = Integer.MAX_VALUE;
    for (int i = 0; i < matchedChars.length(); i++) {
      int index = input.indexOf(matchedChars.charAt(i));
      if (index >= 0) {
        firstIndex = Math.min(firstIndex, index);
      }
    }
    if (firstIndex == Integer.MAX_VALUE) {
      return -1;
    } else {
      return firstIndex;
    }
  }

  public static RangeMatcher fromRanges(String... ranges) {
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
    return new RangeMatcher(finalRange);
  }
}
