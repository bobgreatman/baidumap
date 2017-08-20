package com.example.lewanjiang.baidumap;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolygonOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    private double[][] numhz={{30.1534663943,120.0235534475}
            ,{30.0968848098,120.0565849613}
            ,{30.0852527916,120.0886271134}
            ,{30.1101005124,120.1784629751}
            ,{30.1497003644,120.1634090399}
            ,{30.1546104659,120.1888000077}
            ,{30.1403509179,120.2138654489}
            ,{30.1508624996,120.2266650375}
            ,{30.1786553706,120.2265767468}
            ,{30.1877067213,120.2288439804}
            ,{30.1945899220,120.2406750481}
            ,{30.2287866084,120.2429149411}
            ,{30.2452478036,120.2211540746}
            ,{30.2775019801,120.2442298158}
            ,{30.3006049646,120.2961648959}
            ,{30.2656543013,120.3656936546}
            ,{30.3275291991,120.4095095282}
            ,{30.3489190679,120.3828981293}
            ,{30.3875371137,120.4019935732}
            ,{30.3417155281,120.3025746348}
            ,{30.3244259857,120.2762466345}
            ,{30.3371309331,120.2671819573}
            ,{30.3522655180,120.2478953506}
            ,{30.3733650184,120.2497016759}
            ,{30.3921400730,120.2463668709}
            ,{30.3969629117,120.2051743199}
            ,{30.3890286874,120.1920789693}
            ,{30.3794482159,120.1703110889}
            ,{30.3806885468,120.1647320941}
            ,{30.3821306788,120.1512621312}
            ,{30.3890667700,120.1380479623}
            ,{30.3474250629,120.1423027935}
            ,{30.3436778034,120.1230148638}
            ,{30.3429150159,120.1063789169}
            ,{30.3404033427,120.1005100690}
            ,{30.3379771961,120.0940727674}
            ,{30.3548496887,120.0688443240}
            ,{30.3455335632,120.0325723822}
            ,{30.3059009041,120.0376141847}
            ,{30.3032707777,120.0622285872}
            ,{30.2923417525,120.0630099976}
            ,{30.2838952746,120.0574795541}
            ,{30.2537931785,120.0606866518}};
    private MapView mapView = null;
    private BaiduMap baiduMap;
    public LocationClient mLocationClient = null;
    public BDAbstractLocationListener myListener = new MyLocationListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        mapView = (MapView) findViewById(R.id.bmapView);
        baiduMap = mapView.getMap();
        baiduMap.setMyLocationEnabled(true);

        MapStatusUpdate update = MapStatusUpdateFactory.zoomTo(16.f);
        baiduMap.animateMapStatus(update);
        mapView.getChildAt(2).setPadding(0,0,30,500);

        mLocationClient = new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener( myListener );
        List<String> per = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            per.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE)!= PackageManager.PERMISSION_GRANTED){
            per.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            per.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!per.isEmpty()){
            String[] permissions = per.toArray(new String[per.size()]);
            ActivityCompat.requestPermissions(MainActivity.this,permissions,1);
        } else
            requestLocation();

        FloatingActionButton fab=(FloatingActionButton)findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,MineActivity.class);
                startActivity(intent);
            }
        });

        drawhz();
    }

    private void requestLocation(){
        initLocatioin();
        mLocationClient.start();
    }

    private void initLocatioin(){
        LocationClientOption option = new LocationClientOption();
        option.setScanSpan(5000);
        mLocationClient.setLocOption(option);
        option.setCoorType("bd09ll");
    }

    private void navigateTo(BDLocation location){
        LatLng ll = new LatLng(location.getLatitude(),location.getLongitude());
        MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(ll);
        baiduMap.animateMapStatus(update);

        MyLocationData.Builder locationBuilder = new MyLocationData.Builder();
        locationBuilder.latitude(location.getLatitude());
        locationBuilder.longitude(location.getLongitude());
        MyLocationData locationData = locationBuilder.build();
        baiduMap.setMyLocationData(locationData);

        for (int i = 0;i < numhz.length;i++) {
            LatLng pt = new LatLng(numhz[i][0], numhz[i][1]);
            LatLng pt1 = new LatLng(30.2521521922,119.8370305517);
            if (DistanceUtil.getDistance(ll, pt) < 300){
                Intent intent = new Intent(MainActivity.this,MainActivity.class);
                PendingIntent pi = PendingIntent.getActivity(MainActivity.this,0,intent,0);
                NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                Notification notification = new android.support.v4.app.NotificationCompat.Builder(MainActivity.this)
                        .setContentTitle("注意交警")
                        .setContentText("您将进入禁摩区域")
                        .setWhen(System.currentTimeMillis())
                        .setSmallIcon(R.drawable.at)
                        .setContentIntent(pi)
                        .setAutoCancel(true)
                        .setDefaults(android.support.v4.app.NotificationCompat.DEFAULT_ALL)
                        .build();
                manager.notify(1,notification);
            }
        }

    }

    public class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location.getLocType() == BDLocation.TypeGpsLocation
                    || location.getLocType() == BDLocation.TypeNetWorkLocation) {
                navigateTo(location);
            }
        }
    }

    public void onRequestPermissionResult(int requestCode,String[] permissions,int[] grantResults){
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    for (int result:grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED){
                            Toast.makeText(this,"必须同意权限才能使用",Toast.LENGTH_LONG).show();
                            finish();
                            return;
                        }
                    }
                    requestLocation();
                } else {
                    Toast.makeText(this,"wrong",Toast.LENGTH_LONG).show();
                    finish();
                }
                break;
            default:
        }
    }

    protected void drawhz(){
        LatLng pt1 = new LatLng(30.2470808622,120.0566004094);
        LatLng pt2 = new LatLng(30.2134921357,120.0120987624);
        List<LatLng> pts = new ArrayList<LatLng>();
        pts.add(pt1);
        pts.add(pt2);

        for (int i=0;i<numhz.length;i++) {
            LatLng pt = new LatLng(numhz[i][0], numhz[i][1]);
            pts.add(pt);
        }

        OverlayOptions polygonOption = new PolygonOptions()
                .points(pts)
                .stroke(new Stroke(5, 0xAA00FF00))
                .fillColor(0xAAFFFF00);
        baiduMap.addOverlay(polygonOption);
    }

    protected void onDestroy() {
        super.onDestroy();
        mLocationClient.stop();
        mapView.onDestroy();
        baiduMap.setMyLocationEnabled(false);
    }
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }
}