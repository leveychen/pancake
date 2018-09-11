package cn.levey.pancakedemo1.pancake;


/**
 * Created by Levey on 2018/9/11 10:03.
 * e-mail: m@levey.cn
 */

public interface OnPancakeListener {
    void open(); //顶部菜单打开回调
    void close(); //顶部菜单关闭回调
    void process(boolean isMoveUp,float process); //顶部菜单移动过程比例回调
}
