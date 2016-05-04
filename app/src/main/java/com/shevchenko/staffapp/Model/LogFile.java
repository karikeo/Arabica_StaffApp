package com.shevchenko.staffapp.Model;

public class LogFile {

    public final static String TABLENAME = "log_file";
    public final static String TASKID = "taskid";
    public final static String CAPTURE_FILE = "capture_file";
    public final static String FILE_NAME = "file_name";
    public final static String FILE_CONTENT = "file_content";

    private int taskID;
    private String captureFile;
    private String fileName;
    private String fileContent;

    public LogFile(int taskID, String captureFile, String fileName, String fileContent) {
        this.taskID = taskID;
        this.captureFile = captureFile;
        this.fileName = fileName;
        this.fileContent = fileContent;
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

    public String getFileContent() {
        return fileContent;
    }
}
