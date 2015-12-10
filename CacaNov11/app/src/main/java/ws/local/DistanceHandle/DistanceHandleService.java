package ws.local.DistanceHandle;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class DistanceHandleService extends Service {
    public DistanceHandleService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public double returnClientLocation(double oneUserLocation){

        return oneUserLocation;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        System.out.println("Server is connected.");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("Server is destroyed.");
    }
}
