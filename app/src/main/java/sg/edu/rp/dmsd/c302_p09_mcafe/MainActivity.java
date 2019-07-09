package sg.edu.rp.dmsd.c302_p09_mcafe;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private ArrayAdapter<MenuCategory> adapter;
    private ArrayList<MenuCategory> list;
    private AsyncHttpClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.listViewMenuCategories);
        list = new ArrayList<MenuCategory>();
        adapter = new ArrayAdapter<MenuCategory>(this, android.R.layout.simple_list_item_1, list);
        listView.setAdapter(adapter);

        client = new AsyncHttpClient();

        //TODO: read loginId and apiKey from SharedPreferences
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String loginID = prefs.getString("id", "");
        String apikey = prefs.getString("apikey", "");

        // TODO: if loginId and apikey is empty, go back to LoginActivity
        if (loginID.equalsIgnoreCase("") || apikey.equalsIgnoreCase("")) {
            Intent intent = new Intent(getBaseContext(), LoginActivity.class);
            startActivity(intent);
        }

        //TODO: call getMenuCategories.php to populate the list view
        String url = "http://10.0.2.2/C302_P09_mCafe/getMenuCategories.php";
        RequestParams params = new RequestParams();
        params.add("loginId", loginID);
        params.add("apikey", apikey);
        client.post(url, params, new JsonHttpResponseHandler() {
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                try {
                    Log.i("JSON Results: ", response.toString());
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject jsonObj = response.getJSONObject(i);

                        String categoryId = jsonObj.getString("menu_item_category_id");
                        String description = jsonObj.getString("menu_item_category_description");

                        MenuCategory cat = new MenuCategory(categoryId, description);
                        list.add(cat);
                    }
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                MenuCategory selected = list.get(position);

                //TODO: make Intent to DisplayMenuItemsActivity passing the categoryId
                Intent intent = new Intent(getBaseContext(), DisplayItemsMenuActivity.class);
                intent.putExtra("categoryid", selected.getCategoryId());
                startActivity(intent);

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_logout) {
            // TODO: Clear SharedPreferences
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear();
            editor.commit();

            // TODO: Redirect back to login screen
            Intent intent = new Intent(getBaseContext(), LoginActivity.class);
            startActivity(intent);


            return true;


        }
        return super.onOptionsItemSelected(item);
    }
}
