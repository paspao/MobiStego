package it.mobistego.test;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.test.ApplicationTestCase;
import android.util.Log;

import java.util.List;

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

public class ApplicationTest extends ApplicationTestCase<Application> {


    private static String TAG=ApplicationTest.class.getName();

    public ApplicationTest() {
        super(Application.class);
    }

    public void testAllProcessLsb2Bit(){

        getContext().getResources().getDrawable(R.drawable.test);
        String message="Hello World!!!";
        Bitmap src=BitmapFactory.decodeResource(getContext().getResources(),
                R.drawable.test);

        List<Bitmap> srcList= Utility.splitImage(src);
        List<Bitmap> encodedList=LSB2bit.encodeMessage(srcList,message,new LSB2bit.ProgressHandler() {
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


        Bitmap srcEncoded=Utility.mergeImage(encodedList,src.getHeight(),src.getWidth());
        List<Bitmap> srcEncodedList= Utility.splitImage(srcEncoded);
        String result=LSB2bit.decodeMessage(srcEncodedList);
        Log.i(TAG,"Original message <"+message+"> Decripted message <"+result+">");

        assertEquals(result,message);



    }



    public void testLsb2Bit(){

        getContext().getResources().getDrawable(R.drawable.test);
        String message="Hello World!!!";
        Bitmap src=BitmapFactory.decodeResource(getContext().getResources(),
                it.mobistego.test.R.drawable.test);

        List<Bitmap> srcList= Utility.splitImage(src);
        List<Bitmap> encodedList=LSB2bit.encodeMessage(srcList,message,new LSB2bit.ProgressHandler() {
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

        String resultAsSoon=LSB2bit.decodeMessage(encodedList);
        Log.i(TAG,"Orignal message <"+message+"> Decripted message <"+resultAsSoon+">");


        assertEquals(resultAsSoon,message);



    }
}