package com.solvetech.homeagent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.solvetech.homeagent.model.CustomerInfo;
import com.solvetech.homeagent.model.CustomerStatus;
import com.solvetech.homeagent.model.CustomerSummary;
import com.solvetech.homeagent.model.TypeMetadata;
import com.solvetech.homeagent.R;

import android.app.Activity;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import org.json.JSONException;
import org.json.JSONObject;

import static com.solvetech.homeagent.data.DataAccessClient.BASE_URL;
import static com.solvetech.homeagent.utils.Utils.reportError;

public class CustomerInfoActivity extends Activity {

    private CustomerInfo customer;
	private int customerId;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);

		Bundle bundle = getIntent().getExtras();
		//customerId = bundle.getInt("customer_id");
		//statusList = getCustomerStatusById(customerId);
        customer = (CustomerInfo) bundle.getSerializable("customer");
        //new CustomerRetrievalTask().execute();
        setTitle("客户信息");
		Log.d("CustomerInfoActivity", "on create");
        setContentView(R.layout.customer_info);
        try {
            showCustomer(customer);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    // Inflate the menu items for use in the action bar
		Log.d("CustomerInfoActivity", "Option menu");
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

    public void showCustomer(CustomerInfo customer) throws IOException, JSONException, ClassNotFoundException {
		TypeMetadata meta = TypeMetadata.getInstance(getApplicationContext());
		//TextView nameView = (TextView) findViewById(R.id.name_view);
		TextView phoneNumView = (TextView) findViewById(R.id.phonenum_view);
		TextView propertyTypeView = (TextView) findViewById(R.id.property_type_view);
		TextView locationView = (TextView) findViewById(R.id.location_view);
		TextView priceView = (TextView) findViewById(R.id.price_view);
		TextView layoutView = (TextView) findViewById(R.id.layout_view);
		TextView areaView = (TextView) findViewById(R.id.area_view);
		TextView furnishView = (TextView) findViewById(R.id.furnish_view);
		
		//nameView.setText(customer.getLastName() + " " + customer.getFirstName());
		phoneNumView.setText(customer.getPhoneNumber());
		propertyTypeView.setText(meta.getPropertyClass().get(customer.getPropertyClass()));
		locationView.setText(meta.getLocation().get(customer.getLocationCd()));
		priceView.setText(meta.getPrice().get(customer.getPriceRange()));
		layoutView.setText(meta.getLayout().get(customer.getPriceRange()));
		areaView.setText(meta.getArea().get(customer.getAreaReq()));
		furnishView.setText(meta.getFurnish().get(customer.getFurnish()));
	}

}
