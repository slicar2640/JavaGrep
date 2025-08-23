package old;

public class BackReferenceMatcher implements RegexMatcher {
  int referenceNum;
  Regex parent;

  public BackReferenceMatcher(int num, Regex parent) {
    this.referenceNum = num;
    this.parent = parent;
  }

  public int firstMatchStart(String input, int startIndex) {
    throw new UnsupportedOperationException("Unimplemented method 'firstMatchStart'");
  }

  public Match match(String input, int startIndex) {
    String reference = parent.captureGroups.get(referenceNum);
    for (int i = 0; i < reference.length(); i++) {
      if (input.charAt(startIndex + i) != reference.charAt(i)) {
        return Match.invalid();
      }
    }
    return new Match(startIndex, startIndex + reference.length() - 1, reference);
  }

  public MatchRepeat getRepeat() {
    return MatchRepeat.ONE;
  }
}
