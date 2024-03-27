package speech;

import java.util.List;

public interface SupportedLanguagesListener {
    void onSupportedLanguages(List<String> supportedLanguages);
    void onNotSupported(speech.UnsupportedReason reason);
}
