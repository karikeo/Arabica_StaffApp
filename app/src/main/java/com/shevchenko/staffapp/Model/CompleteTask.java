package com.shevchenko.staffapp.Model;

/**
 * Created by shevchenko on 2015-11-29.
 */
public class CompleteTask {

    public final static String TABLENAME = "tb_completetask";
    public final static String USERID = "userid";
    public final static String TASKID = "taskid";
    public final static String DATE = "date";
    public final static String TASKTYPE = "tasktype";
    public final static String RUTAABASTEIMIENTO = "RutaAbastecimiento";
    public final static String TASKBUSINESSKEY = "TaskBusinessKey";
    public final static String CUSTOMER = "Customer";
    public final static String ADRESS = "Adress";
    public final static String LOCATIONDESC = "LocationDesc";
    public final static String MODEL = "Model";
    public final static String LATITUDE = "latitude";
    public final static String LONGITUDE = "longitude";
    public final static String EPV = "Epv";
    public final static String LOGLATITUDE = "logLatitude";
    public final static String LOGLONGITUDE = "logLongitude";
    public final static String ACTIONDATE = "ActionDate";
    public final static String FILE1 = "file1";
    public final static String FILE2 = "file2";
    public final static String FILE3 = "file3";
    public final static String FILE4 = "file4";
    public final static String FILE5 = "file5";
    public final static String MACHINETYPE = "MachineType";
    public final static String SIGNATURE = "Signature";
    public final static String NUMEROGUIA = "NumeroGuia";
    public final static String GLOSA = "Glosa";
    public final static String AUX_VALOR1 = "Aux_valor1";

    public String userid;
    public int taskid;
    public String date;
    public String tasktype;
    public String RutaAbastecimiento;
    public String TaskBusinessKey;
    public String Customer;
    public String Adress;
    public String LocationDesc;
    public String Model;
    public String latitude;
    public String longitude;
    public String epv;
    public String logLatitude;
    public String logLongitude;
    public String ActionDate;
    public String file1;
    public String file2;
    public String file3;
    public String file4;
    public String file5;
    public String MachineType;
    public String Signature;
    public String NumeroGuia;
    public String Glosa;
    public String Aux_valor1;

    public CompleteTask()
    {
        this.userid = "";
        this.taskid = 0;
        this.date = "";
        this.tasktype = "";
        this.RutaAbastecimiento = "";
        this.TaskBusinessKey = "";
        this.Customer = "";
        this.Adress = "";
        this.LocationDesc = "";
        this.Model = "";
        this.latitude = "";
        this.longitude = "";
        this.epv = "";
        this.logLatitude = "";
        this.logLongitude = "";
        this.ActionDate = "";
        this.file1 = "";
        this.file2 = "";
        this.file3 = "";
        this.file4 = "";
        this.file5 = "";
        this.MachineType = "";
        this.Signature = "";
        this.NumeroGuia = "";
        this.Glosa = "";
        this.Aux_valor1 = "";
    }
    public CompleteTask(String userid, int taskid, String date, String tasktype, String RutaAbastecimiento, String TaskBusinessKey, String Customer, String Adress, String LocationDesc, String Model, String latitude, String longitude, String epv, String logLatitude, String logLongitude, String ActionDate, String file1, String file2, String file3, String file4, String file5, String MachineType, String Signature, String NumeroGuia, String Glosa, String Aux_valor1)
    {
        this.userid = userid;
        this.taskid = taskid;
        this.date = date;
        this.tasktype = tasktype;
        this.RutaAbastecimiento = RutaAbastecimiento;
        this.TaskBusinessKey = TaskBusinessKey;
        this.Customer = Customer;
        this.Adress = Adress;
        this.LocationDesc = LocationDesc;
        this.Model = Model;
        this.latitude = latitude;
        this.longitude = longitude;
        this.epv = epv;
        this.logLatitude = logLatitude;
        this.logLongitude = logLongitude;
        this.ActionDate = ActionDate;
        this.file1 = file1;
        this.file2 = file2;
        this.file3 = file3;
        this.file4 = file4;
        this.file5 = file5;
        this.MachineType = MachineType;
        this.Signature = Signature;
        this.NumeroGuia = NumeroGuia;
        this.Glosa = Glosa;
        this.Aux_valor1 = Aux_valor1;
    }
}
