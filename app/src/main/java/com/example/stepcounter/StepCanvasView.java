package com.example.stepcounter;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class StepCanvasView extends View {

    private int totalSteps = 0;
    private Paint textPaint;

    public StepCanvasView(Context context) {
        super(context);
        init();
    }

    public StepCanvasView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public StepCanvasView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(50);
        textPaint.setTextAlign(Paint.Align.CENTER);
    }

    public void setTotalSteps(int steps) {
        totalSteps = steps;
        invalidate(); // Trigger redraw
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Draw steps
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;

        // Change text color based on total steps
        if (totalSteps > 100) {
            textPaint.setColor(Color.RED);
        } else if (totalSteps > 50) {
            textPaint.setColor(Color.GREEN);
        } else {
            textPaint.setColor(Color.BLACK);
        }

        canvas.drawText("Steps: " + totalSteps, centerX, centerY, textPaint);
    }
}
