package it.mobistego.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.InputStream;

import it.mobistego.R;
import it.mobistego.utils.Constants;

/**
 * Created by paspao on 28/01/15.
 */
public class MainFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = MainFragment.class.getName();

    private OnChoosenImage mCallback;
    private ImageButton buttonTakePhoto;
    private ImageButton buttonPickPhoto;
    private ImageButton buttonPickPhotoDecode;

    public interface OnChoosenImage {
        public void onBitmapSelected(Bitmap btm);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.main_layout, container, false);
        buttonPickPhoto = (ImageButton) view.findViewById(R.id.main_button_pick_photo);
        buttonTakePhoto = (ImageButton) view.findViewById(R.id.main_button_take_photo);
        buttonPickPhotoDecode = (ImageButton) view.findViewById(R.id.main_button_pick_photo_decode);
        buttonTakePhoto.setOnClickListener(this);
        buttonPickPhoto.setOnClickListener(this);
        buttonPickPhotoDecode.setOnClickListener(this);
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);


        try {
            mCallback = (OnChoosenImage) activity;

        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnChoosenImage");
        }
    }


    @Override
    public void onClick(View v) {
        if (v != null) {
            int id = v.getId();
            switch (id) {
                case R.id.main_button_pick_photo:
                    Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                    photoPickerIntent.setType("image/*");
                    startActivityForResult(photoPickerIntent, Constants.SELECT_PHOTO);
                    break;
                case R.id.main_button_pick_photo_decode:
                    Intent photoPickerIntentDecode = new Intent(Intent.ACTION_PICK);
                    photoPickerIntentDecode.setType("image/*");
                    startActivityForResult(photoPickerIntentDecode, Constants.SELECT_PHOTO_DECODE);
                    break;
                case R.id.main_button_take_photo:
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                        startActivityForResult(takePictureIntent, Constants.REQUEST_IMAGE_CAPTURE);
                    }
                    break;
                default:
                    Log.i(TAG, "Unknown action");
                    break;
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap imageBitmap = null;
        switch (requestCode) {
            case Constants.SELECT_PHOTO_DECODE:
                //TODO
                break;
            case Constants.SELECT_PHOTO:
                if (resultCode == Activity.RESULT_OK) {
                    try {
                        final Uri imageUri = data.getData();
                        final InputStream imageStream = getActivity().getContentResolver().openInputStream(imageUri);
                        imageBitmap = BitmapFactory.decodeStream(imageStream);
                        if (imageBitmap != null) {
                            mCallback.onBitmapSelected(imageBitmap);
                        }

                    } catch (FileNotFoundException e) {
                        Toast.makeText(getActivity(), R.string.filenotfoud, Toast.LENGTH_LONG).show();
                        Log.e(TAG, e.getMessage());
                        e.printStackTrace();
                    }
                }
                break;
            case Constants.REQUEST_IMAGE_CAPTURE:
                if (resultCode == Activity.RESULT_OK) {
                    Bundle extras = data.getExtras();
                    imageBitmap = (Bitmap) extras.get("data");
                    if (imageBitmap != null) {
                        mCallback.onBitmapSelected(imageBitmap);
                    }
                    //mImageView.setImageBitmap(imageBitmap);
                }
                break;
            default:
                Log.i(TAG, "Unknown result");
                break;
        }
    }
}
