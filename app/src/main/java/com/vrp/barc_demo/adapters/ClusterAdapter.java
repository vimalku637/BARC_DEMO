package com.vrp.barc_demo.adapters;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.vrp.barc_demo.R;
import com.vrp.barc_demo.interfaces.ClickListener;
import com.vrp.barc_demo.models.ClusterModel;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ClusterAdapter extends RecyclerView.Adapter<ClusterAdapter.ViewHolder> {
    private Context context;
    private ArrayList<ClusterModel> arrayList;
    ClickListener clickListener;

    public ClusterAdapter(Context context, ArrayList<ClusterModel> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.activity_cluster_adapter, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tv_cluster_id.setText(arrayList.get(position).getCluster_id());
        holder.tv_cluster_name.setText(arrayList.get(position).getCluster_name());
        //lock_status (0) means open(O) and if lock_status (1) means locked(L)
        if (arrayList.get(position).getAction().equals("0")) {
            holder.btn_action.setText("O");
        } else {
            holder.btn_action.setText("L");
        }

        holder.btn_action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.onItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_cluster_id)
        MaterialTextView tv_cluster_id;
        @BindView(R.id.tv_cluster_name)
        MaterialTextView tv_cluster_name;
        @BindView(R.id.btn_action)
        MaterialButton btn_action;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
    public void onItemClick(ClickListener listener) {
        this.clickListener=listener;
    }
}