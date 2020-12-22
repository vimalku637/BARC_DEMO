package com.vrp.barc_demo.utils;

import com.vrp.barc_demo.models.AnswerModel;

import java.util.ArrayList;

public interface ActivityCommunicator {
    public void passDataToActivity(ArrayList<ArrayList<AnswerModel>> answerModelListTotal,ArrayList<AnswerModel> answerModelList,int type);
}
