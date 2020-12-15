/*
 * Copyright (c)  2020. Indev Consultancy Private Limited,
 * Auther : Vimal Kumar
 * Date : 2020/12/15
 * Modified Date :
 * Modified By :
 */

package com.vrp.barc_demo.models;

import java.io.Serializable;

public class AnswerModel implements Serializable {
    private String option_value;
    private String option_id;
    private String survey_id;
    private String question_id;
    private String question_name;
    private String question_type;
    private String pre_field;
    private String field_name;
    private String user_id;

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getField_name() {
        return field_name;
    }

    public void setField_name(String field_name) {
        this.field_name = field_name;
    }

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

    public String getPre_field() {
        return pre_field;
    }

    public void setPre_field(String pre_field) {
        this.pre_field = pre_field;
    }
}
