package com.globalclasses;

import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.ivy.SimpleHttpConnection;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by singsys-83 on 11-08-2016.
 */
public class GPSTrackerNew extends Service implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener, CallBackListenar {

    boolean firstTime = false;
    private Activity context;
    private int count = 0;
    private static final int TWO_MINUTES = 1000 * 60 * 2;
    double currentLatitude = 0, currentLongitude = 0, savedLatitude = 0, savedLongitude = 0, distanceCalculated = 0;
    protected static final String TAG = "location-updates-sample";
    SharedPreferences mainPreferences;
    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 1000 * 60 * 2;
    /**
     * The fastest rate for active location updates. Exact. Updates will never be more frequent
     * than this value.
     */
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    // Keys for storing activity state in the Bundle.
    protected final static String REQUESTING_LOCATION_UPDATES_KEY = "requesting-location-updates-key";
    protected final static String LOCATION_KEY = "location-key";
    protected final static String LAST_UPDATED_TIME_STRING_KEY = "last-updated-time-string-key";

    /**
     * Provides the entry point to Google Play services.
     */
    protected GoogleApiClient mGoogleApiClient;

    /**
     * Stores parameters for requests to the FusedLocationProviderApi.
     */
    protected LocationRequest mLocationRequest;

    /**
     * Represents a geographical location.
     */
    protected Location mCurrentLocation;

    public GPSTrackerNew() {

    }

    public GPSTrackerNew(Activity context) {

        this.context = context;
        count = 0;
        mainPreferences = context.getSharedPreferences(Constant.pref_main, Activity.MODE_PRIVATE);

        // Kick off the process of building a GoogleApiClient and requesting the LocationServices
        // API.
        buildGoogleApiClient();
    }

    /**
     * Requests location updates from the FusedLocationApi.
     */
    protected void startLocationUpdates() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // The final argument to {@code requestLocationUpdates()} is a LocationListener
            // (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            } else {
//                Utils.showToastS(context,context.getString(R.string.locationPermission));
            }

        } else {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }

    }

    /**
     * Builds a GoogleApiClient. Uses the {@code #addApi} method to request the
     * LocationServices API.
     */
    protected synchronized void buildGoogleApiClient() {
        Log.i(TAG, "Building GoogleApiClient");
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        createLocationRequest();
    }

    /**
     * Sets up the location request. Android has two location request settings:
     * {@code ACCESS_COARSE_LOCATION} and {@code ACCESS_FINE_LOCATION}. These settings control
     * the accuracy of the current location. This sample uses ACCESS_FINE_LOCATION, as defined in
     * the AndroidManifest.xml.
     * <p/>
     * When the ACCESS_FINE_LOCATION setting is specified, combined with a fast update
     * interval (5 seconds), the Fused Location Provider API returns location updates that are
     * accurate to within a few feet.
     * <p/>
     * These settings are appropriate for mapping applications that show real-time location
     * updates.
     */
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();

        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
