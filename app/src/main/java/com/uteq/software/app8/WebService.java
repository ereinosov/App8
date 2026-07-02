package com.uteq.software.app8;

import android.content.Context;
import android.os.AsyncTask;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class WebService extends AsyncTask<String, String, String> {
    private String url;
    private Map<String, String> datos;
    private Context context;
    private Asynchtask callback;

    public WebService(String url, Map<String, String> datos, Context context, Asynchtask callback) {
        this.url = url;
        this.datos = datos;
        this.context = context;
        this.callback = callback;
    }

    @Override
    protected String doInBackground(String... params) {
        String method = params[0];
        try {
            URL urlObj = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) urlObj.openConnection();
            conn.setRequestMethod(method);
            conn.setConnectTimeout(15000);
            conn.setReadTimeout(15000);

            if (method.equals("POST")) {
                // Implement POST data handling if needed
            }

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                return response.toString();
            }
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        if (callback != null) {
            callback.processFinish(result);
        }
    }
}
