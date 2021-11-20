package com.vrp.barc_demo.models;

import java.io.Serializable;

public class SyncAudioModel implements Serializable {
    private String user_id="",
            survey_id="",
            survey_data_monitoring_id="",
            audio_recording="",
    id="";

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getSurvey_id() {
        return survey_id;
    }

    public void setSurvey_id(String survey_id) {
        this.survey_id = survey_id;
    }

    public String getSurvey_data_monitoring_id() {
        return survey_data_monitoring_id;
    }

    public void setSurvey_data_monitoring_id(String survey_data_monitoring_id) {
        this.survey_data_monitoring_id = survey_data_monitoring_id;
    }

    public String getAudio_recording() {
        return audio_recording;
    }

    public void setAudio_recording(String audio_recording) {
        this.audio_recording = audio_recording;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
