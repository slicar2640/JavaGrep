public abstract class RegexMatcher {
  public abstract boolean test(char input);
  public abstract int match(String input);
  public abstract int match(String input, int startIndex);
  public enum MatchRepeat {
    ONE,
    ONEORMORE,
    ZEROORONE
  }
  MatchRepeat repeat;
}
