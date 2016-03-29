package com.shevchenko.staffapp.Model;

/**
 * Created by shevchenko on 2015-11-29.
 */
public class LogEvent {

    public final static String TABLENAME = "log_event";
    public final static String USERID = "userid";
    public final static String TASKID = "taskid";
    public final static String DATETIME = "datetime";
    public final static String DESCRIPTION = "description";
    public final static String LATITUDE = "latitude";
    public final static String LONGITUDE = "longitude";

    public String userid;
    public String taskid;
    public String datetime;
    public String description;
    public String latitude;
    public String longitude;

    public LogEvent()
    {
        this.userid = "";
        this.taskid = "";
        this.datetime = "";
        this.description = "";
        this.latitude = "";
        this.longitude = "";
    }
    public LogEvent(String userid, String taskid, String datetime, String description, String latitude, String longitude)
    {
        this.userid = userid;
        this.taskid = taskid;
        this.datetime = datetime;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
