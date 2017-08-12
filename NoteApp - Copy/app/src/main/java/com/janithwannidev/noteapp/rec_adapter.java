package com.janithwannidev.noteapp;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by JanithWanni on 03/08/2017.
 */

public class rec_adapter extends RecyclerView.Adapter<rec_adapter.ViewHolder> {

    private String[] dataset;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView mTextView;
        public ViewHolder(View v) {
            super(v);
            mTextView = (TextView) v.findViewById(R.id.info_text);
        }
    }
    public rec_adapter(String[] Dataset){
        dataset = Dataset;
    }
    @Override
    public rec_adapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.text_view,parent,false);
        TextView tv = (TextView) v.findViewById(R.id.info_text);

        tv.setPadding(10,10,10,10);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(rec_adapter.ViewHolder holder, int position) {
        holder.mTextView.setText("This is the"+position);
    }

    @Override
    public int getItemCount() {
        return dataset.length;
    }
}