//        mLocationRequest.setSmallestDisplacement(3);

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
//        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
//        mLocationRequest.setFastestInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mGoogleApiClient.connect();
    }

    /**
     * Callback that fires when the location changes.
     */
    @Override
    public void onLocationChanged(Location location) {

//        Utils.showToastS(context, "getAccuracy====="+location.getAccuracy());

        mCurrentLocation = location;
        count = count + 1;
        if (mCurrentLocation != null) {

            try {
                currentLatitude = location.getLatitude();
                currentLongitude = location.getLongitude();

                if (GlobalMethod.getSavedPreferences(context, Constant.lalitude, "").isEmpty()) {
                    GlobalMethod.savePreferences(context, Constant.lalitude, "" + currentLatitude);
                    GlobalMethod.savePreferences(context, Constant.longitude, "" + currentLongitude);
                }
                savedLatitude = Double.parseDouble(GlobalMethod.getSavedPreferences(context, Constant.lalitude, "0.0"));
                savedLongitude = Double.parseDouble(GlobalMethod.getSavedPreferences(context, Constant.longitude, "0.0"));
                distanceCalculated = getDistance(savedLatitude, savedLongitude, currentLatitude, currentLongitude);
                GlobalMethod.write("currentLatitude====" + currentLatitude + "currentLongitude===" + currentLongitude);
                GlobalMethod.write("savedLatitude====" + savedLatitude + "savedLongitude===" + savedLongitude);
                GlobalMethod.write("distanceCalculated====" + distanceCalculated);

                if (!firstTime) {
                    firstTime = true;
                    GlobalMethod.savePreferences(context, Constant.lalitude, "" + currentLatitude);
                    GlobalMethod.savePreferences(context, Constant.longitude, "" + currentLongitude);
                    if (!mainPreferences.getString(Constant.id_user, "").isEmpty())
                        locationUpdateServiceNew();
                }

                if (distanceCalculated >= 500 && mCurrentLocation.getAccuracy() <= 10) {
                    GlobalMethod.savePreferences(context, Constant.lalitude, "" + currentLatitude);
                    GlobalMethod.savePreferences(context, Constant.longitude, "" + currentLongitude);
                    if (!mainPreferences.getString(Constant.id_user, "").isEmpty())
                        locationUpdateServiceNew();
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        GlobalMethod.write("===GPSTrackerNewonBind");
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        GlobalMethod.write("===GPSTrackerNewonDestroy");
        Constant.running = false;
        stopLocationUpdate();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG, "Connected to GoogleApiClient");


        // If the initial location was never previously requested, we use
        // FusedLocationApi.getLastLocation() to get it. If it was previously requested, we store
        // its value in the Bundle and check for it in onCreate(). We
        // do not request it again unless the user specifically requests location updates by pressing
        // the Start Updates button.
        //
        // Because we cache the value of the initial location in the Bundle, it means that if the
        // user launches the activity,
        // moves to a new location, and then changes the device orientation, the original location
        // is displayed as the activity is re-created.
        if (mCurrentLocation == null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                } else {
                    GlobalMethod.showToast(context, "Please provide location permission.");
                }
            } else {
                mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            }


        }

        // If the user presses the Start Updates button before GoogleApiClient connects, we set
        // mRequestingLocationUpdates to true (see startUpdatesButtonHandler()). Here, we check
        // the value of mRequestingLocationUpdates and if it is true, we start location updates.

        startLocationUpdates();

    }

    @Override
    public void onConnectionSuspended(int i) {
// The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());
    }


    /**
     * this function is use to hit the service.
     */
    private void locationUpdateServiceNew() {

        if (SimpleHttpConnection.isNetworkAvailable(context)) {
            Bundle bundle = new Bundle();
            bundle.putString("user_id", mainPreferences.getString(Constant.id_user, ""));
            bundle.putString("lat", GlobalMethod.getSavedPreferences(context, Constant.lalitude, ""));
            bundle.putString("long", GlobalMethod.getSavedPreferences(context, Constant.longitude, ""));
            new AsyncTaskApp(this, context, Urls.USERLATLONG, "location", false).execute(bundle);
        }
    }

    /**
     * @param result return response from server.
     * @param action return action which was you sent.
     * @throws Exception return exception if data contains error.
     */
    @Override
    public void callBackFunction(String result, String action) {
        try {
            Constant.running = true;
            JSONObject jsonObject = new JSONObject(result);
//            String status = jsonObject.getString("result");
            String message = jsonObject.getString("message");

            GlobalMethod.write("====message" + message);

//            if (status.equalsIgnoreCase("FAIL")) {
////            Utils.showToastS(context, message);
//
//            } else if (status.equalsIgnoreCase("SUCCESS")) {
////				Utils.showToastS(context, message);
//            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void stopLocationUpdate() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location

            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    /**
     * Checks whether two providers are the same
     */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }

    public static float getDistance(double startLati, double startLongi, double goalLati, double goalLongi) {
        float[] resultArray = new float[99];
        Location.distanceBetween(startLati, startLongi, goalLati, goalLongi, resultArray);
        return resultArray[0];
    }

}