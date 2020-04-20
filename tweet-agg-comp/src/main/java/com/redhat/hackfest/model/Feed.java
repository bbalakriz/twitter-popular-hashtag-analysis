package com.redhat.hackfest.model;

public class Feed {

    public String hashtag;
    public String time;

    public Feed() {
        super();
    }

    public Feed(String hashtag, String time) {
        this.hashtag = hashtag;
        this.time = time;
    }

    public String getHashtag() {
        return hashtag;
    }

    public void setHashtag(String hashtag) {
        this.hashtag = hashtag;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return hashtag + " : " + time;
    }


}
