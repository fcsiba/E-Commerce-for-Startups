package pushy.fastech.pk.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import static pushy.fastech.pk.admin.adminportal.AdminDashboard.dialogAdmin;
import static pushy.fastech.pk.admin.adminportal.NotificationView.dialogNotificationView;
import static pushy.fastech.pk.staff.staffportal.StaffDashboard.dialogStaff;

public class NetworkChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (isOnline(context)){
            try {
                dialogAdmin(true);
                dialogStaff(true);
                dialogNotificationView(true);
                Log.e("error gone", "internet on");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else{
            try {
                dialogAdmin(false);
                dialogStaff(false);
                dialogNotificationView(false);
                Log.e("error", "internet off");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private boolean isOnline(Context context){
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            return (netInfo != null && netInfo.isConnected());
        } catch (NullPointerException e) {
            e.printStackTrace();
            return false;
        }
    }
}
