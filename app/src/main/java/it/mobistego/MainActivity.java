package it.mobistego;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.view.ViewGroup;

import java.io.File;

import it.mobistego.beans.MobiStegoItem;
import it.mobistego.fragments.ComposeFragment;
import it.mobistego.fragments.ItemViewFragment;
import it.mobistego.fragments.MainFragment;
import it.mobistego.tasks.EncodeTask;
import it.mobistego.utils.Constants;
import it.mobistego.utils.Utility;

/**
 * Created by paspao on 17/01/15.
 */
public class MainActivity extends FragmentActivity implements MainFragment.OnMainFragment, ComposeFragment.OnComposed, ItemViewFragment.OnItemView {

    private static final String TAG = MainActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_fragment);
        init();

    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() == 0)
            super.onBackPressed();
        else
            getFragmentManager().popBackStack();
    }

    private void init() {
        File f = new File(Environment.getExternalStorageDirectory(),
                Constants.EXT_DIR);
        if (!f.exists()) {
            f.mkdirs();
        }

    }

    @Override
    public void onMainFragmentBitmapSelected(Bitmap btm) {
        if (btm != null) {
            ComposeFragment compose = new ComposeFragment();
            Bundle args = new Bundle();
            compose.setChoosenBitmap(btm);
            compose.setArguments(args);
            FragmentTransaction transaction = getFragmentManager().beginTransaction();

            // Replace whatever is in the fragment_container view with this fragment,
            // and add the transaction to the back stack so the user can navigate back


            //transaction.replace(R.id.main_container, compose);
            transaction.addToBackStack(null);
            compose.show(transaction, "dialog");
            // Commit the transaction
            //transaction.commit();
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

            // Replace whatever is in the fragment_container view with this fragment,
            // and add the transaction to the back stack so the user can navigate back
            Fragment mainFrag = getFragmentManager().findFragmentByTag("MYCONTAINER");

            transaction.replace(((ViewGroup) mainFrag.getView().getParent()).getId(), viewFragment);
            //transaction.replace(R.id.main_container, compose);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }

    @Override
    public void onMessageComposed(String message, Bitmap stegan) {
        MainFragment mainFragment = new MainFragment();
        Bundle args = new Bundle();
        EncodeTask task = new EncodeTask(this);
        MobiStegoItem item = new MobiStegoItem(message, stegan, Constants.NO_NAME, false);
        task.execute(item);
        mainFragment.setArguments(args);

        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack so the user can navigate back
        transaction.replace(R.id.main_container, mainFragment);
        //transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();
    }

    @Override
    public void itemViewOnDelete(MobiStegoItem mobiStegoItem) {
        Utility.deleteMobiStegoItem(mobiStegoItem);
    }

    @Override
    public void itemViewOnShare(MobiStegoItem mobiStegoItem) {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        // set the type to 'email'
        emailIntent.setType("vnd.android.cursor.dir/email");
        //String to[] = {"asd@gmail.com"};
        //emailIntent .putExtra(Intent.EXTRA_EMAIL, to);
        // the attachment
        File f = Utility.getBitmapFile(mobiStegoItem);
        if (f != null) {

            emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(f));
            // the mail subject
            //emailIntent .putExtra(Intent.EXTRA_SUBJECT, "Subject");

            //mobiStegoItem.getUuid()   Uri
            startActivity(Intent.createChooser(emailIntent, "Send email..."));
        }
    }
}
