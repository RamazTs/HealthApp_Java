package speech;

/**
 * @author Zhicheng fu
 */
public class SpeechRecognitionNotAvailable extends Exception {
    public SpeechRecognitionNotAvailable() {
        super("Speech recognition not available");
    }
}
