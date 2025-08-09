public class SubRegex extends Regex implements RegexMatcher {
  MatchRepeat repeat;
  Regex parent;

  public SubRegex(String patternString, Regex parent) {
    super(patternString);
    this.parent = parent;
    this.repeat = MatchRepeat.ONE;
  }

  public SubRegex(String patternString, MatchRepeat repeat, Regex parent) {
    super(patternString);
    this.repeat = repeat;
    this.parent = parent;
  }

  public Match match(String input, int startIndex, int seqOff) {
    sequenceOffset = seqOff;

    if (repeat == MatchRepeat.ONE) {
      return matchOne(input, startIndex);
    } else if (repeat == MatchRepeat.ONEORMORE) {
      return matchOneOrMore(input, startIndex);
    } else if (repeat == MatchRepeat.ZEROORONE) {
      return matchZeroOrOne(input, startIndex);
    } else {
      System.out.println("Invalid repeat value [" + repeat.toString() + "] for regex " + toString());
      return Match.invalid();
    }
  }

  public Match match(String input, int startIndex) {
    sequenceIndex = 0;
    return match(input, startIndex, 0);
  }

  private Match matchOne(String input, int startIndex) {
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
        if (currentSequence().endAtEnd && inputIndex <= input.length()) {
          return Match.invalid();
        } else {
          return new Match(input, startIndex, inputIndex - 1);
        }
      } else {
        if (inputIndex >= input.length()) {
          for (int i = sequenceOffset; i < currentSequence().size(); i++) {
            if (currentSequence().getMatcher(i).getRepeat() != MatchRepeat.ZEROORONE) {
              return Match.invalid();
            }
          }
          return new Match(input, startIndex, input.length() - 1);
        }
      }
    }
    sequenceIndex = 0;
    return Match.invalid();
  }

  private Match matchOneOrMore(String input, int startIndex) {
    int inputIndex = startIndex;

    while (inputIndex < input.length()) {
      Match nextMatch = matchOne(input, inputIndex);
      if (!nextMatch.isValid) {
        break;
      }
      inputIndex = nextMatch.endIndex + 1;
      sequenceOffset = 0;
      if (inputIndex >= input.length()) {
        return new Match(input, startIndex, input.length() - 1);
      } else {
        if (!lastOfParentSequence()
            && parent.lookAhead(input, inputIndex, parent.currentSequence().indexOf(this) + 1)) {
          return new Match(input, startIndex, inputIndex);
        }
      }
    }
    return Match.invalid();
  }

  private Match matchZeroOrOne(String input, int startIndex) {
    if (lastOfParentSequence()) {
      Match m1 = matchOne(input, startIndex);
      if (m1.isValid) {
        return m1;
      } else {
        return Match.empty(startIndex);
      }
    } else {
      if (parent.lookAhead(input, startIndex, parent.currentSequence().indexOf(this) + 1)) {
        return Match.empty(startIndex);
      } else {
        return matchOne(input, startIndex);
      }
    }
  }

  private boolean lastOfParentSequence() {
    return parent.currentSequence().indexOf(this) == parent.currentSequence().size() - 1;
  }

  public RegexMatcher.MatchRepeat getRepeat() {
    return repeat;
  }

  public String toString() {
    String end;
    switch (repeat) {
      case ONEORMORE:
        end = "+";
        break;
      case ZEROORONE:
        end = "?";
        break;
      case ONE:
      default:
        end = "";
        break;
    }
    return "(" + super.toString() + ")" + end;
  }
}
