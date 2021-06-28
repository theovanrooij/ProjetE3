package e3.projet;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.context = getApplicationContext();
        TextView imageSatus = (TextView) findViewById(R.id.connexionStatus);


        SharedPreferences pref = this.context.getSharedPreferences("SSH", 0); // 0 - for private mode

        boolean connexion = pref.getBoolean("connexion",false); // getting String
      
        if (!connexion) {
            imageSatus.setBackgroundResource(R.drawable.red_circle);
            TextView remplissageText = (TextView) findViewById(R.id.textProgress);
            remplissageText.setText("La raspberry n'est pas configuree");
        } 
        
        
        else {
            JSONObject JSON = CommandRaspberry.getJSON(getApplicationContext());

            JSONObject JSONOranges;
            int nbOrangesReservoir=0;
            String nbOranges=""; 
            
           

            try {
                JSONOranges = JSON.getJSONObject("reservoirOranges");
                nbOrangesReservoir = JSONOranges.getInt("nbActuel");
                nbOranges = Integer.toString(nbOrangesReservoir);
            } catch (Exception e) {
                Log.d("Projet E3", "Erreur lors de la lecture du fichier JSON :  " + e.getMessage());
            }


            if (nbOrangesReservoir == 0)
                imageSatus.setBackgroundResource(R.drawable.orange_circle);
            else {
                imageSatus.setBackgroundResource(R.drawable.green_cirle);
                imageSatus.setText(nbOranges);
            }


            JSONObject JSONJus;
            int nbMaxJus=0, nbActuelJus=0;
            
            try {
                JSONJus = JSON.getJSONObject("reservoirJus");
                nbMaxJus = JSONJus.getInt("nbMax");
                nbActuelJus = JSONJus.getInt("nbActuel");
                
            } catch (Exception e) {
                Log.d("Projet E3", "Erreur lors de la lecture du fichier JSON :  " + e.getMessage());
            }

                float tauxFloat =  ((float)nbActuelJus/(float)nbMaxJus)*100;
                int taux = (int) tauxFloat;
                ProgressBar remplissage = (ProgressBar) findViewById(R.id.indeterminateBar);
                if (Build.VERSION.SDK_INT >= 24) {
                    remplissage.setProgress(taux,true);
                }
                TextView remplissageText = (TextView) findViewById(R.id.textProgress);
                remplissageText.setText("Taux de remplissage du réservoir à jus : "+taux+"%");

        }

        // R�cup�ration des datas dans la base de donn�es :
        DBManager dbManager = new DBManager(getApplicationContext());
        dbManager.open();

        Cursor cursor = dbManager.fetchAll();

        dbManager.close();

        LinearLayout scrollLayout = (LinearLayout)findViewById(R.id.scrollLayout);


        if( cursor.getCount() == 0 ){
            TextView title = new TextView(this);

            title.setText("Aucune alarme n'est programmée.");
            title.setTextSize(20);
            title.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT,0));
            if (Build.VERSION.SDK_INT >= 17) {
                title.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            }
            title.setPadding(0,250,0,0);
            scrollLayout.addView(title);
            Intent i = new Intent(this.context, MenuActivity.class);
            startActivity(i);
            return;
        }

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
                        DBManager dbManager = new DBManager(getApplicationContext());
                        dbManager.open();
                        int id = dbManager.updateEnable(idAlarm,1);
                        Cursor cursor = dbManager.fetch(Integer.toString(idAlarm));
                        dbManager.close();
                        Alarm alarm = new Alarm(getApplicationContext(),idAlarm,cursor.getInt(1),cursor.getInt(2),cursor.getInt(3));
                        alarm.setAlarm();
                    } else {
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

                    Alarm.cancelAlarm(idAlarm,getApplicationContext());

                    DBManager dbManager = new DBManager(context);
                    dbManager.open();

                    dbManager.delete(idAlarm);
                    dbManager.close();

                    LinearLayout scrollLayout = (LinearLayout)findViewById(R.id.scrollLayout);

                   Intent intentConfiguration = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intentConfiguration);
                    finish();

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

            line = cursor.moveToNext();
        } while (line != false);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.displayMenu) {
            // Diplay activité avec le menu
            Intent i = new Intent(this.context, MenuActivity.class);
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }
}