package it.mobistego.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import it.mobistego.R;

/**
 * Created by paspao on 28/01/15.
 */
public class ComposeFragment extends DialogFragment implements View.OnClickListener {

    private static final String TAG = ComposeFragment.class.getName();
    private OnComposed mCallback;
    private ImageView imageView;
    private EditText editMessage;
    private Bitmap choosenBitmap;
    private Button buttonEncode;


    public interface OnComposed {

        public void onMessageComposed(String message, Bitmap stegan);

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog d = super.onCreateDialog(savedInstanceState);
        d.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        d.getWindow().setFormat(PixelFormat.RGBA_8888);

        return d;
    }

    public void setChoosenBitmap(Bitmap choosenBitmap) {
        this.choosenBitmap = choosenBitmap;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.compose_layout, container, false);
        imageView = (ImageView) view.findViewById(R.id.compose_background);
        editMessage = (EditText) view.findViewById(R.id.compose_edit);
        buttonEncode = (Button) view.findViewById(R.id.compose_button_encode);

        buttonEncode.setOnClickListener(this);
        //choosenBitmap=(Bitmap)getIntent().getExtras().get(Constants.CHOOSEN_IMAGE);
        Bitmap tmp = Bitmap.createBitmap(choosenBitmap);
        //imageView.setImageBitmap(doGreyscale(tmp));
        imageView.setImageBitmap(tmp);
        return view;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);


        try {
            mCallback = (OnComposed) activity;

        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnComposed");
        }
    }

    @Override
    public void onClick(View v) {
        if (v != null) {
            int id = v.getId();
            switch (id) {
                case R.id.compose_button_encode:
                    String message = editMessage.getText().toString();
                    mCallback.onMessageComposed(message, choosenBitmap);
                    break;
                default:
                    Log.d(TAG, "Unknown action");
                    break;
            }
        }
    }


    public static Bitmap doGreyscale(Bitmap src) {
        // constant factors
        final double GS_RED = 0.299;
        final double GS_GREEN = 0.587;
        final double GS_BLUE = 0.114;

        // create output bitmap
        Bitmap bmOut = Bitmap.createBitmap(src.getWidth(), src.getHeight(), src.getConfig());
        // pixel information
        int A, R, G, B;
        int pixel;

        // get image size
        int width = src.getWidth();
        int height = src.getHeight();

        // scan through every single pixel
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                // get one pixel color
                pixel = src.getPixel(x, y);
                // retrieve color of all channels
                A = Color.alpha(pixel);
                R = Color.red(pixel);
                G = Color.green(pixel);
                B = Color.blue(pixel);
                // take conversion up to one single value
                R = G = B = (int) (GS_RED * R + GS_GREEN * G + GS_BLUE * B);
                // set new pixel color to output bitmap
                bmOut.setPixel(x, y, Color.argb(A, R, G, B));
            }
        }


        // return final image
        return bmOut;
    }

}
