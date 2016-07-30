package com.solvetech.homeagent;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.solvetech.homeagent.model.ProjectInfo;

/**
 * Created by wpy on 8/25/15.
 */
public class PropertyDisplayActivity extends Activity {

    private int propertyId;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_layout_detail);
        Bundle bundle = getIntent().getExtras();
//        propertyId = bundle.getInt("developer_id");
//        PropertyInfo property = getPropertyById(propertyId);
//        showProperty(property);

        Button saleButton = (Button) findViewById(R.id.anything_btn);
        saleButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Comming soon", Toast.LENGTH_SHORT).show();
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        Log.d("PropertyDisplayActivity", "Option menu");
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_back:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
