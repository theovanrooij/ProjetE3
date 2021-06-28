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
    private Cursor cursor;

    // Permet de l'initaliser a partir d'une alarme non existante
    public Alarm (Context context, final int pTimeHour,final int pTimeMinutes,final int pNbOranges,final HashMap<String, Short> pBoolJours) {
        this.timeHour = pTimeHour;
        this.timeMinutes = pTimeMinutes;
        this.nbOranges = pNbOranges;
        this.boolJours = pBoolJours;
        this.context =  context;
    }

    public Alarm(Context context, int idAlarm,final int pTimeHour,final int pTimeMinutes,final int pNbOranges) {
        this.idAlarm = idAlarm;
        this.timeHour = pTimeHour;
        this.timeMinutes = pTimeMinutes;
        this.nbOranges = pNbOranges;
        this.context =  context;


        DBManager dbManager = new DBManager(context);
        dbManager.open();
        this.cursor = dbManager.fetch(Integer.toString(this.idAlarm));

        dbManager.close();
    }

    public void setAlarm() {
        this.setAlarmManager(true);
        this.createAlarmClock();
    }
    
    public void createAlarm() {
        this.saveAlarm();
        this.setAlarm();

    }

    public void setNextAlarm() {
        this.setAlarmManager(false);

    }

    private void createAlarmClock(){
        //�ation de l'alarme Sonore avec alarmClock
        ArrayList<Integer> repeatDays = new ArrayList<>();

        for (int i=0; i<7;i++) { // Nombre de jour de la semaine
            // On regarde si le jour est active
            if(cursor.getShort(5 + i) == 1){
                switch (i) {
                    case 0 :
                        repeatDays.add(Calendar.SUNDAY);
                        break;
                    case 1:
                        repeatDays.add(Calendar.MONDAY);
                        break;
                    case 2 :
                        repeatDays.add(Calendar.TUESDAY);
                        break;
                    case 3:
                        repeatDays.add(Calendar.WEDNESDAY);
                        break;
                    case 4:
                        repeatDays.add(Calendar.THURSDAY);//Monday
                        break;
                    case 5 :
                        repeatDays.add(Calendar.FRIDAY);//Monday
                        break;
                    case 6:
                        repeatDays.add(Calendar.SATURDAY);//Monday
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
        // On d�clenche l'alarme

        context.startActivity(intent);
    }



    private void setAlarmManager(boolean isNewAlarm) {

        // Lancement de l'alarm Manager. L'alarme sonore est d�clencher par le Receiver
        Calendar calendar = getCalendar(isNewAlarm);
        if (calendar == null) { // Il n'y pas d'alarme � set
            // mettre le champs enable � 0
            DBManager dbManager = new DBManager(context);
            dbManager.open();

            int id = dbManager.updateEnable(this.idAlarm,0);
            dbManager.close();

            return;
        }
        displayCalendar(calendar);

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
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            am.setExact(AlarmManager.RTC_WAKEUP, time, pi);
        } else {
            am.set(AlarmManager.RTC_WAKEUP, time, pi);
        }
        Toast.makeText(context, "AlarmManager is set", Toast.LENGTH_SHORT).show();
    }

    // Retourne l'id de l'alarme ins�r�e
    private void saveAlarm(){
        // On ouvre la base de donn�e
        DBManager dbManager = new DBManager(context);
        dbManager.open();

        this.idAlarm = dbManager.insert(timeHour,timeMinutes,nbOranges,true,boolJours);
        cursor = dbManager.fetch(Integer.toString(this.idAlarm));

        dbManager.close();

    }

    private Calendar getCalendar(boolean isNewAlarm){
        // Traitement des checkbox
        // si vide, on regarde si l'heure est pour plus tard dans la journ�e ou si le lendemain afin de pass� les bonnes valeurs dans l'Alarm

        // Si l'alarme n'est pas nouvelle, le prochain jour d'activation est juste � r�cup�rer avec getNextDay
        if (!isNewAlarm) {
            return this.getNextDay(); // retourne un calendrier set au prochain jour d'activation

        }
        // Si isNewAlarm == true alors on doit rechercher quand activer l'alarme
        Calendar calendar = Calendar.getInstance(); // On r�cup�re l'heure actuelle

        int actualHour = calendar.get(Calendar.HOUR_OF_DAY);
        int actualMinutes = calendar.get(Calendar.MINUTE);


        String dayLongName = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());
        int today = Calendar.DAY_OF_WEEK -1;
        if (today == 0){
            today = 6;
        }else if (today == 6){
            today = 0;
        }
        boolean emptyJours = !this.isRepeating();

        // Si aucun jour n'est rempli c'est soit le jour m�me soit le lendemain
        if (emptyJours ) {
            // Par d�faut on programme l'alarm pour le jour m�me
            calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),
                    timeHour, timeMinutes, 0);
            if (!(timeHour > actualHour || actualHour == timeHour && timeMinutes>actualMinutes)) { // Si l'horaire est inf�rieur � l'heure actuelle on programme pour le lendemain
                calendar.add(Calendar.DAY_OF_MONTH, 1);
            }
        } else if ( cursor.getShort(5+today) == 1){

            // Pas regard� la case coch� mais la valeur dans bdd


            // Si le jour m�me est coch�, il faut regard� si on peut programm� pour le jour m�me ou si on prend le prochain jour
            if (!(timeHour > actualHour || actualHour == timeHour && timeMinutes>actualMinutes)) { // Si l'horaire est inf�rieur � l'heure actuelle on programme pour le prochain jour s�lectionn�
                calendar = this.getNextDay(); // retourne un calendrier set au prochain jour d'activation
            } else { // On peut programmer pour aujourd'hui
                calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),
                        timeHour, timeMinutes, 0);

            }
        }  else{ // il faut r�cup�rer le prochain jour
            calendar = this.getNextDay(); // retourne un calendrier set au prochain jour d'activation
        }

        return calendar;
    }

    // https://stackoverflow.com/questions/19051622/set-calendar-to-next-thursday
    private Calendar getNextDay(){

        // R�cup�ration des datas dans la base de donn�es :
        DBManager dbManager = new DBManager(context);
        dbManager.open();

        Cursor cursor = dbManager.fetch(Integer.toString(idAlarm));
        dbManager.close();

        Calendar calendar = Calendar.getInstance(); // On r�cup�re la date actuelle
        calendar.add(Calendar.DAY_OF_WEEK, 1); // On ajoute un jour au jour actuel
        int nextDay = calendar.get(Calendar.DAY_OF_WEEK)-1; // Permet d'obtenir le lendemain
        // On fait -1 car on commence � compter � partir de 1

        int activeDay=0;


        for (int i=0; i<7;i++) { // Nombre de jour de la semaine
            // On commence depuis le lendemain

            activeDay = cursor.getInt(5+nextDay);
            if (activeDay==1) {
                break;
            }
            nextDay+=1; // On passe au jour suivant
            if (nextDay==7){// Pour passer du samedi au dimanche
                nextDay=0;
            }
        }
        if (activeDay==0){ // Cela veut dire que l'alarme n'est pas cens� se r�p�ter
            return null;
        }
        calendar = SetToNextDayOfWeek(nextDay);
        return calendar;
    }

    public Calendar SetToNextDayOfWeek(int dayOfWeekToSet){
        Calendar calendar = Calendar.getInstance(); // On r�cup�re la date actuelle

        // On initialise le Calendar pour le lendemain
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)+1,
                timeHour, timeMinutes, 0); // On retire 5 secondes pour avoir le temps d'effectuer toutes les op�rations dans le Receiver

        int currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)-1; // Day of week commence a 1
        //add 1 day to the current day until we get to the day we want
        int i=0;
        while(currentDayOfWeek != dayOfWeekToSet){
            calendar.add(Calendar.DAY_OF_WEEK, 1);
            currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)-1;
            i+=1;
        }
        return calendar;
    }

    public void displayCalendar(Calendar calendar) {
        Log.d("ProjetE3","Prochaine alarme : "+calendar.get(Calendar.YEAR) + "/"+calendar.get(Calendar.MONTH)+"/"+calendar.get(Calendar.DAY_OF_MONTH)+" "+calendar.get(Calendar.HOUR_OF_DAY)+":"+calendar.get(Calendar.MINUTE));
    }

    public boolean isRepeating(){
        // ON regarde si aucun jour n'est coch�
        for (int i=0; i<7;i++) { // Nombre de jour de la semaine
            // On regarde si le jour est active
            if(cursor.getShort(5 + i) == 1){
                return true;
            }
        }
        return false;
    }

    public static String getStringDays(Cursor cursor){
        String days="";
        for (int i=0; i<7;i++) { // Nombre de jour de la semaine
            // On regarde si le jour est active
            if(cursor.getShort(5 + i) == 1){
                switch (i) {
                    case 0:
                        days+="Dimanche, ";
                        break;
                    case 1 :
                        days+="Lundi, ";
                        break;
                    case 2:
                        days+="Mardi, ";
                        break;
                    case 3 :
                        days+="Mercredi, ";
                        break;
                    case 4:
                        days+="Jeudi, ";
                        break;
                    case 5:
                        days+="Vendredi, ";
                        break;
                    case 6 :
                        days+="Samedi, ";
                        break;
                }
            }
        }
        if(days.isEmpty()) {
            return "Aucune repetition";
        }
        days = days.substring(0,days.length()-1);
        return days;
    }

    public static void cancelAlarm(int idAlarm,Context context) {
        Intent i = new Intent(context, AlarmReceiver.class);

        // On récupère la pendingIntent
        PendingIntent pi = PendingIntent.getBroadcast(context, idAlarm, i,  PendingIntent.FLAG_NO_CREATE);

        // Changer la valeur de Enable
        DBManager dbManager = new DBManager(context);
        dbManager.open();

        dbManager.updateEnable(idAlarm,0);
        dbManager.close();

        // Cancel PendingIntent
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if(pi != null){
            alarmManager.cancel(pi);

        } else {
            Log.d("ProjetE3", "Cancel impossible");

        }

        Intent intent = new Intent(AlarmClock.ACTION_DISMISS_ALARM);
        intent.putExtra(AlarmClock.EXTRA_ALARM_SEARCH_MODE, AlarmClock.ALARM_SEARCH_MODE_LABEL);
        intent.putExtra(AlarmClock.EXTRA_MESSAGE, Integer.toString(idAlarm));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // On desactive l'alarme

        Toast.makeText(context, "Vérifiez que l'alarme ayant pour label : '"+idAlarm+"' soit bien désactivée ou supprimée", Toast.LENGTH_SHORT).show();

        context.startActivity(intent);


    }
}
