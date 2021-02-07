package com.hansen.stlviewer.simplestlviewer.customSpinner;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hansen.stlviewer.simplestlviewer.R;

import java.util.ArrayList;

public class storeSpinnerAdapter extends ArrayAdapter<String> {

    //String[] spinnerNames;
    //int[] spinnerImages;
    ArrayList<String> spinnerNames;
    ArrayList<Integer> spinnerImages;
    Context mContext;

    public storeSpinnerAdapter(@NonNull Context context, ArrayList<String> names, ArrayList<Integer> images){
        super(context, R.layout.store_spinner_row);

        this.spinnerNames = names;
        this.spinnerImages = images;
        this.mContext = context;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getView(position, convertView, parent);
    }

    @Override
    public int getCount() {
        return spinnerNames.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        ViewHolder mViewHolder = new ViewHolder();

        if (convertView == null) {

            LayoutInflater mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.store_spinner_row, parent, false);

            mViewHolder.mImage = (ImageView) convertView.findViewById(R.id.imageview_spinner_image);
            mViewHolder.mName = (TextView) convertView.findViewById(R.id.textview_spinner_name);
            convertView.setTag(mViewHolder);

        } else {

            mViewHolder = (ViewHolder) convertView.getTag();
        }

        mViewHolder.mImage.setImageResource(spinnerImages.get(position));
        mViewHolder.mName.setText(spinnerNames.get(position));

        return convertView;
    }

    private static class ViewHolder {

        ImageView mImage;
        TextView mName;
    }
}
