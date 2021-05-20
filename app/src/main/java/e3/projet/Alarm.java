package e3.projet;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.provider.AlarmClock;
import android.util.Log;
import android.widget.Toast;

import java.util.Calendar;
import java.util.HashMap;

public class Alarm {

    private int idAlarm;
    private int timeHour;
    private int timeMinutes;
    private int nbOranges;
    private HashMap<String, Short> boolJours;

    // Permet de l'initaliser à partir d'une alarme non existante
    public Alarm (final int pTimeHour,final int pTimeMinutes,final int pNbOranges,final HashMap<String, Short> pBoolJours) {
        this.timeHour = pTimeHour;
        this.timeMinutes = pTimeMinutes;
        this.nbOranges = pNbOranges;
        this.boolJours = pBoolJours;
        Log.d("ProjetE3", "Alarm initialisee");
    }

    public Alarm(int idAlarm,final int pTimeHour,final int pTimeMinutes,final int pNbOranges) {
        this.idAlarm = idAlarm;
        this.timeHour = pTimeHour;
        this.timeMinutes = pTimeMinutes;
        this.nbOranges = pNbOranges;
    }

    public void createAlarm(Context context) {
        this.saveAlarm(context);
        Log.d("ProjetE3", "Alarm sauvegardee");
        this.setAlarmManager(context,true);
        Log.d("ProjetE3", "Alarm set");
    }

    public void setNextAlarm(Context context) {
        this.setAlarmManager(context,false);
    }

