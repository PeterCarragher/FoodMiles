package hackersanonymous.foodmiles;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class MainScreen extends Activity{

    private Button mButton;
    RequestTask postRequest;
    private TextView milesPerPound;
    double totalMiles = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        // Scanner

        mButton = (Button) findViewById(R.id.assistant_button);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator integrator = new IntentIntegrator(MainScreen.this);
                integrator.initiateScan();
            }
        });

        Intent i = getIntent();
        totalMiles = i.getDoubleExtra("totalMiles", 0);
        milesPerPound = (TextView)findViewById(R.id.totalMiles);
        milesPerPound.setText("Total FoodMiles: " + String.valueOf(totalMiles));

    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanResult != null) {
            String re = scanResult.getContents();
            String str = scanResult.toString();
        }
        // else continue with any other code you need in the method

        String uri = "http://gepir.gs1.org/v32/xx/gtin.aspx?Lang=en-US";
        postRequest = new RequestTask();
        postRequest.execute(uri);
    }

    class RequestTask extends AsyncTask<String, String, String> {

        String result;

        @Override
        protected String doInBackground(String... uri) {

            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response;
            String responseString = null;
            HttpPost httppost;
            ArrayList<NameValuePair> postParameters;
            httpclient = new DefaultHttpClient();
            httppost = new HttpPost(uri[0]);

            postParameters = new ArrayList<NameValuePair>();
            postParameters.add(new BasicNameValuePair("param1", "param1_value"));
            postParameters.add(new BasicNameValuePair("param2", "param2_value"));

            try {
                httppost.setEntity(new UrlEncodedFormEntity(postParameters));

                response = httpclient.execute(httppost);

                StatusLine statusLine = response.getStatusLine();
                if(statusLine.getStatusCode() == HttpStatus.SC_OK){
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    response.getEntity().writeTo(out);
                    responseString = out.toString();
                    out.close();
                } else{
                    //Closes the connection.
                    response.getEntity().getContent().close();
                    throw new IOException(statusLine.getReasonPhrase());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return responseString;
        }

        @Override
        protected void onPostExecute(String result) {
            //Do anything with response..



            Intent i = new Intent(getApplicationContext(), DisplayProduct.class);

            Pattern pattern = Pattern.compile("[A-Z]{1,2}[0-9R][0-9A-Z]? [0-9][ABD-HJLNP-UW-Z]{2}");
/*

            Document doc = Jsoup.parse(result);

            Element address = doc.select("div.adressscroll").first();
            String divString = address.html();

            Matcher matcher = pattern.matcher(divString);
            if (matcher.find()){
                System.out.println(matcher.group(1));
                this.result = matcher.group(1);
            }

            Log.d("result", result);
*/

            result = "EH165BJ";

            //find way to get food item from barcode
            i.putExtra("postcode",result);
            i.putExtra("currentTotal", totalMiles);
            startActivity(i);
        }

    }



}
