package com.hrjk.zjd;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

import com.hrjk.zjd.UI.MyWebview;
import com.hrjk.zjd.utils.WebAppInterface;

public class MainActivity extends AppCompatActivity {
    MyWebview webView ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initWebView();
    }

    private void initWebView() {
        webView = (MyWebview) findViewById(R.id.webView);
        webView.init();
        webView.loadUrl("file:///android_asset/test.html");
        webView.addJavascriptInterface(new WebAppInterface(this),"android");
    }


}
