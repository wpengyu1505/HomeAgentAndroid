package com.solvetech.homeagent.data;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

/**
 * Created by wpy on 8/30/15.
 */
public class DataAccessClient implements Serializable {

    //public static String BASE_URL = "https://10.0.2.2:8443/homeagent-services";
    //public static String BASE_URL = "http://10.0.2.2:8080/homeagent-services";
    //public static String BASE_URL = "http://solvetech.cn:8080/homeagent-services";
    public static String BASE_URL = "https://solvetech.cn:8443/homeagent-services";
    public String accessToken;

    public DataAccessClient(String token) {
        accessToken = token;
        Log.d("AccessToken: ", accessToken);
    }

    public String retrieveDataInJson(String urlStr) throws IOException {
        BufferedReader br = null;
        String line = null;
        try {
            URL url = new URL(urlStr);
            HttpURLConnection conn = getHttpConnection(url);
            conn.setRequestMethod("GET");
            br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();

            while ((line = br.readLine()) != null) {
                sb.append(line + '\n');
            }
            Log.d("Server Response", sb.toString());
            return sb.toString();

            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } catch (Exception e) {
            throw e;
        } finally {
            if (br != null) {
                br.close();
            }
        }
    }

    public void postDataInJson(String urlStr, String jsonStr) {
        try {
            URL url = new URL(urlStr);
            HttpURLConnection conn = getHttpConnection(url);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestMethod("POST");
            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
            writer.write(jsonStr);
            writer.flush();
            int response = conn.getResponseCode();
            Log.d("Server Response", ""+response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String postDataInJsonWithResponse(String urlStr, String jsonStr) {
        try {
            URL url = new URL(urlStr);
            HttpURLConnection conn = getHttpConnection(url);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestMethod("POST");
            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
            writer.write(jsonStr);
            writer.flush();
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();

            String line = null;
            while ((line = br.readLine()) != null) {
                sb.append(line + '\n');
            }
            Log.d("Server Response", sb.toString());
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public int putDataInJsonWithResponse(String urlStr, String jsonStr) {
        try {
            URL url = new URL(urlStr);
            HttpURLConnection conn = getHttpConnection(url);
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestMethod("PUT");
            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
            writer.write(jsonStr);
            writer.flush();
            writer.close();

            Log.d("message", conn.getResponseMessage());
            return conn.getResponseCode();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int deleteDataWithResponse(String urlStr) {
        try {
            URL url = new URL(urlStr);
            HttpURLConnection conn = getHttpConnection(url);
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded" );
            conn.setRequestMethod("DELETE");
            conn.connect();
            Log.d("message", conn.getResponseMessage());
            return conn.getResponseCode();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public Bitmap getWebImage(String urlStr) {
        Bitmap bitmap = null;
        try {
            URL url = new URL(urlStr);
            HttpURLConnection conn = getHttpConnection(url);
            bitmap = BitmapFactory.decodeStream(conn.getInputStream());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return bitmap;
    }

    private HttpURLConnection getHttpConnection(URL url) throws IOException {

        // HTTPS
        if (url.getProtocol().toLowerCase().equals("https")) {
            TrustManager[] trustManager = new TrustManager[] {new TrustEverythingTrustManager()};
            SSLContext sslContext = null;
            try {
                sslContext = SSLContext.getInstance("TLS");
                sslContext.init(null, trustManager, new java.security.SecureRandom());
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }catch (KeyManagementException e) {
                e.printStackTrace();
            }

            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
            HttpsURLConnection sconn = (HttpsURLConnection) url.openConnection();
            sconn.setHostnameVerifier(new VerifyEverythingHostnameVerifier());
            sconn.setRequestProperty("Authorization", "Bearer " + accessToken);
            return sconn;
        } else {
            // HTTP
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Authorization", "Bearer " + accessToken);
            return conn;
        }
    }
}
