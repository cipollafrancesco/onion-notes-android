package com.francescocipolla.onionnotes.models;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Ciccio on 20/02/2017.
 */

public class Note {
    private String title;
    private String body;
    private String creationDate;
    private String lastUpdateDate;
    private States status;

    private enum States {DONE, FILED, EDITED, RUNNING};

    public Note(String title, String body) {
        this.title = title;
        this.body = body;
        this.creationDate = "2017-02-20";
        this.lastUpdateDate = "2017-02-20";
        this.status = States.RUNNING;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public String getLastUpdateDate() {
        return lastUpdateDate;
    }

    public States getStatus() {
        return status;
    }

    public String getCurrentTime() {
        Calendar calendarObj = Calendar.getInstance();

        String day = String.valueOf(calendarObj.getTime().getDay());
        String month = String.valueOf(calendarObj.getTime().getMonth());
        String year = String.valueOf(calendarObj.getTime().getYear());

        return (year + "-" + month + "-" + day);
    }
}
