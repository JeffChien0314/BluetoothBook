package com.ev.bluetooth.phonebook.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.TextView;

import com.ev.bluetooth.phonebook.R;
import com.ev.bluetooth.phonebook.utils.LogUtils;

public class LetterListView extends TextView {
    private OnLetterChangedListener onLetterChangedListener;
    private String[] letterList = {"A","B","C","D","E","F","G","H","I","J","K","L"
            ,"M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z","#"};
    private int choose = -1;
    private Paint paint = new Paint();
    private boolean showBkg = false;

    public LetterListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public LetterListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LetterListView(Context context) {
        super(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(showBkg){
            canvas.drawColor(Color.parseColor("#40000000"));
        }

        int height = getHeight();
        int width = getWidth();
        int singleHeight = height / letterList.length;
        for(int i=0;i<letterList.length;i++){
            paint.setColor(getResources().getColor(R.color.color_40FFFFFF));
            paint.setTypeface(Typeface.DEFAULT_BOLD);
            paint.setTextSize(22);
            paint.setAntiAlias(true);
            if(i == choose){
                paint.setColor(Color.parseColor("#3399ff"));//点击选中时颜色
                paint.setFakeBoldText(true);
            }
            float xPos = width/2  - paint.measureText(letterList[i])/2;
            float yPos = singleHeight * i + singleHeight;
            canvas.drawText(letterList[i], xPos, yPos, paint);
            paint.reset();
        }

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        int action = event.getAction();
        float y = event.getY();
        int oldChoose = choose;
        int c = (int) (y/getHeight()*letterList.length);

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                showBkg = true;
                if(oldChoose != c && onLetterChangedListener != null){
                    if(c >= 0 && c< letterList.length){
                        onLetterChangedListener.onLetterChanged(letterList[c]);
                        choose = c;
                        invalidate();
                    }
                }

                break;
            case MotionEvent.ACTION_MOVE:
                if(oldChoose != c && onLetterChangedListener != null){
                    if(c > 0 && c< letterList.length){
                        onLetterChangedListener.onLetterChanged(letterList[c]);
                        choose = c;
                        invalidate();
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                showBkg = false;
                choose = -1;
                invalidate();
                break;
        }
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    public void setOnLetterChangedListener(
            OnLetterChangedListener onLetterChangedListener) {
        this.onLetterChangedListener = onLetterChangedListener;
    }

    public interface OnLetterChangedListener{
        public void onLetterChanged(String letter);
    }

}
