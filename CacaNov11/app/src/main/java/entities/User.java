package entities;


public class User {
    private static String userName;
    private static int userId;
    private static int image;
    private static boolean isMale;
    private static String whatsUp;
    private static String pwd;


    public User() {
        userName = "default";
        userId = 0;
        image = 0;
        isMale = false;
        whatsUp = "default";
        pwd = "0000";
    }

    public User(String userName, int userId, int image, boolean isMale, String whatsUp, String pwd) {
        this.userName = userName;
        this.userId = userId;
        this.image = image;
        this.isMale = isMale;
        this.whatsUp = whatsUp;
        this.pwd = pwd;
    }

    public static String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public static int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public static int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public static boolean isMale() {
        return isMale;
    }

    public void setIsMale(boolean isMale) {
        this.isMale = isMale;
    }

    public static String getWhatsUp() {
        return whatsUp;
    }

    public void setWhatsUp(String whatsUp) {
        this.whatsUp = whatsUp;
    }

    public static String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String toString() {
        StringBuilder sBuilder = new StringBuilder();
        sBuilder.append("userName=");
        sBuilder.append(userName);
        sBuilder.append("\tuserId=");
        sBuilder.append(userId);
        sBuilder.append("\timage=");
        sBuilder.append(image);
        sBuilder.append("\tisMale=");
        sBuilder.append(isMale);
        sBuilder.append("\twhatsUp=");
        sBuilder.append(whatsUp);
        return sBuilder.toString();
    }
}
