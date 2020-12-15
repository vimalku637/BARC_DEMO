/*
 * Copyright (c)  2020. Indev Consultancy Private Limited,
 * Auther : Vimal Kumar
 * Date : 2020/12/15
 * Modified Date :
 * Modified By :
 */

package com.vrp.barc_demo.adapters;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.vrp.barc_demo.R;
import com.vrp.barc_demo.interfaces.ClickListener;
import com.vrp.barc_demo.models.SurveyModel;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SurveyListAdapter extends RecyclerView.Adapter<SurveyListAdapter.ViewHolder> {
    private Context context;
    private ArrayList<SurveyModel> arrayList;
    ClickListener clickListener;

    public SurveyListAdapter(Context context, ArrayList<SurveyModel> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.survey_list_adapter, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tv_survey_id.setText(arrayList.get(position).getSurvey_id());
        holder.tv_hh_name.setText("Household 1");

        //change status here
        if (arrayList.get(position).getStatus().equals("0")) {
            holder.btn_status.setText(context.getString(R.string.rejected));
            holder.btn_status.setBackgroundTintList(ColorStateList.valueOf(Color.LTGRAY));
        } else if (arrayList.get(position).getStatus().equals("1")){
            holder.btn_status.setText(context.getString(R.string.completed));
            holder.btn_status.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
        } else if (arrayList.get(position).getStatus().equals("2")) {
            holder.btn_status.setText(context.getString(R.string.halt));
            holder.btn_status.setBackgroundTintList(ColorStateList.valueOf(Color.YELLOW));
        } else if (arrayList.get(position).getStatus().equals("3")) {
            holder.btn_status.setText(context.getString(R.string.terminated));
            holder.btn_status.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
        }

        holder.tv_survey_id.setOnClickListener(new View.OnClickListener() {
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
        @BindView(R.id.tv_survey_id)
        MaterialTextView tv_survey_id;
        @BindView(R.id.tv_hh_name)
        MaterialTextView tv_hh_name;
        @BindView(R.id.btn_status)
        MaterialButton btn_status;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public void onItemClick(ClickListener listener) {
        this.clickListener=listener;
    }
}
