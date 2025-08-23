package grep.rule;

import grep.MatchContext;

public class BackReference extends MatchRule {
  private int referenceIndex;

  public BackReference(int refIndex) {
    referenceIndex = refIndex;
  }

  public boolean matches(String input, int index, MatchContext context) {
    String reference = context.captureRefs[referenceIndex];
    for (int i = 0; i < reference.length(); i++) {
      if (input.charAt(index + i) != reference.charAt(i)) {
        return false;
      }
    }
    context.lastMatch = index + reference.length();
    return next.matches(input, index + reference.length(), context);
  }

  public boolean selfMatches(String input, int index, MatchContext context) {
    throw new UnsupportedOperationException("selfMatches");
  }

  public String toString() {
    return "\\" + (referenceIndex + 1);
  }
}
