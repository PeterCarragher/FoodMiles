package hackersanonymous.foodmiles;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class DisplayProduct extends Activity {

    private TextView text;
    private TextView number;
    private Button add;
    private Button reject;
    private double foodMiles_;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_product);

        Intent i = getIntent();
        String postcode = i.getStringExtra("postcode");
        String location = "";
        final double oldTotal = i.getDoubleExtra("currentTotal", 0);
        //NEED TO SET CURRENT LOCATION

        text = (TextView)findViewById(R.id.productText);
        text.setText("Item: " + postcode);

        foodMiles_ = getDistance(location, postcode);
        String foodMiles = String.valueOf(foodMiles_);
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

    double getDistance(String location, String postcode){
        return 1.0;
    }

}