public class NegativeRangeMatcher extends RangeMatcher {
  public NegativeRangeMatcher(String... ranges) {
    super(ranges);
  }

  @Override
  public boolean test(char input) {
    return !matchedChars.contains("" + input);
  }

  @Override
  public int match(String input) {
    for(int i = 0; i < input.length(); i++) {
      if(!matchedChars.contains("" + input.charAt(i))) {
        return i;
      }
    }
    return -1;
  }
}
