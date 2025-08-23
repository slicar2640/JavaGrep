package grep.rule;

import grep.MatchContext;

public class StartAnchor extends MatchRule {
  public boolean selfMatches(String input, int index, MatchContext context) {
    if (index == 0) {
      context.lastMatch = 0;
      return true;
    } else {
      return false;
    }
  }

  public String toString() {
    return "^";
  }
}
