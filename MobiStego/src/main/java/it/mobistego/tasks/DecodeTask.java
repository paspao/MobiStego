package it.mobistego.tasks;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.List;

import it.mobistego.R;
import it.mobistego.beans.MobiStegoItem;
import it.mobistego.business.LSB2bit;
import it.mobistego.fragments.AlertNotMobistegoFragment;
import it.mobistego.fragments.MainFragment;
import it.mobistego.utils.Utility;

/**
 * Created by paspao on 15/02/15.
 * 
 * <p/>
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
public class DecodeTask extends AsyncTask<File, Void, MobiStegoItem> {
   
    private final static String TAG=DecodeTask.class.getName();
    private Activity activity;
    private ProgressDialog progressDialog;
    private boolean isMobistegoImage;
    
    public DecodeTask(Activity m){
        super();
        this.activity=m;
        isMobistegoImage=false;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = new ProgressDialog(activity); //Here I get an error: The constructor ProgressDialog(PFragment) is undefined
        progressDialog.setMessage(activity.getString(R.string.loading));
        progressDialog.setTitle(activity.getString(R.string.decoding));
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        
    }
    
    @Override
    protected MobiStegoItem doInBackground(File... params) {
        MobiStegoItem result=null;
        publishProgress();
        Bitmap bitmap = BitmapFactory.decodeFile(params[0].getAbsolutePath());
        if (bitmap == null)
            return result;
        List<Bitmap> srcEncodedList = Utility.splitImage(bitmap);
        String decoded = LSB2bit.decodeMessage(srcEncodedList);
        for(Bitmap bitm:srcEncodedList)
            bitm.recycle();
        if(!Utility.isEmpty(decoded)) {
            try {
                isMobistegoImage=true;
                result = Utility.saveMobiStegoItem(decoded, bitmap,activity);
            } catch (IOException e) {
                Log.e(TAG, "Error", e);
                //e.printStackTrace();
            }
        }
        
        return result;
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
        progressDialog.show();
    }

    @Override
    protected void onPostExecute(MobiStegoItem mobiStegoItem) {
        super.onPostExecute(mobiStegoItem);
        progressDialog.dismiss();
        if(!isMobistegoImage)
        {

            AlertNotMobistegoFragment compose = new AlertNotMobistegoFragment();
            Bundle args = new Bundle();

            compose.setArguments(args);
            FragmentTransaction transaction = activity.getFragmentManager().beginTransaction();

            transaction.addToBackStack(null);
            compose.show(transaction, "dialog");
        } else {
            ((MainFragment.OnMainFragment) activity).onMainFragmentGridItemSelected(mobiStegoItem);
        }
    }
}
