package dk.akorach.android.tingle;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by SG on 19.02.2016.
 */
public class TingleFragment extends Fragment {
    private static final String TAG = "TingleFragment";

    private static final String KEY_PHOTO_FILENAME = "photoFilename";

    private static final int REQUEST_BARCODE = 0;
    private static final int REQUEST_PHOTO = 1;

    // Whether there is a Wi-Fi connection.
    private static boolean wifiConnected = false;
    // Whether there is a mobile connection.
    private static boolean mobileConnected = false;
    // The user's current network preference setting.
    public static String sPref = null;

    private String mPhotoFilename;

    //GUI variables
    private Button mAddThing;
    private ImageView mPhotoView;
    private TextView mLastAdded;
    private TextView mNewWhat, mNewWhere, mNewBarcode;

    public interface ToActivity { void stateChange(); }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null)
            mPhotoFilename = savedInstanceState.getString(KEY_PHOTO_FILENAME, "");

        if(mPhotoFilename == null || mPhotoFilename.isEmpty())
            mPhotoFilename = ThingsLab.getInstance(getActivity()).getNewFileName();

        Log.i(TAG, "Photo filename: " + mPhotoFilename);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        Log.i(TAG, "onSaveInstanceState");
        savedInstanceState.putString(KEY_PHOTO_FILENAME, mPhotoFilename);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tingle, container, false);

        //Accessing the GUI elements
        mLastAdded = (TextView) v.findViewById(R.id.last_thing);
        mAddThing = (Button) v.findViewById(R.id.add_button);
        mNewWhat = (TextView) v.findViewById(R.id.what_text);
        mNewBarcode = (TextView) v.findViewById(R.id.barcode_text);
        mNewWhere = (TextView) v.findViewById(R.id.where_text);

        //View products click event
        mAddThing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ((mNewWhat.getText().length() > 0) && (mNewWhere.getText().length() > 0)) {
                    Thing thing = new Thing();
                    thing.setWhat(mNewWhat.getText().toString().trim());
                    thing.setBarcode(mNewBarcode.getText().toString().trim());
                    thing.setWhere(mNewWhere.getText().toString().trim());
                    File file = getNewFileLocation(mPhotoFilename);
                    if(file != null && file.exists()) {
                        thing.setFilename(mPhotoFilename);

                    } else {
                        Log.i(TAG, "File is null or does not exist");
                    }

                    ThingsLab.getInstance(getContext()).addThing(thing);

                    mNewWhat.setText("");
                    mNewBarcode.setText("");
                    mNewWhere.setText("");
                    updateUI();

                    mPhotoFilename = ThingsLab.getInstance(getActivity()).getNewFileName();
                    Log.i(TAG, "Photo filename: " + mPhotoFilename);

                    ((ToActivity) getActivity()).stateChange();
                }
            }
        });

        mPhotoView = (ImageView) v.findViewById(R.id.thing_photo);

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();

        // Gets the user's network preference settings
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        // Retrieves a string value for the preferences. The second parameter
        // is the default value to use if a preference value is not found.
        String key = getString(R.string.pref_download_key);
        String def = getString(R.string.pref_download_any_network);
        sPref = sharedPrefs.getString(key, def);

        updateConnectedFlags();
    }

    // Checks the network connection and sets the wifiConnected and mobileConnected
    // variables accordingly.
    private void updateConnectedFlags() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeInfo = connMgr.getActiveNetworkInfo();
        if (activeInfo != null && activeInfo.isConnected()) {
            wifiConnected = activeInfo.getType() == ConnectivityManager.TYPE_WIFI;
            mobileConnected = activeInfo.getType() == ConnectivityManager.TYPE_MOBILE;
        } else {
            wifiConnected = false;
            mobileConnected = false;
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            if (requestCode == REQUEST_BARCODE) {
                Toast toast = Toast.makeText(getActivity(), "Scan was cancelled!",
                        Toast.LENGTH_LONG);
                toast.show();
            } else if(requestCode == REQUEST_PHOTO) {
                Toast toast = Toast.makeText(getActivity(), "Photo was not taken!",
                        Toast.LENGTH_LONG);
                toast.show();
            }
            return;
        }

        if (requestCode == REQUEST_BARCODE) {
            String contents = data.getStringExtra("SCAN_RESULT");
            String format = data.getStringExtra("SCAN_RESULT_FORMAT");
            mNewBarcode.setText(contents);

            String any = getString(R.string.pref_download_any_network);
            String wifi = getString(R.string.pref_download_wifi_only);
            updateConnectedFlags();
            if (((sPref.equals(any)) && (wifiConnected || mobileConnected))
                    || ((sPref.equals(wifi)) && (wifiConnected))) {
                new FetchNameTask().execute(contents);
            } else {
                Toast.makeText(getActivity(), "Preferred connection unavailable."
                        + " Preferred connection: " + sPref + ".",
                        Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REQUEST_PHOTO) {
            Toast toast = Toast.makeText(getActivity(), "Photo taken!",
                    Toast.LENGTH_LONG);
            toast.show();
        }
    }

    public void takePhoto() {
        File file = getNewFileLocation(mPhotoFilename);
        Uri uri = Uri.fromFile(file);

        Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(captureImage, REQUEST_PHOTO);
    }

    public void scanBarcode() {
        Intent scan = new Intent("com.google.zxing.client.android.SCAN");
        scan.putExtra("SCAN_MODE", "PRODUCT_MODE");
        startActivityForResult(scan, REQUEST_BARCODE);
    }

    private void updateUI(){
        Thing lastThing = ThingsLab.getInstance(getContext()).getLastThing();
        mLastAdded.setText(
                lastThing.toString());

        updatePhotoView(lastThing);
    }

    private void updatePhotoView(Thing thing) {
        File file = ThingsLab.getInstance(getActivity()).getPhotoFile(thing);
        if(file == null || !file.exists()) {
            mPhotoView.setImageDrawable(null);
        } else {
            Bitmap bitmap = PictureUtils.getScaledBitmap(
                    file.getPath(), getActivity());
            mPhotoView.setImageBitmap(bitmap);
        }
    }

    private File getNewFileLocation(String filename) {
        File externalFilesDir = getActivity()
                .getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        if(externalFilesDir == null) {
            return null;
        }

        return new File(externalFilesDir, filename);
    }

    private class FetchNameTask extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... params) {
            return new BarcodeFetcher().fetchName(params[0]);
        }

        @Override
        protected void onPostExecute(String name) {
            if (!name.equals("null"))
                mNewWhat.setText(name);
        }
    }
}
