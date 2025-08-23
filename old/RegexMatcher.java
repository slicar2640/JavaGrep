package old;

public interface RegexMatcher {
  public int firstMatchStart(String input, int startIndex);

  public Match match(String input, int startIndex);

  public MatchRepeat getRepeat();

  public enum MatchRepeat {
    ONE,
    ONEORMORE,
    ZEROORONE
  }
}
