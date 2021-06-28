package e3.projet;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Belal on 8/29/2017.
 * Modified by Théo Van Rooij on 06/28/2021.
 */

//class extending the Broadcast Receiver
public class AlarmReceiver extends BroadcastReceiver {

    private int idAlarm;
    //the method will be fired when the alarm is triggerred
    @Override
    public void onReceive(Context context, Intent intent) {

        //but you can do any task here that you want to be done at a specific time everyday

        Log.d("ProjetE3", "Alarme déclenchée");

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