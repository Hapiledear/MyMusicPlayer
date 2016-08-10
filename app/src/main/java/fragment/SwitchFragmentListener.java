package fragment;

/**
 * Created by Administrator on 2016/8/3.
 * 专门用于fragment中切换的回调接口
 * 在activity中实现接口及其方法。
 * 在fragment中拿到接口的引用，并调用相关方法。
 */
public interface SwitchFragmentListener {
    public void switchToOnline();
    public void switchToMain();
    public void switchToAbout();

    public void openMenu();
}
