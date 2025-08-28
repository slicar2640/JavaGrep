package grep.rule.quantifier;

import java.util.ArrayList;

import grep.MatchContext;
import grep.Pattern;
import grep.rule.MatchRule;

public class MatchZeroOrOne extends MatchRule {
  public MatchRule baseRule;

  public MatchZeroOrOne(MatchRule baseRule) {
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
    baseRule.selfMatches(input, index, context);
    return true;
  }

  @Override
  public boolean matches(String input, int index, MatchContext context) {
    if (index >= input.length()) {
      return true;
    }
    return baseRule.selfMatches(input, index, context) && next.matches(input,
        context.lastMatch, context)
        || next.matches(input, index, context);
  }

  public String toString() {
    return baseRule.toString() + "?";
  }
}
