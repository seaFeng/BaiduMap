package com.hrjk.zjd.UI.activity;

import android.annotation.TargetApi;
import android.app.Application;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.navisdk.adapter.BNOuterLogUtil;
import com.baidu.navisdk.adapter.BNOuterTTSPlayerCallback;
import com.baidu.navisdk.adapter.BNRoutePlanNode;
import com.baidu.navisdk.adapter.BNaviSettingManager;
import com.baidu.navisdk.adapter.BaiduNaviManager;
import com.hrjk.zjd.MyApplication;
import com.hrjk.zjd.R;
import com.hrjk.zjd.listener.MyRoutePlanListener;
import com.hrjk.zjd.utils.MapMarkersUtils;
import com.hrjk.zjd.utils.runtimepermissions.PermissionsManager;
import com.hrjk.zjd.utils.runtimepermissions.PermissionsResultAction;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MapActivity extends AppCompatActivity {
    private MapView mMapView;
    public BaiduMap mBaiduMap;
    private MapMarkersUtils markersUtils;           // 生成标注。

    private static int HOME = 1;                    // 标注为家的地址。
    private static int ADDRESS = 2;                 // 标注为现居地。

    // 定位相关
    LocationClient mLocClient;
    boolean isFirstLoc = true; // 是否首次定位
    BitmapDescriptor mCurrentMarker;
    private static final int accuracyCircleFillColor = 0xAAFFFF88;
    private static final int accuracyCircleStrokeColor = 0xAA00FF00;

    private MyLocationListenner locationListenner= new MyLocationListenner();

    // 导航
    // private BNRoutePlanNode mBNRoutePlanNode = null;        // 算路节点。

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        requestPermissions();
        mMapView = (MapView) findViewById(R.id.map);
        mBaiduMap = mMapView.getMap();
        initMap();

        // 添加导航标注

        markersUtils = new MapMarkersUtils(this,mBaiduMap);
        markersUtils.creatMarker(new LatLng(39.915160800132085,116.40386525193937),HOME);
        markersUtils.creatMarker(new LatLng(40.057009624099436,116.30784537597782),ADDRESS);

        // 打开log开关
		//BNOuterLogUtil.setLogSwitcher(true);

		if (initDirs()) {
			initNavi();
		}
        /*markersUtils.registerGuideListenner(new MapMarkersUtils.GuideListenner() {
            @Override
            public void goDestination() {
                if (BaiduNaviManager.isNaviInited()) {
                    Toast.makeText(MapActivity.this, "开始导航", Toast.LENGTH_SHORT).show();
                    //routeplanToNavi();
                }
            }
        });*/


    }


    /**
     *  设置地图。
     */
    private void initMap() {
        // 设置地图类型：卫星地图
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        //mBaiduMap.setTrafficEnabled(true);
        //mBaiduMap.setBaiduHeatMapEnabled(true);
        mBaiduMap.setMyLocationEnabled(true);

        // 是否允许显示方向信息
        mLocClient = new LocationClient(this);
        mLocClient.registerLocationListener(locationListenner);
        // 设置定位参数
        getMapLocation();
        //mCurrentMarker = BitmapDescriptorFactory.fromResource(R.drawable.icon_geo);
        mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL,true,null));
        mLocClient.start();
    }

    /**
     *  配置定位参数。
     */
    private void getMapLocation(){
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setOpenGps(true);
        option.setCoorType("bd09ll");
        option.setScanSpan(1000);           // 设置扫描间隔。
        option.setLocationNotify(true);     //设置是否当GPS有效时按照1S/1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);     // 是否能返回文字的描述的位置信息。
        option.setIgnoreKillProcess(false);
        mLocClient.setLocOption(option);
    }

    /**
     *  定位sdk监听器。
     */
    public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            // 全局化定位信息
            MyApplication.sLatLng = new LatLng(bdLocation.getLatitude(),bdLocation.getLongitude());

            // mapView 被销毁以后不再处理新接收的位置。
            if (mLocClient == null || mMapView == null) {
                return;
            }
            MyLocationData locationData = new MyLocationData.Builder()
                        .accuracy(bdLocation.getRadius())
                        .direction(100)
                        .latitude(bdLocation.getLatitude())
                        .longitude(bdLocation.getLongitude())
                        .build();
            mBaiduMap.setMyLocationData(locationData);
            //Toast.makeText(MapActivity.this, bdLocation.getLocationDescribe(), Toast.LENGTH_SHORT).show();

            if (isFirstLoc) {
                isFirstLoc = false;
                LatLng ll = new LatLng(bdLocation.getLatitude(),bdLocation.getLongitude());
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(ll).zoom(18.0f);                  // 设置地图的中心点，设置地图的缩放比例。
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
            }
            //Toast.makeText(MapActivity.this, "" + bdLocation.getLocType(), Toast.LENGTH_SHORT).show();
        }
    }

    @TargetApi(23)
    private void requestPermissions() {
        PermissionsManager.getInstance().requestAllManifestPermissionsIfNecessary(this, new PermissionsResultAction() {
            @Override
            public void onGranted() {
                Toast.makeText(MapActivity.this, "All permissions have been granted", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDenied(String permission) {
                Toast.makeText(MapActivity.this, "Permission " + permission + " has been denied", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String mSDCardPath = null;
    private static final String APP_FOLDER_NAME = "BNSDKSimpleDemo";
    String authinfo = null;

    private void initNavi() {
        BNOuterTTSPlayerCallback ttsCallback = null;

        BaiduNaviManager.getInstance().init(this, mSDCardPath, APP_FOLDER_NAME, new BaiduNaviManager.NaviInitListener() {
            @Override
            public void onAuthResult(int status, String msg) {
                if (0 == status) {
                    authinfo = "key校验成功!";
                } else {
                    authinfo = "key校验失败, " + msg;
                }
                MapActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        Toast.makeText(MapActivity.this, authinfo, Toast.LENGTH_LONG).show();
                    }
                });
            }
            @Override
            public void initSuccess() {
                Toast.makeText(MapActivity.this, "百度导航引擎初始化成功", Toast.LENGTH_SHORT).show();
                initSetting();
            }

            public void initStart() {
                Toast.makeText(MapActivity.this, "百度导航引擎初始化开始", Toast.LENGTH_SHORT).show();
            }

            public void initFailed() {
                Toast.makeText(MapActivity.this, "百度导航引擎初始化失败", Toast.LENGTH_SHORT).show();
            }


        },  null, ttsHandler, ttsPlayStateListener);
    }

    private boolean initDirs() {
        mSDCardPath = getSdcardDir();
        if (mSDCardPath == null) {
            return false;
        }
        File f = new File(mSDCardPath, APP_FOLDER_NAME);
        if (!f.exists()) {
            try {
                f.mkdir();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    private String getSdcardDir() {
        if (Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
            return Environment.getExternalStorageDirectory().toString();
        }
        return null;
    }

    private void initSetting(){
        // 设置设置全程路况显示
        BNaviSettingManager.setShowTotalRoadConditionBar(BNaviSettingManager.PreViewRoadCondition.ROAD_CONDITION_BAR_SHOW_ON);
        // 设置导航播报模式
        BNaviSettingManager.setVoiceMode(BNaviSettingManager.VoiceMode.Veteran);
        // 是否开启路况
        BNaviSettingManager.setRealRoadCondition(BNaviSettingManager.RealRoadCondition.NAVI_ITS_ON);
    }

    /**
     * 内部TTS播报状态回传handler
     */
    private Handler ttsHandler = new Handler() {
        public void handleMessage(Message msg) {
            int type = msg.what;
            switch (type) {
                case BaiduNaviManager.TTSPlayMsgType.PLAY_START_MSG: {
                    showToastMsg("Handler : TTS play start");
                    break;
                }
                case BaiduNaviManager.TTSPlayMsgType.PLAY_END_MSG: {
                    showToastMsg("Handler : TTS play end");
                    break;
                }
                default :
                    break;
            }
        }
    };

    public void showToastMsg(final String msg) {
        MapActivity.this.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(MapActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 内部TTS播报状态回调接口
     */
    private BaiduNaviManager.TTSPlayStateListener ttsPlayStateListener = new BaiduNaviManager.TTSPlayStateListener() {

        @Override
        public void playEnd() {
            showToastMsg("TTSPlayStateListener : TTS play end");
        }

        @Override
        public void playStart() {
            showToastMsg("TTSPlayStateListener : TTS play start");
        }
    };



    /**
     *  导航设置
     */
    /*public void routeplanToNavi() {
        BNRoutePlanNode sNode = new BNRoutePlanNode(116.30784537597782, 40.057009624099436, "百度大厦", null, BNRoutePlanNode.CoordinateType.BD09LL);
        BNRoutePlanNode eNode = new BNRoutePlanNode(116.40386525193937, 39.915160800132085, "北京天安门", null, BNRoutePlanNode.CoordinateType.BD09LL);

       *//* public static final int	ROUTE_PLAN_MOD_AVOID_TAFFICJAM	16      //躲避拥堵
        public static final int	ROUTE_PLAN_MOD_MIN_DIST	4                   // 少走高速
        public static final int	ROUTE_PLAN_MOD_MIN_TIME	2                   // 高速优先
        public static final int	ROUTE_PLAN_MOD_MIN_TOLL	8                   // 少收费。
        public static final int	ROUTE_PLAN_MOD_RECOMMEND	1*//*             // 推荐

        if (sNode != null && eNode != null) {
            List<BNRoutePlanNode> list = new ArrayList<>();
            list.add(sNode);
            list.add(eNode);
            BaiduNaviManager.getInstance().launchNavigator(this,list,1,true,new MyRoutePlanListener(sNode,this));      // 是不是GPS，导航回调监听
        }

    }*/

    @Override
    protected void onDestroy() {
        // 退出时销毁定位
        mLocClient.stop();
        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);

        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
        mMapView = null;

        super.onDestroy();
    }
    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();


    }
    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理


        mMapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();

    }



    @Override
    protected void onStart() {
        super.onStart();
    }

    private BNOuterTTSPlayerCallback mTTSCallback = new BNOuterTTSPlayerCallback() {       //TTs播报回调接口。

        @Override
        public void stopTTS() {
            // TODO Auto-generated method stub
            Log.e("test_TTS", "stopTTS");
        }

        @Override
        public void resumeTTS() {
            // TODO Auto-generated method stub
            Log.e("test_TTS", "resumeTTS");
        }

        @Override
        public void releaseTTSPlayer() {
            // TODO Auto-generated method stub
            Log.e("test_TTS", "releaseTTSPlayer");
        }

        @Override
        public int playTTSText(String speech, int bPreempt) {
            // TODO Auto-generated method stub
            Log.e("test_TTS", "playTTSText" + "_" + speech + "_" + bPreempt);

            return 1;
        }

        @Override
        public void phoneHangUp() {
            // TODO Auto-generated method stub
            Log.e("test_TTS", "phoneHangUp");
        }

        @Override
        public void phoneCalling() {
            // TODO Auto-generated method stub
            Log.e("test_TTS", "phoneCalling");
        }

        @Override
        public void pauseTTS() {
            // TODO Auto-generated method stub
            Log.e("test_TTS", "pauseTTS");
        }

        @Override
        public void initTTSPlayer() {
            // TODO Auto-generated method stub
            Log.e("test_TTS", "initTTSPlayer");
        }

        @Override
        public int getTTSState() {
            // TODO Auto-generated method stub
            Log.e("test_TTS", "getTTSState");
            return 1;
        }
    };
}
