package cn.levey.pancakedemo1;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewTreeObserver;

import com.chad.library.adapter.base.BaseQuickAdapter;

import java.util.ArrayList;
import java.util.List;

import cn.levey.pancakedemo1.data.RvAdapter;
import cn.levey.pancakedemo1.data.RvItemData;
import cn.levey.pancakedemo1.pancake.OnPancakeListener;
import cn.levey.pancakedemo1.pancake.PancakeLayout;
import cn.levey.pancakedemo1.utils.ScreenUtil;


/**
 * Created by Levey on 2018/9/10 15:42.
 * e-mail: m@levey.cn
 */

public class MainActivity extends AppCompatActivity {

    private PancakeLayout layoutPancake;
    private View menu1,menu2,menu3,menu4,menu5;

    private SparseArray<Integer[]> map = new SparseArray<>();

    private static final int layoutMenuContainerHeight =  460;
    private static final int layoutMenuHeaderHeight =  100;

    private boolean isFirstVisible;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pancake);
        layoutPancake = findViewById(R.id.layout_pancake);


        RecyclerView recyclerView = findViewById(R.id.recycler_view);

        LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);
        RvAdapter adapter = new RvAdapter(R.layout.rv_item_data, getFakeList());
        recyclerView.setAdapter(adapter);

        layoutPancake.setLayoutContainerMaxHeight(ScreenUtil.dp2px(layoutMenuContainerHeight));
        layoutPancake.setLayoutContainerMinHeight(ScreenUtil.dp2px(layoutMenuHeaderHeight));


        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

                if(position % 2 ==0){
                    layoutPancake.open();
                }else {
                    layoutPancake.close();
                }
            }
        });
        menu1 = findViewById(R.id.menu_1);
        menu2 = findViewById(R.id.menu_2);
        menu3 = findViewById(R.id.menu_3);
        menu4 = findViewById(R.id.menu_4);
        menu5 = findViewById(R.id.menu_5);





        layoutPancake.setOnPancakeListener(new OnPancakeListener() {
            @Override
            public void open() {

            }

            @Override
            public void close() {

            }

            @Override
            public void process(boolean isMoveUp, float process) {

                moveViewByLayout(menu1,75 * process,250 * process);
                moveViewByLayout(menu2,15 * process,50 * process);
                moveViewByLayout(menu4,-15 * process,50 * process);
                moveViewByLayout(menu5,-75 * process,250 * process);
            }

        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                 if (dy > 0) {
                     if(layoutPancake.isContainerOpen()){
                         System.out.println("layoutPancake = layoutPancake");
                         layoutPancake.close();
                     }
                 }
            }
        });


        ViewTreeObserver viewTreeObserver = layoutPancake.getViewTreeObserver();
        viewTreeObserver.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                if(!isFirstVisible) {
                    addMenu(menu1,"menu1");
                    addMenu(menu2,"menu2");
                    addMenu(menu3,"menu3");
                    addMenu(menu4,"menu4");
                    addMenu(menu5,"menu5");
                    isFirstVisible = true;
                }
                return true;
            }
        });


    }

//    private boolean isClickEvent = false;
//
//    private float yDown = 0f;
//    private float yUp = 0f;

    private void addMenu(final View menu, final String name){
        int left = menu.getLeft();
        int top = menu.getTop();
        map.put(menu.getId(),new Integer[]{left,top});




//        menu.setOnTouchListener(new View.OnTouchListener() {
//            @SuppressLint("ClickableViewAccessibility")
//            @Override
//            public boolean onTouch(View view, MotionEvent ev) {
//
//                switch (ev.getAction()){
//                    case MotionEvent.ACTION_DOWN:
//                        yDown = ev.getRawY();
//                        System.out.println("onTouch ACTION_DOWN");
//                        //isClickEvent = true;
//                        view.dispatchTouchEvent(ev);
//                        return true;
//
//                    case MotionEvent.ACTION_MOVE:
//                        System.out.println("onTouch ACTION_MOVE");
//                        //isClickEvent = false;
//                        view.dispatchTouchEvent(ev);
//                        return true;
//
//                    case MotionEvent.ACTION_UP:
//
//                        yUp = ev.getRawY();
//                        System.out.println("onTouch ACTION_UP = ");
//
//                        if(Math.abs(yUp - yDown) < 100){
//                            //if(isClickEvent){
//                                // menu.performClick();
//                                Toast.makeText(getApplicationContext(),"点击了 " + name,Toast.LENGTH_SHORT).show();
//                                view.dispatchTouchEvent(ev);
//                                return true;
//                            //}
//                        }else {
//                            return true;
//                        }
//                }
//                return true;
//            }
//        });

    }

    private List<RvItemData> getFakeList(){
        List<RvItemData> list = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            RvItemData item = new RvItemData();
            item.title = (i % 2 == 0) ? "点我就能打开了"  + (i + 1) : "点一下就关闭" + (i + 1);
            item.content = "这是文章正文内容这是文章正文内容这是文章正文内容这是文章正文内容这是文章正文内容" + (i + 1);
            item.author = "作者" + (i + 1);
            list.add(item);
        }
        return list;
    }


    private void moveViewByLayout(View view, float x, float y) {
        int left,top,right,bottom;
        left = (int) (map.get(view.getId())[0] + x);
        top = (int) (map.get(view.getId())[1] + y);
        right = left + view.getWidth();
        bottom = top + view.getHeight();
        view.layout(left, top, right, bottom);
    }


}
