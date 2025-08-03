public class CharacterMatcher implements RegexMatcher {
  char character;

  public CharacterMatcher(char character) {
    this.character = character;
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
