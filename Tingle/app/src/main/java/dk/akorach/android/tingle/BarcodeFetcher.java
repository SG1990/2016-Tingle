package dk.akorach.android.tingle;

import android.net.Uri;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by akor on 02.04.2016.
 */
public class BarcodeFetcher {

    private static final String TAG = "BarcodeFetcher";
    private static final String API_KEY = "b7b2f1af1add9ae8b9790c89c8158a67";

    public byte[] getUrlBytes(String urlSpec) throws IOException {
        InputStream in = null;
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            in = connection.getInputStream();

            if(connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(connection.getResponseMessage() +
                        ": with " +
                        urlSpec);
            }

            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }

            out.close();
            return out.toByteArray();

        } finally {
            if(in != null)
                in.close();
            connection.disconnect();
        }
    }

    public String getUrlString(String urlSpec) throws IOException{
        return new String(getUrlBytes(urlSpec));
    }

    public String fetchName(String barcode) {
        String name = "";

        try {
            String url = Uri.parse("https://api.outpan.com/v2/products/")
                    .buildUpon()
                    .appendPath(barcode)
                    .appendQueryParameter("apikey", API_KEY)
                    .build().toString();
            String jsonString = getUrlString(url);
            JSONObject jsonBody = new JSONObject(jsonString);
            name = parseInfo(jsonBody);
            Log.i(TAG, "Received JSON: " + jsonString);
        } catch (JSONException je) {
            Log.e(TAG, "Failed to parse JSON", je);
        } catch (IOException ioe) {
            Log.e(TAG, "Failed to fetch items", ioe);
        }

        return name;
    }

    private String parseInfo(JSONObject jsonBody)
            throws IOException, JSONException{

        return jsonBody.getString("name");
    }
}
