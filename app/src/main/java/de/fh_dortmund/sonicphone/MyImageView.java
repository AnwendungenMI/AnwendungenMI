package de.fh_dortmund.sonicphone;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by JW on 17.11.2015.
 */
public class MyImageView extends ImageView {

    private OnDrawFinishedListener mDrawFinishedListener;

    public MyImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mDrawFinishedListener != null) {
            mDrawFinishedListener.onOnDrawFinish();
        }
    }

    public void setOnDrawFinishedListener(OnDrawFinishedListener listener) {
        mDrawFinishedListener = listener;
    }

    public interface OnDrawFinishedListener {
        public void onOnDrawFinish();
    }

}
