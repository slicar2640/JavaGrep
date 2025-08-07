import java.util.HashSet;

public class CharacterMatcher extends RegexMatcher {
  static final String lowercaseAlphabet = "abcdefghijklmnopqrstuvwxyz";
  static final String uppercaseAlphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
  static final String digits = "0123456789";

  public enum MatchRepeat {
    ONE,
    ONEORMORE,
    ZEROORONE
  }

  MatchRepeat repeat;
  HashSet<Character> matchedChars = new HashSet<>();
  boolean isNegative;

  public CharacterMatcher(String chars, MatchRepeat repeat, boolean negative) {
    this.repeat = repeat;
    this.isNegative = negative;
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

  public CharacterMatcher(String chars, boolean negative) {
    this(chars, MatchRepeat.ONE, negative);
  }

  public CharacterMatcher(String chars, MatchRepeat repeat) {
    this(chars, repeat, false);
  }

  public CharacterMatcher(String chars) {
    this(chars, MatchRepeat.ONE, false);
  }

  public boolean test(char input) {
    return matchedChars.contains(input) ^ isNegative;
  }

  public int firstMatch(String input, int startIndex) {
    for (int i = startIndex; i < input.length(); i++) {
      if (test(input.charAt(i))) {
        return i;
      }
    }
    return -1;
  }

  public int match(String input, int startIndex, Regex parent) {
    System.out.println("char match " + startIndex + " " + input.charAt(startIndex));
    System.out.println(repeat.toString());
    switch (repeat) {
      case ONE:
        if (test(input.charAt(startIndex))) {
          return startIndex + 1;
        } else {
          return -1;
        }

      case ONEORMORE:
        for (int i = startIndex; i < input.length(); i++) {
          System.out.println("L " + i + " " + input.charAt(i));
          if (!test(input.charAt(startIndex))) {
            break;
          }
          if (i < input.length() - 1
              && parent.match(input, i + 1, parent.currentSequence().matcherList.indexOf(this) + 1, null) >= 0) {
            System.out.println(i + 1);
            return i + 1;
          }
        }
        return -1;

      case ZEROORONE:
        if (parent.match(input, startIndex, parent.currentSequence().matcherList.indexOf(this) + 1, null) >= 0) {
          System.out.println("Marius");
          return startIndex;
        } else {
          System.out.println("Enjolras");
          parent.sequenceOffset--;
          if (test(input.charAt(startIndex))) {
            System.out.println("red");
            return startIndex + 1;
          } else {
            System.out.println("black");
            return -1;
          }
        }

      default:
        System.out.println(
            "Invalid repeat property [" + repeat + "] on CharacterMatcher with characters " + getMatchedCharsString());
        return -1;
    }
  }

  String getMatchedCharsString() {
    String ret = isNegative ? "^" : "";
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
}
