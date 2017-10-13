package mahlabs.mapsfriends.data;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.io.IOException;

import mahlabs.mapsfriends.Controller;
import mahlabs.mapsfriends.MainActivity;
import mahlabs.mapsfriends.connection.JsonHandler;
import mahlabs.mapsfriends.fragments.MapFragment;

/**
 * Created by 13120dde on 2017-10-13.
 */

public class Locator extends Thread {

    public static final int REQUEST_ACCESS_FINE_LOCATION = 1;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private MainActivity activity;
    private Controller controller;
    private long startTime;
    private boolean first = true;

    public Locator(MainActivity activity, Controller controller){
        this.activity=activity;
        this.controller = controller;
        initLocationManager();
    }

    private void initLocationManager() {
        locationManager = (LocationManager)activity.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LockList();

        if (ContextCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(activity, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_ACCESS_FINE_LOCATION);
        } else {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_ACCESS_FINE_LOCATION :
                if (ContextCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                }
                break;
        }
        startTime = System.currentTimeMillis();
    }

    private class LockList implements LocationListener {



        @Override
        public void onLocationChanged(Location location) {
            long elapsedTime = System.currentTimeMillis();
            if(first || elapsedTime-startTime>=30000){
                Log.d("IN_LOC","Tick: "+(elapsedTime-startTime));
                double lon = location.getLongitude();
                double lat = location.getLatitude();
                String id = controller.getThisUser().getId();
                try {
                    controller.sendToServer(JsonHandler.setPosition(id,lon,lat));
                    startTime=elapsedTime;
                    first=false;
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    }
}
