package grep.rule;

import java.util.HashSet;

import grep.MatchContext;

public class CharacterRule extends MatchRule {
  static final String lowercaseAlphabet = "abcdefghijklmnopqrstuvwxyz";
  static final String uppercaseAlphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
  static final String digits = "0123456789";
  static final String wordCharacters = lowercaseAlphabet + uppercaseAlphabet + digits + '_';

  private HashSet<Character> matchedChars;
  private boolean positiveMatch;
  private String sourceCharString;

  public CharacterRule(String chars, boolean positive) {
    sourceCharString = chars;
    positiveMatch = positive;
    matchedChars = new HashSet<>();
    for (int i = 0; i < chars.length(); i++) {
      matchedChars.add(chars.charAt(i));
    }
  }

  public boolean selfMatches(String input, int index, MatchContext context) {
    if (index >= input.length()) {
      return false;
    }
    if (matchedChars.contains(input.charAt(index)) ^ !positiveMatch) {
      context.lastMatch = index + 1;
      return true;
    } else {
      return false;
    }
  }

  public static CharacterRule anyDigit(boolean positive) {
    CharacterRule rule = new CharacterRule(digits, positive);
    rule.sourceCharString = "\\d";
    return rule;
  }

  public static CharacterRule anyDigit() {
    CharacterRule rule = new CharacterRule(digits, true);
    rule.sourceCharString = "\\d";
    return rule;
  }

  public static CharacterRule anyWordCharacter(boolean positive) {
    CharacterRule rule = new CharacterRule(wordCharacters, positive);
    rule.sourceCharString = "\\w";
    return rule;
  }

  public static CharacterRule anyWordCharacter() {
    CharacterRule rule = new CharacterRule(wordCharacters, true);
    rule.sourceCharString = "\\w";
    return rule;
  }

  public static CharacterRule anyCharacter() {
    CharacterRule rule = new CharacterRule("", false);
    rule.sourceCharString = ".";
    return rule;
  }

  public static boolean isLowercaseLetter(char c) {
    return lowercaseAlphabet.indexOf(c) >= 0;
  }

  public static boolean isUppercaseLetter(char c) {
    return uppercaseAlphabet.indexOf(c) >= 0;
  }

  public static boolean isDigit(char c) {
    return digits.indexOf(c) >= 0;
  }

  public String toString() {
    if (sourceCharString.length() == 1 || sourceCharString.startsWith("\\")) {
      return sourceCharString;
    }
    StringBuilder builder = new StringBuilder();

    if (!positiveMatch || sourceCharString.length() > 1) {
      builder.append('[');
    }
    if (!positiveMatch) {
      builder.append('^');
    }
    builder.append(sourceCharString);
    if (!positiveMatch || sourceCharString.length() > 1) {
      builder.append(']');
    }
    return builder.toString();
  }
}
