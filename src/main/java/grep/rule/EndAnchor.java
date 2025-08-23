package grep.rule;

import grep.MatchContext;

public class EndAnchor extends MatchRule {
  public boolean selfMatches(String input, int index, MatchContext context) {
    return index == input.length();
  }

  public String toString() {
    return "$";
  }
}
