/*
 * Copyright (c)  2020. Indev Consultancy Private Limited,
 * Auther : Vimal Kumar
 * Date : 2020/12/15
 * Modified Date :
 * Modified By :
 */

package com.vrp.barc_demo.models;

import java.io.Serializable;

public class ScreenWiseQuestionModel implements Serializable {
    private String screen_id;
    private String questions;

    public String getscreen_id() {
        return screen_id;
    }

    public void setscreen_id(String screen_id) {
        this.screen_id = screen_id;
    }

    public String getquestions() {
        return questions;
    }

    public void setquestions(String questions) {
        this.questions = questions;
    }
}
