package it.mobistego.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

import com.afollestad.materialdialogs.MaterialDialog;

import java.io.File;
import java.io.IOException;
import java.util.List;

import it.mobistego.R;
import it.mobistego.adapters.ListAdapter;
import it.mobistego.beans.MobiStegoItem;
import it.mobistego.utils.Constants;
import it.mobistego.utils.Utility;

/**
 * Created by paspao on 28/01/15.
 * <p/>
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
public class MainFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener {

    private static final String TAG = MainFragment.class.getName();

    private OnMainFragment mCallback;
    private ImageButton buttonTakePhoto;
    private Button buttonAdd;
    private ImageButton buttonPickPhoto;
    private ImageButton buttonPickPhotoDecode;
    private ListView listView;
    private List<MobiStegoItem> mobiStegoItems;
    private File filePhotoTaken;
    private ListAdapter listAdapter;
    private View createdView;


    public interface OnMainFragment {
        public void onMainFragmentBitmapSelectedToEncode(File btm);

        public void onMainFragmentBitmapSelectedToDecode(File btm);

        public void onMainFragmentGridItemSelected(MobiStegoItem mobiStegoItem);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        List<MobiStegoItem> mobiTmp = null;

        mobiTmp = Utility.listMobistegoItem();

        if (createdView == null) {
            createdView = inflater.inflate(R.layout.main_layout, container, false);
            listView = (ListView) createdView.findViewById(R.id.list_view);
            buttonAdd = (Button) createdView.findViewById(R.id.button_add);
            /*buttonPickPhoto = (ImageButton) createdView.findViewById(R.id.main_button_pick_photo);
            buttonTakePhoto = (ImageButton) createdView.findViewById(R.id.main_button_take_photo);
            buttonPickPhotoDecode = (ImageButton) createdView.findViewById(R.id.main_button_pick_photo_decode);
            buttonTakePhoto.setOnClickListener(this);
            buttonPickPhoto.setOnClickListener(this);
            buttonPickPhotoDecode.setOnClickListener(this);
*/
            buttonAdd.setOnClickListener(this);

            boolean changed = false;
            if (mobiStegoItems == null || mobiStegoItems.size() != mobiTmp.size()) {
                mobiStegoItems = mobiTmp;
                changed = true;
            }

            if (listAdapter == null) {
                listAdapter = new ListAdapter(getActivity(), mobiStegoItems);
                listView.setAdapter(listAdapter);
            } else if (changed) {

            }


            listView.setOnItemClickListener(this);
        } else if ((mobiStegoItems != null && mobiTmp != null && mobiStegoItems.size() != mobiTmp.size())) {
            listAdapter.setItems(mobiStegoItems);
            listAdapter.notifyDataSetChanged();
        }
        return createdView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);


        try {
            mCallback = (OnMainFragment) activity;

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
                case R.id.button_add:
                    new MaterialDialog.Builder(getActivity())
                            .title(R.string.choose)
                            .items(R.array.operations)
                            .itemsCallback(new MaterialDialog.ListCallback() {
                                @Override
                                public void onSelection(MaterialDialog materialDialog, View view, int i, CharSequence charSequence) {

                                    switch (i) {
                                        case 0:
                                            try {
                                                filePhotoTaken = Utility.createImageFile();
                                                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                                                        Uri.fromFile(filePhotoTaken));
                                                if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                                                    startActivityForResult(takePictureIntent, Constants.REQUEST_IMAGE_CAPTURE);
                                                }
                                            } catch (IOException e) {
                                                Log.e(TAG, e.getMessage());
                                                //e.printStackTrace();
                                            }
                                            break;
                                        case 1:

                                            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                                            photoPickerIntent.setType("image/*");
                                            startActivityForResult(photoPickerIntent, Constants.SELECT_PHOTO);
                                            break;
                                        case 2:
                                            Intent photoPickerIntentDecode = new Intent(Intent.ACTION_PICK);
                                            photoPickerIntentDecode.setType("image/*");
                                            startActivityForResult(photoPickerIntentDecode, Constants.SELECT_PHOTO_DECODE);
                                            break;


                                        default:
                                            Log.i(TAG, "Unknown action");
                                            break;
                                    }
                                }
                            })

                            .show();

                default:
                    Log.i(TAG, "Unknown action");
                    break;
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d(TAG, "Clicked position " + position);
        mCallback.onMainFragmentGridItemSelected(mobiStegoItems.get(position));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap imageBitmap = null;
        switch (requestCode) {
            case Constants.SELECT_PHOTO_DECODE:
                if (resultCode == Activity.RESULT_OK) {

                        final Uri imageUri = data.getData();
                    //final InputStream imageStream = getActivity().getContentResolver().openInputStream(imageUri);
                    //imageBitmap = BitmapFactory.decodeStream(imageStream);
                        if (imageBitmap != null) {
                            mCallback.onMainFragmentBitmapSelectedToDecode(new File(imageUri.getPath()));
                        }


                }
                break;
            case Constants.SELECT_PHOTO:
                if (resultCode == Activity.RESULT_OK) {

                    final Uri imageUri = data.getData();
                    //final InputStream imageStream = getActivity().getContentResolver().openInputStream(imageUri);
                    //imageBitmap = BitmapFactory.decodeStream(imageStream);
                    String path = Utility.getRealPathFromURI(imageUri, getActivity().getContentResolver());
                    mCallback.onMainFragmentBitmapSelectedToEncode(new File(path));


                }
                break;
            case Constants.REQUEST_IMAGE_CAPTURE:
                if (resultCode == Activity.RESULT_OK) {
                    if (filePhotoTaken != null) {
                        //imageBitmap=BitmapFactory.decodeFile(filePhotoTaken.getAbsolutePath());


                        mCallback.onMainFragmentBitmapSelectedToEncode(filePhotoTaken);


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
