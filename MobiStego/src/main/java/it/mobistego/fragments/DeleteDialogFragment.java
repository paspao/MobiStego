package it.mobistego.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import it.mobistego.R;
import it.mobistego.beans.MobiStegoItem;
import it.mobistego.utils.Utility;

/**
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
public class DeleteDialogFragment extends DialogFragment {


    private OnItemDeleted mCallback;
    private MobiStegoItem mobiStegoItem;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.sure_delete)
                .setPositiveButton(R.string.yes, (dialog, id) -> {
                    Utility.deleteMobiStegoItem(mobiStegoItem, getActivity());
                    mCallback.onDelete();

                })
                .setNegativeButton(R.string.no, (dialog, id) -> {
                    //do nothing
                });

        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Activity activity) {
        super.onAttach(activity);


        try {
            mCallback = (OnItemDeleted) activity;

        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnItemDeleted");
        }
    }

    public void setMobiStegoItem(MobiStegoItem mobiStegoItem) {
        this.mobiStegoItem = mobiStegoItem;
    }

    public interface OnItemDeleted {
        void onDelete();

    }

}