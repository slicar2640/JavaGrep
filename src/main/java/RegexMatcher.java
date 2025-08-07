public abstract class RegexMatcher {
  public abstract int firstMatch(String input, int startIndex);
  public abstract int match(String input, int startIndex, Regex parent);
}
