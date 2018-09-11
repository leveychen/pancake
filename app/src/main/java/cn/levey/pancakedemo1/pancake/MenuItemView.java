package cn.levey.pancakedemo1.pancake;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import android.widget.Toast;

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

    private float yDown = 0f;
    private float yUp = 0f;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                yDown = ev.getRawY();
                System.out.println("dispatchTouchEvent ACTION_DOWN");
                //isClickEvent = true;

                return super.dispatchTouchEvent(ev);
            case MotionEvent.ACTION_MOVE:
                System.out.println("dispatchTouchEvent ACTION_MOVE");
                //isClickEvent = false;
                return super.dispatchTouchEvent(ev);

            case MotionEvent.ACTION_UP:

                yUp = ev.getRawY();
                System.out.println("dispatchTouchEvent ACTION_UP = ");

                if(Math.abs(yUp - yDown) < 100){
                    //if(isClickEvent){
                    // menu.performClick();
                    Toast.makeText(getContext(),"点击了 ",Toast.LENGTH_SHORT).show();
                    return true;
                    //}
                }else {
                    return super.dispatchTouchEvent(ev);
                }
        }
        return super.dispatchTouchEvent(ev);

    }
}
