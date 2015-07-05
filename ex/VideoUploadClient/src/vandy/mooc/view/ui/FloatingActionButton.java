package vandy.mooc.view.ui;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;

/**
 * CustomView that shows how to create a Floating Action Button, as
 * per Google's Material Design principles.
 */
public class FloatingActionButton extends View {
    /**
     * An interpolator where the change flings forward and overshoots
     * the last value then comes back.
     */
    final static OvershootInterpolator overshootInterpolator =
        new OvershootInterpolator();
    
    /**
     * An interpolator where the rate of change starts out slowly and
     * and then accelerates.
     */
    final static AccelerateInterpolator accelerateInterpolator =
        new AccelerateInterpolator();

    /**
     * Gets access to application-specific resources.
     */
    Context context;
    
    /**
     * Paints used to draw the Button in Canvas.
     */
    Paint mButtonPaint;
    Paint mDrawablePaint;
    
    /**
     * Bitmap of the icon present in Floating Action Button
     */
    Bitmap mBitmap;
    
    /**
     * Boolean to indicate if the Button is hidden or not. 
     */
    boolean mHidden = false;

    
    /**
     * Constructor that initializes the Floating
     * Action Button.
     * 
     * @param context
     */
    public FloatingActionButton(Context context) {
        super(context);
        this.context = context;
        init(Color.WHITE);
    }

    /**
     * Sets the Color of FloatingActionButton.
     * 
     * @param FloatingActionButtonColor
     */
    public void setFloatingActionButtonColor(int FloatingActionButtonColor) {
        init(FloatingActionButtonColor);
    }

    /**
     * Sets the Icon of FloatingActionButton.
     * 
     * @param FloatingActionButtonDrawable
     */
    public void setFloatingActionButtonDrawable(Drawable FloatingActionButtonDrawable) {
        mBitmap = ((BitmapDrawable) FloatingActionButtonDrawable).getBitmap();
        invalidate();
    }

    /**
     * Initialize all the Resources needed before drawing.
     * 
     * @param FloatingActionButtonColor
     */
    public void init(int FloatingActionButtonColor) {
        setWillNotDraw(false);
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        mButtonPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mButtonPaint.setColor(FloatingActionButtonColor);
        mButtonPaint.setStyle(Paint.Style.FILL);
        mButtonPaint.setShadowLayer(10.0f,
                                    0.0f,
                                    3.5f,
                                    Color.argb(100,
                                               0,
                                               0,
                                               0));
        mDrawablePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        invalidate();
    }

    /**
     * Hook method called to draw the View on the Canvas.
     * 
     *@param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        setClickable(true);
        canvas.drawCircle(getWidth() / 2,
                          getHeight() / 2,
                          (float) (getWidth() / 2.6),
                          mButtonPaint);
        canvas.drawBitmap(mBitmap, (getWidth() - mBitmap.getWidth()) / 2,
                          (getHeight() - mBitmap.getHeight()) / 2,
                          mDrawablePaint);
    }

    /**
     * Hook method called when View is Touched.
     * 
     * @param event
     */
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            setAlpha(1.0f);
        } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
            setAlpha(0.6f);
        }
        return super.onTouchEvent(event);
    }

    /**
     * Hides the Floating Action Button with some Animation.
     */
    public void hideFloatingActionButton() {
        if (!mHidden) {
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(this, "scaleX", 1, 0);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(this, "scaleY", 1, 0);
            AnimatorSet animSetXY = new AnimatorSet();
            animSetXY.playTogether(scaleX, scaleY);
            animSetXY.setInterpolator(accelerateInterpolator);
            animSetXY.setDuration(100);
            animSetXY.start();
            mHidden = true;
        }
    }

    /**
     * Shows the Floating Action Button with some Animation.
     */
    public void showFloatingActionButton() {
        if (mHidden) {
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(this, "scaleX", 0, 1);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(this, "scaleY", 0, 1);
            AnimatorSet animSetXY = new AnimatorSet();
            animSetXY.playTogether(scaleX, scaleY);
            animSetXY.setInterpolator(overshootInterpolator);
            animSetXY.setDuration(200);
            animSetXY.start();
            mHidden = false;
        }
    }

    /**
     * @return True if the View is hidden.
     */
    public boolean isHidden() {
        return mHidden;
    }

    /**
     * Builder pattern used to Build the Floating
     * Action Button.
     */
    static public class Builder {
        private FrameLayout.LayoutParams params;
        private final Activity activity;
        int gravity = Gravity.BOTTOM | Gravity.END; // default bottom right
        Drawable drawable;
        int color = Color.WHITE;
        int size = 0;
        float scale = 0;

        /**
         * Constructor used to initialize the Builder.
         * 
         * @param context
         */
        public Builder(Activity context) {
            scale = context.getResources().getDisplayMetrics().density;
            size = convertToPixels(72, scale); // default size is 72dp by 72dp
            params = new FrameLayout.LayoutParams(size, size);
            params.gravity = gravity;

            this.activity = context;
        }
    
        /**
         * Sets the Gravity of the View.
         * 
         * @param gravity
         * @return Builder
         */
        public Builder withGravity(int gravity) {
            this.gravity = gravity;
            return this;
        }

        /**
         * Sets the Margins of the View.
         * 
         * @param left
         * @param top
         * @param right
         * @param bottom
         * 
         * @return Builder
         */
        public Builder withMargins(int left, int top, int right, int bottom) {
            params.setMargins(
                              convertToPixels(left, scale),
                              convertToPixels(top, scale),
                              convertToPixels(right, scale),
                              convertToPixels(bottom, scale));
            return this;
        }
    
        /**
         * Sets the Drawable used by the View.
         * 
         * @param drawable
         * @return Builder
         */
        public Builder withDrawable(final Drawable drawable) {
            this.drawable = drawable;
            return this;
        }
    
        /**
         * Sets the color used by the View.
         * 
         * @param color
         * @return Builder
         */
        public Builder withButtonColor(final int color) {
            this.color = color;
            return this;
        }
    
        /**
         * Sets the size of the View.
         * 
         * @param size
         * @return Builder
         */
        public Builder withButtonSize(int size) {
            size = convertToPixels(size, scale);
            params = new FrameLayout.LayoutParams(size, size);
            return this;
        }

        /**
         * Creates the Floating Action Button.
         * 
         * @return FloatingActionButton
         */
        public FloatingActionButton create() {
            final FloatingActionButton button = new FloatingActionButton(activity);
            button.setFloatingActionButtonColor(this.color);
            button.setFloatingActionButtonDrawable(this.drawable);
            params.gravity = this.gravity;
            ViewGroup root = (ViewGroup) activity.findViewById(android.R.id.content);
            root.addView(button, params);
            return button;
        }

        /**
         * Calculate and scale the values to fit
         * the larger devices.
         * 
         * @param dp
         * @param scale
         * @return
         */
        private int convertToPixels(int dp, float scale){
            return (int) (dp * scale + 0.5f) ;
        }
    }
}
