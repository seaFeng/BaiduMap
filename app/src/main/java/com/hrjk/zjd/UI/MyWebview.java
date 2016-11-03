package com.hrjk.zjd.UI;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.hrjk.zjd.utils.WebAppInterface;

import java.net.URL;

/**
 * Created by 15137 on 2016/10/27.
 */

public class MyWebview extends WebView {
    Context context;

    public MyWebview(Context context) {
        super(context);
        this.context = context;
    }

    public MyWebview(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public MyWebview(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public MyWebview(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context = context;
    }

    /**
     *  一些webview的初始化设置。
     */
    public void init() {
        WebSettings settings = getSettings();
        settings.setJavaScriptEnabled(true);
        setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }
        });
        addJavascriptInterface(new WebAppInterface(context),"Android");
    }
}
