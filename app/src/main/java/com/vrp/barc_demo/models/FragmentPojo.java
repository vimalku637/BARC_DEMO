/*
 * Copyright (c)  2020. Indev Consultancy Private Limited,
 * Auther : Vimal Kumar
 * Date : 2020/12/15
 * Modified Date :
 * Modified By :
 */

package com.vrp.barc_demo.models;

import java.io.Serializable;

public class FragmentPojo implements Serializable {
    private String startScreenPosition;
    private String endScreenPosition;

    public String getstartScreenPosition() { return startScreenPosition; }
    public void setstartScreenPosition(String startScreenPosition) { this.startScreenPosition = startScreenPosition; }

    public String getendScreenPosition() { return endScreenPosition; }
    public void setendScreenPosition(String endScreenPosition) { this.endScreenPosition = endScreenPosition; }

}
