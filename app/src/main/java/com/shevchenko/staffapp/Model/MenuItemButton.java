package com.shevchenko.staffapp.Model;

import android.view.Menu;

/**
 * Created by shevchenko on 2015-12-27.
 */
public class MenuItemButton {
    public String txtMenu;
    public int imgDrawable;

    public MenuItemButton(){
        txtMenu = "";
        imgDrawable = 0;
    }
    public MenuItemButton(String strMenu, int drawable){
        this.txtMenu = strMenu;
        this.imgDrawable = drawable;
    }
}
