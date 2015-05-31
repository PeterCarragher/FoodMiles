package hackersanonymous.foodmiles;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.maps.GeoPoint;

import java.io.IOException;
import java.util.List;


public class DisplayProduct extends Activity {

    private TextView text;
    private TextView number;
    private Button add;
    private Button reject;
    private double foodMiles_;
    private Location currentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_product);

        LocationManager locationManager=    (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if(currentLocation == null){
            currentLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }



        Intent i = getIntent();
        String postcode = i.getStringExtra("postcode");
        String location = "";
        final double oldTotal = i.getDoubleExtra("currentTotal", 0);
        //NEED TO SET CURRENT LOCATION

        text = (TextView)findViewById(R.id.productText);
        text.setText("Item: " + postcode);

        foodMiles_ = getDistance(currentLocation, postcode);
        String foodMiles = String.valueOf((int)foodMiles_);
        final double newTotal = oldTotal + foodMiles_;
        number = (TextView)findViewById(R.id.foodMiles);
        number.setText("FoodMiles: " + foodMiles);

        add = (Button) findViewById(R.id.add_button);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DisplayProduct.this, MainScreen.class);
                //push data to table
                i.putExtra("totalMiles", newTotal);
                DisplayProduct.this.startActivity(i);
            }
        });

        reject = (Button) findViewById(R.id.reject_button);
        reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DisplayProduct.this, MainScreen.class);
                //dont add anything to table
                i.putExtra("totalMiles", oldTotal);
                DisplayProduct.this.startActivity(i);
            }
        });

    }
/*
    private final LocationListener mLocationListener = new LocationListener() {

        public void onCreate(){

        }
        @Override
        public void onStatusChanged(String x, int y, Bundle z) {
            //your code here
        }

        @Override
        public void onProviderEnabled(String x) {
            //your code here
        }

        @Override
        public void onProviderDisabled(String x) {
            //your code here
        }

        @Override
        public void onLocationChanged(final Location location) {
            currentLocation = new Location(location);
        }
    };

*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.display_product, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    double getDistance(Location shopLocation, String postcode){
        float distance = 1;
        GeoPoint foodGeoPoint = getLocationFromAddress(postcode);
        Location foodLocation = new Location(shopLocation);
        foodLocation.setLatitude(foodGeoPoint.getLatitudeE6());
        foodLocation.setLongitude(foodGeoPoint.getLongitudeE6());
        distance = shopLocation.distanceTo(foodLocation);
        return (double)distance*0.000621371192;

    }

    public GeoPoint getLocationFromAddress(String strAddress){

        Log.d("Geo: Address", strAddress);
        Geocoder coder = new Geocoder(this);
        List<Address> address;
        GeoPoint p1;

        try {
            address = coder.getFromLocationName("EH165BJ",1);
          /*  if (address.isEmpty()) {
                return null;
            }*/
            Address location = address.get(0);
            Log.d("Geo: Location", address.get(0).getAddressLine(0));
            Log.d("Geo: Location: Longitude", String.valueOf(address.get(0).getLongitude()));
            Log.d("Geo: Location: Latitude", String.valueOf(address.get(0).getLatitude()));
            p1 = new GeoPoint((int)(address.get(0).getLatitude() * 1E6), (int)(address.get(0).getLongitude()*1E6));

            return p1;
        }
        catch(IOException e){
            e.printStackTrace();
            return null;
        }

    }

    public class MyCurrentLoctionListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {

            currentLocation = new Location(location);

            location.getLatitude();
            location.getLongitude();

            String myLocation = "Latitude = " + location.getLatitude() + " Longitude = " + location.getLongitude();

            //I make a log to see the results
            Log.e("MY CURRENT LOCATION", myLocation);

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
