package com.solvetech.homeagent.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.solvetech.homeagent.AddCustomerActivity;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wpy on 9/5/15.
 */
public class Utils {
    public static void reportError(Context context, String err) {
        Toast.makeText(context, err, Toast.LENGTH_SHORT).show();
    }

    public static Integer getKeyFromHash(HashMap<Integer, String> map, String value) {
        for (Map.Entry<Integer, String> entry : map.entrySet()) {
            if (entry.getValue().equals(value)) {
                return entry.getKey();
            }
        }
        return null;
    }

    public static List<String> convertHashKeyToArray(HashMap<Integer, String> map) {
        List<Integer> keyList = new ArrayList<Integer>(map.keySet());
        List<String> valueList = new ArrayList<String>();
        Collections.sort(keyList);
        for (Integer key : keyList) {
            valueList.add(map.get(key));
        }
        return valueList;
    }

    public static void startSelectedActivity(Context context, Class<?> activity) throws ClassNotFoundException {
        Intent intent = new Intent(context , activity);
        context.startActivity(intent);
    }

    public static String formatPrice(double price) {
        DecimalFormat formatter = new DecimalFormat("\u00A5###,###.###");
        return formatter.format(price);
    }
}
