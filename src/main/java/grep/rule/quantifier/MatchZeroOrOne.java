package grep.rule.quantifier;

import java.util.ArrayList;

import grep.MatchContext;
import grep.rule.MatchRule;

public class MatchZeroOrOne extends MatchRule {
  private MatchRule baseRule;

  public MatchZeroOrOne(MatchRule baseRule) {
    this.baseRule = baseRule;
  }

  @Override
  public void connect(ArrayList<MatchRule> rules, int index) {
    super.connect(rules, index);
    baseRule.setNext(MatchRule.END);
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
    // boolean nextMatches;
    // if (baseRule.selfMatches(input, index, context)) {
    // System.out.println("A");
    // nextMatches = next.matches(input, context.lastMatch, context);
    // } else {
    // System.out.println("B");
    // nextMatches = next.matches(input, index, context);
    // }
    // System.out.println(nextMatches);
    // return nextMatches;
  }

  public String toString() {
    return baseRule.toString() + "?";
  }
}
