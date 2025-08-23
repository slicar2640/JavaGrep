package grep;

public class MatchContext {
  public String source;
  public String[] captureRefs = new String[9];
  public int lastMatch = 0;
  public int numCaptures = 0;

  public MatchContext(String source) {
    this.source = source;
  }

  public void addCaptureRef(int index, String ref) {
    captureRefs[index] = ref;
  }

  public void removeCaptureRef(int index) {
    captureRefs[index] = null;
  }
}
