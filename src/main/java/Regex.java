import java.util.ArrayList;

class Regex {
  ArrayList<MatcherSequence> matchSequences = new ArrayList<>();
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
    } else {
      currentSequence = matchSequences.get(matchSequences.size() - 1);
    }

    for (int i = 0; i < patternString.length(); i++) {
      switch (patternString.charAt(i)) {
        case '\\':
          if (i < patternString.length() - 2 && patternString.charAt(i + 2) == '+') {
            currentSequence
                .addMatcher(
                    new CharacterMatcher(patternString.substring(i, i + 2), RegexMatcher.MatchRepeat.ONEORMORE, this));
            i += 2;
          } else if (i < patternString.length() - 2 && patternString.charAt(i + 2) == '?') {
            currentSequence
                .addMatcher(
                    new CharacterMatcher(patternString.substring(i, i + 2), RegexMatcher.MatchRepeat.ZEROORONE, this));
            i += 2;
          } else {
            currentSequence.addMatcher(new CharacterMatcher(patternString.substring(i, i + 2), this));
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
                    new CharacterMatcher("", RegexMatcher.MatchRepeat.ONEORMORE, true, this));
            i++;
          } else if (i < patternString.length() - 1 && patternString.charAt(i + 1) == '?') {
            currentSequence
                .addMatcher(
                    new CharacterMatcher("", RegexMatcher.MatchRepeat.ZEROORONE, true, this));
            i++;
          } else {
            currentSequence.addMatcher(new CharacterMatcher("", true, this));
          }
          break;
        case '[':
          int closeBracket = patternString.indexOf(']', i);
          if (patternString.charAt(i + 1) == '^') {
            String sub = patternString.substring(i + 2, closeBracket);
            currentSequence.addMatcher(new CharacterMatcher(sub, true, this));
          } else {
            String sub = patternString.substring(i + 1, closeBracket);
            currentSequence.addMatcher(new CharacterMatcher(sub, this));
          }
          i = closeBracket;
          break;
        case '(':
          int closeParenth = i;
          int parenthCount = 1;
          while (closeParenth < patternString.length() && parenthCount > 0) {
            closeParenth++;
            if (patternString.charAt(closeParenth) == '(') {
              parenthCount++;
            } else if (patternString.charAt(closeParenth) == ')') {
              parenthCount--;
            }
          }
          if (closeParenth < patternString.length() - 1 && patternString.charAt(closeParenth + 1) == '+') {
            currentSequence
                .addMatcher(new SubRegex(patternString.substring(i + 1, closeParenth),
                    RegexMatcher.MatchRepeat.ONEORMORE, this));
            i = closeParenth + 1;
          } else if (closeParenth < patternString.length() - 1 && patternString.charAt(closeParenth + 1) == '?') {
            currentSequence
                .addMatcher(new SubRegex(patternString.substring(i + 1, closeParenth),
                    RegexMatcher.MatchRepeat.ZEROORONE, this));
            i = closeParenth + 1;
          } else {
            currentSequence.addMatcher(new SubRegex(patternString.substring(i + 1, closeParenth), this));
            i = closeParenth;
          }
          break;
        case '|':
          currentSequence = new MatcherSequence();
          matchSequences.add(currentSequence);
          break;
        default:
          if (i < patternString.length() - 1 && patternString.charAt(i + 1) == '+') {
            currentSequence
                .addMatcher(
                    new CharacterMatcher(patternString.substring(i, i + 1), RegexMatcher.MatchRepeat.ONEORMORE, this));
            i++;
          } else if (i < patternString.length() - 1 && patternString.charAt(i + 1) == '?') {
            currentSequence
                .addMatcher(
                    new CharacterMatcher(patternString.substring(i, i + 1), RegexMatcher.MatchRepeat.ZEROORONE, this));
            i++;
          } else {
            currentSequence.addMatcher(new CharacterMatcher(patternString.substring(i, i + 1), this));
          }
          break;
      }
    }
  }

  public int firstMatchStart(String input, int startIndex, int seqOff) {
    for (int i = 0; i < matchSequences.size(); i++) {
      if (matchSequences.get(i).startAtStart) {
        sequenceIndex = i;
        return 0;
      }
      int idx = matchSequences.get(i).getMatcher(seqOff).firstMatchStart(input, startIndex);
      if (idx != -1) {
        sequenceIndex = i;
        return idx;
      }
    }
    return -1;
  }

  public int firstMatchStart(String input, int startIndex) {
    return firstMatchStart(input, startIndex, 0);
  }

  public Match initialMatch(String input) {
    int inputIndex = firstMatchStart(input, 0, 0);
    if (inputIndex == -1) {
      return Match.invalid();
    }
    return match(input, inputIndex, 0);
  }

  public Match match(String input, int startIndex, int seqOff) {
    sequenceOffset = seqOff;
    int inputIndex = startIndex;

    sequenceLoop: while (sequenceIndex < matchSequences.size()) {
      int beforeIndex = inputIndex;
      while (sequenceOffset < currentSequence().size() && inputIndex < input.length()) {
        RegexMatcher matcher = currentSequence().getMatcher(sequenceOffset);
        Match nextMatch = matcher.match(input, inputIndex);
        if (nextMatch.isValid) {
          if (nextMatch.match.length() == 0) {
            inputIndex = nextMatch.endIndex;
          } else {
            inputIndex = nextMatch.endIndex + 1;
          }

          sequenceOffset++;
        } else {
          sequenceIndex++;
          sequenceOffset = 0;
          inputIndex = beforeIndex;
          continue sequenceLoop;
        }
      }
      if (sequenceOffset >= currentSequence().size()) {
        if (currentSequence().endAtEnd && inputIndex < input.length()) {
          return Match.invalid();
        } else {
          return new Match(input, startIndex, inputIndex - 1);
        }
      } else {
        if (inputIndex >= input.length()) {
          return Match.invalid();
        }
      }
    }
    return Match.invalid();
  }

  public Match match(String input, int startIndex) {
    sequenceIndex = 0;
    return match(input, startIndex, 0);
  }

  public boolean lookAhead(String input, int startIndex, int seqOff) {
    int startSeqOff = sequenceOffset;
    int startSeqInd = sequenceIndex;
    Match match = match(input, startIndex, seqOff);
    sequenceOffset = startSeqOff;
    sequenceIndex = startSeqInd;
    return match.isValid;
  }

  public MatcherSequence currentSequence() {
    if (sequenceIndex < matchSequences.size()) {
      return matchSequences.get(sequenceIndex);
    } else {
      return null;
    }
  }

  public String toString() {
    String ret = "";
    for (MatcherSequence sequence : matchSequences) {
      ret += sequence.toString();
      ret += "|";
    }
    return ret.substring(0, ret.length() - 1);
  }

  public String sequenceToString(int seqIndex) {
    return matchSequences.get(seqIndex).toString();
  }
}