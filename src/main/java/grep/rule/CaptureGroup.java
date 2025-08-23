package grep.rule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.NoSuchElementException;

import grep.MatchContext;
import grep.Pattern;

public class CaptureGroup extends MatchRule {
  private ArrayList<MatchRule> rules = new ArrayList<>();
  boolean isMatching = false;
  int startOfMatch = 0, endOfMatch = 0;
  int captureIndex = -1;

  @Override
  public void connect(ArrayList<MatchRule> rules, int index, Pattern pattern) {
    captureIndex = pattern.numCaptureGroups;
    pattern.numCaptureGroups++;
    super.connect(rules, index, pattern);
  }

  public boolean matches(String input, int index, MatchContext context) {
    if (isMatching) {
      context.addCaptureRef(captureIndex, input.substring(startOfMatch, index));
      System.out.println("<" + input.substring(startOfMatch, index) + ">");
      System.out.println(Arrays.toString(context.captureRefs));
      boolean nextMatches = next.matches(input, index, context);
      System.out.println("nm " + nextMatches);
      context.removeCaptureRef(captureIndex);
      endOfMatch = index;
      return nextMatches;
    } else {
      isMatching = true;
      startOfMatch = index;
      boolean didMatch = false;
      if (rules.get(0).matches(input, index, context)) {
        context.lastMatch = endOfMatch;
        System.out.println("<s> " + input.substring(index, context.lastMatch));
        context.addCaptureRef(captureIndex, input.substring(index, context.lastMatch));
        System.out.println(Arrays.toString(context.captureRefs));
        didMatch = next.matches(input, context.lastMatch, context);
      }
      isMatching = false;
      return didMatch;
    }
  }

  public boolean selfMatches(String input, int index, MatchContext context) {
    for (MatchRule rule : rules) {
      if (context.lastMatch < input.length())
        System.out.println(rule + " - " + context.lastMatch + " -> " + input.charAt(context.lastMatch));
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
