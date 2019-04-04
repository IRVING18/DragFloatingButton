package com.ivring.drag.deskfloating;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;

import com.ivring.drag.Utils;

/**
 * 桌面悬浮框
 */
public class DragTableButton extends AppCompatImageView {

    private static final String TAG = "DragTableButton";

    private int screenHeight;
    private int screenWidth;
    private int startX, startY;
    private int lastX, lastY;
    WindowManager windowManager;
    private WindowManager.LayoutParams mLayoutParams;
    //WindowManager.LayoutParams 的x坐标
    private float lp_x;

    public DragTableButton(Context context) {
        super(context);
        init();
    }

    public DragTableButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    void init() {
        windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        screenHeight = Utils.getScreenHeight(getContext());
        screenWidth = Utils.getScreenWidth(getContext());
    }

    public boolean onTouchEvent(MotionEvent event) {
        //获取相对屏幕的X，Y
        int rawX = (int) event.getRawX();
        int rawY = (int) event.getRawY();
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                startX = lastX = rawX;
                startY = lastY = rawY;
                break;
            case MotionEvent.ACTION_MOVE:
                //计算手指移动的距离
                float x = rawX - lastX;
                float y = rawY - lastY;
                if (getLayoutParams() instanceof WindowManager.LayoutParams) {
                    mLayoutParams = (WindowManager.LayoutParams) getLayoutParams();
                    //将移动距离累加到Lp中
                    mLayoutParams.x += (int) x;
                    mLayoutParams.y += (int) y;
                    //将Lp设置给DragTableButton
                    windowManager.updateViewLayout(this, mLayoutParams);
                }
                lastX = rawX;
                lastY = rawY;
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                int dx = (int) event.getRawX() - startX;
                int dy = (int) event.getRawY() - startY;
                if (Math.abs(dx) < 3 && Math.abs(dy) < 3) {
                    performClick();//重点，确保DragFloatActionButton2.setOnclickListener生效
                    break;
                }
                if (rawX >= screenWidth / 2) {
                    //靠右吸附
                    ObjectAnimator oa = ObjectAnimator.ofFloat(this, "lp_x", mLayoutParams.x, screenWidth - getWidth());
                    oa.setInterpolator(new DecelerateInterpolator());
                    oa.setDuration(500);
                    oa.start();
                } else {
                    //靠左吸附
                    ObjectAnimator oa = ObjectAnimator.ofFloat(this, "lp_x", mLayoutParams.x, 0);
                    oa.setInterpolator(new DecelerateInterpolator());
                    oa.setDuration(500);
                    oa.start();
                }
                break;
        }
        return true;
    }


    public float getLp_x() {
        return lp_x;
    }

    public void setLp_x(float lp_x) {
        this.lp_x = lp_x;
        mLayoutParams.x = (int) lp_x;
        windowManager.updateViewLayout(this,mLayoutParams);
    }
}
