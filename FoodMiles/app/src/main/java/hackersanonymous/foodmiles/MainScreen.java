package hackersanonymous.foodmiles;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
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
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

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
        String str = "";
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanResult != null) {
            str = scanResult.getContents();
            Log.d("barcodefirst", str);
;        }
        // else continue with any other code you need in the method

        String uri = "http://gepir.gs1.org/v32/xx/gtin.aspx?Lang=en-US";
        postRequest = new RequestTask(str);
        postRequest.execute(uri);
    }

    class RequestTask extends AsyncTask<String, String, String> {

        String result;
        String barcodeNum;
        boolean seenAddress = false;

        RequestTask(String str){
            barcodeNum = str;
        }

        @Override
        protected String doInBackground(String... uri) {

            HttpClient httpclient;
            HttpResponse response;
            StringBuilder responseString = new StringBuilder();
            responseString.insert(0," ");
            HttpPost httppost;
            ArrayList<NameValuePair> postParameters;
            httpclient = new DefaultHttpClient();
            httppost = new HttpPost(uri[0]);

            assert !barcodeNum.isEmpty();
            Log.d("barcode", barcodeNum);

            postParameters = new ArrayList<NameValuePair>();
            postParameters.add(new BasicNameValuePair("_ctl0_cphMain_LoginPanel_ScriptManager_HiddenField", ";;AjaxControlToolkit:en-US:c5c982cc-4942-4683-9b48-c2c58277700f:865923e8:411fea1c:e7c87f07;AjaxControlToolkit,+Version=1.0.20229.20821,+Culture=neutral,+PublicKeyToken=28f01b0e84b6d53e:en-US:c5c982cc-4942-4683-9b48-c2c58277700f:865923e8:91bd373d:ad1f21ce:596d588c:8e72a662:411fea1c:acd642d2:77c58d20:14b56adc:269a19ae:d7349d0c;;AjaxControlToolkit:en-US:c5c982cc-4942-4683-9b48-c2c58277700f:865923e8:411fea1c:e7c87f07;AjaxControlToolkit,+Version=1.0.20229.20821,+Culture=neutral,+PublicKeyToken=28f01b0e84b6d53e:en-US:c5c982cc-4942-4683-9b48-c2c58277700f:865923e8:91bd373d:ad1f21ce:596d588c:8e72a662:411fea1c:acd642d2:77c58d20:14b56adc:269a19ae:d7349d0c"));
            postParameters.add(new BasicNameValuePair("__EVENTTARGET",""));
            postParameters.add(new BasicNameValuePair("__EVENTARGUMENT",""));
            postParameters.add(new BasicNameValuePair("_ctl0_cphMain_TabContainerGTIN_ClientState","{\"ActiveTabIndex\":0,\"TabState\":[true]}"));
            postParameters.add(new BasicNameValuePair("__VIEWSTATE", "/wEPDwUJLTg0MDI5NTk5D2QWAmYPZBYCAgEPZBYCAgEPZBYGAgEPDxYCHgdWaXNpYmxlaGRkAgMPZBYCAgMPZBYCAgMPPCsACgEADxYCHhJEZXN0aW5hdGlvblBhZ2VVcmwFMGh0dHA6Ly9nZXBpci5nczEub3JnL3YzMi94eC9ndGluLmFzcHg/TGFuZz1lbi1VU2RkAgcPZBYCZg9kFgYCAQ9kFgJmD2QWAgIBD2QWAgIHDw8WAh4EVGV4dAUGU2VhcmNoZGQCAw8PFgIfAGhkZAIFDw8WAh8AaGRkGAIFHl9fQ29udHJvbHNSZXF1aXJlUG9zdEJhY2tLZXlfXxYCBTNfY3RsMDpjcGhNYWluOkxvZ2luUGFuZWw6TG9naW5DdHJsOkxvZ2luSW1hZ2VCdXR0b24FHl9jdGwwOmNwaE1haW46VGFiQ29udGFpbmVyR1RJTgUeX2N0bDA6Y3BoTWFpbjpUYWJDb250YWluZXJHVElODw9kZmSrlQu81cwxTJPrpnK5BNtHLDGK7w=="));
            postParameters.add(new BasicNameValuePair("__VIEWSTATEGENERATOR","F155F32B"));
            postParameters.add(new BasicNameValuePair("_ctl0:cphMain:LoginPanel:LoginCtrl:UserName",""));
            postParameters.add(new BasicNameValuePair("_ctl0:cphMain:LoginPanel:LoginCtrl:Password",""));
            postParameters.add(new BasicNameValuePair("_ctl0:cphMain:TabContainerGTIN:TabPanelGTIN:txtRequestGTIN", barcodeNum));
            postParameters.add(new BasicNameValuePair("_ctl0:cphMain:TabContainerGTIN:TabPanelGTIN:rblGTIN","party"));
            postParameters.add(new BasicNameValuePair("_ctl0:cphMain:TabContainerGTIN:TabPanelGTIN:btnSubmitGTIN","Search"));

            try {
                httppost.setEntity(new UrlEncodedFormEntity(postParameters));

                response = httpclient.execute(httppost);

                StatusLine statusLine = response.getStatusLine();
                if(statusLine.getStatusCode() == HttpStatus.SC_OK){
                    responseString.ensureCapacity(1000000);

                    BufferedReader reader =
                            new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        //THE ERROR IS HERE, STRING BUILDER CANNOT ALLOCATE ENOUGH MEMORY!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                        //responseString.ensureCapacity(responseString.capacity() + line.getBytes().length);

                        if(line.indexOf("addressscroll")>-1) {
                            seenAddress = true;
                        }
                        if(seenAddress){
                            responseString.append(line + "\n");
                        }

                        if(line.indexOf("</div>")>-1) {
                            seenAddress = false;
                        }

                    }

                } else{
                    //Closes the connection.
                    response.getEntity().getContent().close();
                    throw new IOException(statusLine.getReasonPhrase());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d("builder", responseString.toString());
            return responseString.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            //Do anything with response..

            Intent i = new Intent(getApplicationContext(), DisplayProduct.class);

           // Pattern pattern = Pattern.compile("[A-Z]{1,2}[0-9R][0-9A-Z]? [0-9][ABD-HJLNP-UW-Z]{2}");


            Document doc = Jsoup.parse(result);
            this.result = doc.text();
            /*
            Log.d("responce", result);
            Element address = doc.select("div.addressscroll").first();
            String divString = address.html();

            Matcher matcher = pattern.matcher(divString);
            if (matcher.find()){
                System.out.println(matcher.group(1));
                this.result = matcher.group(1);
            }
            */
            Log.d("result", this.result);

            //find way to get food item from barcode
            i.putExtra("postcode",this.result);
            i.putExtra("currentTotal", totalMiles);
            startActivity(i);
        }

    }



}
