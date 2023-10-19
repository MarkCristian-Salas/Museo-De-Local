package com.example.museodelokal;

public class Data_Upload {
    public String uDescription;
    public String uImageUrl;
    public String uTitle;
    //step 2 sa floating naglagay ng key
    private String key;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Long uPrice;

    public String uFullname;
    public Data_Upload() {

    }

    public String getuTitle() {
        return uTitle;
    }

    public Long getuPrice() {
        return uPrice; // Updated to Long
    }

    public String getuDescription() {
        return uDescription;
    }

    public String getuImageUrl() {
        return uImageUrl;
    }

    public String getuFullname() {
        return uFullname;
    }

    public Data_Upload(String uTitle, Long uPrice, String uDescription, String uImageUrl, String uFullname) {
        this.uTitle = uTitle;
        this.uPrice = uPrice;
        this.uDescription = uDescription;
        this.uImageUrl = uImageUrl;
        this.uFullname = uFullname;
    }
}
