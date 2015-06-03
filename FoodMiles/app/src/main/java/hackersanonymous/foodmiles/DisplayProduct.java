package hackersanonymous.foodmiles;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class DisplayProduct extends Activity {

    private TextView text;
    private TextView number;
    private Button add;
    private Button reject;
    private float foodMiles_;
    private Location currentLocation;
    String foodMiles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_product);

        LocationManager locationManager=    (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if(currentLocation == null){
            currentLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }
        String myLocation = "Latitude = " + currentLocation.getLatitude() + " Longitude = " + currentLocation.getLongitude();

        //I make a log to see the results
        Log.d("MY CURRENT LOCATION", myLocation);

        Intent i = getIntent();
        String postcode = i.getStringExtra("postcode");
        String location = "";
        final double oldTotal = i.getDoubleExtra("currentTotal", 0);
        //NEED TO SET CURRENT LOCATION

        text = (TextView)findViewById(R.id.productText);
        text.setText("Item: " + postcode);

        foodMiles_ = getDistance(currentLocation, postcode);
        foodMiles = String.valueOf((int)foodMiles_);
        final double newTotal = oldTotal + foodMiles_;
        number = (TextView)findViewById(R.id.foodMiles);
        number.setText("FoodMiles: " + foodMiles); //for debug puposes

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

    float getDistance(Location shopLocation, String address){
        GeoPoint foodGeoPoint = getLocationFromAddress(address);
        //GeoPoint foodGeoPoint = new GeoPoint(getLocationFromAddress(address).getLatitudeE6(), getLocationFromAddress(address).getLongitudeE6());
        Location foodLocation = new Location(shopLocation);
        foodLocation.setLatitude(foodGeoPoint.getLatitudeE6());
        foodLocation.setLongitude(foodGeoPoint.getLongitudeE6());
        float distance = (float)(shopLocation.distanceTo(foodLocation)/1609.344);
        return distance;
    }

    public GeoPoint getLocationFromAddress(String strAddress){

        Log.d("Geo: Address", strAddress);
        Geocoder coder = new Geocoder(this);
        List<Address> address;
        GeoPoint p1;

        //Geocoder not working

        try {
            address = coder.getFromLocationName(strAddress,1);

            if(address.isEmpty()){
                //now try to use pattern matching to get a postcode
                Pattern pattern = Pattern.compile("[A-Z]{1,2}[0-9R][0-9A-Z]? [0-9][ABD-HJLNP-UW-Z]{2}");
                Matcher matcher = pattern.matcher(strAddress);
                if(matcher.find()){
                    String postcode = matcher.group(0);
                    address = coder.getFromLocationName(postcode, 1);
                }

                if(address.isEmpty()) {
                    //now try and slim down the address to just a city/town/country
                    //any 4 words near the end should get a result

                    if(address.isEmpty()) //if its still empty assign a random address
                        address = coder.getFromLocationName("EH165BJ", 1);
                }
            }

            Address location = address.get(0);
            Log.d("Geo: Location", address.get(0).getAddressLine(0));
            Log.d("Geo: Location: Longitude", String.valueOf(address.get(0).getLongitude()));
            Log.d("Geo: Location: Latitude", String.valueOf(address.get(0).getLatitude()));
            p1 = new GeoPoint((int)(location.getLatitude() * 1E6), (int)(location.getLongitude()*1E6));

            return p1;
        }
        catch(IOException e){
            e.printStackTrace();
            return null;
        }

    }


}
