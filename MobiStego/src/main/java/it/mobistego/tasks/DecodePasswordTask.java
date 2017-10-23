package it.mobistego.tasks;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import it.mobistego.R;
import it.mobistego.utils.Utility;

/**
 * Created by paspao on 19/07/17.
 */

public class DecodePasswordTask extends AsyncTask<String, Void, String> {

    private static final String TAG = DecodePasswordTask.class.getName();

    private TextView view;
    private Activity activity;

    public DecodePasswordTask(Activity activity, TextView view) {
        this.view = view;
        this.activity = activity;
    }

    @Override
    protected String doInBackground(String... message) {
        String result = null;
        try {
            result = Utility.decrypt(message[0].getBytes("UTF-8"), message[1]);
        } catch (Exception e) {

            Log.e(TAG, e.getMessage(), e);
        }

        return result;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if (!Utility.isEmpty(result))
            view.setText(result);
        else
            Toast.makeText(activity, R.string.wrong_password, Toast.LENGTH_LONG).show();
    }
}