package dk.akorach.android.tingle;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by SG on 19.02.2016.
 */
public class TingleFragment extends Fragment {

    private static final int REQUEST_BARCODE = 0;

    //GUI variables
    private Button mListThings;
    private Button mScanThing;
    private Button mAddThing;
    private TextView mLastAdded;
    private TextView mNewWhat, mNewWhere, mNewBarcode;

    public interface ToActivity { void stateChange(); }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tingle, container, false);

        //Accessing the GUI elements
        mLastAdded = (TextView) v.findViewById(R.id.last_thing);
        mScanThing = (Button) v.findViewById(R.id.scan_button);
        mAddThing = (Button) v.findViewById(R.id.add_button);
        mNewWhat = (TextView) v.findViewById(R.id.what_text);
        mNewBarcode = (TextView) v.findViewById(R.id.barcode_text);
        mNewWhere = (TextView) v.findViewById(R.id.where_text);


        final Intent scan = new Intent("com.google.zxing.client.android.SCAN");
        mScanThing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scan.putExtra("SCAN_MODE", "PRODUCT_MODE");
                startActivityForResult(scan, REQUEST_BARCODE);
            }
        });

        //View products click event
        mAddThing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ((mNewWhat.getText().length() > 0) && (mNewWhere.getText().length() > 0)) {
                    Thing thing = new Thing();
                    thing.setWhat(mNewWhat.getText().toString().trim());
                    thing.setBarcode(mNewBarcode.getText().toString().trim());
                    thing.setWhere(mNewWhere.getText().toString().trim());
                    ThingsLab.getInstance(getContext()).addThing(thing);

                    mNewWhat.setText("");
                    mNewBarcode.setText("");
                    mNewWhere.setText("");
                    updateUI();

                    ((ToActivity) getActivity()).stateChange();
                }
            }
        });

        mListThings = (Button) v.findViewById(R.id.list_button);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            mListThings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(getActivity(), ListActivity.class);
                    startActivity(i);

                }
            });
        } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mListThings.setVisibility(View.GONE);
        }

        PackageManager packageManager = getActivity().getPackageManager();
        if(packageManager.resolveActivity(scan,
                PackageManager.MATCH_DEFAULT_ONLY) == null) {
            mScanThing.setEnabled(false);
        }

        return v;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            if (requestCode == REQUEST_BARCODE) {
                Toast toast = Toast.makeText(getActivity(), "Scan was Cancelled!",
                        Toast.LENGTH_LONG);
                toast.show();
            }
            return;
        }

        if (requestCode == REQUEST_BARCODE) {
            String contents = data.getStringExtra("SCAN_RESULT");
            String format = data.getStringExtra("SCAN_RESULT_FORMAT");
            mNewBarcode.setText(contents);

            // check connection and get name
            ConnectivityManager connMgr = (ConnectivityManager)
                    getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                new FetchNameTask().execute(contents);
            } else {
                Toast.makeText(getActivity(), "No network connection available.",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void updateUI(){
        mLastAdded.setText(
                ThingsLab.getInstance(getContext()).getLastThing().toString());
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
