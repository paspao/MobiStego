package it.mobistego.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.util.List;

import it.mobistego.R;
import it.mobistego.beans.MobiStegoItem;
import it.mobistego.fragments.ItemViewFragment;
import it.mobistego.tasks.BitmapWorkerTask;
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


public class ListAdapter extends BaseAdapter {

    private final static String TAG = ListAdapter.class.getName();
    private List<MobiStegoItem> items;
    private Context context;
    private ItemViewFragment.OnItemView mCallback;

    public interface OnGridEvent {
        public void gridAdapterOnClick();

    }

    public ListAdapter(Context context, List<MobiStegoItem> mobileValues) {
        this.context = context;
        this.mCallback = (ItemViewFragment.OnItemView) context;
        this.items = mobileValues;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View gridView;

        if (convertView == null) {




            gridView = inflater.inflate(R.layout.grid_item, null);

            ImageView image = (ImageView) gridView.findViewById(R.id.grid_image);
            View layoutButton = gridView.findViewById(R.id.grid_item_buttons);
            ImageButton buttonShare = (ImageButton) gridView.findViewById(R.id.button_grid_share);
            ImageButton buttonDelete = (ImageButton) gridView.findViewById(R.id.button_grid_delete);
            buttonDelete.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (items != null) {
                        MobiStegoItem item = items.get(position);
                        if (item != null) {
                            mCallback.itemViewOnDelete(item);
                        }

                    }


                }
            });

            buttonShare.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (items != null) {
                        MobiStegoItem item = items.get(position);
                        if (item != null) {
                            mCallback.itemViewOnShare(item);
                        }

                    }


                }
            });
            layoutButton.setVisibility(View.VISIBLE);
            ProgressBar progressBar = (ProgressBar) gridView.findViewById(R.id.progrss_grid);
            if (items != null) {
                MobiStegoItem item = items.get(position);
                if (item != null) {
                    BitmapWorkerTask bitmW = new BitmapWorkerTask(image, progressBar);
                    bitmW.execute(item.getBitmapCompressed());
                }

            }
        } else {
            gridView = (View) convertView;
        }

        return gridView;
    }

    @Override
    public int getCount() {
        if(!Utility.isEmpty(items))
            return items.size();
        else 
            return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public void setItems(List<MobiStegoItem> items) {
        this.items = items;
    }
}
