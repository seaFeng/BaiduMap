package com.hrjk.zjd.utils;

import android.content.Context;
import android.content.Intent;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import com.hrjk.zjd.UI.activity.MapActivity;

import java.util.Map;

/**
 * Created by 15137 on 2016/10/27.
 */

public class WebAppInterface {
    private Context context;

    public WebAppInterface(Context context) {
        this.context = context;
    }

    @JavascriptInterface
    public String getString() {
        return "返回一个字符串";
    }

    @JavascriptInterface
    public void toMapView() {
        context.startActivity(new Intent(context, MapActivity.class));
    }

    @JavascriptInterface
    public void showToast() {
        Toast.makeText(context, "js弹出了一个Toast", Toast.LENGTH_SHORT).show();
    }
}
