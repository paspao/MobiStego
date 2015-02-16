package it.mobistego;

import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;

import java.io.File;

import it.mobistego.fragments.ComposeFragment;
import it.mobistego.fragments.MainFragment;
import it.mobistego.utils.Constants;

/**
 * Created by paspao on 17/01/15.
 */
public class MainActivity extends FragmentActivity implements MainFragment.OnChoosenImage,ComposeFragment.OnComposed {

    private static final String TAG = MainActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_fragment);
        init();

    }

    private void init() {
        File f = new File(Environment.getExternalStorageDirectory(),
                Constants.EXT_DIR);
        if (!f.exists()) {
            f.mkdirs();
        }

    }

    @Override
    public void onBitmapSelected(Bitmap btm) {
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
    public void onMessageComposed(String message, Bitmap stegan) {
        MainFragment mainFragment = new MainFragment();
        Bundle args = new Bundle();

        mainFragment.setArguments(args);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack so the user can navigate back
        transaction.replace(R.id.main_container, mainFragment);
        //transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();
    }
}
