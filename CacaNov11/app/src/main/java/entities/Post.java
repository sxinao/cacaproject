package entities;

public class Post {
    private String timestamp;
    private int userId;
    private String postText;
    private double location;
    private int image;

    public Post() {
    }

    public Post(String timestamp, int userId, String postText, double location, int image) {
        this.timestamp = timestamp;
        this.userId = userId;
        this.postText = postText;
        this.location = location;
        this.image = image;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getPostText() {
        return postText;
    }

    public void setPostText(String postText) {
        this.postText = postText;
    }

    public double getLocation() {
        return location;
    }

    public void setLocation(double location) {
        this.location = location;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }
}