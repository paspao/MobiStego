package it.mobistego.tasks;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import java.io.IOException;
import java.util.List;

import it.mobistego.R;
import it.mobistego.beans.MobiStegoItem;
import it.mobistego.business.LSB2bit;
import it.mobistego.fragments.MainFragment;
import it.mobistego.utils.Constants;
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
public class EncodeTask extends AsyncTask<MobiStegoItem, Integer, MobiStegoItem> {

    private static String TAG = EncodeTask.class.getName();

    private int maxProgeress;

    private Activity activity;

    private ProgressDialog progressDialog;

    public EncodeTask(Activity act) {
        super();
        this.activity = act;
    }


    @Override
    protected MobiStegoItem doInBackground(MobiStegoItem... params) {
        MobiStegoItem result = null;
        maxProgeress = 0;
        if (params.length > 0) {
            MobiStegoItem mobistego = params[0];
            Bitmap bitm = BitmapFactory.decodeFile(mobistego.getBitmap().getAbsolutePath());
            int originalHeight = bitm.getHeight();
            int originalWidth = bitm.getWidth();
            List<Bitmap> srcList = Utility.splitImage(bitm);

            bitm.recycle();
            List<Bitmap> encodedList = LSB2bit.encodeMessage(srcList, mobistego.getMessage(), new LSB2bit.ProgressHandler() {
                @Override
                public void setTotal(int tot) {
                    maxProgeress = tot;
                    progressDialog.setMax(maxProgeress);
                    Log.d(TAG, "Total lenght " + tot);
                }

                @Override
                public void increment(int inc) {
                    publishProgress(inc);
                }

                @Override
                public void finished() {
                    Log.d(TAG, "Message encoding end!");
                    progressDialog.setIndeterminate(true);
                    progressDialog.setTitle("Merging images...");
                }
            });
            //free memory
            System.gc();
            Bitmap srcEncoded = Utility.mergeImage(encodedList, originalHeight, originalWidth);
            try {
                result = Utility.saveMobiStegoItem(mobistego.getMessage(), srcEncoded);
                result.setEncoded(true);
            } catch (IOException e) {
                e.printStackTrace();
                //TODO manage exception
            }

            //free memory
            for (Bitmap bitmamp : srcList)
                bitmamp.recycle();

            for (Bitmap bitmamp : encodedList)
                bitmamp.recycle();


        }
        return result;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {

        super.onProgressUpdate(values);
        progressDialog.show();
        progressDialog.incrementProgressBy(values[0]);
        Log.d(TAG, "Progress " + values[0]);

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage(activity.getString(R.string.loading));
        progressDialog.setTitle(activity.getString(R.string.encoding));
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(false);
        
        
    }

    @Override
    protected void onPostExecute(MobiStegoItem mobiStegoItem) {
        super.onPostExecute(mobiStegoItem);
        progressDialog.dismiss();

        Bundle args = new Bundle();
        MainFragment mainFragment = new MainFragment();
        mainFragment.setArguments(args);


        FragmentTransaction tx = activity.getFragmentManager().beginTransaction();
        tx.replace(R.id.listFragment, mainFragment, Constants.CONTAINER);

        tx.commit();
    }
}
