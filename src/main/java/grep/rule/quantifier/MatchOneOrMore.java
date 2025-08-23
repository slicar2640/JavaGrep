package grep.rule.quantifier;

import java.util.ArrayList;

import grep.MatchContext;
import grep.rule.MatchRule;

public class MatchOneOrMore extends MatchRule {
  private MatchRule baseRule;

  public MatchOneOrMore(MatchRule baseRule) {
    this.baseRule = baseRule;
  }

  @Override
  public void connect(ArrayList<MatchRule> rules, int index) {
    super.connect(rules, index);
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
      System.out.println(baseRule + " " + input.charAt(index));
      if (baseRule.selfMatches(input, index, context)) {
        System.out.println(next);
        if (index == context.lastMatch) {
          return false;
        }
        index = context.lastMatch;
        System.out.println("-----" + index);
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
