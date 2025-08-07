import java.util.ArrayList;

public class MatcherSequence {
  public ArrayList<RegexMatcher> matcherList = new ArrayList<>();
  public boolean startAtStart;
  public boolean endAtEnd;

  public MatcherSequence(ArrayList<RegexMatcher> matchers) {
    matcherList = matchers;
  }

  public MatcherSequence() {
    matcherList = new ArrayList<>();
  }

  public void addMatcher(RegexMatcher matcher) {
    matcherList.add(matcher);
  }

  public RegexMatcher getMatcher(int index) {
    return matcherList.get(index);
  }

  public String toString() {
    String ret = startAtStart ? "^" : "";
    for(RegexMatcher matcher : matcherList) {
      ret += matcher.toString();
    }
    return ret + (endAtEnd ? "$" : "");
  }
}
