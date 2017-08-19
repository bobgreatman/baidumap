package com.example.lewanjiang.baidumap;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

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
                Toast.makeText(MainActivity.this,"FAB clicked",Toast.LENGTH_LONG).show();
            }
        });
    }

    private void requestLocation(){
        initLocatioin();
        mLocationClient.start();
    }

    private void initLocatioin(){
        LocationClientOption option = new LocationClientOption();
        option.setScanSpan(5000);
        mLocationClient.setLocOption(option);
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