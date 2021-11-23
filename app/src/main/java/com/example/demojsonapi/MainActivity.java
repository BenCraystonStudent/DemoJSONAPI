package com.example.demojsonapi;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private TextView mTextViewJSONName;
    private TextView mTextViewJSONTheme;
    private TextView mTextViewJSONData;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get references to the TextViews to allow us to update them
        mTextViewJSONName = (TextView)findViewById(R.id.textViewName);
        mTextViewJSONTheme = (TextView)findViewById(R.id.textViewTheme);
        mTextViewJSONData = (TextView)findViewById(R.id.textViewJSON);

        // This is the call to the  method getHTTPData(); that handles all the requesting
        // of the api data should anything fail it will throw an IOException
        try
        {
            getHTTPData();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    void getHTTPData() throws IOException
    {
        OkHttpClient client = new OkHttpClient();

        HttpUrl.Builder urlBuilder = HttpUrl.parse("https://services.arcgis.com/JJzESW51TqeY9uat/arcgis/rest/services/Ancient_Woodland_England/FeatureServer/0/query").newBuilder();

        urlBuilder.addQueryParameter("where", "NAME LIKE 'ABBOTS WOOD'");
        urlBuilder.addQueryParameter("outFields", "OBJECTID,NAME,THEME,THEMNAME,THEMID,STATUS,X_COORD,Y_COORD");
        urlBuilder.addQueryParameter("returnGeometry", "false");
        urlBuilder.addQueryParameter("returnDistinctValues", "true");
        urlBuilder.addQueryParameter("outSR", "4326");
        urlBuilder.addQueryParameter("f", "json");

        String url = urlBuilder.build().toString();

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback()
                {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        call.cancel();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {

                        final String myResponse = response.body().string();

                        response.close();

                        MainActivity.this.runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                try {

                                    JSONObject json = new JSONObject(myResponse);

                                    mTextViewJSONData.setText(json.toString());

                                    JSONObject oAttributes = json.getJSONArray("features")
                                            .getJSONObject(0)
                                            .getJSONObject("attributes");

                                    mTextViewJSONTheme.setText(oAttributes.getString("THEME"));
                                    mTextViewJSONName.setText(oAttributes.getString("NAME"));


                                } catch (JSONException e) {

                                    e.printStackTrace();

                                }

                            }

                        });
                    }

                }
        );
    }

}

