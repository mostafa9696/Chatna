package com.example.mostafahussien.chatna.Model;

/**
 * Created by Mostafa Hussien on 20/04/2018.
 */

public class Conv {

    public boolean seen;
    public long timestamp;
    public Conv(boolean seen, long timestamp) {
        this.seen = seen;
        this.timestamp = timestamp;
    }
    public Conv(){

    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }


}
