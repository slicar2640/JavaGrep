package grep.rule;

import java.util.ArrayList;

import grep.MatchContext;
import grep.Pattern;

public abstract class MatchRule {
  public MatchRule next;
  public Pattern pattern;
  public static final MatchRule END = new MatchRule() {
    public boolean selfMatches(String input, int index, MatchContext context) {
      return true;
    }

    public String toString() {
      return "~~";
    }

    @Override
    public void setNext(MatchRule rule) {
      next = null;
    }

    @Override
    public void connect(ArrayList<MatchRule> rules, int index, Pattern pattern) {
      this.pattern = pattern;
      next = null;
    }
  };

  public abstract boolean selfMatches(String input, int index, MatchContext context);

  public boolean matches(String input, int index, MatchContext context) {
    if (selfMatches(input, index, context)) {
      if (next != null) {
        return next.matches(input, context.lastMatch, context);
      } else {
        return true;
      }
    } else {
      return false;
    }
  }

  public void connect(ArrayList<MatchRule> rules, int index, Pattern pattern) {
    this.pattern = pattern;
    if (index >= rules.size()) {
      next = END;
      return;
    }
    next = rules.get(index);
    next.connect(rules, index + 1, pattern);
  }

  public void setNext(MatchRule rule) {
    next = rule;
  }

  public abstract String toString();

  public void toStringChain(StringBuilder builder) {
    builder.append(toString());
    if (next != null) {
      next.toStringChain(builder);
    }
  }

  public String toStringChain() {
    StringBuilder builder = new StringBuilder();
    toStringChain(builder);
    return builder.toString();
  }
}