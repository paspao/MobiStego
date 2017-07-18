package it.mobistego.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import it.mobistego.R;
import it.mobistego.beans.MobiStegoItem;
import it.mobistego.tasks.BitmapWorkerTask;

/*
 * Copyright (C) 2015  Pasquale Paola
 * <p/>
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * <p/>
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
public class ItemViewFragment extends Fragment implements View.OnClickListener {

    private final static String TAG = ItemViewFragment.class.getName();
    private MobiStegoItem mobiStegoItem;
    private ImageView imageView;
    private ProgressBar progressBar;
    private ImageButton buttonDelete;
    private ImageButton buttonShare;
    private Button buttonDecode;
    private TextView textView;
    private OnItemView mCallback;

    public interface OnItemView {
        void itemViewOnDelete(MobiStegoItem mobiStegoItem);

        void itemViewOnShare(MobiStegoItem mobiStegoItem);

        void itemViewOnDecrypt(String message,String password,TextView textView);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.item_view_layout, container, false);
        buttonDelete = (ImageButton) view.findViewById(R.id.button_delete);
        buttonShare = (ImageButton) view.findViewById(R.id.button_share);
        textView = (TextView) view.findViewById(R.id.text_view_item);
        imageView = (ImageView) view.findViewById(R.id.image_view_item);
        progressBar = (ProgressBar) view.findViewById(R.id.progrss_view);
        buttonDecode=(Button) view.findViewById(R.id.button_decrypt);
        BitmapWorkerTask workerBimt = new BitmapWorkerTask(imageView, progressBar);
        workerBimt.execute(mobiStegoItem.getBitmapCompressed());
        textView.setText(mobiStegoItem.getMessage());
        buttonDelete.setOnClickListener(this);
        buttonShare.setOnClickListener(this);
        buttonDecode.setOnClickListener(this);
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);


        try {
            mCallback = (OnItemView) activity;

        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnItemView");
        }
    }

    public void setMobiStegoItem(MobiStegoItem mobiStegoItem) {
        this.mobiStegoItem = mobiStegoItem;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_delete:
                mCallback.itemViewOnDelete(mobiStegoItem);
                break;
            case R.id.button_share:
                mCallback.itemViewOnShare(mobiStegoItem);
                break;
            case R.id.button_decrypt:
                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                alertDialogBuilder.setMessage(R.string.enter_password);
                alertDialogBuilder.setView(R.layout.password_dialog);
                alertDialogBuilder.setPositiveButton(R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int arg1) {
                                EditText editText=(EditText) ((AlertDialog) dialog).findViewById(R.id.text_password_dialog);
                                mCallback.itemViewOnDecrypt(mobiStegoItem.getMessage(),editText.getText().toString(),textView);

                            }
                        });
                alertDialogBuilder.setNegativeButton(R.string.abort,new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                break;
            default:
                Log.d(TAG, "Unknown Action");
                break;
        }
    }


}
