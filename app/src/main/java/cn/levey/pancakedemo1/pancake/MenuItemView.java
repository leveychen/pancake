package cn.levey.pancakedemo1.pancake;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Created by Levey on 2018/9/11 10:41.
 * e-mail: m@levey.cn
 * 自定义一个组件，用于处理事件分发，防止点击事件干扰滑动事件
 */
public class MenuItemView extends LinearLayout {
    public MenuItemView(Context context) {
        super(context);
    }

    public MenuItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MenuItemView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MenuItemView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private float yDown,yUp;

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        getParent().requestDisallowInterceptTouchEvent(true);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                yDown = (int) event.getRawY();

                System.out.println("---ACTION_DOWN--");
                break;

            case MotionEvent.ACTION_MOVE:
                System.out.println("---ACTION_MOVE--");

                break;
            case MotionEvent.ACTION_UP:

                yUp = (int) event.getRawY();
                System.out.println("---ACTION_UP--");

                if (Math.abs(yUp - yDown) > 30) {
                    System.out.println("---ACTION_UP-- 点击了");
                    return true;
                }
        }
        return ((View) getParent()).onTouchEvent(event);

    }
}
