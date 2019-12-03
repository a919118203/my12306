package com.example.huangxiaoyang.my12306;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

public class NawView extends View{
    private Paint Pt = new Paint();
    private String[] Word = new String[]{"A", "B", "C", "D", "E",
            "F", "G", "H", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S",
            "T", "W", "X", "Y", "Z","#"};
    private int choose = -1;
    private TextView tv_word;
    public NawView(Context context) {
        super(context);
    }

    public NawView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public NawView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    private void initPaint() {
        Pt.setTextSize(20);
        Pt.setAntiAlias(true);
        Pt.setColor(Color.BLACK);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();
        int word_height = height / Word.length;
        for(int i = 0;i < Word.length; i++){
            initPaint();
            if(choose == i){
                Pt.setColor(Color.BLUE);
            }
            float x = (width - Pt.measureText(Word[i])) / 2;
            float y = (i + 1) * word_height;
            canvas.drawText(Word[i], x, y, Pt);
            Pt.reset();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        int index = (int) (event.getY() / getHeight() * Word.length);
        if (index >= Word.length) {
            index = Word.length - 1;
        } else if (index < 0) {
            index = 0;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                setBackgroundColor(Color.GRAY);
                choose = index;
                tv_word.setVisibility(VISIBLE);
                tv_word.setText(Word[choose]);
                if (listener != null) {
                    listener.touchCharacterListener(Word[choose]);
                }
                invalidate();
                break;
            default:
                setBackgroundColor(Color.TRANSPARENT);
                choose = -1;
                tv_word.setVisibility(GONE);
                invalidate();
                break;
        }
        return true;
    }

    public onTouchCharacterListener listener;

    public interface onTouchCharacterListener {
        void touchCharacterListener(String s);
    }

    public void setListener(onTouchCharacterListener listener) {
        this.listener = listener;
    }
    public void setTextView(TextView tv_word) {
        this.tv_word = tv_word;
    }

}
