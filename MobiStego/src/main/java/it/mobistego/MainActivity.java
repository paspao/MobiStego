package it.mobistego;

import android.Manifest;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.LabeledIntent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import it.mobistego.beans.MobiStegoItem;
import it.mobistego.fragments.ComposeFragment;
import it.mobistego.fragments.DeleteDialogFragment;
import it.mobistego.fragments.ItemViewFragment;
import it.mobistego.fragments.MainFragment;
import it.mobistego.tasks.DecodePasswordTask;
import it.mobistego.tasks.DecodeTask;
import it.mobistego.tasks.EncodeTask;
import it.mobistego.utils.Constants;
import it.mobistego.utils.GPU;
import it.mobistego.utils.Utility;

/**
 * Created by paspao on 17/01/15.
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA*
 */
public class MainActivity extends FragmentActivity implements MainFragment.OnMainFragment, ComposeFragment.OnComposed, ItemViewFragment.OnItemView,DeleteDialogFragment.OnItemDeleted {

    private static final String TAG = MainActivity.class.getName();
    public volatile static int TEXTURE_SIZE_GL10 = 0;
    public volatile static int TEXTURE_SIZE_GL20 = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_fragment);
        init();
        FragmentManager ft = getFragmentManager();
        FragmentTransaction tx = ft.beginTransaction();
        MainFragment mainF = new MainFragment();

        tx.replace(R.id.listFragment, mainF, Constants.CONTAINER);

        tx.commit();


    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() == 0)
            super.onBackPressed();
        else
            getFragmentManager().popBackStack();
    }

    private void init() {

        if (TEXTURE_SIZE_GL20 == -1 && TEXTURE_SIZE_GL10 == -1) {
            GPU gpu = new GPU(this);
            gpu.loadOpenGLGles20Info(new GPU.OnCompleteCallback<GPU.OpenGLGles20Info>() {
                @Override
                public void onComplete(GPU.OpenGLGles20Info result) {
                    TEXTURE_SIZE_GL20 = result.GL_MAX_TEXTURE_SIZE;
                }
            });
            gpu.loadOpenGLGles10Info(new GPU.OnCompleteCallback<GPU.OpenGLGles10Info>() {
                @Override
                public void onComplete(GPU.OpenGLGles10Info result) {
                    TEXTURE_SIZE_GL10 = result.GL_MAX_TEXTURE_SIZE;
                }
            });

        }


    }



    @Override
    public void onMainFragmentBitmapSelectedToEncode(File btm) {
        if (btm != null) {
            ComposeFragment compose = new ComposeFragment();
            Bundle args = new Bundle();
            compose.setChoosenBitmap(btm);
            compose.setArguments(args);
            FragmentTransaction transaction = getFragmentManager().beginTransaction();

            transaction.addToBackStack(null);
            compose.show(transaction, "dialog");

        }
    }

    @Override
    public void onMainFragmentBitmapSelectedToDecode(File btm) {
        if (btm != null) {
            DecodeTask task = new DecodeTask(this);
            task.execute(btm);
            
            //TODO update grid list
        }
    }

    @Override
    public void onMainFragmentGridItemSelected(MobiStegoItem mobiStegoItem) {
        if (mobiStegoItem != null) {
            ItemViewFragment viewFragment = new ItemViewFragment();
            Bundle args = new Bundle();
            viewFragment.setArguments(args);
            viewFragment.setMobiStegoItem(mobiStegoItem);
            FragmentTransaction transaction = getFragmentManager().beginTransaction();

            Fragment mainFrag = getFragmentManager().findFragmentByTag(Constants.CONTAINER);

            transaction.replace(mainFrag.getId(), viewFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }

    @Override
    public void onMessageComposed(String message, String password,File stegan) {


        EncodeTask task = new EncodeTask(this);
        MobiStegoItem item = new MobiStegoItem(message, stegan, Constants.NO_NAME, false,password);
        task.execute(item);

    }

    @Override
    public void itemViewOnDelete(MobiStegoItem mobiStegoItem) {

        DeleteDialogFragment deleteDialogFragment = new DeleteDialogFragment();
        Bundle args = new Bundle();
        deleteDialogFragment.setMobiStegoItem(mobiStegoItem);
        deleteDialogFragment.setArguments(args);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();


        transaction.addToBackStack(null);
        deleteDialogFragment.show(transaction, "dialogdelete");


    }

    @Override
    public void onDelete() {
        FragmentManager ft = getFragmentManager();
        FragmentTransaction tx = ft.beginTransaction();
        MainFragment mainF = new MainFragment();

        tx.replace(R.id.listFragment, mainF, Constants.CONTAINER);

        tx.commit();
    }

    @Override
    public void itemViewOnShare(MobiStegoItem mobiStegoItem) {
        PackageManager pm = getPackageManager();
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        //emailIntent.setType("vnd.android.cursor.item/*");
        emailIntent.setType("vnd.android.cursor.item/*");
        Intent openInChooser = Intent.createChooser(new Intent(), getResources().getString(R.string.send));

        File f = mobiStegoItem.getBitmap();
        if (f != null) {


            List<ResolveInfo> resInfo = pm.queryIntentActivities(emailIntent, 0);
            List<LabeledIntent> intentList = new ArrayList<>();
            for (int i = 0; i < resInfo.size(); i++) {
                // Extract the label, append it, and repackage it in a LabeledIntent
                ResolveInfo ri = resInfo.get(i);
                String packageName = ri.activityInfo.packageName;
                if(packageName.contains("whatsapp")||packageName.contains("telegram"))
                    continue;

                Intent intent = new Intent();
                intent.setComponent(new ComponentName(packageName, ri.activityInfo.name));
                intent.setAction(Intent.ACTION_SEND);
                intent.setType("vnd.android.cursor.item/*");
                intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(f));

                intentList.add(new LabeledIntent(intent, packageName, ri.loadLabel(pm), ri.icon));

            }

            // convert intentList to array
            LabeledIntent[] extraIntents = intentList.toArray( new LabeledIntent[ intentList.size() ]);

            openInChooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, extraIntents);
            startActivity(openInChooser);



            //startActivity(Intent.createChooser(emailIntent, getResources().getString(R.string.send)));
        }
    }

    @Override
    public void itemViewOnDecrypt(String message, String password, TextView view) {

        DecodePasswordTask dd=new DecodePasswordTask(this,view);
        dd.execute(message,password);
    }

}
