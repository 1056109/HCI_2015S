package at.at.tuwien.hci.hciss2015.domain;

import java.util.Date;

/**
 * Created by Amer Salkovic on 16.7.2015.
 */
public class LogItem {

    private String message;
    private String timestamp;

    public LogItem(String message, String timestamp) {
        this.message = message;
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
