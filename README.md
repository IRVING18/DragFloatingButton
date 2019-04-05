# DragFloatingButton
1、应用内悬浮框 2、桌面悬浮框

![linear](https://github.com/IRVING18/FlowLayout/blob/master/float1.gif)
## 一、应用内悬浮框实现
### 1、实现思路
- 1.1 实现可拖动的view，首先想到肯定是onTouchEvent()去监听手势事件
- 1.2 通过手势获取手指按下的点当做原点，然后在Move中不断计算间距。然后就可以计算位移了。
- 1.3 由于悬浮框都是相对整个屏幕的，所以获取event.getRawX/Y()获取相对屏幕的距离。
- 1.4 计算完位移，那么通过setX(),setY()来设置view在父view中的坐标（左上角）；
### 2、实现需要技能
- 2.1 onTouchEvent()通过event.getRawX/Y()获取相对屏幕的距离。
- 2.2 view中setX/Y()设置view在父view的左上角坐标位置。
- 2.3 通过 ObjectAnimator.ofFloat(this, "x", getX(), 0); 可以改变view中自带的x的值，就是左上角的坐标。
### 3、实现代码
#### 3.1按下时获取起点
```java
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
        }
        return true;
    }
```
#### 3.2移动时计算滑动距离
```java

    public boolean onTouchEvent(MotionEvent event) {
        //获取相对屏幕的X，Y
        int rawX = (int) event.getRawX();
        int rawY = (int) event.getRawY();
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
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
        }
        return true;
    }
```

#### 3.3抬起时自动滑动到两边
```java
    public boolean onTouchEvent(MotionEvent event) {
        //获取相对屏幕的X，Y
        int rawX = (int) event.getRawX();
        int rawY = (int) event.getRawY();
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
```

## 二、桌面悬浮框

### 1、实现思路
- 1.1 和应用内悬浮框一样，都是通过onTouchEvent来处理手指滑动。
- 1.2 只是位移算出来之后是通过WindowManager.LayoutParams.x/y来设置移动的，而不是setX/Y()了
- 1.3 显示在桌面上需要WindowManager.addView()来设置到桌面
- 1.4 windowManager需要 
```java
<uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW"/> 
```

### 2、实现代码
```java
  /**
     * 显示悬浮框
     *
     * @param view     :显示的view
     * @param activity
     */
    public static void showDragTableButton(View view, Activity activity) {

        if (mWindowManager == null) {
            mWindowManager = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
        }
        int screenWidth = 0, screenHeight = 0;
        if (mWindowManager != null) {
            //获取屏幕的宽和高
            Point point = new Point();
            mWindowManager.getDefaultDisplay().getSize(point);
            screenWidth = point.x;
            screenHeight = point.y;
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            //设置type
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                //26及以上必须使用TYPE_APPLICATION_OVERLAY   @deprecated TYPE_PHONE
                layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
            } else {
                layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
            }
            //设置flags
            layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                    | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED;
            layoutParams.gravity = Gravity.START | Gravity.TOP;
            //背景设置成透明
            layoutParams.format = PixelFormat.TRANSPARENT;
            layoutParams.x = screenWidth;
            layoutParams.y = screenHeight / 2;
            //将View添加到屏幕上
            mWindowManager.addView(view, layoutParams);
        }
    }

```

```java
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
```
