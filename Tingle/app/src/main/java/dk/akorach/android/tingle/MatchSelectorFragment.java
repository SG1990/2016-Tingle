package dk.akorach.android.tingle;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by akor on 24.04.2016.
 */
public class MatchSelectorFragment extends DialogFragment {

    public static final String EXTRA_THING =
            "dk.akorach.android.tingle.thing";
    private static final String ARG_ITEMS = "items";

    public static MatchSelectorFragment newInstance(ArrayList<Thing> things) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_ITEMS, things);

        MatchSelectorFragment fragment = new MatchSelectorFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final ArrayList<Thing> things =
                (ArrayList<Thing>) getArguments().getSerializable(ARG_ITEMS);
        CharSequence[] items = new CharSequence[things.size()];
        for(int i = 0; i < things.size(); i++) {
            Thing t = things.get(i);
            items[i] = t.getWhat() + ", "
                    + t.getWhere();
        }

        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.match_selector_title)
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendResult(Activity.RESULT_OK, things.get(which).getId());
                        dialog.dismiss();
                    }
                })
                .create();
    }

    private void sendResult(int resultCode, UUID thingId) {
        if(getTargetFragment() == null) {
            return;
        }

        Intent intent = new Intent();
        intent.putExtra(EXTRA_THING, thingId);

        getTargetFragment()
                .onActivityResult(getTargetRequestCode(), resultCode, intent);
    }
}
