public class NegativeRangeMatcher extends RangeMatcher {
  public NegativeRangeMatcher(String... ranges) {
    super(ranges);
  }

  public NegativeRangeMatcher(MatchRepeat repeat, String... ranges) {
    super(repeat, ranges);
  }

  @Override
  public boolean test(char input) {
    return !matchedChars.contains(input);
  }

  @Override
  public int match(String input) {
    for(int i = 0; i < input.length(); i++) {
      if(!matchedChars.contains(input.charAt(i))) {
        return i;
      }
    }
    return -1;
  }

  @Override
  public int match(String input, int startIndex) {
    for(int i = startIndex; i < input.length(); i++) {
      if(!matchedChars.contains(input.charAt(i))) {
        return i;
      }
    }
    return -1;
  }
}
