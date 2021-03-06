package ws.epod;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import ws.epod.Adapter.JobAdapter;
import ws.epod.Helper.ConnectionDetector;
import ws.epod.Helper.DatabaseHelper;
import ws.epod.Helper.DirectionsJSONParser;
import ws.epod.Helper.NarisBaseValue;
import ws.epod.ObjectClass.CheckGPS.GPSActive;
import ws.epod.ObjectClass.LanguageClass;
import ws.epod.ObjectClass.SQLiteModel.JobList_Model;
import ws.epod.ObjectClass.SQLiteModel.RouteMap_Model;


public class DropPoint_Activity extends AppCompatActivity {

    private Toolbar toolbar;
    private MapView mMapView;
    private RecyclerView rvJob;
    private ImageView imgBack_Job;
    private TextView tvDelevery_no;

    Marker mMarker;
    LocationManager lm;
    double lat, lng;

    ConnectionDetector netCon;
    DatabaseHelper databaseHelper;
    NarisBaseValue narisv;

    JobAdapter jobAdapter;
    String getDate = "";
    GPSActive gpsActive;


    ArrayList<LatLng> markerPoints;

    private ArrayList<LatLng> coords = new ArrayList<LatLng>();
    private GoogleMap mMap;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LanguageClass.setLanguage(getApplicationContext());
        setContentView(R.layout.activity_drop_point_);

        gpsActive = new GPSActive();

        narisv = new NarisBaseValue(getApplicationContext());
        netCon = new ConnectionDetector(getApplicationContext());
        databaseHelper = new DatabaseHelper(getApplicationContext());
        markerPoints = new ArrayList();

        String pattern = "yyyy-MM-dd kk:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern, new Locale("en", "th"));
        getDate = sdf.format(Calendar.getInstance().getTime());

        imgBack_Job = findViewById(R.id.imgBack_Job);
        tvDelevery_no = findViewById(R.id.tvDelevery_no);

