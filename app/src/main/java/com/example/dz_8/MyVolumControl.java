package com.example.dz_8;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

public class MyVolumControl extends View implements View.OnTouchListener {
    private Paint paint;
    {
        this.paint = new Paint();
        this.paint.setAntiAlias(true);
    }

    public	interface	OnVolumSelectListener{
        void	OnVolumSelect(int pos);
    }
    private ArrayList<OnVolumSelectListener> listeners	= new ArrayList<>();

    private static int numPos = 16;
    private static  int interval = 4;
    private static Canvas canvas;
    private static int[] arrPos = new int[numPos];  //только кол. позиц. - уровни громк нужно еще вычислить в отд. массив
    private	int	curPos	= 4;    // начальн поз

    public MyVolumControl(Context context)
    {
        super(context);
    }
    public MyVolumControl(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public MyVolumControl(Context context, AttributeSet attrs, int defStyleAttr){
        super(context, attrs, defStyleAttr);
    }

    public	void  addOnVolumSelectListener(MyVolumControl.OnVolumSelectListener listener) {
        this.listeners.add(listener);
    }

    public	void  removeOnVolumSelectListener(MyVolumControl.OnVolumSelectListener listener){
        this.listeners.remove(listener);
    }
    {
        this.setOnTouchListener(this);
    }
    public void setCurPos(int curPos) {
        this.curPos = curPos;
    }

    public int getCurPos() {
        return curPos;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onDraw(Canvas canvas){
        int width = this.getWidth() / numPos;     //-----ширина одной шкалы
        int height = this.getHeight() / numPos;   //-----высота  одной шкалы
        //canvas.drawColor(R.color.darkBrown);  //холст белым
        int curHeight = 0;
        int tempPos = 0;
        int constHeight = this.getHeight();
        for(int i = 0; i < numPos; i++){
            if(i <= curPos)
                paint.setColor(Color.WHITE);
            else
                paint.setColor(Color.GRAY);
            tempPos = numPos - i -1;
            curHeight = height *tempPos;
            canvas.drawRect(interval + i * width, curHeight, (i + 1) * width - interval, constHeight, this.paint);
        }
        MyVolumControl.canvas = canvas;
    }
//перерисю исходя из тек. позиц. (curPos)
    public void resetDraw(){
//        int tempPos = 0;
//        int curHeight = 0;
//        for(int i = 0; i < numPos; i++){
//            if(i <= curPos)
//                paint.setColor(Color.WHITE);
//            else
//                paint.setColor(Color.GRAY);
//
//            tempPos = numPos - i -1;
//            curHeight = height *tempPos;
//            canvas.drawRect(interval + i *width, curHeight, (i + 1) *width - interval, constHeight , this.paint);
//        }
        this.invalidate();
    }
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        float currX =  motionEvent.getX();
        curPos = (int)((currX/this.getWidth())*16.0);
        Log.e("ZZZZZZZZZZ", String.valueOf(curPos));
        resetDraw();
        //уведомл. обработчиков (в т.ч регул. звука в MainActiv.
        for(int i = 0; i < this.listeners.size(); i++){
            this.listeners.get(i).OnVolumSelect(this.curPos);
        }
        return true;
    }
}
