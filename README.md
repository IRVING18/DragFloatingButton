# DragFloatingButton
1、应用内悬浮框 2、桌面悬浮框

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
