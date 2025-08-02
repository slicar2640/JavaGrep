public class RangeMatcher extends RegexMatcher {
  String matchedChars;
  public RangeMatcher(String chars) {
    matchedChars = chars;
  }

  public boolean test(char input) {
    return matchedChars.contains("" + input);
  }

  public int match(String input) {
    int firstIndex = Integer.MAX_VALUE;
    for(int i = 0; i < matchedChars.length(); i++) {
      int index = input.indexOf(matchedChars.charAt(i));
      if(index >= 0) {
        firstIndex = Math.min(firstIndex, index);
      }
    }
    if(firstIndex == Integer.MAX_VALUE) {
      return -1;
    } else {
      return firstIndex;
    }
  }
}
