package grep;

import java.util.ArrayList;
import java.util.List;
import java.util.ArrayDeque;

import grep.rule.*;
import grep.rule.quantifier.*;

public class PatternCompiler {
  String pattern;
  List<MatchRule> compiledRules;
  ArrayDeque<State> state;
  ArrayDeque<CaptureGroup> groupStack;
  int alternationNum;
  int position;

  public PatternCompiler(String pattern) {
    this.pattern = pattern;
    compiledRules = new ArrayList<>();
    state = new ArrayDeque<>();
    state.push(State.DEFAULT);
    groupStack = new ArrayDeque<>();
    alternationNum = 1;
    position = 0;
  }

  public Pattern compile() {
    while (position < pattern.length()) {
      char curChar = pattern.charAt(position);
      switch (curChar) {
        case '\\':
          position++;
          if (pattern.charAt(position) == 'd') {
            addRule(CharacterRule.anyDigit());
          } else if (pattern.charAt(position) == 'w') {
            addRule(CharacterRule.anyWordCharacter());
          } else if (CharacterRule.isDigit(pattern.charAt(position))) {
            addRule(new BackReference(pattern.charAt(position) - '1'));
          }
          break;
        case '^':
          addRule(new StartAnchor());
          break;
        case '$':
          addRule(new EndAnchor());
          break;
        case '[':
          position++;
          boolean positive = true;
          if (pattern.charAt(position) == '^') {
            positive = false;
            position++;
          }
          int closeBracket = pattern.indexOf(']', position);
          addRule(new CharacterRule(pattern.substring(position, closeBracket), positive));
          position = closeBracket;
          break;
        case '(': {
          state.push(State.IN_PARENTHESES);
          groupStack.push(new CaptureGroup());
          break;
        }
        case ')':
          addRule(groupStack.pop());
          if (state.peek() == State.IN_ALTERNATION) {
            AlternationRule alternation = new AlternationRule();
            for (int i = 0; i < alternationNum; i++) {
              MatchRule last = removeLastRule();
              if (last instanceof CaptureGroup g) {
                alternation.addGroup(0, g);
              } else {
                throw new RuntimeException("Last Rule " + last.getClass().getName() + " should be a group");
              }
            }
            addRule(alternation);
            alternationNum = 1;
          }
          state.pop();
          break;
        case '|':
          if (state.peek() == State.IN_PARENTHESES) {
            state.pop();
            state.push(State.IN_ALTERNATION);
          }
          addRule(groupStack.pop());
          groupStack.push(new CaptureGroup());
          alternationNum++;
          break;
        case '.':
          addRule(CharacterRule.anyCharacter());
          break;
        case '+':
          addRule(new MatchOneOrMore(removeLastRule()));
          break;
        case '?':
          addRule(new MatchZeroOrOne(removeLastRule()));
          break;
        default:
          addRule(new CharacterRule("" + curChar, true));
          break;
      }

      position++;
    }
    return new Pattern(new ArrayList<>(compiledRules));
  }

  private void addRule(MatchRule rule) {
    if (groupStack.isEmpty()) {
      compiledRules.add(rule);
    } else {
      groupStack.peek().addRule(rule);
    }
  }

  private MatchRule removeLastRule() {
    if (groupStack.isEmpty()) {
      return compiledRules.removeLast();
    } else {
      return groupStack.peek().removeLastRule();
    }
  }

  private enum State {
    DEFAULT,
    IN_PARENTHESES,
    IN_ALTERNATION
  }
}
