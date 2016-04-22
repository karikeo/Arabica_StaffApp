package com.shevchenko.staffapp.Model;

public class LogFile {

    public final static String TABLENAME = "log_file";
    public final static String TASKID = "taskid";
    public final static String CAPTURE_FILE = "capture_file";
    public final static String FILE_NAME = "file_name";

    public int taskID;
    public String captureFile;
    public String fileName;

    public LogFile(){
        this.taskID = 0;
        this.captureFile = "";
        this.fileName = "";
    }
    public LogFile(int taskID, String captureFile, String fileName) {
        this.taskID = taskID;
        this.captureFile = captureFile;
        this.fileName = fileName;
    }

    public int getTaskID() {
        return taskID;
    }

    public String getCaptureFile() {
        return captureFile;
    }

    public String getFileName() {
        return fileName;
    }
}
