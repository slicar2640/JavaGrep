public class RegexMatcher {
  public RegexMatcher() {
    this.repeat = MatchRepeat.ONE;
  }
  public RegexMatcher(MatchRepeat repeat) {
    this.repeat = repeat;
  }
  public boolean test(char input) {
    return true;
  }
  public int match(String input) {
    return 0;
  }
  public int match(String input, int startIndex) {
    return startIndex;
  }
  public enum MatchRepeat {
    ONE,
    ONEORMORE,
    ZEROORONE
  }
  MatchRepeat repeat;
}
