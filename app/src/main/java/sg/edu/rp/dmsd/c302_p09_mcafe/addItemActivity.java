package sg.edu.rp.dmsd.c302_p09_mcafe;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class addItemActivity extends AppCompatActivity {

    Button btnAdd;
    EditText etPrice, etItem;
    private AsyncHttpClient client;

    String user = "";
    String apikey = "";
    String catID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        etItem = findViewById(R.id.etItem);
        etPrice = findViewById(R.id.etPrice);
        btnAdd = findViewById(R.id.btnAdd);

        Intent intent = getIntent();
        catID = intent.getStringExtra("categoryID");

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String item = etItem.getText().toString();
                String price = etPrice.getText().toString();

                if (item.length() == 0 || price.length() == 0) {
                    Toast.makeText(getBaseContext(), "Fields must not be empty", Toast.LENGTH_LONG).show();
                } else {
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(addItemActivity.this);
                    user = prefs.getString("id", "");
                    apikey = prefs.getString("apikey","");
                    AsyncHttpClient client = new AsyncHttpClient();
                    RequestParams params = new RequestParams();
                    params.add("description", item);
                    params.add("price", price);
                    params.add("loginId", user);
                    params.add("apikey", apikey);
                    params.add("menu_item_cat_id", catID);
                    client.post("http://10.0.2.2/C302_P09_mCafe/addMenuItem.php", params, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            try {
                                String message = response.getString("message");
                                Toast.makeText(getBaseContext(), message, Toast.LENGTH_LONG).show();
                                Intent i = new Intent();
                                setResult(RESULT_OK,i);
                                finish();
                            }
                            catch (JSONException e) {

                            }
                        }
                    });
                }
            }
        });
    }
}
