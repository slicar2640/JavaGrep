import java.util.ArrayList;

class Regex extends RegexMatcher {
  ArrayList<MatcherSequence> matchSequences = new ArrayList<>();
  ArrayList<Boolean> sequenceStartAtStart = new ArrayList<>();
  ArrayList<Boolean> sequenceEndAtEnd = new ArrayList<>();
  int sequenceIndex = 0;
  int sequenceOffset = 0;

  public Regex(String patternString) {
    parsePattern(patternString);
  }

  private void parsePattern(String patternString) {
    MatcherSequence currentSequence;
    if (matchSequences.size() == 0) {
      currentSequence = new MatcherSequence();
      matchSequences.add(currentSequence);
      sequenceStartAtStart.add(false);
      sequenceEndAtEnd.add(false);
    } else {
      currentSequence = matchSequences.get(matchSequences.size() - 1);
    }

    for (int i = 0; i < patternString.length(); i++) {
      switch (patternString.charAt(i)) {
        case '\\':
          if (i < patternString.length() - 2 && patternString.charAt(i + 2) == '+') {
            currentSequence
                .addMatcher(
                    new CharacterMatcher(patternString.substring(i, i + 2), CharacterMatcher.MatchRepeat.ONEORMORE));
            i += 2;
          } else if (i < patternString.length() - 2 && patternString.charAt(i + 2) == '?') {
            currentSequence
                .addMatcher(
                    new CharacterMatcher(patternString.substring(i, i + 2), CharacterMatcher.MatchRepeat.ZEROORONE));
            i += 2;
          } else {
            currentSequence.addMatcher(new CharacterMatcher(patternString.substring(i, i + 2)));
            i++;
          }
          break;
        case '^':
          currentSequence.startAtStart = true;
          break;
        case '$':
          currentSequence.endAtEnd = true;
          break;
        case '.':
          if (i < patternString.length() - 1 && patternString.charAt(i + 1) == '+') {
            currentSequence
                .addMatcher(
                    new CharacterMatcher("", CharacterMatcher.MatchRepeat.ONEORMORE, true));
            i++;
          } else if (i < patternString.length() - 1 && patternString.charAt(i + 1) == '?') {
            currentSequence
                .addMatcher(
                    new CharacterMatcher("", CharacterMatcher.MatchRepeat.ZEROORONE, true));
            i++;
          } else {
            currentSequence.addMatcher(new CharacterMatcher("", true));
          }
          break;
        case '[':
          int closeBracket = patternString.indexOf(']', i);
          if (patternString.charAt(i + 1) == '^') {
            String sub = patternString.substring(i + 2, closeBracket);
            currentSequence.addMatcher(new CharacterMatcher(sub, true));
          } else {
            String sub = patternString.substring(i + 1, closeBracket);
            currentSequence.addMatcher(new CharacterMatcher(sub));
          }
          i = closeBracket;
          break;
        case '(':
          int closeParenth = patternString.indexOf(')', i);
          currentSequence.addMatcher(new Regex(patternString.substring(i + 1, closeParenth)));
          i = closeParenth;
          break;
        case '|':
          currentSequence = new MatcherSequence();
          matchSequences.add(currentSequence);
          break;
        default:
          if (i < patternString.length() - 1 && patternString.charAt(i + 1) == '+') {
            currentSequence
                .addMatcher(
                    new CharacterMatcher(patternString.substring(i, i + 1), CharacterMatcher.MatchRepeat.ONEORMORE));
            i++;
          } else if (i < patternString.length() - 1 && patternString.charAt(i + 1) == '?') {
            currentSequence
                .addMatcher(
                    new CharacterMatcher(patternString.substring(i, i + 1), CharacterMatcher.MatchRepeat.ZEROORONE));
            i++;
          } else {
            currentSequence.addMatcher(new CharacterMatcher(patternString.substring(i, i + 1)));
          }
          break;
      }
    }
  }

  public int firstMatch(String input, int startIndex, int seqOff) {
    for (int i = 0; i < matchSequences.size(); i++) {
      if (matchSequences.get(i).startAtStart) {
        sequenceIndex = i;
        return 0;
      }
      int idx = matchSequences.get(i).getMatcher(seqOff).firstMatch(input, startIndex);
      if (idx != -1) {
        sequenceIndex = i;
        return idx;
      }
    }
    return -1;
  }

  public int firstMatch(String input, int startIndex) {
    return firstMatch(input, startIndex, 0);
  }

  public int match(String input, int startIndex, int seqOff, Regex parent) {
    sequenceOffset = seqOff;

    int inputIndex = firstMatch(input, startIndex, sequenceOffset);
    if (inputIndex == -1) {
      return -1;
    }

    sequenceLoop: while (sequenceIndex < matchSequences.size()) {
      while (sequenceOffset < currentSequence().matcherList.size() && inputIndex < input.length()) {
        RegexMatcher matcher = currentSequence().getMatcher(sequenceOffset);
        int newIndex = matcher.match(input, inputIndex, this);
        if (newIndex == -1) {
          if (sequenceIndex < matchSequences.size() - 1) {
            sequenceIndex++;
            sequenceOffset = 0;
            continue sequenceLoop;
          } else {
            return -1;
          }
        } else {
          inputIndex = newIndex;
          sequenceOffset++;
        }
      }
      if (sequenceOffset >= currentSequence().matcherList.size()) {
        return currentSequence().endAtEnd && inputIndex < input.length() ? -1 : inputIndex + 1;
      } else {
        if (inputIndex >= input.length()) {
          return -1;
        }
      }
    }
    return -1;
  }

  public int match(String input, int startIndex, Regex parent) {
    sequenceIndex = 0;
    return match(input, startIndex, 0, parent);
  }

  public MatcherSequence currentSequence() {
    return matchSequences.get(sequenceIndex);
  }

  public String toString() {
    String ret = "(";
    for (MatcherSequence sequence : matchSequences) {
      ret += sequence.toString();
      ret += "|";
    }
    return ret.substring(0, ret.length() - 1) + ")";
  }

  public String sequenceToString(int seqIndex) {
    return matchSequences.get(seqIndex).toString();
  }
}