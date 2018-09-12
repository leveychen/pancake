package cn.levey.pancakedemo1.pancake;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.RelativeLayout;

import cn.levey.pancakedemo1.utils.ScreenUtil;


/**
 * Created by Levey on 2018/9/11 09:42.
 * e-mail: m@levey.cn
 */

public class PancakeLayout extends RelativeLayout implements
        View.OnTouchListener {


    /**
     * 滑动布局监听器
     */

    private OnPancakeListener onPancakeListener;

    /**
     * 顶部容器的最大高度
     */
    private int layoutContainerMaxHeight = 0;

    /**
     * 顶部容器的最小高度
     */
    private int layoutContainerMinHeight = 0;

    /**
     * 最小的滑动距离，小于该值判定为此次滑动无效
     */
    private int minScrollDistance = ScreenUtil.dp2px(70);

    /**
     * 顶部容器是否展开
     */
    private boolean isContainerOpen;

    /**
     * 容器打开关闭的速度，越大越快
     */
    private int scrollSpeed = 50;



    /**
     * 滚动显示和隐藏上侧布局时，手指滑动需要达到的速度。
     */
    private static final int SNAP_VELOCITY = 200;

    /**
     * 滑动状态的一种，表示未进行任何滑动。
     */
    private static final int DO_NOTHING = 0;
    /**
     * 滑动状态的一种，表示正在滑出底部界面。
     */
    private static final int SHOW_UP_MENU = 1;

    /**
     * 滑动状态的一种，表示正在隐藏底部界面。
     */
    private static final int HIDE_UP_MENU = 3;

    /**
     * 记录当前的滑动状态
     */
    private int slideState;

    /**
     * 屏幕搞度值。
     */
    private int screenHeight;



    /**
     * 记录手指按下时的纵坐标。
     */
    private float yDown;


    /**
     * 记录手机抬起时的纵坐标。
     */
    private float yUp;


    /**
     * 是否正在滑动。
     */
    private boolean isSliding;


    /**
     * 是否是新的一次按下，仅判断第一次滑动方向，作为最终滑动方向，防止反向滑动导致错乱。
     */

    private boolean isFirsTouchDown;

    /**
     * 内容布局对象。
     */
    private View contentLayout;

    /**
     * 用于监听滑动事件的View。
     */
    private View mBindView;

    /**
     * 顶部布局参数。
     */
    private MarginLayoutParams upMenuLayoutParams;

    /**
     * 内容布局的参数。
     */
    private RelativeLayout.LayoutParams contentLayoutParams;

    /**
     * 用于计算手指滑动的速度。
     */
    private VelocityTracker mVelocityTracker;


    public void setLayoutContainerMinHeight(int layoutContainerMinHeight) {
        this.layoutContainerMinHeight = layoutContainerMinHeight;
    }

    public void setLayoutContainerMaxHeight(int layoutContainerMaxHeight) {
        this.layoutContainerMaxHeight = layoutContainerMaxHeight;
    }

    public void setOnPancakeListener(OnPancakeListener onPancakeListener) {
        this.onPancakeListener = onPancakeListener;
    }

    public boolean isContainerOpen() {
        return isContainerOpen;
    }


    public void setMinMoveDistance(int minScrollDistance) {
        this.minScrollDistance = minScrollDistance;
    }


    public void setScrollSpeed(int scrollSpeed) {
        this.scrollSpeed = scrollSpeed;
    }

    public PancakeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        screenHeight = ScreenUtil.getScreenHeight(getContext());
    }

    /**
     * 绑定监听滑动事件的View。
     *
     * @param bindView
     *            需要绑定的View对象。
     */
    public void setScrollEvent(View bindView) {
        mBindView = bindView;
        mBindView.setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        // TODO Auto-generated method stub
        createVelocityTracker(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 手指按下时，记录按下时的坐标
                yDown = event.getRawY();
                // 将滑动状态初始化为DO_NOTHING
                slideState = DO_NOTHING;
                isFirsTouchDown = true;
                break;
            case MotionEvent.ACTION_MOVE:
                //记录手指移动时的纵坐标
                float yMove = event.getRawY();
                int moveDistanceY = (int) (yMove - yDown);
                // 检查当前的滑动状态

                checkSlideState(moveDistanceY);
                switch (slideState) {
                    case SHOW_UP_MENU:
                        if(contentLayoutParams.topMargin >= layoutContainerMaxHeight || moveDistanceY <=0) break;
                        moveDistanceY = layoutContainerMinHeight + moveDistanceY;
                        if(moveDistanceY > layoutContainerMaxHeight) moveDistanceY = layoutContainerMaxHeight;
                        contentLayoutParams.topMargin = moveDistanceY;
                        contentLayout.setLayoutParams(contentLayoutParams);
                        sendProcess();
                        break;
                    case HIDE_UP_MENU:
                        if(contentLayoutParams.topMargin <= layoutContainerMinHeight || moveDistanceY >= 0 ) break;
                        moveDistanceY = layoutContainerMaxHeight + moveDistanceY;
                        if(moveDistanceY < layoutContainerMinHeight) moveDistanceY = layoutContainerMinHeight;
                        contentLayoutParams.topMargin = moveDistanceY;
                        contentLayout.setLayoutParams(contentLayoutParams);
                        sendProcess();
                        break;
                    default:
                        break;
                }
                break;
            case MotionEvent.ACTION_UP:
                yUp = event.getRawY();

                if( Math.abs(yUp - yDown) < minScrollDistance) {
                    if(!isContainerOpen){
                        closeContainer();
                    }else {
                        openContainer();
                    }
                    return false;

                }
                if (isSliding) {
                    // 手指抬起时，进行判断当前手势的意图
                    switch (slideState) {
                        case SHOW_UP_MENU:
                            if (shouldScrollToUpMenu()) {
                                openContainer();
                            } else {
                                closeContainer();
                            }
                            break;
                        case HIDE_UP_MENU:
                            if (shouldScrollToContentFromUpMenu()) {
                                closeContainer();
                            } else {
                                openContainer();
                            }
                            break;
                        default:
                            break;
                    }
                }

                break;
        }
        recycleVelocityTracker();
        return true;
    }

    /**
     * 创建VelocityTracker对象，并将触摸事件加入到VelocityTracker当中。
     *
     * @param event
     *
     */
    private void createVelocityTracker(MotionEvent event) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
    }

    /**
     * 根据手指移动的距离，判断当前用户的滑动意图，然后给slideState赋值成相应的滑动状态值。
     * @param moveDistanceY
     *            纵向移动的距离
     */
    private void checkSlideState(int moveDistanceY) {


        if( moveDistanceY < 0 && isFirsTouchDown){
            isSliding = true;
            slideState = HIDE_UP_MENU;
            isFirsTouchDown = false;
            return;
        }

        if(moveDistanceY > 0 && isFirsTouchDown){
            isSliding = true;
            slideState = SHOW_UP_MENU;
            isFirsTouchDown = false;
            return;
        }

        if(moveDistanceY == 0){
            isSliding = false;
            slideState = DO_NOTHING;

        }

    }

    /**
     * 判断是否应该滚动将底部界面展示出来。如果手指移动距离大于屏幕宽度的1/4，或者手指移动速度大于SNAP_VELOCITY，
     * 就认为应该滚动将底部界面展示出来。
     *
     * @return 如果应该将底部界面展示出来返回true，否则返回false。
     */
    private boolean shouldScrollToUpMenu() {
        return yUp - yDown > upMenuLayoutParams.height / 4
                || getScrollVelocity() > SNAP_VELOCITY;
    }

    /**
     * 判断是否应该从底部界面滚动到内容布局，如果手指移动距离大于屏幕宽度的1/4，或者手指移动速度大于SNAP_VELOCITY，
     * 就认为应该从底部界面滚动到内容布局。
     *
     * @return 如果应该从底部界面滚动到内容布局返回true，否则返回false。
     */
    private boolean shouldScrollToContentFromUpMenu() {
        return yDown - yUp > upMenuLayoutParams.height / 4
                || getScrollVelocity() > SNAP_VELOCITY;
    }

    /**
     * 获取手指在绑定布局上的滑动速度。
     *
     * @return 滑动速度，以每秒钟移动了多少像素值为单位。
     */
    private int getScrollVelocity() {
        mVelocityTracker.computeCurrentVelocity(200);
        int velocity = (int) mVelocityTracker.getXVelocity();
        return Math.abs(velocity);
    }

    @SuppressLint("StaticFieldLeak")
    class MenuScrollTask extends AsyncTask<Integer, Integer, Integer> {

        @Override
        protected Integer doInBackground(Integer... speed) {
            int topMargin = contentLayoutParams.topMargin;
            // 根据传入的速度来滚动界面，当滚动到达边界值时，跳出循环。
            while (true) {
                topMargin = topMargin - speed[0];
                if (topMargin > layoutContainerMaxHeight) {
                    topMargin = layoutContainerMaxHeight;
                    break;
                }
                if (topMargin < layoutContainerMinHeight) {
                    topMargin = layoutContainerMinHeight;
                    break;
                }
                publishProgress(topMargin);
                // 为了要有滚动效果产生，每次循环使线程睡眠一段时间，这样肉眼才能够看到滚动动画。
                sleep(10);
            }
            isSliding = false;
            return topMargin;
        }

        @Override
        protected void onProgressUpdate(Integer... topMargin) {
            contentLayoutParams.topMargin = topMargin[0];
            contentLayout.setLayoutParams(contentLayoutParams);
            sendProcess();
            unFocusBindView();
        }

        @Override
        protected void onPostExecute(Integer topMargin) {
            contentLayoutParams.topMargin = topMargin;
            contentLayout.setLayoutParams(contentLayoutParams);
            sendProcess();
        }
    }

    /**
     * 使用监听将拖拽进度抛回。
     */

    private void sendProcess(){
        if(onPancakeListener !=null) onPancakeListener.process( slideState == SHOW_UP_MENU,(float) (contentLayoutParams.topMargin - layoutContainerMinHeight) / (float)(layoutContainerMaxHeight - layoutContainerMinHeight));
    }

    /**
     * 使当前线程睡眠指定的毫秒数。
     *
     * @param millis
     *            指定当前线程睡眠多久，以毫秒为单位
     */
    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 使用可以获得焦点的控件在滑动的时候失去焦点。
     */
    private void unFocusBindView() {
        if (mBindView != null) {
            mBindView.setPressed(false);
            mBindView.setFocusable(false);
            mBindView.setFocusableInTouchMode(false);
        }
    }

    /**
     * 将界面滚动到底部界面界面，滚动速度设定为scrollSpeed.
     */
    public void openContainer() {
        new MenuScrollTask().execute(-scrollSpeed);
        if(onPancakeListener != null) onPancakeListener.open();
        isContainerOpen = true;
    }

    /**
     * 将界面从底部界面滚动到内容界面，滚动速度设定为scrollSpeed.
     */
    public void closeContainer() {
        new MenuScrollTask().execute(scrollSpeed);
        if(onPancakeListener != null) onPancakeListener.close();
        isContainerOpen = false;
    }

    /**
     * 在onLayout中重新设定底部界面、下侧菜单、以及内容布局的参数。
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (changed) {
            /*
             * 底部界面布局对象。
             */
            View layoutMenu = getChildAt(0);
            upMenuLayoutParams = (MarginLayoutParams) layoutMenu
                    .getLayoutParams();
            setScrollEvent(getRootView());
            // 获取内容布局对象
            contentLayout = getChildAt(1);
            contentLayout.setVisibility(VISIBLE);
            contentLayoutParams = (RelativeLayout.LayoutParams) contentLayout
                    .getLayoutParams();
            contentLayoutParams.height = screenHeight;
            contentLayoutParams.topMargin = layoutContainerMinHeight;
            contentLayout.setLayoutParams(contentLayoutParams);
        }
    }

    /**
     * 回收VelocityTracker对象。
     */
    private void recycleVelocityTracker() {
        mVelocityTracker.recycle();
        mVelocityTracker = null;
    }

    public void open(){
        openContainer();
    }

    public void close(){
        closeContainer();
    }




    private RecyclerView recyclerView;
    //抛出方法，传入RecyclerView
    public void setRecyclerView(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }

    private float iYDown = 0f;
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                iYDown = ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                float iYUp = ev.getRawY();
                //当下拉幅度超过最小滑动值，且 Menu容器未打开，RecyclerView 处于顶部时出发Menu打开
                if(iYUp - iYDown > minScrollDistance && !isContainerOpen && recyclerView != null && !recyclerView.canScrollVertically(-1)) {
                    openContainer();
                    return true;
                }
            break;
            default:
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override //销毁RV
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if(recyclerView != null){
            recyclerView = null;
        }
    }
}