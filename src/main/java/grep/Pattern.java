package grep;

import java.util.ArrayList;

import grep.rule.CaptureGroup;
import grep.rule.MatchRule;
import grep.rule.quantifier.MatchOneOrMore;
import grep.rule.quantifier.MatchZeroOrOne;

public class Pattern {
  public MatchRule rootRule;
  public int numCaptureGroups = 0;

  public Pattern(ArrayList<MatchRule> rules) {
    rootRule = rules.get(0);
    rootRule.connect(rules, 1, this);
    for (MatchRule rule : rules) {
      if (rule instanceof CaptureGroup cap) {
        cap.setCaptureIndex(this);
      } else if (rule instanceof MatchOneOrMore mat) {
        if (mat.baseRule instanceof CaptureGroup cap) {
          cap.setCaptureIndex(this);
        }
      } else if (rule instanceof MatchZeroOrOne mat) {
        if (mat.baseRule instanceof CaptureGroup cap) {
          cap.setCaptureIndex(this);
        }
      }
    }
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
