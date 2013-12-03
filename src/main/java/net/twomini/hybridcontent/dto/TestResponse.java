package net.twomini.hybridcontent.dto;

import java.util.Date;

public class TestResponse {

    private Boolean testing = true;

    private String now = new Date().toString();

    private String userName;

    public TestResponse(String userName) {
        this.userName = userName;
    }

    public Boolean getTesting() {
        return testing;
    }

    public void setTesting(Boolean testing) {
        this.testing = testing;
    }

    public String getNow() {
        return now;
    }

    public void setNow(String now) {
        this.now = now;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
