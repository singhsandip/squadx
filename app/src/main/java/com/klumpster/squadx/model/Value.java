package com.klumpster.squadx.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by BOX on 07-01-2018.
 */

public class Value {
    @SerializedName("x")
    @Expose
    private int x;
    @SerializedName("y")
    @Expose
    private double y;

    public Integer getX() {
        return x;
    }

    public void setX(Integer x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }
}
