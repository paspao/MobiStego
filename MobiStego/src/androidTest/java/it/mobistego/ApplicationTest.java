package it.mobistego;


import org.junit.Before;
import org.junit.Test;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import junit.framework.Assert;

import java.io.IOException;

import java.util.List;

import it.mobistego.beans.MobiStegoItem;
import it.mobistego.business.LSB2bit;
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

@org.junit.runner.RunWith(AndroidJUnit4.class)
public class ApplicationTest {


    private static String TAG = ApplicationTest.class.getName();

    private int total;

    private Context context;

    @Before
    public void setUp() {
        // In case you need the context in your test
        context = InstrumentationRegistry.getTargetContext();
    }


    private Context getContext(){


        return context;
    }

    @Test
    public void testAllProcessLsb2Bit() {

        getContext().getResources().getDrawable(it.mobistego.test.R.drawable.test);
        String message = "Hello è World!!!";
        Bitmap src = BitmapFactory.decodeResource(getContext().getResources(),
                it.mobistego.test.R.drawable.test);

        List<Bitmap> srcList = Utility.splitImage(src);
        List<Bitmap> encodedList = LSB2bit.encodeMessage(srcList, message, new LSB2bit.ProgressHandler() {
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


        Bitmap srcEncoded = Utility.mergeImage(encodedList, src.getHeight(), src.getWidth());
        List<Bitmap> srcEncodedList = Utility.splitImage(srcEncoded);
        String result = LSB2bit.decodeMessage(srcEncodedList);
        Log.i(TAG, "Original message <" + message + "> Decripted message <" + result + ">");

        Assert.assertEquals(result, message);


    }

    @Test
    public void testLsb2Bit() {

        getContext().getResources().getDrawable(it.mobistego.test.R.drawable.test);
        String message = "Hello World!!!";
        Bitmap src = BitmapFactory.decodeResource(getContext().getResources(),
                it.mobistego.test.R.drawable.test);

        List<Bitmap> srcList = Utility.splitImage(src);
        List<Bitmap> encodedList = LSB2bit.encodeMessage(srcList, message, new LSB2bit.ProgressHandler() {
            @Override
            public void setTotal(int tot) {
                Log.d(TAG, "Total set to " + tot);
                total = tot;
            }

            @Override
            public void increment(int inc) {
                Log.d(TAG, "Progress " + inc + " on " + total);
            }

            @Override
            public void finished() {
                Log.d(TAG, "Encoding finised!..Now merging");
            }
        });

        String resultAsSoon = LSB2bit.decodeMessage(encodedList);
        Log.i(TAG, "Orignal message <" + message + "> Decripted message <" + resultAsSoon + ">");


        Assert.assertEquals(resultAsSoon, message);


    }

    @Test
    public void ignoretestSaving() {

        getContext().getResources().getDrawable(it.mobistego.test.R.drawable.test);
        String message = "Hello World!!!";
        Bitmap src = BitmapFactory.decodeResource(getContext().getResources(),
                it.mobistego.test.R.drawable.test);

        List<Bitmap> srcList = Utility.splitImage(src);
        List<Bitmap> encodedList = LSB2bit.encodeMessage(srcList, message, new LSB2bit.ProgressHandler() {
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


        Bitmap srcEncoded = Utility.mergeImage(encodedList, src.getHeight(), src.getWidth());
        List<Bitmap> srcEncodedList = Utility.splitImage(srcEncoded);
        String result = LSB2bit.decodeMessage(srcEncodedList);
        Log.i(TAG, "Original message <" + message + "> Decripted message <" + result + ">");
        Assert.assertEquals(result, message);
        MobiStegoItem m;
        try {
            m = Utility.saveMobiStegoItem(message, srcEncoded,getContext());
        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail();
        }

    }
    @Test
    public void testLoadingGallery() {
        try {
            List<MobiStegoItem> lista = Utility.listMobistegoItem(getContext());
            if (lista != null)
                for (MobiStegoItem item : lista) {
                    Log.d(TAG, "Name uuid " + item.getUuid());
                    Log.d(TAG, "Message " + item.getMessage());

                    List<Bitmap> srcEncodedList = Utility.splitImage(BitmapFactory.decodeFile(item.getBitmap().getAbsolutePath()));
                    String result = LSB2bit.decodeMessage(srcEncodedList);
                    Log.i(TAG, "Original message <" + item.getMessage() + "> Decripted message <" + result + ">");
                    Assert.assertEquals(result, item.getMessage());
                }
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }

    }
    @Test
    public void testKeySecret(){
        try {
            String encode=Utility.encrypt("CiaoPipppo","pippopippo");
            //String encoded64=Base64.encodeToString(encode.getBytes("UTF-8"),Base64.NO_CLOSE);
            Log.i(TAG,"Base64: " +encode);

            String decoded=Utility.decrypt(encode.getBytes("UTF-8"),"pippopippo");


            Log.i(TAG,decoded);

        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

}