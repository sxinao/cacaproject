package exception;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;


public class CustomException extends Exception{
    private int errnocode;
    private String msg;

    public int getErrnocode() {
        return errnocode;
    }

    public void setErrnocode(int errnocode) {
        this.errnocode = errnocode;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public CustomException(){
        super();
    }

    public CustomException(int errno){
        this.errnocode = errno;
        writeLog(errno);
    }


    public String getErrMsg(int errno){
        switch(errno){
            case 1:
                msg = " Empty username.";
                break;
            case 2:
                msg = " Empty password.";
                break;
            case 3:
                msg = " This user does not exist.";
                break;
            case 4:
                msg = " Empty post.";
                break;
            case 5:
                msg = " Empty connects. Set administrators as default friends of the users.";
                break;
            default:
                msg = " unknown mistakes";
                break;
        }
        return msg;
    }

    public String printError(int errno){
        StringBuilder errinfo = new StringBuilder();
        errinfo.append("AutoException Error Code:");
        errinfo.append(errno);
        errinfo.append(", Error Message:");
        errinfo.append(getErrMsg(errno));
        errinfo.append("\n");
        return errinfo.toString();
    }

    public void writeLog(int errno){
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            String errorInfo = format.format(new Date()) + ":" + printError(errno);
            Log.e("errorInfo",errorInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}