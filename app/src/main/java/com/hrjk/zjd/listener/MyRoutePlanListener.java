package com.hrjk.zjd.listener;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.baidu.navisdk.adapter.BNRoutePlanNode;
import com.baidu.navisdk.adapter.BaiduNaviManager;
import com.hrjk.zjd.UI.activity.BNDemoGuideActivity;

/**
 * Created by 15137 on 2016/10/31.
 */

public class MyRoutePlanListener implements BaiduNaviManager.RoutePlanListener {
    private BNRoutePlanNode mBNRoutePlanNode = null;
    private Activity activity;

    public MyRoutePlanListener(BNRoutePlanNode node,Activity activity) {
        mBNRoutePlanNode = node;
        this.activity = activity;
    }

    @Override
    public void onJumpToNavigator() {
        /*
			 * 设置途径点以及resetEndNode会回调该接口
			 */

       /*// for (Activity ac : activityList) {

            if (ac.getClass().getName().endsWith("BNDemoGuideActivity")) {

                return;
            }
       // }*/
        Toast.makeText(activity, "开始导航！！", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(activity, BNDemoGuideActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("routePlanNode", (BNRoutePlanNode) mBNRoutePlanNode);
        intent.putExtras(bundle);
        activity.startActivity(intent);
    }

    @Override
    public void onRoutePlanFailed() {
        // TODO Auto-generated method stub
        Toast.makeText(activity, "算路失败", Toast.LENGTH_SHORT).show();
    }
}
