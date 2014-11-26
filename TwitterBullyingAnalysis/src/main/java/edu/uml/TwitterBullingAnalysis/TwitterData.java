package edu.uml.TwitterBullingAnalysis;

public class TwitterData {

    private String user;
    private String tweet;
    private boolean bullying;
    
    public TwitterData(String user, String tweet, boolean bullying) {
        this.user = user;
        this.tweet = tweet;
        this.bullying = bullying;
    }

    public String getUser() {
        return user;
    }

    public String getTweet() {
        return tweet;
    }

    public boolean isBullying() {
        return bullying;
    }
}
