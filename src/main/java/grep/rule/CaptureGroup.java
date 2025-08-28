package grep.rule;

import java.util.ArrayList;
import java.util.NoSuchElementException;

import grep.MatchContext;
import grep.Pattern;

public class CaptureGroup extends MatchRule {
  private ArrayList<MatchRule> rules = new ArrayList<>();
  boolean isMatching = false;
  int startOfMatch = 0, endOfMatch = 0;
  int captureIndex = -1;

  public boolean matches(String input, int index, MatchContext context) {
    if (isMatching) { // Called once all of this group's rules have checked themselves
      context.addCaptureRef(captureIndex, input.substring(startOfMatch, index));
      boolean nextMatches = next.matches(input, index, context);
      endOfMatch = index;
      return nextMatches;
    } else {
      isMatching = true;
      startOfMatch = index;
      boolean didMatch = false;
      if (rules.get(0).matches(input, index, context)) {
        context.lastMatch = endOfMatch; // Set in above case (end of rules list)
        context.addCaptureRef(captureIndex, input.substring(index, context.lastMatch));
        didMatch = next.matches(input, context.lastMatch, context);
      }
      isMatching = false;
      return didMatch;
    }
  }

  public boolean selfMatches(String input, int index, MatchContext context) {
    for (MatchRule rule : rules) {
      if (context.lastMatch < input.length())
        if (!rule.selfMatches(input, context.lastMatch, context)) {
          return false;
        }
    }
    return true;
  }

  public void addRule(MatchRule rule) {
    if (!rules.isEmpty()) {
      rules.getLast().setNext(rule);
    }
    rules.add(rule);
    rule.setNext(this);
  }

  public void setCaptureIndex(Pattern pattern) {
    captureIndex = pattern.numCaptureGroups;
    pattern.numCaptureGroups++;
    for (MatchRule rule : rules) {
      if (rule instanceof CaptureGroup cap) {
        cap.setCaptureIndex(pattern);
      }
    }
  }

  public MatchRule getLastRule() {
    return rules.getLast();
  }

  public MatchRule removeLastRule() {
    if (rules.isEmpty()) {
      throw new NoSuchElementException("Capture Group is empty");
    }
    return rules.removeLast();
  }

  public String toString() {
    StringBuilder builder = new StringBuilder("(");
    for (MatchRule rule : rules) {
      builder.append(rule.toString());
    }
    builder.append(")");
    return builder.toString();
  }
}
