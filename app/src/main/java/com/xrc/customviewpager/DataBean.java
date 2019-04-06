package com.xrc.customviewpager;

public class DataBean {
    private String name;
    private int drawableRes;

    public DataBean(String name, int drawableRes) {
        this.name = name;
        this.drawableRes = drawableRes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDrawableRes() {
        return drawableRes;
    }

    public void setDrawableRes(int drawableRes) {
        this.drawableRes = drawableRes;
    }
}
