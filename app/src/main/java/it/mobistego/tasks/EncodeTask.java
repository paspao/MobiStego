package it.mobistego.tasks;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import java.util.List;

import it.mobistego.beans.MobiStegoItem;
import it.mobistego.business.LSB2bit;
import it.mobistego.utils.Utility;

/**
 *
 * Copyright (C) 2015  Pasquale Paola

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
public class EncodeTask  extends AsyncTask<MobiStegoItem,Integer,MobiStegoItem>{

    private static String TAG=EncodeTask.class.getName();
    @Override
    protected MobiStegoItem doInBackground(MobiStegoItem... params) {
        MobiStegoItem result=null;
        if(params.length>0) {
            MobiStegoItem mobistego=params[0];
            List<Bitmap> srcList = Utility.splitImage(mobistego.getBitmap());
            List<Bitmap> encodedList= LSB2bit.encodeMessage(srcList, mobistego.getMessage(), new LSB2bit.ProgressHandler() {
                @Override
                public void setTotal(int tot) {

                }

                @Override
                public void increment(int inc) {

                }

                @Override
                public void finished() {

                }
            });

            Bitmap srcEncoded=Utility.mergeImage(encodedList,mobistego.getBitmap().getHeight(),mobistego.getBitmap().getWidth());
            result=new MobiStegoItem(mobistego.getMessage(),srcEncoded,true);

        }
        return result;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }
    
    
}
