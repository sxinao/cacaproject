package ws.remote.Socket;

public interface RemoteClientInterface {

    public void sendMessage(String readyToSendMessage);

    public void receiveMessage(String readyToReceiveMessage);

    public void postMessage(String postMessage);

    public void profileEdit(String uniqueUserName);

    public void sendLocation(String GEOLocation);



}
