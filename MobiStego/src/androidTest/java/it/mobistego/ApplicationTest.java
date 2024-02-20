package it.mobistego;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

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

@RunWith(AndroidJUnit4.class)
public class ApplicationTest {

    private static final String TAG = ApplicationTest.class.getName();

    private int total;

    @Before
    public void setUp() {
    }

    private Context getContext() {
        return InstrumentationRegistry.getInstrumentation().getContext();
    }

    private Context getInstrumentationContext() {
        return InstrumentationRegistry.getInstrumentation().getTargetContext();
    }

    @Test
    public void testAllProcessLsb2Bit() {
        //getContext().getResources().getDrawable(it.mobistego.test.R.drawable.test);
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
        Log.i(TAG, "Original message <" + message + "> Decrypted message <" + result + ">");

        Assert.assertEquals(result, message);
    }

    @Test
    public void testLsb2Bit() {
        //getContext().getResources().getDrawable(it.mobistego.test.R.drawable.test);
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
                Log.d(TAG, "Encoding finished!..Now merging");
            }
        });

        String resultAsSoon = LSB2bit.decodeMessage(encodedList);
        Log.i(TAG, "Original message <" + message + "> Decrypted message <" + resultAsSoon + ">");


        Assert.assertEquals(resultAsSoon, message);


    }

    @Test
    public void ignoreTestSaving() {
        //getContext().getResources().getDrawable(it.mobistego.test.R.drawable.test);
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
        Log.i(TAG, "Original message <" + message + "> Decrypted message <" + result + ">");
        Assert.assertEquals(result, message);
        MobiStegoItem m;
        try {
            m = Utility.saveMobiStegoItem(message, srcEncoded, getInstrumentationContext());
        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail();
        }

    }

    @Test
    public void testLoadingGallery() {
        try {
            List<MobiStegoItem> list = Utility.listMobistegoItem(getInstrumentationContext());
            if (list != null)
                for (MobiStegoItem item : list) {
                    Log.d(TAG, "Name uuid " + item.getUuid());
                    Log.d(TAG, "Message " + item.getMessage());

                    List<Bitmap> srcEncodedList = Utility.splitImage(BitmapFactory.decodeFile(item.getBitmap().getAbsolutePath()));
                    String result = LSB2bit.decodeMessage(srcEncodedList);
                    Log.i(TAG, "Original message <" + item.getMessage() + "> Decrypted message <" + result + ">");
                    Assert.assertEquals(result, item.getMessage());
                }
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }

    }

    @Test
    public void testKeySecret() {
        try {
            String encode = Utility.encrypt("CiaoPipppo", "pippopippo");
            //String encoded64=Base64.encodeToString(encode.getBytes("UTF-8"),Base64.NO_CLOSE);
            Log.i(TAG, "Base64: " + encode);

            String decoded = Utility.decrypt(encode.getBytes("UTF-8"), "pippopippo");


            Log.i(TAG, decoded);

        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

}