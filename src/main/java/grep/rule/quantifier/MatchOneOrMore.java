package grep.rule.quantifier;

import java.util.ArrayList;

import grep.MatchContext;
import grep.Pattern;
import grep.rule.MatchRule;

public class MatchOneOrMore extends MatchRule {
  public MatchRule baseRule;

  public MatchOneOrMore(MatchRule baseRule) {
    this.baseRule = baseRule;
  }

  @Override
  public void connect(ArrayList<MatchRule> rules, int index, Pattern pattern) {
    super.connect(rules, index, pattern);
    baseRule.setNext(MatchRule.END);
  }

  @Override
  public void setNext(MatchRule rule) {
    super.setNext(rule);
    baseRule.setNext(rule);
  }

  public boolean selfMatches(String input, int index, MatchContext context) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean matches(String input, int index, MatchContext context) {
    while (index < input.length()) {
      if (baseRule.selfMatches(input, index, context)) {
        if (index == context.lastMatch) {
          return false;
        }
        index = context.lastMatch;
        if (next.matches(input, index, context)) {
          return true;
        }
      } else {
        return false;
      }
    }
    return false;
  }

  public int oneMatchLength(String input, int index, MatchContext context) {
    return -1;
  }

  public String toString() {
    return baseRule.toString() + "+";
  }
}
