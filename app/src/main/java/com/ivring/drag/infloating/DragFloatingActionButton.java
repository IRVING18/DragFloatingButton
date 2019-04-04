package com.ivring.drag.infloating;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;

import com.ivring.drag.Utils;

/**
 * 悬浮框，应用内使用。
 * 需要<uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW"/>
 */
public class DragFloatingActionButton extends AppCompatImageView {

    private static final String TAG = "DragFloatActionButton";

    private int parentHeight;
    private int parentWidth;
    private int startX, startY;
    private int lastX, lastY;
    private boolean isMove;

    public DragFloatingActionButton(Context context) {
        super(context);
    }

    public DragFloatingActionButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public boolean onTouchEvent(MotionEvent event) {
        //获取相对屏幕的X，Y
        int rawX = (int) event.getRawX();
        int rawY = (int) event.getRawY();
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                Log.e(TAG, "ACTION_DOWN: ");
                isMove = false;
                startX = lastX = rawX;
                startY = lastY = rawY;
                ViewGroup parent;
                if (getParent() != null) {
                    parent = (ViewGroup) getParent();
                    parentHeight = parent.getHeight();
                    parentWidth = parent.getWidth();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                isMove = true;
                Log.e(TAG, "ACTION_MOVE: ");
                float x = getX() + (rawX - lastX);
                float y = getY() + (rawY - lastY);
//                //检测是否到达边缘 左上右下
                x = x < 0 ? 0 : (x > parentWidth - getWidth() ? parentWidth - getWidth() : x);
                //下边界再40dp以上
                if (y < 0) {
                    y = 0;
                } else if (y > parentHeight - getHeight() - Utils.dip2px(40)) {
                    y = parentHeight - getHeight() - Utils.dip2px(40);
                }
                setX(x);
                setY(y);
                lastX = rawX;
                lastY = rawY;
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                Log.e(TAG, "ACTION_UP: ");
                int dx = (int) event.getRawX() - startX;
                int dy = (int) event.getRawY() - startY;
                if (Math.abs(dx) < 3 && Math.abs(dy) < 3) {
                    performClick();//重点，确保DragFloatActionButton2.setOnclickListener生效
                    break;
                }
                if (rawX >= parentWidth / 2) {
                    //靠右吸附
                    animate().setInterpolator(new DecelerateInterpolator())
                            .setDuration(500)
                            .xBy(parentWidth - getWidth() - getX())
                            .start();
                } else {
                    //靠左吸附
                    ObjectAnimator oa = ObjectAnimator.ofFloat(this, "x", getX(), 0);
                    oa.setInterpolator(new DecelerateInterpolator());
                    oa.setDuration(500);
                    oa.start();
                }
                break;
        }
        return true;
    }

}
