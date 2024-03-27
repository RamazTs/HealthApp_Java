package speech;

/**
 * @author zhicheng fu
 */

public class GoogleVoiceTypingDisabledException extends Exception {
    public GoogleVoiceTypingDisabledException() {
        super("Google voice typing must be enabled");
    }
}
