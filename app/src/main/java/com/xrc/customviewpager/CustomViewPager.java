package com.xrc.customviewpager;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.LinkedList;

public class CustomViewPager extends FrameLayout {

    private Context mContext;

    private boolean mHandleInterceptEvent = true;

    private int mCurrentIndex = 0;

    private final int mMaxLimit = 3;

    private float mDownX;

    private LinkedList<View> mCacheView = new LinkedList<>();

    private DataBean[] mDataBeans = {new DataBean("第1张", R.mipmap.ic_launcher),
            new DataBean("第2张", R.mipmap.ic_launcher_round),
            new DataBean("第3张", R.mipmap.ic_launcher),
            new DataBean("第4张", R.mipmap.ic_launcher_round),
            new DataBean("第5张", R.mipmap.ic_launcher),
            new DataBean("第6张", R.mipmap.ic_launcher_round),
            new DataBean("第7张", R.mipmap.ic_launcher),
            new DataBean("第8张", R.mipmap.ic_launcher),
            new DataBean("第9张", R.mipmap.ic_launcher)};

    public CustomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        layoutByCurrentIndex();
    }

    private void layoutByCurrentIndex() {
        int temp = 0;
        removeAllViews();
        for (int i = 0; i < mDataBeans.length; i++) {
            if (i == mCurrentIndex - 1) { //上一个
                View child;
                if (i < 2) {
                    child = mCacheView.get(i);
                } else {
                    child = mCacheView.get(1);
                }
                child.setRotation(-30);
                child.setTranslationX(-180);
                child.setScaleX(0.85f);
                child.setScaleY(0.85f);
                child.setTranslationZ(1);
                addView(child);
                temp++;
            } else if (i == mCurrentIndex) { //当前的
                View child;
                if (i < 2) {
                    child = mCacheView.get(i);
                } else {
                    child = mCacheView.get(2);
                }
                child.setRotation(0);
                child.setTranslationX(0);
                child.setScaleX(1);
                child.setScaleY(1);
                child.setTranslationZ(2);
                addView(child);
                temp++;
            } else if (i == mCurrentIndex + 1) { //下一个
                View child;
                if (i <= 2) {
                    child = mCacheView.get(i);
                } else {
                    child = mCacheView.get(3);
                }
                child.setRotation(30);
                child.setTranslationX(180);
                child.setScaleX(0.85f);
                child.setScaleY(0.85f);
                child.setTranslationZ(1);
                addView(child);
                temp++;
            }
            if (mCurrentIndex == 0 || mCurrentIndex == mDataBeans.length - 1) {
                if (temp == mMaxLimit - 1) {
                    break;
                }
            } else {
                if (temp == mMaxLimit) {
                    break;
                }
            }

        }
    }

    private void init() {
        generateItems();
    }

    private void generateItems() {
        for (int i = 0; i < mMaxLimit; i++) {
            DataBean dataBean = mDataBeans[i];
            View item = LayoutInflater.from(mContext).inflate(R.layout.item, this, false);
            ImageView iv = item.findViewById(R.id.iv_icon);
            TextView tv = item.findViewById(R.id.tv_name);
            iv.setImageResource(dataBean.getDrawableRes());
            tv.setText(dataBean.getName());
            mCacheView.add(item);
        }
    }

    private View generateItemByIndex(int index) {
        DataBean dataBean = mDataBeans[index];
        View item = LayoutInflater.from(mContext).inflate(R.layout.item, this, false);
        ImageView iv = item.findViewById(R.id.iv_icon);
        TextView tv = item.findViewById(R.id.tv_name);
        iv.setImageResource(dataBean.getDrawableRes());
        tv.setText(dataBean.getName());
        return item;
    }

    /**
     * 保证缓存里最多只有最新的5个view，3个用来显示，2个用来预加载
     */
    private void changeCacheAndRefresh(int newIndex) {
        if (newIndex > mCurrentIndex) { //往右滑动
            if (newIndex < mDataBeans.length - 2) {
                mCacheView.add(generateItemByIndex(newIndex + 2));
                if (mCacheView.size() > 5) { //数量超过5个移除第一个元素
                    mCacheView.remove(0);
                }
            } else { //移除第一个元素
                mCacheView.remove(0);
            }
        } else { //往左滑动
            if (newIndex >= 2) {
                mCacheView.add(0, generateItemByIndex(newIndex - 2));
                if (mCacheView.size() > 5) { //数量超过5个移除最后一个元素
                    mCacheView.remove(5);
                }
            } else {
                if (newIndex + 2 <= mCacheView.size()) { //移除最后一个元素
                    mCacheView.remove(mCacheView.size() - 1);
                }
            }
        }
        mCurrentIndex = newIndex;
        print();
        layoutByCurrentIndex();
    }

    private void print() {
        for (int i = 0; i < mCacheView.size(); i++) {
            ViewGroup vp = (ViewGroup) mCacheView.get(i);
            TextView tv = (TextView) vp.getChildAt(1);
            Log.e("tag", tv.getText().toString());
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (mCurrentIndex == 2 && mHandleInterceptEvent) {
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mDownX = ev.getX();
                    return true;
                case MotionEvent.ACTION_UP:
                    if (ev.getX() - mDownX < 0) {
                        Toast.makeText(mContext, "拦截事件", Toast.LENGTH_SHORT).show();
                        mHandleInterceptEvent = false;
                        return true;
                    }
                    break;
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownX = event.getX();
                return true;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (mCurrentIndex < mDataBeans.length) {
                    float moveX = event.getX() - mDownX;
                    if (moveX > 0) { //左
                        if (mCurrentIndex > 0) {
                            changeCacheAndRefresh(mCurrentIndex - 1);
                        }
                    } else { //右
                        if (mCurrentIndex < mDataBeans.length - 1) {
                            changeCacheAndRefresh(mCurrentIndex + 1);
                        }
                    }
                    return true;
                }
        }
        return super.onTouchEvent(event);
    }
}
