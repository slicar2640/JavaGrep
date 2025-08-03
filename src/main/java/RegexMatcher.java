public interface RegexMatcher {
  public boolean test(char input);
  public int match(String input);
  public int match(String input, int startIndex);
}
