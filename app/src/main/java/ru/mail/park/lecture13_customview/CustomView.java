package ru.mail.park.lecture13_customview;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class CustomView extends View {
    private static final String TAG = CustomView.class.getSimpleName();
    private static final String CURRENT_SHAPE_INDEX = "state_currentShapeIndex";
    private static final String INSTANCE_STATE = "state_instance";

    public enum Shape {
        square, circle
    }

    private Paint paintShape;
    private Paint paintText;
    private int shapeColor;

    private float textSize;
    private int textColor;

    private float textHeight;

    private Shape currentShape;

    private boolean isPressed;

    public CustomView(Context context) {
        super(context);
    }

    public CustomView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setupAttributes(attrs);
        setupPaint();
    }

    private void setupAttributes(AttributeSet attrs) {
        // Получаем TypedArray аттрибутов
        TypedArray a = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.CustomView, 0, 0);

        // Получаем свойства и запоминаем в поля
        try {
            int shapeParam = a.getInt(R.styleable.CustomView_initialShape, 0);
            currentShape = Shape.values()[shapeParam % Shape.values().length];
            shapeColor = a.getColor(R.styleable.CustomView_shapeColor, Color.GREEN);

            textColor = a.getColor(R.styleable.CustomView_textColor, Color.RED);
            textSize = a.getDimension(R.styleable.CustomView_textSize, 48);
        } finally {
            // Обязательно нужно вызвать!
            a.recycle();
        }
    }

    private void setupPaint() {
        paintShape = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintShape.setStyle(Paint.Style.FILL);
        paintShape.setColor(shapeColor);

        paintText = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintText.setTextSize(textSize);
        paintText.setColor(textColor);
        paintText.setTextAlign(Paint.Align.CENTER);
    }

    public int getShapeColor() {
        return shapeColor;
    }

    public void setShapeColor(int color) {
        this.shapeColor = color;

        invalidate();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        Log.d(TAG, "onAttachedToWindow");
    }

    private String getSpecModeName(int measureSpec) {
        String modeName = null;
        switch (MeasureSpec.getMode(measureSpec)) {
            case MeasureSpec.AT_MOST:
                modeName = "AT_MOST";
                break;
            case MeasureSpec.EXACTLY:
                modeName = "EXACTLY";
                break;
            case MeasureSpec.UNSPECIFIED:
                modeName = "UNSPECIFIED";
                break;
        }
        return modeName;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // no super()

        Log.v(TAG, "onMeasure:  widthMode: " + getSpecModeName(widthMeasureSpec)
                + " parentWidth: " + MeasureSpec.getSize(widthMeasureSpec)
                + "\nonMeasure: heightMode: " + getSpecModeName(heightMeasureSpec)
                + " parentHeight: " + MeasureSpec.getSize(heightMeasureSpec));

        String label = getSelectedShapeName();

        Paint.FontMetrics fontMetrics = paintText.getFontMetrics();
        float textWidth = paintText.measureText(label);
        textHeight = fontMetrics.bottom - fontMetrics.top;

        float shapeSize = textWidth * 1.5f;
        int minWidth = Math.round(shapeSize + getPaddingLeft() + getPaddingRight());
        int minHeight = Math.round(shapeSize + getPaddingBottom() + getPaddingTop());

        int w = resolveSizeAndState(minWidth, widthMeasureSpec, 0);
        int h = resolveSizeAndState(minHeight, heightMeasureSpec, 0);

        setMeasuredDimension(w, h);
    }

    private String getSelectedShapeName() {
        return currentShape.name();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int measuredWidth = getMeasuredWidth();
        int measuredHeight = getMeasuredHeight();

        int centerX = measuredWidth / 2;
        int centerY = measuredHeight / 2;

        Log.d(TAG, "onDraw. width: " + measuredWidth + " height: " + measuredHeight);

        int shapeSize = isPressed ? measuredWidth : (int) (measuredWidth * 0.9);
        int offset = measuredHeight - shapeSize;

        switch (currentShape) {
            case square:
                canvas.drawRect(offset, offset, shapeSize, shapeSize, paintShape);
                break;
            case circle:
                canvas.drawCircle(centerX, centerY, shapeSize / 2f, paintShape);
                break;
        }

        canvas.drawText(currentShape.name(), centerX, centerY + textHeight / 4f, paintText);
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.e(TAG, "Touch ACTION_DOWN");
                isPressed = true;
                postInvalidate();
                requestLayout();
                return true;

            case MotionEvent.ACTION_UP:
                Log.e(TAG, "Touch ACTION_UP");
                isPressed = false;
                currentShape = nextShape();
                postInvalidate();
                requestLayout();
                return true;

            case MotionEvent.ACTION_MOVE:
                Log.e(TAG, "Touch ACTION_MOVE X: " + event.getX() + " Y: " + event.getY());
                return super.onTouchEvent(event);

            default:
                isPressed = false;
                return super.onTouchEvent(event);
        }
    }

    private Shape nextShape() {
        int currentShapeInt = currentShape.ordinal();
        return Shape.values()[(++ currentShapeInt) % Shape.values().length];
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(INSTANCE_STATE, super.onSaveInstanceState());
        bundle.putInt(CURRENT_SHAPE_INDEX, currentShape.ordinal());

        return bundle;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (state != null && state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            currentShape = Shape.values()[bundle.getInt(CURRENT_SHAPE_INDEX, 0)];

            super.onRestoreInstanceState(bundle.getParcelable(INSTANCE_STATE));
        } else
            super.onRestoreInstanceState(state);
    }
}
