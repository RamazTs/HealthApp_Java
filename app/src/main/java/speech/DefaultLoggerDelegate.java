package speech;

import android.util.Log;

/**
 * Default logger delegate implementation which logs in LogCat with {@link Log}.
 * Log tag is set to <b>UploadService</b> for all the logs.
 * @author Zhicheng fu
 */
public class DefaultLoggerDelegate implements Logger.LoggerDelegate {

    private static final String TAG = Speech.class.getSimpleName();

    @Override
    public void error(String tag, String message) {
        Log.e(TAG, tag + " - " + message);
    }

    @Override
    public void error(String tag, String message, Throwable exception) {
        Log.e(TAG, tag + " - " + message, exception);
    }

    @Override
    public void debug(String tag, String message) {
        Log.d(TAG, tag + " - " + message);
    }

    @Override
    public void info(String tag, String message) {
        Log.i(TAG, tag + " - " + message);
    }
}
