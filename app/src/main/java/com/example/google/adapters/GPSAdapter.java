package com.example.google.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.google.R;

import java.util.List;

public class GPSAdapter extends RecyclerView.Adapter<GPSAdapter.ViewHolder> {
    private List<String> gpsParams;
    private int layout;

    public GPSAdapter(List<String> gpsParams, int layout) {
        this.gpsParams = gpsParams;
        this.layout = layout;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(layout,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }
    @Override
    public int getItemCount() {
        int size = 0 ;
        try {
            size =gpsParams.size();
        } catch (Exception e) {
            size = 0;
        }
        return size;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tv_gps_params.setText(gpsParams.get(position));

    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView tv_gps_params;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_gps_params = itemView.findViewById(R.id.tv_gps_params);



        }
    }


}