        imgBack_Job.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.alpha);
                imgBack_Job.startAnimation(animation);
                finish();
            }
        });


        rvJob = findViewById(R.id.rvJob);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        rvJob.setLayoutManager(layoutManager);

        mMapView = findViewById(R.id.mapRoute);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                isMapRoute(googleMap);
            }
        });


    }


    private void isListDrop() {
        //int round = getIntent().getExtras().getInt("round");

        String delivery_date = getIntent().getExtras().getString("delivery_date");
        String delivery_no = getIntent().getExtras().getString("delivery_no");
/*        String sql = "select pl.station_name, pl.station_address, pl.plan_seq, pl.plan_in, plan_out, pl.delivery_no, pl.station_lat, pl.station_lon, pl.station_code  \n" +
                ", (select count( pl2.box_no) from Plan pl2 where pl2.activity_type = 'LOAD' and pl2.delivery_no = pl.delivery_no and pl2.station_code = pl.station_code and pl2.trash = pl.trash) as pick  \n" +
                ", (select count( pl2.box_no) from Plan pl2 where pl2.activity_type = 'LOAD' and pl2.is_scaned <> '0'and pl2.delivery_no = pl.delivery_no and pl2.station_code = pl.station_code and pl2.trash = pl.trash \n" +
                "and pl2.order_no in (select order_no from pic_sign where pic_sign_load <> '')) as pickUp  \n" +
                ", (select count( pl2.box_no) from Plan pl2 where pl2.activity_type = 'UNLOAD' and pl2.delivery_no = pl.delivery_no and pl2.station_code = pl.station_code and pl2.trash = pl.trash \n" +
                "and pl2.order_no in (select order_no from pic_sign where pic_sign_load <> '')) as deli  \n" +
                ", (select count( pl2.box_no) from Plan pl2 where pl2.activity_type = 'UNLOAD' and pl2.is_scaned <> '0' and pl2.delivery_no = pl.delivery_no and pl2.station_code = pl.station_code and pl2.trash = pl.trash \n" +
                "and pl2.order_no in (select order_no from pic_sign where pic_sign_unload <> '')) as delivery\n" +
                ", ROW_NUMBER() OVER(ORDER BY pl.plan_seq) AS rows_num \n" +
                "from Plan pl where pl.delivery_date='" + delivery_date + "' and pl.delivery_no = '" + delivery_no + "' and pl.trash = '0'  " +
                "GROUP BY pl.delivery_no, pl.station_name order by cast(pl.plan_seq as real) asc";*/
/*       String sql = "select pl.station_name, pl.station_address, cast(pl.plan_seq as int) as plan_seq, pl.plan_in, plan_out, pl.delivery_no, pl.station_lat, pl.station_lon, pl.station_code   \n" +
                ", (select count( pl2.box_no) from Plan pl2 where pl2.activity_type = 'LOAD' and pl2.delivery_no = pl.delivery_no and pl2.station_code = pl.station_code and pl2.trash = pl.trash) as pick   \n" +
                ", (select count( pl2.box_no) from Plan pl2 where pl2.activity_type = 'LOAD' and pl2.is_scaned <> '0'and pl2.delivery_no = pl.delivery_no and pl2.station_code = pl.station_code and pl2.trash = pl.trash  \n" +
                "and pl2.order_no in (select order_no from pic_sign where pic_sign_load <> '')) as pickUp   \n" +
                ", (select count( pl2.box_no) from Plan pl2 where pl2.activity_type = 'UNLOAD' and pl2.delivery_no = pl.delivery_no and pl2.station_code = pl.station_code and pl2.trash = pl.trash  \n" +
                "and pl2.order_no in (select order_no from pic_sign where pic_sign_load <> '')) as deli   \n" +
                ", (select count( pl2.box_no) from Plan pl2 where pl2.activity_type = 'UNLOAD' and pl2.is_scaned <> '0' and pl2.delivery_no = pl.delivery_no and pl2.station_code = pl.station_code and pl2.trash = pl.trash  \n" +
                "and pl2.order_no in (select order_no from pic_sign where pic_sign_unload <> '')) as delivery \n" +
                ", ROW_NUMBER() OVER(ORDER BY pl.plan_seq) AS rows_num \n" +
                ", 1 as fin " +
                "from Plan pl where pl.delivery_date = '" + delivery_date + "' and pl.delivery_no = '" + delivery_no + "' and pl.trash = '0'   " +
                "GROUP BY pl.delivery_no, pl.station_name \n" +
                "having pick = pickUp and deli = delivery\n" +
                "union\n" +
                "select pl.station_name, pl.station_address, cast(pl.plan_seq as int) as plan_seq, pl.plan_in, plan_out, pl.delivery_no, pl.station_lat, pl.station_lon, pl.station_code   \n" +
                ", (select count( pl2.box_no) from Plan pl2 where pl2.activity_type = 'LOAD' and pl2.delivery_no = pl.delivery_no and pl2.station_code = pl.station_code and pl2.trash = pl.trash) as pick   \n" +
                ", (select count( pl2.box_no) from Plan pl2 where pl2.activity_type = 'LOAD' and pl2.is_scaned <> '0'and pl2.delivery_no = pl.delivery_no and pl2.station_code = pl.station_code and pl2.trash = pl.trash  \n" +
                "and pl2.order_no in (select order_no from pic_sign where pic_sign_load <> '')) as pickUp   \n" +
                ", (select count( pl2.box_no) from Plan pl2 where pl2.activity_type = 'UNLOAD' and pl2.delivery_no = pl.delivery_no and pl2.station_code = pl.station_code and pl2.trash = pl.trash  \n" +
                "and pl2.order_no in (select order_no from pic_sign where pic_sign_load <> '')) as deli   \n" +
                ", (select count( pl2.box_no) from Plan pl2 where pl2.activity_type = 'UNLOAD' and pl2.is_scaned <> '0' and pl2.delivery_no = pl.delivery_no and pl2.station_code = pl.station_code and pl2.trash = pl.trash  \n" +
                "and pl2.order_no in (select order_no from pic_sign where pic_sign_unload <> '')) as delivery \n" +
                ", ROW_NUMBER() OVER(ORDER BY pl.plan_seq) AS rows_num\n" +
                ", 0 as fin " +
                "from Plan pl where pl.delivery_date = '" + delivery_date + "' and pl.delivery_no = '" + delivery_no + "' and pl.trash = '0'   " +
                "GROUP BY pl.delivery_no, pl.station_name \n" +
                "having pick <> pickUp or deli <> delivery\n" +
                "\n" +
                "order by fin, plan_seq\n";*/

        String sql = "select x1.*\n" +
                ", (case when x1.pick = x1.pickUp and x1.deli = x1.delivery then 1 else 0 end) as fin\n" +
                "from(\n" +
                "select pl.station_name, pl.station_address, cast(pl.plan_seq as int) as plan_seq, pl.plan_in, plan_out, pl.delivery_no, pl.station_lat, pl.station_lon, pl.station_code   \n" +
                ", (select count( pl2.box_no) from Plan pl2 where pl2.activity_type = 'LOAD' and pl2.delivery_no = pl.delivery_no and pl2.station_code = pl.station_code and pl2.trash = pl.trash) as pick   \n" +
                ", (select count( pl2.box_no) from Plan pl2 where pl2.activity_type = 'LOAD' and pl2.is_scaned <> '0'and pl2.delivery_no = pl.delivery_no and pl2.station_code = pl.station_code and pl2.trash = pl.trash  \n" +
                "and pl2.order_no in (select order_no from pic_sign where pic_sign_load <> '')) as pickUp   \n" +
                ", (select count( pl2.box_no) from Plan pl2 where pl2.activity_type = 'UNLOAD' and pl2.delivery_no = pl.delivery_no and pl2.station_code = pl.station_code and pl2.trash = pl.trash  \n" +
                "and pl2.order_no in (select order_no from pic_sign where pic_sign_load <> '')) as deli   \n" +
                ", (select count( pl2.box_no) from Plan pl2 where pl2.activity_type = 'UNLOAD' and pl2.is_scaned <> '0' and pl2.delivery_no = pl.delivery_no and pl2.station_code = pl.station_code and pl2.trash = pl.trash  \n" +
                "and pl2.order_no in (select order_no from pic_sign where pic_sign_unload <> '')) as delivery \n" +
                ", ROW_NUMBER() OVER(ORDER BY cast(pl.plan_seq as int)) AS rows_num\n" +
                "\n" +
                "from Plan pl where pl.delivery_date = '" + delivery_date + "' and pl.delivery_no = '" + delivery_no + "' and pl.trash = '0'   " +
                "GROUP BY pl.delivery_no, pl.station_name \n" +
                ")x1\n" +
                "order by fin, x1.plan_seq";
        Log.d("isListDrop", "total line " + sql);
        Cursor cursor = databaseHelper.selectDB(sql);
        Log.d("isListDrops8a3a89", "total line " + cursor.getCount());
        ArrayList<JobList_Model> jobList_models = new ArrayList<>();
        cursor.moveToFirst();
        do {
            if (cursor.getCount() > 0) {
                String station_name = cursor.getString(cursor.getColumnIndex("station_name"));
                String station_address = cursor.getString(cursor.getColumnIndex("station_address"));
                String plan_in = cursor.getString(cursor.getColumnIndex("plan_in"));
                String plan_out = cursor.getString(cursor.getColumnIndex("plan_out"));
                int plan_seq = cursor.getInt(cursor.getColumnIndex("plan_seq"));
                double station_lat = cursor.getDouble(cursor.getColumnIndex("station_lat"));
                double station_lon = cursor.getDouble(cursor.getColumnIndex("station_lon"));
                String pick = cursor.getString(cursor.getColumnIndex("pick"));
                String pickUp = cursor.getString(cursor.getColumnIndex("pickUp"));
                String deli = cursor.getString(cursor.getColumnIndex("deli"));
                String delivery = cursor.getString(cursor.getColumnIndex("delivery"));
                String station_code = cursor.getString(cursor.getColumnIndex("station_code"));
                String rows_num = cursor.getString(cursor.getColumnIndex("rows_num"));

                jobList_models.add(new JobList_Model(station_name, station_address, plan_seq, plan_in, plan_out, station_lat, station_lon, pick, pickUp, deli, delivery, delivery_no, station_code, rows_num));

                //Log.d("fk6d5d4", "isListDrop: -> " + plan_seq);
            }

        } while (cursor.moveToNext());

        ArrayList<JobList_Model> jobList_2 = new ArrayList<>();
        ArrayList<JobList_Model> jobList_3 = new ArrayList<>();


        /*for (int i = 0; i < jobList_models.size(); i++) {

            Log.d("dsfs7822s", "isListDrop: " + jobList_models.get(i).getRows_num());

            String pick = jobList_models.get(i).getPick();
            String pickup = jobList_models.get(i).getPickUp();
            String deli = jobList_models.get(i).getDeli();
            String delivery = jobList_models.get(i).getDelivery();

            if (!pick.equals("0") || !deli.equals("0")) {
                if ((pick.equals(pickup) && !deli.equals("0") && deli.equals(delivery)) ||
                        (deli.equals(delivery) && !pick.equals("0") && pick.equals(pickup))) {

                    jobList_2.add(jobList_models.get(i));
                }

            }


        }

        for (int i = 0; i < jobList_2.size(); i++) {
            // ลบหัวอันที่แอดเข้าไปแล้ว
            jobList_models.remove(jobList_2.get(i));

        }

        jobList_3.addAll(jobList_models);
        jobList_3.addAll(jobList_2);*/


        //setToolbar
        tvDelevery_no.setText(delivery_no);

        //setAdapter Lsit
        jobAdapter = new JobAdapter(jobList_models, getApplicationContext());
        rvJob.setAdapter(jobAdapter);
        // cursor.close();

    }


    private void isMapRoute(GoogleMap map) {
        mMap = map;
        //int round = getIntent().getExtras().getInt("round");
        String delivery_date = getIntent().getExtras().getString("delivery_date");
        String delivery_no = getIntent().getExtras().getString("delivery_no");
        String sql = "select pl.station_lat, pl.station_lon,(select GROUP_CONCAT( DISTINCT pl2.plan_seq) from Plan pl2 where pl2.delivery_no = pl.delivery_no and pl2.station_lat = pl.station_lat and pl2.station_lon = pl.station_lon and pl2.trash = pl.trash) as plan_seq" +
                ", (select GROUP_CONCAT(pl3.station_name, ',\n')from (select DISTINCT pl2.station_name from Plan pl2 where pl2.delivery_no = pl.delivery_no and pl2.station_lat = pl.station_lat and pl2.station_lon = pl.station_lon and pl2.trash = pl.trash) as pl3) as station_name" +
                " from Plan pl where pl.delivery_date='" + delivery_date + "' and pl.delivery_no = '" + delivery_no + "' and pl.trash = '0'" +
                " GROUP BY pl.station_lat, pl.station_lon order by pl.plan_seq ";
        Log.d("isMapRoute", "total line " + sql);
        Cursor cursor = databaseHelper.selectDB(sql);
        Log.d("isMapRoute", "total line " + cursor.getCount());
        final ArrayList<RouteMap_Model> studentArrayList = new ArrayList<>();

        cursor.moveToFirst();
        do {
            if (cursor.getCount() > 0) {

                String station_name = cursor.getString(cursor.getColumnIndex("station_name"));
                String station_lat = cursor.getString(cursor.getColumnIndex("station_lat"));
                String station_lon = cursor.getString(cursor.getColumnIndex("station_lon"));
                String plan_seq = cursor.getString(cursor.getColumnIndex("plan_seq"));
                studentArrayList.add(new RouteMap_Model(station_name, station_lat, station_lon, plan_seq));
            }
        } while (cursor.moveToNext());

        for (int i = 0; i < studentArrayList.size(); i++) {
            String plan_seq = studentArrayList.get(i).getPlan_seq();

            double latitude = Double.parseDouble(studentArrayList.get(i).getStation_lat());
            double longitude = Double.parseDouble(studentArrayList.get(i).getStation_lon());
            final String station_name = studentArrayList.get(i).getStation_name();

            coords.add(new LatLng(latitude, longitude));

            if (latitude == 0 && longitude == 0) {

                Toast.makeText(this, "not have station location.", Toast.LENGTH_SHORT).show();
                LocationListener listener = new LocationListener() {
                    public void onLocationChanged(Location loc) {
                        LatLng coordinate = new LatLng(loc.getLatitude()
                                , loc.getLongitude());
                        lat = loc.getLatitude();
                        lng = loc.getLongitude();

                        if (mMarker != null)
                            mMarker.remove();

                        mMarker = mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(lat, lng)));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                coordinate, 16));
                    }

                    public void onStatusChanged(String provider, int status
                            , Bundle extras) {
                    }

                    public void onProviderEnabled(String provider) {
                    }

                    public void onProviderDisabled(String provider) {
                    }
                };
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER
                        , 5000, 10, listener);
                Location loc = lm.getLastKnownLocation(
                        LocationManager.GPS_PROVIDER);
                if (loc != null) {
                    lat = loc.getLatitude();
                    lng = loc.getLongitude();
                }


            } else {

                Log.d("asdfwa", "isMapRoute: " + station_name);

                mMap.addMarker(new MarkerOptions()
                        .position(coords.get(i))
                        .icon(BitmapDescriptorFactory.fromBitmap(createStoreMarker(plan_seq)))
                        .title(station_name)
                        .anchor(0.5f, 1));

                mMap.setMyLocationEnabled(true);


                mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                    @Override
                    public View getInfoWindow(Marker markerss) {
                        // TODO Auto-generated method stub
                        return null;
                    }

                    @Override
                    public View getInfoContents(Marker markerss) {
                        // TODO Auto-generated method stub

                        View ll = getLayoutInflater().inflate(R.layout.layout_custom_title_marker, null);
                        TextView tviNamePopup = ll.findViewById(R.id.tviNamePopup);

                        String title = markerss.getTitle();
                        tviNamePopup.setText(title);

                        return ll;
                    }
                });

            }

        }

        //check center map camera
        if (mMapView.getViewTreeObserver().isAlive()) {
            mMapView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @SuppressLint("NewApi")
                @Override
                public void onGlobalLayout() {
                    LatLngBounds.Builder bld = new LatLngBounds.Builder();
                    for (int i = 0; i < studentArrayList.size(); i++) {
                        double latitude = Double.parseDouble(studentArrayList.get(i).getStation_lat());
                        double longitude = Double.parseDouble(studentArrayList.get(i).getStation_lon());
                        LatLng ll = new LatLng(latitude, longitude);
                        bld.include(ll);
                    }

//                    LatLngBounds bounds = bld.build();
//
//                    // Setup camera movement
//                    final int width = getResources().getDisplayMetrics().widthPixels;
//                    final int height = getResources().getDisplayMetrics().heightPixels;
//                    final int minMetric = Math.min(width, height);
//                    final int padding = (int) (minMetric * 0.40); // offset from edges of the map in pixels
//                    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);


                    LatLngBounds bounds = bld.build();
                    mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 0));
                    mMapView.getViewTreeObserver().removeGlobalOnLayoutListener(this);

                }
            });
        }

    }

    private Bitmap createStoreMarker(String dropCount) {
        View markerLayout = getLayoutInflater().inflate(R.layout.store_marker_layout, null);

        ImageView markerImage = (ImageView) markerLayout.findViewById(R.id.marker_image);
        TextView markerRating = (TextView) markerLayout.findViewById(R.id.marker_text);
        markerImage.setImageResource(R.drawable.default_marker);
        markerRating.setText(dropCount);

        markerLayout.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        markerLayout.layout(0, 0, markerLayout.getMeasuredWidth(), markerLayout.getMeasuredHeight());

        final Bitmap bitmap = Bitmap.createBitmap(markerLayout.getMeasuredWidth(), markerLayout.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        markerLayout.draw(canvas);
        return bitmap;
    }


    private String getDirectionsUrl(LatLng origin, LatLng dest) {

// Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

// Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

// Sensor enabled
        String sensor = "sensor=false";

// Waypoints
        String waypoints = "";
        for (int i = 2; i < markerPoints.size(); i++) {
            LatLng point = (LatLng) markerPoints.get(i);
            if (i == 2)
                waypoints = "waypoints=";
            waypoints += point.latitude + "," + point.longitude + "|";
        }

// Building the parameters to the web service
        String key = "key=" + getResources().getString(R.string.google_maps_key);
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&units=metric&mode=driving&" + key + "&" + waypoints;

// Output format
        String output = "json";

// Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;

        return url;
    }

    /**
     * A method to download json data from url
     */

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("downloading url", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    private class DownloadTask extends AsyncTask<String, Void, String> {

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            Log.d("resultDownloadTask", "onPostExecute: " + result + " success");
//            ParserTask parserTask = new ParserTask();
//
//            // Invokes the thread for parsing the JSON data
//            parserTask.execute(result);

        }
    }

    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {

            ArrayList points = null;
            PolylineOptions lineOptions = null;

            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(2);
                lineOptions.color(Color.RED);
            }

// Drawing polyline in the Google Map for the i-th route
            mMap.addPolyline(lineOptions);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
        isListDrop();
        if (!gpsActive.EnableGPSIfPossible(DropPoint_Activity.this)) {
            Log.d("AS9d6asd", "yes");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_job, menu);
//        MenuItem item = menu.add(1, 2, 2, delivery_no).setEnabled(false);
//        item.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS);

        return super.onCreateOptionsMenu(menu);
    }


}
