package model;

import com.google.gson.annotations.SerializedName;

public class Tweet {

    private long id;
    private String text;
    private String lang;
    private User user;

    @SerializedName("retweet_count")
    private int retweetCount;

    @SerializedName("favourite_count")
    private int favoriteCount;

    public Tweet(long id, String text, String lang, User user, int retweetCount, int favoriteCount){

        this.id = id;
        this.text = text;
        this.lang = lang;
        this.user = user;
        this.retweetCount = retweetCount;
        this.favoriteCount = favoriteCount;
    }

    public long getId(){

        return id;
    }

    public String getText(){

        return text;
    }

    public String getLang(){

        return lang;
    }

    public User getUser(){

        return user;
    }

    public int getRetweetCount(){

        return retweetCount;
    }

    public int getFavoriteCount(){

        return favoriteCount;
    }

    public void setId(long id){

        this.id = id;
    }

    public void setText(String text){

        this.text = text;
    }

    public void setLang(String lang){

        this.lang = lang;
    }

    public void setUser(User user){

        this.user = user;
    }

    public void setRetweetCount(int retweetCount){

        this.retweetCount = retweetCount;
    }

    public void setFavoriteCount(int favoriteCount){

        this.favoriteCount =favoriteCount;
    }


}
