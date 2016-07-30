package com.solvetech.homeagent.model;

import android.content.Context;
import android.util.Log;

import com.solvetech.homeagent.HomeActivity;
import com.solvetech.homeagent.data.DataAccessClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;

import static com.solvetech.homeagent.data.DataAccessClient.BASE_URL;

public class TypeMetadata implements Serializable {

	private HashMap<Integer, String> city;
	private HashMap<Integer, String> location;
	private HashMap<Integer, String> propertyClass;
	private HashMap<Integer, String> layout;
	private HashMap<Integer, String> area;
	private HashMap<Integer, String> price;
	private HashMap<Integer, String> furnish;
	DataAccessClient client;
	public static TypeMetadata instance;
	
	public static TypeMetadata getInstance(Context context) throws IOException, JSONException, ClassNotFoundException {
		if (instance == null) {
			return getMetadata(context);
		} else {
			return instance;
		}
	}
	
	private TypeMetadata(Context context) throws JSONException, IOException {
        String accessToken = context.getSharedPreferences(HomeActivity.PREFS_NAME, Context.MODE_PRIVATE).getString("accessToken", null);
        client = new DataAccessClient(accessToken);
        String url = BASE_URL + "/meta";
        Log.d("Metadata", url);
        String response = null;
        response = client.retrieveDataInJson(url);
        loadMetadata(response);
	}

    public static TypeMetadata pullMetadata(Context context) throws JSONException, IOException, ClassNotFoundException {
        try {
            return new TypeMetadata(context);
        } catch (IOException e) {
            return getMetadata(context);
        }
    }

	private void loadMetadata(String jsonStr) throws JSONException {
		city = new HashMap<Integer, String>();
		location = new HashMap<Integer, String>();
		propertyClass = new HashMap<Integer, String>();
		layout = new HashMap<Integer, String>();
		area = new HashMap<Integer, String>();
		price = new HashMap<Integer, String>();
		furnish = new HashMap<Integer, String>();

		JSONObject obj = new JSONObject(jsonStr);
		Iterator keyItr = null;
        JSONObject item = null;
		// Loading propertyClass
        item = obj.getJSONObject("propertyClass");
		keyItr = item.keys();
		while (keyItr.hasNext()) {
			String key = (String) keyItr.next();
			String value = item.getString(key);
			propertyClass.put(Integer.parseInt(key), value);
		}

		// Loading furnish
        item = obj.getJSONObject("furnish");
        keyItr = item.keys();
        while (keyItr.hasNext()) {
            String key = (String) keyItr.next();
            String value = item.getString(key);
            furnish.put(Integer.parseInt(key), value);
        }

		// Loading area
        item = obj.getJSONObject("area");
        keyItr = item.keys();
        while (keyItr.hasNext()) {
            String key = (String) keyItr.next();
            String value = item.getString(key);
            area.put(Integer.parseInt(key), value);
        }

		// Loading city
        item = obj.getJSONObject("city");
        keyItr = item.keys();
        while (keyItr.hasNext()) {
            String key = (String) keyItr.next();
            String value = item.getString(key);
            city.put(Integer.parseInt(key), value);
        }

		// Loading price
        item = obj.getJSONObject("price");
        keyItr = item.keys();
        while (keyItr.hasNext()) {
            String key = (String) keyItr.next();
            String value = item.getString(key);
            price.put(Integer.parseInt(key), value);
        }

		// Loading Layout
        item = obj.getJSONObject("layout");
        keyItr = item.keys();
        while (keyItr.hasNext()) {
            String key = (String) keyItr.next();
            String value = item.getString(key);
            layout.put(Integer.parseInt(key), value);
        }

        item = obj.getJSONObject("location");
        keyItr = item.keys();
        while (keyItr.hasNext()) {
            String key = (String) keyItr.next();
            JSONObject value = item.getJSONObject(key);
            location.put(Integer.parseInt(key), value.getString("locationName"));
        }

	}

    public void persistMetadata(Context context) throws IOException {
        FileOutputStream fos = context.openFileOutput("meta.obj", Context.MODE_PRIVATE);
        ObjectOutputStream os = new ObjectOutputStream(fos);
        os.writeObject(this);
        os.close();
        fos.close();
    }

    public static TypeMetadata getMetadata(Context context) throws IOException, ClassNotFoundException {
        FileInputStream fis = context.openFileInput("meta.obj");
        ObjectInputStream is = new ObjectInputStream(fis);
        TypeMetadata meta = (TypeMetadata) is.readObject();
        is.close();
        fis.close();
        return meta;
    }


    private void loadDummyData() {
        city = new HashMap<Integer, String>();
        location = new HashMap<Integer, String>();
        propertyClass = new HashMap<Integer, String>();
        layout = new HashMap<Integer, String>();
        area = new HashMap<Integer, String>();
        price = new HashMap<Integer, String>();
        furnish = new HashMap<Integer, String>();

        city.put(0, "New York");
        HashMap<String, String> locationInfo = new HashMap<String, String>();
        location.put(0, "Lower East Side");
        propertyClass.put(0, "Enterprise");
        propertyClass.put(1, "Civilian");
        layout.put(0, "2 Beds 2 Living");
        layout.put(1, "3 Beds 2 Living");
        area.put(0, "100m2 - 150m2");
        area.put(1, "150m2 - 200m2");
        price.put(0, "1000000 - 1500000");
        price.put(1, "1500000 - 2000000");
        furnish.put(0, "good");
        furnish.put(1, "bad");
    }

	public HashMap<Integer, String> getCity() {
		return city;
	}

	public void setCity(HashMap<Integer, String> city) {
		this.city = city;
	}

	public HashMap<Integer, String> getLocation() {
		return location;
	}

	public void setLocation(HashMap<Integer, String> location) {
		this.location = location;
	}

	public HashMap<Integer, String> getPropertyClass() {
		return propertyClass;
	}

	public void setPropertyClass(HashMap<Integer, String> propertyClass) {
		this.propertyClass = propertyClass;
	}

	public HashMap<Integer, String> getLayout() {
		return layout;
	}

	public void setLayout(HashMap<Integer, String> layout) {
		this.layout = layout;
	}

	public HashMap<Integer, String> getArea() {
		return area;
	}

	public void setArea(HashMap<Integer, String> area) {
		this.area = area;
	}

	public HashMap<Integer, String> getPrice() {
		return price;
	}

	public void setPrice(HashMap<Integer, String> price) {
		this.price = price;
	}

	public HashMap<Integer, String> getFurnish() {
		return furnish;
	}

	public void setFurnish(HashMap<Integer, String> furnish) {
		this.furnish = furnish;
	}

}
