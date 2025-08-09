public class Match {
  int startIndex;
  int endIndex;
  String match;
  boolean isValid;

  public Match(int start, int end, String match, boolean valid) {
    this.startIndex = start;
    this.endIndex = end;
    this.match = match;
    this.isValid = valid;
  }

  public Match(int start, int end, String match) {
    this(start, end, match, true);
  }

  public Match(String source, int start, int end) {
    this(start, end, source.substring(start, end + 1));
  }

  static Match invalid() {
    return new Match(0, 0, "", false);
  }

  static Match empty(int index) {
    return new Match(index, index, "", true);
  }
}
