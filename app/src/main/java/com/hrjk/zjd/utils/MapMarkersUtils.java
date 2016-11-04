package com.hrjk.zjd.utils;

import android.app.Activity;
import android.app.backup.BackupDataInput;
import android.content.Context;
import android.view.View;
import android.widget.Button;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.navisdk.adapter.BNRoutePlanNode;
import com.baidu.navisdk.adapter.BaiduNaviManager;
import com.hrjk.zjd.MyApplication;
import com.hrjk.zjd.R;
import com.hrjk.zjd.listener.MyRoutePlanListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 15137 on 2016/11/1.
 *  生成地图标注并且导航的工具类。
 */

public class MapMarkersUtils  {
    private Activity context;
    private BaiduMap map;

    public MapMarkersUtils(Activity context,BaiduMap map){
        this.context = context;
        this.map = map;
    }

    /**
     *  在百度地图上创建一个标记.
     * @param flag      标记
     */
    public void creatMarker(LatLng latLng,int flag) {
        BitmapDescriptor bitmap;
        switch(flag) {
            case 1 :
                bitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_marka);
                break;
            case 2 :
                bitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_markb);
                break;
            case 3 :
                bitmap= BitmapDescriptorFactory.fromResource(R.drawable.icon_markc);
                break;
            default:
                return;
        }
        // 定义一个标记点
        MarkerOptions options = new MarkerOptions()
                    .position(latLng)
                    .icon(bitmap);
        // 将标记点添加到地图上。
        map.addOverlay(options);
        map.setOnMarkerClickListener(new MyOnMarkerClikerListener(context,latLng,map));
    }

    /**
     *  标注点击监听器。
     */
    public class MyOnMarkerClikerListener implements BaiduMap.OnMarkerClickListener{

        private Context context;
        private LatLng latLng;
        private BaiduMap map;

        public MyOnMarkerClikerListener(Context context,LatLng latLng,BaiduMap map){
            this.context = context;
            this.latLng = latLng;
            this.map = map;
        }

        @Override
        public boolean onMarkerClick(Marker marker)  {
            Button btn = new Button(context);
            btn.setBackgroundResource(R.drawable.popup);
            btn.setText("点击去这里");
            btn.setOnClickListener(new MOnclickListenner(MyApplication.sLatLng,marker.getPosition()));       // 起点,终点
            InfoWindow infoWindow = new InfoWindow(btn,marker.getPosition(),-50);
            map.showInfoWindow(infoWindow);
            return true;
        }
    }

    /*private GuideListenner guideListenner = null;
    *//**
     *  注册导航监听
     *//*
    public void registerGuideListenner (GuideListenner listenner) {
        guideListenner = listenner;
    }*/

    /**
     *  弹出窗口的点击监听器。
     */
    private class MOnclickListenner implements View.OnClickListener {

        private LatLng sLatLng;
        private LatLng eLatLng;

        public MOnclickListenner(LatLng sLatLng,LatLng eLatLng){
            this.sLatLng = sLatLng;
            this.eLatLng = eLatLng;
        }

        @Override
        public void onClick(View v) {
            //guideListenner.goDestination();
            routeplanToNavi(sLatLng,eLatLng);
            map.hideInfoWindow();
        }
    }



    public void routeplanToNavi(LatLng sLatLng,LatLng eLatLng) {
        BNRoutePlanNode sNode = new BNRoutePlanNode(sLatLng.longitude, sLatLng.latitude, "当前位置", null, BNRoutePlanNode.CoordinateType.BD09LL);
        BNRoutePlanNode eNode = new BNRoutePlanNode(eLatLng.longitude, sLatLng.latitude, "北京天安门", null, BNRoutePlanNode.CoordinateType.BD09LL);

       /* public static final int	ROUTE_PLAN_MOD_AVOID_TAFFICJAM	16      //躲避拥堵
        public static final int	ROUTE_PLAN_MOD_MIN_DIST	4                   // 少走高速
        public static final int	ROUTE_PLAN_MOD_MIN_TIME	2                   // 高速优先
        public static final int	ROUTE_PLAN_MOD_MIN_TOLL	8                   // 少收费。
        public static final int	ROUTE_PLAN_MOD_RECOMMEND	*/             // 推荐

        if (sNode != null && eNode != null) {
            List<BNRoutePlanNode> list = new ArrayList<>();
            list.add(sNode);
            list.add(eNode);
            BaiduNaviManager.getInstance().launchNavigator(context,list,1,true,new MyRoutePlanListener(sNode,context));      // 是不是GPS，导航回调监听
        }

    }
}
