package com.novigosolutions.certiscisco_pcsbr.models;

/**
 * Created by irvin on 1/27/17.
 */
public class MenuForm {

    private int photoid;
    private String menuName;

    public String getMenuDescription() {
        return menuDescription;
    }

    public void setMenuDescription(String menuDescription) {
        this.menuDescription = menuDescription;
    }

    private String menuDescription;



    public MenuForm() {
    }


    public MenuForm(String menuName, int photoid , String menuDescription) {

        this.menuName = menuName;
        this.photoid = photoid;
        this.menuDescription = menuDescription;

    }


    public int getPhotoid() {
        return photoid;
    }

    public String getMenuName() {
        return menuName;
    }
}
