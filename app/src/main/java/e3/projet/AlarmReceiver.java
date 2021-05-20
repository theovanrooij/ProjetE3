package e3.projet;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.PowerManager;
import android.os.StrictMode;
import android.os.SystemClock;
import android.provider.AlarmClock;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.util.Properties;

/**
 * Created by Belal on 8/29/2017.
 */

//class extending the Broadcast Receiver
public class AlarmReceiver extends BroadcastReceiver {

    private int idAlarm;
    //the method will be fired when the alarm is triggerred
    @Override
    public void onReceive(Context context, Intent intent) {

        //but you can do any task here that you want to be done at a specific time everyday

        Log.d("MyAlarmBelal", "Alarm just fired");

        String idAlarmString = intent.getStringExtra("EXTRA_ID_ALARM");

        idAlarm = Integer.parseInt(idAlarmString);

        String hourString = intent.getStringExtra("EXTRA_HOUR");

        int timeHour = Integer.parseInt(hourString);

        String minutesString = intent.getStringExtra("EXTRA_MINUTES");

        int timeMinutes = Integer.parseInt(minutesString);

        String orangesString = intent.getStringExtra("EXTRA_ORANGES");

        int nbOranges = Integer.parseInt(orangesString);

        // Lancement de l'alarme
       // ringAlarm(context,timeHour,timeMinutes);

        // Execution de la commande
        //sendCommandRaspberry(context,cursor.getInt(3););

        Alarm alarm = new Alarm(idAlarm,timeHour,timeMinutes,nbOranges);
        //alarm.setNextAlarm(context);
        SystemClock.sleep(1000);


    }
    // https://www.youtube.com/watch?v=77sGWNGmLhw
    // http://www.jcraft.com/jsch/examples/Exec.java.html
    private void sendCommandRaspberry(Context context,int nbOranges){

        SharedPreferences pref = context.getApplicationContext().getSharedPreferences("SSH", 0); // 0 - for private mode
        String ip = pref.getString("ip", null); // getting String
        String user = pref.getString("user", null); // getting String
        String password = pref.getString("password", null); // getting String

        int port = 22;
        String command="touch "+Integer.toString(nbOranges)+".txt";

        try {

            // On est dans l'activité main donc besoin de rajouter ça

            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

            StrictMode.setThreadPolicy(policy);

            JSch jsch = new JSch();
            Session session = jsch.getSession(user, ip, port);
            session.setPassword(password);

            // Avoid asking for key confirmation
            Properties prop = new Properties();
            prop.put("StrictHostKeyChecking", "no");
            session.setConfig(prop);
            session.connect();

            Channel channel=session.openChannel("exec");
            ((ChannelExec)channel).setCommand(command);
            channel.setInputStream(null);

            ((ChannelExec)channel).setErrStream(System.err);

            channel.connect();

            channel.disconnect();
            session.disconnect();

            Toast.makeText(context, "Commande éxécutée", Toast.LENGTH_SHORT).show();
        } catch(Exception e){
            Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }


    private void ringAlarm(Context context,int timeHour,int timeMinutes){
        //Création de l'alarme Sonore avec alarmClock
        // https://www.youtube.com/watch?v=qZdVUyLR-_M
        Intent intent = new Intent(AlarmClock.ACTION_SET_ALARM);
        intent.putExtra(AlarmClock.EXTRA_SKIP_UI,true);
        intent.putExtra(AlarmClock.EXTRA_HOUR, timeHour);
        intent.putExtra(AlarmClock.EXTRA_MINUTES, timeMinutes);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_USER_ACTION);
        // On déclenche l'alarme
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);

        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK| PowerManager.ACQUIRE_CAUSES_WAKEUP, "MyApp::MyWakelockTag");
        wakeLock.acquire(6000);
        context.startActivity(intent);
        Log.d("ProjetE3", "AlarmClock set");
    }

}