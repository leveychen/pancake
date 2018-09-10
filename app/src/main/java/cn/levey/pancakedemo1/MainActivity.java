package cn.levey.pancakedemo1;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    private int mHiddenViewMeasuredHeight = 1200;

    private static final float MIN_MOVE = 200f;

    private boolean IS_PANCAKE_OPEN = false;

    private LinearLayout layoutPancake;
    private RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recycler_view);
        layoutPancake = findViewById(R.id.layout_pancake);
        LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);
        RvAdapter adapter = new RvAdapter(R.layout.rv_item_data, getFakeList());
        recyclerView.setAdapter(adapter);
        setGestureListener();


    }



    private float mPosY,mCurPosY;

    @SuppressLint("ClickableViewAccessibility")
    private void setGestureListener(){
        layoutPancake.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:
                        mPosY = event.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        mCurPosY = event.getY();

                        break;
                    case MotionEvent.ACTION_UP:
                        if (mCurPosY - mPosY > 0
                                && (Math.abs(mCurPosY - mPosY) > MIN_MOVE)) {
                            //向下滑動
                            if(!IS_PANCAKE_OPEN){
                                animateRun();
                            }

                        } else if (mCurPosY - mPosY < 0
                                && (Math.abs(mCurPosY - mPosY) > MIN_MOVE)) {
                            //向上滑动
                            if(IS_PANCAKE_OPEN){
                                animateRun();
                            }
                        }

                        break;
                }
                return true;
            }

        });
    }


    private void animateRun() {
        int origHeight = layoutPancake.getHeight();
        ValueAnimator animator;
        if(IS_PANCAKE_OPEN){
            animator = createDropAnimator(layoutPancake, origHeight, origHeight - mHiddenViewMeasuredHeight);
        }else {
            animator = createDropAnimator(layoutPancake, origHeight, origHeight + mHiddenViewMeasuredHeight);
        }
        animator.start();
        this.IS_PANCAKE_OPEN = !IS_PANCAKE_OPEN;

    }
    private ValueAnimator createDropAnimator(final View v, int start, int end) {
        ValueAnimator animator = ValueAnimator.ofInt(start, end);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator arg0) {
                int value = (int) arg0.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = v.getLayoutParams();
                layoutParams.height = value;
                v.setLayoutParams(layoutParams);
            }
        });
        return animator;
    }

    private List<RvItemData> getFakeList(){
        List<RvItemData> list = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            RvItemData item = new RvItemData();
            item.title = "这是标题 " + (i + 1);
            item.content = "这是文章正文内容这是文章正文内容这是文章正文内容这是文章正文内容这是文章正文内容" + (i + 1);
            item.author = "作者" + (i + 1);
            list.add(item);
        }
        return list;
    }
}
