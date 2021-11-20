package com.vrp.barc_demo.adapters;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textview.MaterialTextView;
import com.vrp.barc_demo.R;
import com.vrp.barc_demo.interfaces.ClickListener;
import com.vrp.barc_demo.models.ClusterModel;
import com.vrp.barc_demo.models.SurveyModel;
import com.vrp.barc_demo.models.SyncAudioModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AudioAdapter extends RecyclerView.Adapter<AudioAdapter.ViewHolder> {
    private Context context;
    private ArrayList<SurveyModel> arrayList;
    ClickListener clickListener;
    boolean isSelectedAll;

    HashMap<Integer,String> valuesId =new HashMap<>();
    public HashMap<Integer,String> getCheckedValues(){
        return valuesId;
    }

    HashMap<Integer,ArrayList<String>> listHashMap = new HashMap<Integer,ArrayList<String>>();

    public HashMap<Integer, ArrayList<String>> getSelectedValues() {
        return listHashMap;
    }

    public AudioAdapter(Context context, ArrayList<SurveyModel> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.activity_audio_adapter, parent, false);
        return new ViewHolder(view);
    }

    public void selectAll(){
        isSelectedAll=true;
        notifyDataSetChanged();
    }
    public void unSelectAll(){
        isSelectedAll=false;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull AudioAdapter.ViewHolder holder, int position) {
        holder.tv_survey_id.setText(arrayList.get(position).getSurvey_id());
        holder.tv_audio_name.setText(arrayList.get(position).getAudio_recording());

        if (!isSelectedAll)
            holder.check_box.setChecked(false);
        else
            holder.check_box.setChecked(true);

        holder.check_box.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //valuesId.put(Integer.valueOf(arrayList.get(position).getId()), "checked");
                    ArrayList<String> list = new ArrayList<String>();
                    list.add(arrayList.get(position).getId());
                    list.add(arrayList.get(position).getSurvey_id());
                    list.add(arrayList.get(position).getAudio_recording());

                    listHashMap.put(Integer.valueOf(arrayList.get(position).getId()), list);
                } else {
                    //valuesId.remove(arrayList.get(position).getId());
                }
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
        @BindView(R.id.tv_audio_name)
        MaterialTextView tv_audio_name;
        @BindView(R.id.check_box)
        MaterialCheckBox check_box;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}