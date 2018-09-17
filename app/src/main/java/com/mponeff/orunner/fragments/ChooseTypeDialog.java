package com.mponeff.orunner.fragments;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.simplelist.MaterialSimpleListAdapter;
import com.afollestad.materialdialogs.simplelist.MaterialSimpleListItem;
import com.mponeff.orunner.activities.SaveActivity;

import java.util.Arrays;
import java.util.List;

/**
 * Extend DialogFragment and use that class as wrapper of MaterialDialog class
 */
public class ChooseTypeDialog extends DialogFragment {
    private static final String DIALOG_TITLE = "Choose type of activity";
    private static final String TYPE_COMPETITION = "Competition";
    private static final String TYPE_TRAINING = "Training";

    private static final List<String> TYPES = Arrays.asList(
        TYPE_COMPETITION,
        TYPE_TRAINING
    );

    private MaterialSimpleListAdapter setAdapter(List<String> types, final Context context) {
        final MaterialSimpleListAdapter adapter = new MaterialSimpleListAdapter(new MaterialSimpleListAdapter.Callback() {
            @Override
            public void onMaterialListItemSelected(MaterialDialog mtrDialog, int index, MaterialSimpleListItem item) {
                String type = index == 0 ? TYPE_COMPETITION : TYPE_TRAINING;

                Intent addExercise = new Intent(context, SaveActivity.class);
                Bundle outState = new Bundle();
                outState.putString("type", type);
                outState.putString("mode", "Save");
                Log.e(DIALOG_TITLE, "Type " + type);
                addExercise.putExtras(outState);
                startActivity(addExercise);
                dismiss();
            }
        });

        for (int i = 0; i < types.size(); i++) {
            adapter.add(new MaterialSimpleListItem.Builder(context)
                    .content(types.get(i))
                    .build());
        }
        return adapter;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new MaterialDialog.Builder(getActivity())
                .title(DIALOG_TITLE)
                .adapter(setAdapter(TYPES, getActivity()), null)
                .build();
    }
}
