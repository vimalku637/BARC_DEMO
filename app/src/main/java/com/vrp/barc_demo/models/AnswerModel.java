package com.vrp.barc_demo.models;

import java.io.Serializable;

public class AnswerModel implements Serializable {
    private String option_value;
    private String option_id;
    private String survey_id;
    private String question_id;
    private String question_name;
    private String question_type;

    public String getOption_value() {
        return option_value;
    }

    public void setOption_value(String option_value) {
        this.option_value = option_value;
    }

    public String getOption_id() {
        return option_id;
    }

    public void setOption_id(String option_id) {
        this.option_id = option_id;
    }

    public String getSurveyID() {
        return survey_id;
    }

    public void setSurveyID(String survey_id) {
        this.survey_id = survey_id;
    }
    public String getQuestionID() {
        return question_id;
    }

    public void setQuestionID(String question_id) {
        this.question_id = question_id;
    }

    public String getQuestion_name() {
        return question_name;
    }

    public void setQuestion_name(String question_name) {
        this.question_name = question_name;
    }

    public String getQuestion_type() {
        return question_type;
    }

    public void setQuestion_type(String question_type) {
        this.question_type = question_type;
    }
}
