package e3.projet;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.provider.AlarmClock;
import android.util.Log;
import android.view.Gravity;
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

import java.io.IOException;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;


public class MainActivity extends AppCompatActivity {
    private Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.context = getApplicationContext();
        SharedPreferences pref = this.context.getSharedPreferences("SSH", 0); // 0 - for private mode

        boolean connexion = pref.getBoolean("connexion",false); // getting String

        ImageView imageSatus = (ImageView) findViewById(R.id.connexionStatus);
        Log.d("Projet","ConnexionMain : "+connexion);

        if (!connexion) {
            imageSatus.setBackgroundResource(R.drawable.red_circle);
        } else {
            String nbOranges = CommandRaspberry.readFile(this.context,"nbOrangesReservoir");
            Log.d("ProjetE3", "nbOranges : "+nbOranges);
            if (Integer.parseInt(nbOranges) == 1)
                imageSatus.setBackgroundResource(R.drawable.orange_circle);
            else
                imageSatus.setBackgroundResource(R.drawable.green_cirle);
        }

        String remplissageInfo = CommandRaspberry.readFile(this.context,"remplissageJus");

        Log.d("ProjetE3", "Remplissage"+remplissageInfo);

        if (remplissageInfo != null) {
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
        } else {
            Log.d("ProjetE3", "NULL");
            TextView remplissageText = (TextView) findViewById(R.id.textProgress);
            remplissageText.setText("La raspberry n'est pas configuree");
        }



        // R�cup�ration des datas dans la base de donn�es :
        DBManager dbManager = new DBManager(this.context);
        dbManager.open();

        Cursor cursor = dbManager.fetchAll();

        dbManager.close();


        LinearLayout scrollLayout = (LinearLayout)findViewById(R.id.scrollLayout);


        if( cursor.getCount() == 0 ){
            TextView title = new TextView(this);

            title.setText("Aucune alarme n'est programmée.");
            title.setTextSize(20);
            title.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT,0));
            title.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            title.setPadding(0,250,0,0);
            scrollLayout.addView(title);
            Intent i = new Intent(this.context, MenuActivity.class);
            startActivity(i);
            return;
        }
        Log.d("MyAlarmBelal", "Nombre : "+cursor.getCount());
        boolean line = true;
        // Regarder si cursor pas vide
        do {

            LinearLayout alarmLayout = new LinearLayout(this);
            alarmLayout.setOrientation(LinearLayout.HORIZONTAL);
            alarmLayout.setId(cursor.getInt(0)*3);
            LinearLayout.LayoutParams paramsLayout = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            paramsLayout.setMargins(10,20,10,20);
            alarmLayout.setLayoutParams(paramsLayout);

            LinearLayout alarmLayoutLeft = new LinearLayout(this);
            alarmLayoutLeft.setOrientation(LinearLayout.HORIZONTAL);
            alarmLayoutLeft.setLayoutParams(new LinearLayout.LayoutParams(300, LinearLayout.LayoutParams.MATCH_PARENT,0));

            TextView textViewLeft = new TextView(this);

            String hourString = Integer.toString(cursor.getInt(1));
            if (cursor.getInt(1) <10){
                hourString = "0"+hourString;
            }

            String minutesString = Integer.toString(cursor.getInt(2));
            if (cursor.getInt(2) <10){
                minutesString = "0"+minutesString;
            }

            textViewLeft.setText(hourString+":"+minutesString);
            textViewLeft.setTextSize(35);
            textViewLeft.setGravity(Gravity.CENTER);
            textViewLeft.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT,0));
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

            LinearLayout alarmLayoutRightBig = new LinearLayout(this);
            alarmLayoutRightBig.setOrientation(LinearLayout.VERTICAL);
            alarmLayoutRightBig.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT,0));

            LinearLayout alarmLayoutRight = new LinearLayout(this);
            alarmLayoutRight.setOrientation(LinearLayout.HORIZONTAL);
            alarmLayoutRight.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT,0));
            alarmLayoutRight.setGravity(Gravity.CENTER);

            Switch switchButton = new Switch(this);
            switchButton.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            switchButton.setGravity(Gravity.CENTER);
            switchButton.setId(cursor.getInt(0)*3+1);
            if(cursor.getInt(4)==1){
                switchButton.setChecked(true);
            }


            switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    int idAlarm = ((int)buttonView.getId()-1)/3;
                    
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

                        Alarm.cancelAlarm(idAlarm,getApplicationContext());
                    }
                }
            });

            alarmLayoutRight.addView(switchButton);

            ViewGroup.LayoutParams params = new ActionBar.LayoutParams(50,50,Gravity.CENTER);

            ImageView binImage = new ImageView(this);
            binImage.setLayoutParams(params);
            binImage.setBackgroundResource(R.drawable.bin);
            binImage.setId(cursor.getInt(0)*3+2);
            binImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // your code
                    int idAlarm = ((int)v.getId()-2)/3;
                    Log.d("MyAlarmBelal", "Delete : "+idAlarm);

                    Alarm.cancelAlarm(idAlarm,getApplicationContext());

                    LinearLayout scrollLayout = (LinearLayout)findViewById(R.id.scrollLayout);

                   /* Intent intentConfiguration = new Intent(this.context, MainActivity.class);
                    startActivity(intentConfiguration);
                    finish();*/

                    LinearLayout alarmLayout = (LinearLayout) findViewById(idAlarm*3);
                    scrollLayout.removeView(alarmLayout);
                }
            });

            alarmLayoutRight.addView(binImage);

            alarmLayout.addView(alarmLayoutLeft);
            alarmLayout.addView(alarmLayoutCenter);
            alarmLayout.addView(alarmLayoutRight);

            scrollLayout.addView(alarmLayout);

            View horizontalRule = new View(this.context);
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
        this.checkHosts();

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
            Intent i = new Intent(this.context, MenuActivity.class);
            startActivity(i);


        }
        return super.onOptionsItemSelected(item);
    }

    public static void checkHosts() {
        try {
            Enumeration nis = NetworkInterface.getNetworkInterfaces();
            while(nis.hasMoreElements())
            {
                NetworkInterface ni = (NetworkInterface) nis.nextElement();
                Enumeration ias = ni.getInetAddresses();
                while (ias.hasMoreElements())
                {
                    InetAddress ia = (InetAddress) ias.nextElement();
                    if (ia.getClass() != Inet6Address.class) {
                        Log.d("IP",ia.getHostAddress());
                    }
                }
            }
        } catch (SocketException ex) {
            Log.d("IP","marche po");
        }
    }

}