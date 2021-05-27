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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class Alarm {

    private int idAlarm;
    private int timeHour;
    private int timeMinutes;
    private int nbOranges;
    private HashMap<String, Short> boolJours;
    private Context context;

    // Permet de l'initaliser a partir d'une alarme non existante
    public Alarm (Context context, final int pTimeHour,final int pTimeMinutes,final int pNbOranges,final HashMap<String, Short> pBoolJours) {
        this.timeHour = pTimeHour;
        this.timeMinutes = pTimeMinutes;
        this.nbOranges = pNbOranges;
        this.boolJours = pBoolJours;
        this.context =  context;
        //Log.d("ProjetE3", "Alarm initialisee");
    }

    public Alarm(Context context, int idAlarm,final int pTimeHour,final int pTimeMinutes,final int pNbOranges) {
        this.idAlarm = idAlarm;
        this.timeHour = pTimeHour;
        this.timeMinutes = pTimeMinutes;
        this.nbOranges = pNbOranges;
        this.context =  context;
    }

    public void createAlarm() {
        this.saveAlarm();
        this.setAlarmManager(true);
        this.createAlarmClock();

    }

    public void setNextAlarm() {

        this.setAlarmManager(false);
    }

    private void createAlarmClock(){
        //éation de l'alarme Sonore avec alarmClock
        ArrayList<Integer> repeatDays = new ArrayList<>();
        for  (String i : boolJours.keySet()) {

            if (boolJours.get(i).shortValue() == 1){
                switch (i){
                    case "MONDAY":
                        repeatDays.add(Calendar.MONDAY);//Monday
                        break;
                    case "TUESDAY":
                        repeatDays.add(Calendar.TUESDAY);//Monday
                        break;
                    case "WEDNESDAY":
                        repeatDays.add(Calendar.WEDNESDAY);//Monday
                        break;
                    case "THURSDAY":
                        repeatDays.add(Calendar.THURSDAY);//Monday
                        break;
                    case "FRIDAY":
                        repeatDays.add(Calendar.FRIDAY);//Monday
                        break;
                    case "SATURDAY":
                        repeatDays.add(Calendar.SATURDAY);//Monday
                        break;
                    case "SUNDAY":
                        repeatDays.add(Calendar.SUNDAY);//Monday
                        break;
                }
            }
        }
        // https://www.youtube.com/watch?v=qZdVUyLR-_M
        Intent intent = new Intent(AlarmClock.ACTION_SET_ALARM);
        intent.putExtra(AlarmClock.EXTRA_DAYS,repeatDays); // time
        intent.putExtra(AlarmClock.EXTRA_SKIP_UI,true);
        intent.putExtra(AlarmClock.EXTRA_HOUR, timeHour);
        intent.putExtra(AlarmClock.EXTRA_MINUTES, timeMinutes);
        intent.putExtra(AlarmClock.EXTRA_MESSAGE, Integer.toString(idAlarm));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // On déclenche l'alarme

        context.startActivity(intent);
    }



    private void setAlarmManager(boolean isNewAlarm) {

        // Lancement de l'alarm Manager. L'alarme sonore est déclencher par le Receiver
        Calendar calendar = getCalendar(isNewAlarm);
        if (calendar == null) { // Il n'y pas d'alarme à set
            Log.d("ProjetE3","Pas de repetition");
            // mettre le champs enable à 0
            DBManager dbManager = new DBManager(context);
            dbManager.open();

            int id = dbManager.updateEnable(this.idAlarm,0);
            Log.d("ProjetE3","Update : " +Integer.toString(id));
            dbManager.close();

            return;
        }
        displayCalendar(calendar);

        //Log.d("ProjetE3", "Calendar set");
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
        PendingIntent pi = PendingIntent.getBroadcast(context, this.idAlarm, i, PendingIntent.FLAG_UPDATE_CURRENT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // if you don't want the alarm to go off even in Doze mode, use
            // setExact instead
            am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,time, pi);
            //Log.d("ProjetE3", "Calendar new");
        } else //setting the repeating alarm that will be fired every day
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            am.setExact(AlarmManager.RTC_WAKEUP, time, pi);
        } else {
            am.set(AlarmManager.RTC_WAKEUP, time, pi);
        }
        Log.d("ProjetE3", "Alarm set");
        Toast.makeText(context, "AlarmManager is set", Toast.LENGTH_SHORT).show();
    }

    public static void test(Context context) {
        //creating a new intent specifying the broadcast receiver
        Intent i2 = new Intent(context, AlarmReceiver.class);

        i2.putExtra("EXTRA_ID_ALARM", Long.toString(-1));

        //creating a pending intent using the intent
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, i2, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()
                + 5000, pi);

    }
    // Retourne l'id de l'alarme insérée
    private void saveAlarm(){
        // On ouvre la base de donnée
        DBManager dbManager = new DBManager(context);
        dbManager.open();

        this.idAlarm = dbManager.insert(timeHour,timeMinutes,nbOranges,true,boolJours);
        dbManager.close();

        Log.d("ProjetE3", "idAlarme = "+Integer.toString(this.idAlarm));
    }

    private Calendar getCalendar(boolean isNewAlarm){
        // Traitement des checkbox
        // si vide, on regarde si l'heure est pour plus tard dans la journée ou si le lendemain afin de passé les bonnes valeurs dans l'Alarm

        // Si l'alarme n'est pas nouvelle, le prochain jour d'activation est juste à récupérer avec getNextDay
        if (!isNewAlarm) {
            //Log.d("ProjetE3", "Calendar next");
            return this.getNextDay(); // retourne un calendrier set au prochain jour d'activation

        }
        //Log.d("ProjetE3", "Calendar new");
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
        //Log.d("ProjetE3", "Boolean");
        String dayLongName = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());

        // Si aucun jour n'est rempli c'est soit le jour même soit le lendemain
        if (emptyJours ) {
            Log.d("ProjetE3", "Empty");
            // Par défaut on programme l'alarm pour le jour même
            calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),
                    timeHour, timeMinutes, 0);
            if (!(timeHour > actualHour || actualHour == timeHour && timeMinutes>actualMinutes)) { // Si l'horaire est inférieur à l'heure actuelle on programme pour le lendemain
                Log.d("ProjetE3", "demain");
                //Log.d("ProjetE3", Integer.toString(actualHour)+" "+Integer.toString(timeHour) + " |"+Integer.toString(actualMinutes)+" "+Integer.toString(timeMinutes));
                calendar.add(Calendar.DAY_OF_MONTH, 1);
            }
        } else if ( boolJours.get(dayLongName.toUpperCase()).shortValue() == 1){
            // Si le jour même est coché, il faut regardé si on peut programmé pour le jour même ou si on prend le prochain jour
            if (!(timeHour > actualHour || actualHour == timeHour && timeMinutes>actualMinutes)) { // Si l'horaire est inférieur à l'heure actuelle on programme pour le prochain jour sélectionné
                calendar = this.getNextDay(); // retourne un calendrier set au prochain jour d'activation
            } else { // On peut programmer pour aujourd'hui
                calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),
                        timeHour, timeMinutes, 0);

            }
        }  else{ // il faut récupérer le prochain jour
            Log.d("ProjetE3", "Not Empty");
            calendar = this.getNextDay(); // retourne un calendrier set au prochain jour d'activation
        }

        return calendar;
    }

    // https://stackoverflow.com/questions/19051622/set-calendar-to-next-thursday
    private Calendar getNextDay(){

        // Récupération des datas dans la base de données :
        DBManager dbManager = new DBManager(context);
        dbManager.open();

        Cursor cursor = dbManager.fetch(Integer.toString(idAlarm));
        //Log.d("ProjetE3", "id Alarme" + Integer.toString(idAlarm));
        dbManager.close();

        Calendar calendar = Calendar.getInstance(); // On récupère la date actuelle
        //Log.d("ProjetE3", "date actuelle");
        displayCalendar(calendar);
        calendar.add(Calendar.DAY_OF_WEEK, 1); // On ajoute un jour au jour actuel
        //Log.d("ProjetE3", "date j+1");
        displayCalendar(calendar);
        int nextDay = calendar.get(Calendar.DAY_OF_WEEK)-1; // Permet d'obtenir le lendemain
        // On fait -1 car on commence à compter à partir de 1

        int activeDay=0;


        for (int i=0; i<7;i++) { // Nombre de jour de la semaine
            // On commence depuis le lendemain

            activeDay = cursor.getInt(5+nextDay);
            //Log.d("ProjetE3", "Boucle : " +Integer.toString(nextDay) +" Valeur : " + Integer.toString(activeDay));
            if (activeDay==1) {
                break;
            }
            nextDay+=1; // On passe au jour suivant
            if (nextDay==7){// Pour passer du samedi au dimanche
                nextDay=0;
            }
        }
        //Log.d("ProjetE3", "sortie for");
        if (activeDay==0){ // Cela veut dire que l'alarme n'est pas censé se répéter
            return null;
        }
        Log.d("ProjetE3", "nextDay = "+Integer.toString(nextDay));
        calendar = SetToNextDayOfWeek(nextDay);
        return calendar;
    }

    public Calendar SetToNextDayOfWeek(int dayOfWeekToSet){
        Calendar calendar = Calendar.getInstance(); // On récupère la date actuelle

        // On initialise le Calendar pour le lendemain
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)+1,
                timeHour, timeMinutes, 0); // On retire 5 secondes pour avoir le temps d'effectuer toutes les opérations dans le Receiver

        int currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)-1; // Day of week commence a 1
        //add 1 day to the current day until we get to the day we want
        //Log.d("ProjetE3", "entree while : set : "+Integer.toString(dayOfWeekToSet) + " | actuel : "+Integer.toString(currentDayOfWeek));
        int i=0;
        while(currentDayOfWeek != dayOfWeekToSet){
            calendar.add(Calendar.DAY_OF_WEEK, 1);
            currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)-1;
            i+=1;
        }
        //Log.d("ProjetE3", "sortie while");
        return calendar;
    }

    public void displayCalendar(Calendar calendar) {

        Log.d("ProjetE3","Prochaine alarme : "+calendar.get(Calendar.YEAR) + "/"+calendar.get(Calendar.MONTH)+"/"+calendar.get(Calendar.DAY_OF_MONTH)+" "+calendar.get(Calendar.HOUR_OF_DAY)+":"+calendar.get(Calendar.MINUTE));
    }
}
