package com.shevchenko.staffapp.Model;

/**
 * Created by shevchenko on 2015-11-28.
 */
public class TaskInfo {

    public final static String TABLENAME = "tb_imcomplete";
    public final static String USERID = "userid";
    public final static String TASKID = "taskID";
    public final static String DATE = "date";
    public final static String TASKTYPE = "taskType";
    public final static String RUATABASTECIMIENTO = "RutaAbastecimiento";
    public final static String TASKBUSINESSKEY = "TaskBusinessKey";
    public final static String CUSTOMER = "Customer";
    public final static String ADRESS = "Adress";
    public final static String LOCATIONDESC = "LocationDesc";
    public final static String MODEL = "Model";
    public final static String LATITUDE = "latitude";
    public final static String LONGITUDE = "longitude";
    public final static String EPV= "epv";
    public final static String MACHINETYPE = "MachineType";
    public final static String DISTANCE = "distance";
    public final static String AUX_VALOR1 = "Aux_valor1";

    public String userid;
    public int taskID;
    public String date;
    public String taskType;
    public String RutaAbastecimiento;
    public String TaskBusinessKey;
    public String Customer;
    public String Adress;
    public String LocationDesc;
    public String Model;
    public String latitude;
    public String longitude;
    public String epv;
    public String MachineType;
    public String distance;
    public String Aux_valor1;

    public TaskInfo(String userid, int id, String date, String taskType, String RutaAbastecimiento, String TaskBusinessKey, String Customer, String Adress, String LocationDesc, String Model, String latitude, String longitude, String epv, String MachineType, String distance, String Aux_valor1) {
        this.userid = userid;
        this.taskID = id;
        this.date = date;
        this.taskType = taskType;
        this.latitude = latitude;
        this.longitude = longitude;
        this.RutaAbastecimiento = RutaAbastecimiento;
        this.TaskBusinessKey = TaskBusinessKey;
        this.Customer = Customer;
        this.Adress = Adress;
        this.LocationDesc = LocationDesc;
        this.Model = Model;
        this.epv = epv;
        this.MachineType = MachineType;
        this.Aux_valor1 = Aux_valor1;
        this.distance = distance;
    }

    public TaskInfo() {
        this.userid = "";
        this.taskID = 0;
        this.date = "";
        this.taskType = "";
        this.longitude = "";
        this.latitude = "";
        this.RutaAbastecimiento = "";
        this.TaskBusinessKey = "";
        this.Customer = "";
        this.Adress = "";
        this.LocationDesc = "";
        this.Customer = "";
        this.Adress = "";
        this.LocationDesc = "";
        this.Model = "";
        this.epv = "";
        this.MachineType = "";
        this.distance = "";
        this.Aux_valor1 = "";
    }

    public String getUserid() { return this.userid; }

    public int getTaskID() {
        return this.taskID;
    }

    public String getDate() {
        return this.date;
    }

    public String getTaskType() { return this.taskType; }

    public String getLatitude() {
        return this.latitude;
    }

    public String getLongitude() {
        return this.longitude;
    }


    public String getRutaAbastecimiento() {
        return this.RutaAbastecimiento;
    }

    public String getTaskBusinessKey() {
        return this.TaskBusinessKey;
    }

    public String getCustomer() {
        return this.Customer;
    }

    public String getAdress() {
        return this.Adress;
    }

    public String getLocationDesc() { return this.LocationDesc; }

    public String getModel() { return this.Model; }

    public String getepv() { return this.epv; }

    public String getMachineType() { return this.MachineType; }

    public String getDistance() { return this.distance; }

    public String getAux_valor1() { return this.Aux_valor1; }
}
