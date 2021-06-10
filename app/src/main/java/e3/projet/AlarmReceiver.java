package e3.projet;


import android.app.AlarmManager;
import android.app.PendingIntent;
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


        // Execution de la commande
        CommandRaspberry.sendCommandOrangeRaspberry(context,orangesString);


        Alarm alarm = new Alarm(context,idAlarm,timeHour,timeMinutes,nbOranges);
        alarm.setNextAlarm();

        Intent i = new Intent(context, MainActivity.class);
        context.startActivity(i);


    }



}