public class CharacterMatcher extends RegexMatcher {
  char character;

  public CharacterMatcher(char character) {
    this.character = character;
    repeat = MatchRepeat.ONE;
  }

  public CharacterMatcher(char character, MatchRepeat repeat) {
    this.character = character;
    this.repeat = repeat;
  }

  public boolean test(char input) {
    return input == character;
  }

  public int match(String input) {
    return input.indexOf(character);
  }

  public int match(String input, int startIndex) {
    return input.indexOf(character, startIndex);
  }
}
