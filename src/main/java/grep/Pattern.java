package grep;

import java.util.ArrayList;

import grep.rule.MatchRule;

public class Pattern {
  public MatchRule rootRule;

  public Pattern(ArrayList<MatchRule> rules) {
    rootRule = rules.get(0);
    rootRule.connect(rules, 1);
  }

  public boolean matches(String input) {
    for (int i = 0; i < input.length(); i++) {
      MatchContext context = new MatchContext(input);
      if (rootRule.matches(input, i, context)) {
        return true;
      }
    }
    return false;
  }

  public String toString() {
    return rootRule.toStringChain();
  }
}
