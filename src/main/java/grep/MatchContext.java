package grep;

public class MatchContext {
  public String source;
  public String[] captureRefs = new String[9];
  public int lastCapture = -1;
  public int lastMatch = 0;

  public MatchContext(String source) {
    this.source = source;
  }

  public void addCaptureRef(String ref) {
    captureRefs[++lastCapture] = ref;
  }

  public void removeCaptureRef(int capIndex) {
    for (int i = capIndex + 1; i < 9; i++) {
      captureRefs[i - 1] = captureRefs[i];
    }
    captureRefs[captureRefs.length - 1] = null;
    lastCapture--;
  }
}
