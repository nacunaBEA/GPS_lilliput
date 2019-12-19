package com.example.google.modules;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import static android.content.Context.LOCATION_SERVICE;

public class GPSservice implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = "GPSservice";
    private final boolean isThereGPSNetwork;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private GpsStatus.NmeaListener myNmaListener;
    private Context context;
    private double longitude = 0;
    private double latitude = 0;
    private static final int LOCATION_TIME = 1000;
    private static final float LOCATION_DISTANCE = 0;
    private double PDOP;
    private double velocity_km_hr = 1;
    private double distance = 0;
    private String GPSparams ="";
    private boolean isThereGPSProvider;
    private boolean GPSfix;
    private boolean GPSfixCopy;
    private double noSatelite;
    private boolean GPSfixNet = false;
    private String Fix = "";

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest locationrequest;
    private HighFusedLocationListener highFusedLocationListener;
    private IgpsListener callBack;


    public  interface IgpsListener {
        void onGetGPSparameters(String params);
    }




    //region  Getters and extras
    private void createGPStext() {
        GPSparams = "\n\t" + getGPSName() +": Latitud:" + latitude + " , Longitude: " + longitude
                +  " , PDOP: " + PDOP + " , Velocity: " + velocity_km_hr
                + " , Distance: " + distance + "\n";

        Log.d(TAG, "createGPStext: " +GPSparams);

        callBack.onGetGPSparameters(GPSparams);

    }
    public void resetDistance() {
        distance = 0;
    }
    private void addDistance(double distance) {
        this.distance += distance;
        Log.d(TAG, "Distance: " + this.distance);
        Log.d(TAG, "Velocidad en Km/hr: " + this.velocity_km_hr);

    }
    public boolean isGPSfix() {
        return GPSfix || GPSfixNet;
    }
    public String getGPSName(){
        if (GPSfix)return "GPS";
        else return "AGPS";
    }
    public double getDistance() {
        return distance;
    }
    public double getVelocity_km_hr() {
        return velocity_km_hr;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getPDOP() {
        return PDOP;
    }


    //endregion

    //region GPSservice  constructor
    public GPSservice(Context context, IgpsListener callBack) {
        this.context = context;
        this.callBack = callBack;
        locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        isThereGPSProvider = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isThereGPSNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        mGoogleApiClient = new GoogleApiClient.Builder(this.context)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mGoogleApiClient.connect();

        locationListener = new mLocationListener();
        myNmaListener = new myGpsStatusNmeaListener();
        highFusedLocationListener = new HighFusedLocationListener();

        locationrequest = LocationRequest.create();
        locationrequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationrequest.setInterval(1000);

        if (isThereGPSProvider) {
            gpsStartService();
        }
    }

    //endregion

    //region method  to allor location updates and nmea
    private void gpsStartService() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_TIME, LOCATION_DISTANCE, locationListener);


            locationManager.addNmeaListener(myNmaListener);
        }
    }
    //endregion

    //region nmea class
    private class myGpsStatusNmeaListener implements GpsStatus.NmeaListener {

        @Override
        public void onNmeaReceived(long timestamp, String nmea) {

            if (nmea != null && nmea.length() > 0)
                nmea = nmea.replaceAll("\\*..$", "");

            String[] nmeaSplit = nmea.split(",");


            if (nmea.contains("GGA")) {
                try {
                    try {
                        GPSfixCopy = (!nmeaSplit[6].isEmpty() ? (Integer.parseInt(nmeaSplit[6]) >= 2 ? true : false) : false);
                        noSatelite = !nmeaSplit[7].isEmpty()  ? (Integer.parseInt(nmeaSplit[7]) >= 2 ? Integer.parseInt(nmeaSplit[7]) : 0) : 0;

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }

            }
            if (nmea.contains("GPGSA")) {

                try {
                    PDOP = Double.parseDouble(nmeaSplit[nmeaSplit.length - 3].isEmpty() ? "99.99" : nmeaSplit[nmeaSplit.length - 3]);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //endregion

    //region location listener with GPS
    private class mLocationListener implements LocationListener
    {
        @Override
        public void onLocationChanged(Location location) {
            velocity_km_hr = (location.getSpeed() * 3600) / 1000;
            if (GPSfixCopy && velocity_km_hr < 100 && PDOP<=5) {
                latitude = (location.getLatitude());
                longitude = (location.getLongitude());
                GPSfix =true;
                if (velocity_km_hr < 5) {
                    velocity_km_hr = 0.0;
                }
                if (velocity_km_hr >= 5) {
                    addDistance(location.getSpeed());
                }
                createGPStext();

            }

        }
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras)
        {
            Log.d(TAG, "onStatusChanged: " + provider);
            Log.d(TAG, "onStatusChanged: " + status);
        }
        @Override
        public void onProviderEnabled(String provider)
        {
            Log.d(TAG, "onProviderEnabled: " + provider);
        }
        @Override
        public void onProviderDisabled(String provider) {
            Log.d(TAG, "onProviderDisabled: " + provider);
        }
    }



    //endregion

    //region location listener with AGPS
    private class HighFusedLocationListener implements com.google.android.gms.location.LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            velocity_km_hr = (location.getSpeed() * 3600) / 1000;
            if (!GPSfixCopy && velocity_km_hr < 100 && velocity_km_hr> 2 ) {
                latitude = (location.getLatitude());
                longitude = (location.getLongitude());
                double precition = location.getAccuracy();


                PDOP = Math.random() + 1.1;
                noSatelite = 5;
                GPSfixNet = true;
                if (velocity_km_hr < 5) {
                    velocity_km_hr = 0.0;
                }
                if (velocity_km_hr >= 5) {
                    addDistance(location.getSpeed());
                }
                createGPStext();
            }
        }
    }
    //endregion

    //region ConnectionCallbacks

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        int d = Log.d(TAG, "onConnected: ");
        PendingResult<Status> val = LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationrequest, highFusedLocationListener);
        Log.d(TAG, "onConnected: " + val);
    }



    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "onConnectionSuspended: ");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed: ");
    }

    //endregion

}