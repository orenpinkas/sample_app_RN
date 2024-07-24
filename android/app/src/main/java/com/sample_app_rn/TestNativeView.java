package com.sample_app_rn;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

public class TestNativeView extends View {
    private Paint textPaint;
    private Paint backgroundPaint;
    private String displayText = "Test View";

    public TestNativeView(Context context) {
        super(context);
        init();
    }

    private void init() {
        textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(40);
        textPaint.setAntiAlias(true);

        backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.BLUE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Draw background
        canvas.drawRect(0, 0, getWidth(), getHeight(), backgroundPaint);

        // Draw text
        float x = getWidth() / 2f;
        float y = getHeight() / 2f;
        canvas.drawText(displayText, x - textPaint.measureText(displayText) / 2, y, textPaint);
    }

    public void setDisplayText(String text) {
        this.displayText = text;
        invalidate(); // Request a redraw
    }

    public void customMethod(String message) {
        setDisplayText(message);
    }
}

