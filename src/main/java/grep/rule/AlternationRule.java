package grep.rule;

import java.util.ArrayList;

import grep.MatchContext;

public class AlternationRule extends MatchRule {
  private ArrayList<CaptureGroup> groups = new ArrayList<>();

  public AlternationRule(CaptureGroup... groups) {
    for (CaptureGroup g : groups) {
      this.groups.add(g);
    }
  }

  @Override
  public void connect(ArrayList<MatchRule> rules, int index) {
    if (index >= rules.size()) {
      next = MatchRule.END;
    } else {
      next = rules.get(index);
    }
    System.out.println(next.toString());
    for (CaptureGroup g : groups) {
      g.setNext(next);
    }
    next.connect(rules, index + 1);
  }

  @Override
  public void setNext(MatchRule rule) {
    super.setNext(rule);
    for (CaptureGroup g : groups) {
      g.setNext(next);
    }
  }

  public boolean selfMatches(String input, int index, MatchContext context) {
    String[] captureRefs = context.captureRefs.clone();
    int lastCap = context.lastCapture;
    int lastMatch = context.lastMatch;
    for (CaptureGroup g : groups) {
      if (index < input.length())
        System.out.println(g + " / " + input.charAt(index));
      context.captureRefs = captureRefs.clone();
      context.lastCapture = lastCap;
      context.lastMatch = lastMatch;
      if (g.selfMatches(input, index, context)) {
        return true;
      }
    }
    return false;
  }

  public boolean matches(String input, int index, MatchContext context) {
    String[] captureRefs = context.captureRefs.clone();
    int lastCap = context.lastCapture;
    for (CaptureGroup g : groups) {
      context.captureRefs = captureRefs.clone();
      context.lastCapture = lastCap;
      if (g.matches(input, index, context)) {
        return true;
      }
    }
    return false;
  }

  public void addGroup(CaptureGroup group) {
    groups.add(group);
    if (next != null) {
      group.setNext(next);
    }
  }

  public void addGroup(int index, CaptureGroup group) {
    groups.add(index, group);
    if (next != null) {
      group.setNext(next);
    }
  }

  public String toString() {
    StringBuilder builder = new StringBuilder("(");
    for (int i = 0; i < groups.size(); i++) {
      builder.append(groups.get(i).toString());
      if (i < groups.size() - 1) {
        builder.append('|');
      }
    }
    builder.append(")");
    return builder.toString();
  }

  @Override
  public void toStringChain(StringBuilder builder) {
    builder.append(toString());
    if (next != null) {
      next.toStringChain(builder);
    }
  }
}
