package it.mobistego;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import it.mobistego.utils.Utility;


public class SplitActivity extends ActionBarActivity  implements View.OnClickListener{


    private static final String TAG=SplitActivity.class.getName();
    public static final int SQUARE_BLOCK=512;
    Button split;
    ImageView imageV;
    ImageView imageM;
    Handler handler;
    Bitmap merged;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_split);
        split=(Button)findViewById(R.id.split_button);
        split.setOnClickListener(this);
        imageV=(ImageView)findViewById(R.id.image_v);
        imageM=(ImageView)findViewById(R.id.image_m);
        handler=new Handler();
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_split, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    @Override
    public void onClick(View v) {
        BitmapDrawable drawable = (BitmapDrawable) imageV.getDrawable();
        Bitmap bitmap = drawable.getBitmap();
        List<Bitmap> result= Utility.splitImage(bitmap);

        //Log.d("TEST!!!",""+result.size());
        //BitmapDrawable drawable = (BitmapDrawable) imageV.getDrawable();
        //Bitmap bitmap = drawable.getBitmap();
        if(result!=null)
        {
            int index=1;
            for(Bitmap b:result){
                Log.d(TAG," "+index++);
                Log.d(TAG,"Height "+b.getHeight());
                Log.d(TAG,"Width "+b.getWidth());
            }
            Log.d(TAG,"Altezza orig "+bitmap.getHeight()+" Larghezza orig "+bitmap.getWidth());
            Bitmap merg= Utility.mergeImage(result,bitmap.getHeight(),bitmap.getWidth());
            Log.d(TAG,"Altezza merged "+bitmap.getHeight()+" Larghezza merged "+bitmap.getWidth());
        }


    }
}
