import java.util.HashSet;

public class CharacterMatcher implements RegexMatcher {
  static final String lowercaseAlphabet = "abcdefghijklmnopqrstuvwxyz";
  static final String uppercaseAlphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
  static final String digits = "0123456789";
  HashSet<Character> matchedChars = new HashSet<>();
  boolean isNegative;
  Regex parent;
  RegexMatcher.MatchRepeat repeat;

  public CharacterMatcher(String chars, RegexMatcher.MatchRepeat repeat, boolean negative, Regex parent) {
    this.repeat = repeat;
    this.isNegative = negative;
    this.parent = parent;
    String finalRange = "";
    for (int i = 0; i < chars.length(); i++) {
      char thisChar = chars.charAt(i);
      if (thisChar == '\\') {
        i++;
        switch (chars.charAt(i)) {
          case 'd':
            finalRange += digits;
            break;
          case 'w':
            finalRange += lowercaseAlphabet + uppercaseAlphabet + digits + "_";
            break;
        }
      } else {
        if (i < chars.length() - 1 && chars.charAt(i + 1) == '-') {
          String correctRange;
          if (lowercaseAlphabet.indexOf(thisChar) >= 0) {
            correctRange = lowercaseAlphabet;
          } else if (uppercaseAlphabet.indexOf(thisChar) >= 0) {
            correctRange = uppercaseAlphabet;
          } else if (digits.indexOf(thisChar) >= 0) {
            correctRange = digits;
          } else {
            throw new IllegalArgumentException(
                "Invalid range in char string \"" + chars + "\" [" + chars.substring(i, i + 3) + "]");
          }
          finalRange += correctRange.substring(correctRange.indexOf(thisChar),
              correctRange.indexOf(chars.charAt(i + 2)));
        } else {
          finalRange += thisChar;
        }
      }
    }
    for (int i = 0; i < finalRange.length(); i++) {
      matchedChars.add(finalRange.charAt(i));
    }
  }

  public CharacterMatcher(String chars, boolean negative, Regex parent) {
    this(chars, RegexMatcher.MatchRepeat.ONE, negative, parent);
  }

  public CharacterMatcher(String chars, RegexMatcher.MatchRepeat repeat, Regex parent) {
    this(chars, repeat, false, parent);
  }

  public CharacterMatcher(String chars, Regex parent) {
    this(chars, RegexMatcher.MatchRepeat.ONE, false, parent);
  }

  public boolean test(char input) {
    return matchedChars.contains(input) ^ isNegative;
  }

  public int firstMatchStart(String input, int startIndex) {
    for (int i = startIndex; i < input.length(); i++) {
      if (test(input.charAt(i))) {
        return i;
      }
    }
    return -1;
  }

  public Match match(String input, int startIndex) {
    switch (repeat) {
      case ONE:
        if (test(input.charAt(startIndex))) {
          return new Match(input, startIndex, startIndex);
        } else {
          return Match.invalid();
        }

      case ONEORMORE:
        for (int i = startIndex; i < input.length(); i++) {
          if (!test(input.charAt(startIndex))) {
            break;
          }
          if (lastOfParentSequence()) {

          } else {
            if (i < input.length() - 1
                && parent.lookAhead(input, i + 1, parent.currentSequence().indexOf(this) + 1)) {
              return new Match(input, startIndex, i);
            }
          }
        }
        return Match.invalid();

      case ZEROORONE:
        if (lastOfParentSequence()) {
          if (test(input.charAt(startIndex))) {
            return new Match(input, startIndex, startIndex);
          } else {
            return Match.empty(startIndex);
          }
        } else {
          if (parent.lookAhead(input, startIndex, parent.currentSequence().indexOf(this) + 1)) {
            return Match.empty(startIndex);
          } else {
            if (test(input.charAt(startIndex))) {
              return new Match(input, startIndex, startIndex);
            } else {
              return Match.invalid();
            }
          }
        }

      default:
        System.out.println(
            "Invalid repeat property [" + repeat + "] on CharacterMatcher with characters " + getMatchedCharsString());
        return Match.invalid();
    }
  }

  private boolean lastOfParentSequence() {
    return parent.currentSequence().indexOf(this) == parent.currentSequence().size() - 1;
  }

  String getMatchedCharsString() {
    String ret = isNegative ? "+" : "";
    for (char c : matchedChars) {
      ret += c;
    }
    return ret;
  }

  public String toString() {
    String ending;
    switch (repeat) {
      case ONEORMORE:
        ending = "+";
        break;
      case ZEROORONE:
        ending = "?";
        break;
      case ONE:
      default:
        ending = "";
        break;
    }
    return getMatchedCharsString() + ending;
  }

  public RegexMatcher.MatchRepeat getRepeat() {
    return repeat;
  }
}