    private void setAlarmManager(Context context, boolean isNewAlarm) {

        // Lancement de l'alarm Manager. L'alarme sonore est déclencher par le Receiver
        Calendar calendar = getCalendar(context, isNewAlarm);
        if (calendar == null) { // Il n'y pas d'alarme à set
            return;
        }
        Log.d("ProjetE3", "Calendar set");
        long time = calendar.getTimeInMillis();
        //getting the alarm manager
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        //creating a new intent specifying the broadcast receiver
        Intent i = new Intent(context, AlarmReceiver.class);

        i.putExtra("EXTRA_ID_ALARM", Long.toString(this.idAlarm));
        i.putExtra("EXTRA_HOUR", Integer.toString(this.timeHour));
        i.putExtra("EXTRA_MINUTES", Integer.toString(this.timeMinutes));
        i.putExtra("EXTRA_ORANGES", Integer.toString(this.nbOranges));

        //creating a pending intent using the intent
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // if you don't want the alarm to go off even in Doze mode, use
            // setExact instead
            am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,time, pi);
            Log.d("ProjetE3", "Calendar new");
        } else //setting the repeating alarm that will be fired every day
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            am.setExact(AlarmManager.RTC_WAKEUP, time, pi);
        } else {
            am.set(AlarmManager.RTC_WAKEUP, time, pi);
        }

        Intent i2 = new Intent(AlarmClock.ACTION_SET_ALARM);
        i2.putExtra(AlarmClock.EXTRA_SKIP_UI, true);
        i2.putExtra(AlarmClock.EXTRA_HOUR, timeHour);
        i2.putExtra(AlarmClock.EXTRA_MINUTES, timeMinutes);
        i2.putExtra(AlarmClock.EXTRA_MESSAGE, "Good Morning");
        i2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent alarmIntent = PendingIntent.getActivity( context, 0, i2, PendingIntent.FLAG_UPDATE_CURRENT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // if you don't want the alarm to go off even in Doze mode, use
            // setExact instead
            alarmMgr.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,time, alarmIntent);
        } else //setting the repeating alarm that will be fired every day
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                alarmMgr.setExact(AlarmManager.RTC_WAKEUP, time, alarmIntent);
            } else {
                alarmMgr.set(AlarmManager.RTC_WAKEUP, time, alarmIntent);
            }
        Toast.makeText(context, "AlarmManager is set", Toast.LENGTH_SHORT).show();
    }


    // Retourne l'id de l'alarme insérée
    private void saveAlarm(Context context){
        // On ouvre la base de donnée
        DBManager dbManager = new DBManager(context);
        dbManager.open();

        this.idAlarm = dbManager.insert(timeHour,timeMinutes,nbOranges,true,boolJours);
        dbManager.close();
    }

    private Calendar getCalendar(Context context,boolean isNewAlarm){
        // Traitement des checkbox
        // si vide, on regarde si l'heure est pour plus tard dans la journée ou si le lendemain afin de passé les bonnes valeurs dans l'Alarm

        // Si l'alarme n'est pas nouvelle, le prochain jour d'activation est juste à récupérer avec getNextDay
        if (!isNewAlarm) {
            Log.d("ProjetE3", "Calendar next");
            return this.getNextDay(context); // retourne un calendrier set au prochain jour d'activation

        }
        Log.d("ProjetE3", "Calendar new");
        // Si isNewAlarm == true alors on doit rechercher quand activer l'alarme
        Calendar calendar = Calendar.getInstance(); // On récupère l'heure actuelle

        int actualHour = calendar.get(Calendar.HOUR_OF_DAY);
        int actualMinutes = calendar.get(Calendar.MINUTE);

        // ON regarde si aucun jour n'est coché
        boolean emptyJours = true;
        for  (String i : boolJours.keySet()) {
            if (boolJours.get(i).shortValue() == 1)
                emptyJours = false;
        }
        Log.d("ProjetE3", "Boolean");
        if (emptyJours) {
            Log.d("ProjetE3", "Not Empty");
            calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),
                    timeHour, timeMinutes-1, 55); // On retire 5 secondes pour avoir le temps d'effectuer toutes les opérations dans le Receiver
            if (!(timeHour > actualHour || actualHour == timeHour && timeMinutes>actualMinutes)) { // Le temps renseigné est le même jour mais plus tard
                Log.d("ProjetE3", "demain");
                Log.d("ProjetE3", Integer.toString(actualHour)+" "+Integer.toString(timeHour) + " |"+Integer.toString(actualMinutes)+" "+Integer.toString(timeMinutes));
                calendar.add(Calendar.DAY_OF_MONTH, 1);
            }
        } else { // il faut récupérer le prochain jour
            Log.d("ProjetE3", "Empty");
            calendar = this.getNextDay(context); // retourne un calendrier set au prochain jour d'activation
        }

        return calendar;
    }

    // https://stackoverflow.com/questions/19051622/set-calendar-to-next-thursday
    private Calendar getNextDay(Context context){

        // Récupération des datas dans la base de données :
        DBManager dbManager = new DBManager(context);
        dbManager.open();

        Cursor cursor = dbManager.fetch(Integer.toString(idAlarm));

        dbManager.close();

        Calendar calendar = Calendar.getInstance(); // On récupère la date actuelle

        calendar.add(Calendar.DAY_OF_WEEK, 1);
        int nextDay = calendar.get(Calendar.DAY_OF_WEEK); // Permet d'obtenir le lendemain

        int activeDay=0;

        for (int i=0; i<7;i++) { // Nombre de jour de la semaine
            // On commence depuis le lendemain
            activeDay = cursor.getInt(4+nextDay);
            if (activeDay==1) {
                break;
            }
            nextDay+=1; // On passe au jour suivant
            if (nextDay==7){// Pour passer du samedi au dimanche
                nextDay=0;
            }
        }
        if (activeDay==0){ // Cela veut dire que l'alarme n'est pas censé se répéter
            return null;
        }
        calendar = SetToNextDayOfWeek(nextDay);

        return calendar;
    }

    public Calendar SetToNextDayOfWeek(int dayOfWeekToSet){
        Calendar calendar = Calendar.getInstance(); // On récupère la date actuelle

        // On initialise le Calendar pour le lendemain
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)+1,
                timeHour, timeMinutes-1, 55); // On retire 5 secondes pour avoir le temps d'effectuer toutes les opérations dans le Receiver

        int currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        //add 1 day to the current day until we get to the day we want
        while(currentDayOfWeek != dayOfWeekToSet){
            calendar.add(Calendar.DAY_OF_WEEK, 1);
            currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        }
        return calendar;
    }

}
