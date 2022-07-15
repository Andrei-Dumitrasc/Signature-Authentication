package com.andrei.signatureauthentication;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

public class CanvasView extends View {

    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Path mPath;
    private Paint mPaint;
    private float mX, mY;
    private static final float TOLERANCE = 0;
    private ArrayList<Float> xCoord,yCoord;
    private ArrayList<Integer> status;
    Context context;
    
    public ArrayList<Float> getSignatureX(){
        return xCoord;
    }
    
    public ArrayList<Float> getSignatureY(){
        return yCoord;
    }
    
    public ArrayList<Integer> getSignaturePenUps(){
        return status;
    }

    public CanvasView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;

        mPath = new Path();

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeWidth(4f);
        
        xCoord=new ArrayList<>();
        yCoord=new ArrayList<>();
        status=new ArrayList<>();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
    }

    private void startTouch(float x, float y) {
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
    }

    private void moveTouch(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOLERANCE || dy >= TOLERANCE) {
            mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            mX = x;
            mY = y;
        }
    }

    public void clearCanvas() {
        mPath.reset();
        xCoord.clear();
        yCoord.clear();
        status.clear();
        invalidate();
    }

    private void upTouch() {
        mPath.lineTo(mX, mY);
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPath(mPath,mPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        xCoord.add(x);
        float y = event.getY();
        yCoord.add(y);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startTouch(x, y);
                status.add(1);
                break;
            case MotionEvent.ACTION_MOVE:
                moveTouch(x, y);  
                status.add(1);
                break;
            case MotionEvent.ACTION_UP:
                upTouch();
                status.add(0);
                break;
        }
        invalidate();        
        return true;
    }
}
