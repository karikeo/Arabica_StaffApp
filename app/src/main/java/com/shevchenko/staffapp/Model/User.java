package com.shevchenko.staffapp.Model;

/**
 * Created by shevchenko on 2015-11-29.
 */
public class User {

    public final static String TABLENAME = "tb_user";
    public final static String USERID = "userid";
    public final static String PASSWORD = "password";

    public String userid;
    public String password;

    public User()
    {
        this.userid = "";
        this.password = "";
    }
    public User(String userid, String password)
    {
        this.userid = userid;
        this.password = password;
    }
}
