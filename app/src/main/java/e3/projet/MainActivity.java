package e3.projet;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.provider.AlarmClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String remplissageInfo = CommandRaspberry.getRemplissage(getApplicationContext());

        Log.d("ProjetE3", "Remplissage"+remplissageInfo);

        String[] splitLine = remplissageInfo.split(":");
        Log.d("ProjetE3", splitLine[0] + " / "+splitLine[1]);
        float tauxFloat = Float.parseFloat(splitLine[1])/Float.parseFloat(splitLine[0])*100;
        int taux = (int) tauxFloat;
        Log.d("ProjetE3", "Taux : "+taux);
        ProgressBar remplissage = (ProgressBar) findViewById(R.id.indeterminateBar);
        if (Build.VERSION.SDK_INT >= 24) {
            remplissage.setProgress(taux,true);
        }

        TextView remplissageText = (TextView) findViewById(R.id.textProgress);
        remplissageText.setText("Taux de remplissage du réservoir à jus : "+taux+"%");

        // R�cup�ration des datas dans la base de donn�es :
        DBManager dbManager = new DBManager(this.getApplicationContext());
        dbManager.open();

        Cursor cursor = dbManager.fetchAll();

        dbManager.close();


        LinearLayout scrollLayout = (LinearLayout)findViewById(R.id.scrollLayout);


        if( cursor.getCount() == 0 ){
            return;
        }
        Log.d("MyAlarmBelal", "Nombre : "+cursor.getCount());
        boolean line = true;
        // Regarder si cursor pas vide
        do {

            LinearLayout alarmLayout = new LinearLayout(this);
            alarmLayout.setId(cursor.getInt(0));
            alarmLayout.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams paramsLayout = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            paramsLayout.setMargins(0,20,0,20);
            alarmLayout.setLayoutParams(paramsLayout);

            LinearLayout alarmLayoutLeft = new LinearLayout(this);
            alarmLayoutLeft.setOrientation(LinearLayout.VERTICAL);
            alarmLayoutLeft.setLayoutParams(new LinearLayout.LayoutParams(300, LinearLayout.LayoutParams.WRAP_CONTENT,0));

            TextView textViewLeft = new TextView(this);
            textViewLeft.setText(cursor.getInt(1)+":"+cursor.getInt(2));
            textViewLeft.setTextSize(35);

            alarmLayoutLeft.addView(textViewLeft);

            LinearLayout alarmLayoutCenter = new LinearLayout(this);
            alarmLayoutCenter.setOrientation(LinearLayout.VERTICAL);
            alarmLayoutCenter.setLayoutParams(new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT,1));

            TextView textViewUp = new TextView(this);
            textViewUp.setText(Short.toString(cursor.getShort(3))+" oranges");
            textViewUp.setTextSize(15);

            TextView textViewBottom = new TextView(this);
            textViewBottom.setText(Alarm.getStringDays(cursor));
            textViewBottom.setTextSize(15);

            alarmLayoutCenter.addView(textViewUp);
            alarmLayoutCenter.addView(textViewBottom);



            LinearLayout alarmLayoutRight = new LinearLayout(this);
            alarmLayoutRight.setOrientation(LinearLayout.VERTICAL);
            alarmLayoutRight.setLayoutParams(new LinearLayout.LayoutParams(170, LinearLayout.LayoutParams.MATCH_PARENT,0));

            Switch switchButton = new Switch(this);
            switchButton.setLayoutParams(new LinearLayout.LayoutParams(120, LinearLayout.LayoutParams.WRAP_CONTENT));
            switchButton.setGravity(17);
            switchButton.setId(cursor.getInt(0));
            if(cursor.getInt(4)==1){
                switchButton.setChecked(true);
            }


            switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    int idAlarm = buttonView.getId();
                    //creating a new intent specifying the broadcast receiver
                    Intent i = new Intent(getApplicationContext(), AlarmReceiver.class);

                    // On récupère la pendingIntent
                    PendingIntent pi = PendingIntent.getBroadcast(getApplicationContext(), idAlarm, i,  PendingIntent.FLAG_NO_CREATE);
                    Log.d("MyAlarmBelal", "Register : "+ (pi!=null));


                    if (isChecked) {
                        Log.d("MyAlarmBelal", "Set : "+idAlarm);

                        //Update Enable dans BDD
                        DBManager dbManager = new DBManager(getApplicationContext());
                        dbManager.open();

                        int id = dbManager.updateEnable(idAlarm,1);
                        Cursor cursor = dbManager.fetch(Integer.toString(idAlarm));

                        Log.d("ProjetE3","Update 0 : " +Integer.toString(id));
                        dbManager.close();

                        // Utiliser la classe Alarm avec setNextAlarm
                        Alarm alarm = new Alarm(getApplicationContext(),idAlarm,cursor.getInt(1),cursor.getInt(2),cursor.getInt(3));
                        alarm.setAlarm();

                        // set alarm clock
                        // Regarder si il y a plusieurs jours sur l'alarme en question
                        // Si oui ne rien faire car on ne peut pas les desactiver donc reactiver
                        Log.d("MyAlarmBelal", "repeating : "+alarm.isRepeating());


                    } else {
                        Log.d("MyAlarmBelal", "Unset : "+idAlarm);

                        // Changer la valeur de Enable
                        DBManager dbManager = new DBManager(getApplicationContext());
                        dbManager.open();

                        int id = dbManager.updateEnable(idAlarm,0);
                        Log.d("ProjetE3","Update 0 : " +Integer.toString(id));
                        dbManager.close();

                        // Cancel PendingIntent
                        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                        if(pi != null){
                            alarmManager.cancel(pi);
                            Log.d("MyAlarmBelal", "Cancel : ");

                        } else {
                            Log.d("MyAlarmBelal", "Pas possible cancel : ");

                        }

                        Toast.makeText(getApplicationContext(), "Vérifiez que l'alarme ayant pour label : '"+idAlarm+"' soit bien désactivée ou supprimée", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(AlarmClock.ACTION_DISMISS_ALARM);
                        intent.putExtra(AlarmClock.EXTRA_ALARM_SEARCH_MODE, AlarmClock.ALARM_SEARCH_MODE_LABEL);
                        intent.putExtra(AlarmClock.EXTRA_MESSAGE, Integer.toString(idAlarm));
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        // On desactive l'alarme

                        getApplicationContext().startActivity(intent);
                    }
                }
            });

            alarmLayoutRight.addView(switchButton);

            LinearLayout alarmLayoutRightBottom = new LinearLayout(this);
            alarmLayoutRightBottom.setOrientation(LinearLayout.HORIZONTAL);
            alarmLayoutRightBottom.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));


            LinearLayout alarmLayoutRightBottomRight = new LinearLayout(this);
            alarmLayoutRightBottomRight.setOrientation(LinearLayout.VERTICAL);
            alarmLayoutRightBottomRight.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT,1));

            LinearLayout alarmLayoutRightBottomLeft = new LinearLayout(this);
            alarmLayoutRightBottomLeft.setOrientation(LinearLayout.VERTICAL);
            alarmLayoutRightBottomLeft.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT,1));

            ViewGroup.LayoutParams params = new ActionBar.LayoutParams(50,50,17);

            ImageView editImage = new ImageView(this);
            editImage.setLayoutParams(params);
            editImage.setBackgroundResource(R.drawable.edit);


            ImageView binImage = new ImageView(this);
            binImage.setLayoutParams(params);
            binImage.setBackgroundResource(R.drawable.bin);
            binImage.setId(cursor.getInt(0));
            binImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // your code
                    int idAlarm = v.getId();
                    Intent i = new Intent(getApplicationContext(), AlarmReceiver.class);

                    // On récupère la pendingIntent
                    PendingIntent pi = PendingIntent.getBroadcast(getApplicationContext(), idAlarm, i,  PendingIntent.FLAG_NO_CREATE);
                    Log.d("MyAlarmBelal", "Register : "+ (pi!=null));

                    // Changer la valeur de Enable
                    DBManager dbManager = new DBManager(getApplicationContext());
                    dbManager.open();

                    dbManager.delete(idAlarm);
                    dbManager.close();

                    // Cancel PendingIntent
                    AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    if(pi != null){
                        alarmManager.cancel(pi);
                        Log.d("MyAlarmBelal", "Cancel : ");

                    } else {
                        Log.d("MyAlarmBelal", "Pas possible cancel : ");

                    }

                    Toast.makeText(getApplicationContext(), "Vérifiez que l'alarme ayant pour label : '"+idAlarm+"' soit bien désactivée ou supprimée", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(AlarmClock.ACTION_DISMISS_ALARM);
                    intent.putExtra(AlarmClock.EXTRA_ALARM_SEARCH_MODE, AlarmClock.ALARM_SEARCH_MODE_LABEL);
                    intent.putExtra(AlarmClock.EXTRA_MESSAGE, Integer.toString(idAlarm));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    // On desactive l'alarme

                    getApplicationContext().startActivity(intent);


                    Log.d("MyAlarmBelal", "Delete : "+idAlarm);
                    LinearLayout scrollLayout = (LinearLayout)findViewById(R.id.scrollLayout);
                    LinearLayout alarmLayout = (LinearLayout) findViewById(idAlarm);
                    scrollLayout.removeView(alarmLayout);
                }
            });

            alarmLayoutRightBottomLeft.addView(editImage);
            alarmLayoutRightBottomRight.addView(binImage);

            alarmLayoutRightBottom.addView(alarmLayoutRightBottomLeft);
            alarmLayoutRightBottom.addView(alarmLayoutRightBottomRight);

            alarmLayoutRight.addView(alarmLayoutRightBottom);

            alarmLayout.addView(alarmLayoutLeft);
            alarmLayout.addView(alarmLayoutCenter);
            alarmLayout.addView(alarmLayoutRight);

            scrollLayout.addView(alarmLayout);

            View horizontalRule = new View(getApplicationContext());
            horizontalRule.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));
            horizontalRule.setMinimumHeight(2);
            horizontalRule.setBackgroundColor(Color.BLACK);
            scrollLayout.addView(horizontalRule);

            Log.d("MyAlarmBelal", "id Alarme : "+Integer.toString(cursor.getInt(0)));
            line = cursor.moveToNext();
        } while (line != false);
        Log.d("MyAlarmBelal", "Fin cursor");


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d("Projet","onCreateOptionsMenu");
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d("Projet","onOptionsItemSelected");
        int id = item.getItemId();
        if (id == R.id.displayMenu) {

            Log.d("Projet","Menu");
            // Diplay activité avec le menu
            Intent i = new Intent(getApplicationContext(), menu.class);
            startActivity(i);


        }
        return super.onOptionsItemSelected(item);
    }


}