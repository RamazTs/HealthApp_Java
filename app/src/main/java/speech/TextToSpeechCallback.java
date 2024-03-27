package speech;

/**
 * Contains the methods which are called to notify text to speech progress status.
 *
 * @author Zhicheng fu
 */
public interface TextToSpeechCallback {
    void onStart();
    void onCompleted();
    void onError();
}
