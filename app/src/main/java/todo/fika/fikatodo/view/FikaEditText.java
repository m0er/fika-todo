package todo.fika.fikatodo.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.EditText;

import lombok.Setter;
import todo.fika.fikatodo.util.FikaCallback;

/**
 * Created by AidenChoi on 2016. 3. 18..
 */
public class FikaEditText extends EditText {

    @Setter
    private FikaCallback onKeyboardHideCallback;

    public FikaEditText(Context context) {
        super(context);
    }

    public FikaEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FikaEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * @see http://stackoverflow.com/questions/5014219/multiline-edittext-with-done-softinput-action-label-on-2-3
     */
    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        InputConnection conn = super.onCreateInputConnection(outAttrs);
        outAttrs.imeOptions &= ~EditorInfo.IME_FLAG_NO_ENTER_ACTION;
        return conn;
    }

    /**
     * @see http://stackoverflow.com/questions/5113437/get-back-key-event-on-edittext
     */
    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            clearFocus();
            if (onKeyboardHideCallback != null) {
                onKeyboardHideCallback.callback();
            }
            return false;
        }
        return super.dispatchKeyEvent(event);
    }

}
