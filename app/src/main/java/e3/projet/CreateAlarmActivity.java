package e3.projet;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ClipData;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Button;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import android.provider.AlarmClock;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap; // import the HashMap class


public class CreateAlarmActivity extends AppCompatActivity {
    private TimePicker picker;
    private TextView mTextView;
    private EditText mEditText;
    private String jours;
    private int timeHour;
    private int timeMinutes;
    private int nbOranges;
    private HashMap<String, Short> boolJours;
    private Short TRUE = new Short((short)1);
    private Short FALSE = new Short((short)0);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_alarm);
        picker = findViewById(R.id.timePicker);
        picker.setIs24HourView(true);

        // https://codinginflow.com/tutorials/android/alarmmanager
        mTextView = findViewById(R.id.textView2);
        mEditText =findViewById(R.id.nbOranges);


        initHashMap();

        Button buttonReturn = findViewById(R.id.buttonReturn);

        buttonReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }});
        Button buttonSubmit = findViewById(R.id.buttonSubmit);

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Récupération de l'heure et des minutes
                timeHour = picker.getCurrentHour();
                timeMinutes = picker.getCurrentMinute();
                nbOranges=0;
                nbOranges = Short.parseShort(mEditText.getText().toString());

                mTextView.setText("Heure : " + timeHour + ":" + timeMinutes +", " + nbOranges +" oranges \n");


                // Création de la tache programmée
                Alarm vAlarm = new Alarm(getApplicationContext(),timeHour,timeMinutes,nbOranges,boolJours);
                vAlarm.createAlarm();

                // Nécessaire sinon l'alarmClock n'est pas set, on change d'activité trop rapidement
                SystemClock.sleep(1000);


                Intent intentMain = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intentMain);
            }
        });
    }
    public void onCheckboxClicked(View view) {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();

        // Check which checkbox was clicked
        switch(view.getId()) {
            case R.id.checkbox_lundi:
                if (checked)
                    boolJours.put("MONDAY",TRUE);
                else
                    boolJours.put("MONDAY",FALSE);
                break;
            case R.id.checkbox_mardi:
                if (checked)
                    boolJours.put("TUESDAY",TRUE);
                else
                    boolJours.put("TUESDAY",FALSE);
                break;
            case R.id.checkbox_mercredi:
                if (checked)
                    boolJours.put("WEDNESDAY",TRUE);
                else
                    boolJours.put("WEDNESDAY",FALSE);
                break;
            case R.id.checkbox_jeudi:
                if (checked)
                    boolJours.put("THURSDAY",TRUE);
                else
                    boolJours.put("THURSDAY",FALSE);
                break;
            case R.id.checkbox_vendredi:
                if (checked)
                    boolJours.put("FRIDAY",TRUE);
                else
                    boolJours.put("FRIDAY",FALSE);
                break;
            case R.id.checkbox_samedi:
                if (checked)
                    boolJours.put("SATURDAY",TRUE);
                else
                    boolJours.put("SATURDAY",FALSE);
                break;
            case R.id.checkbox_dimanche:
                if (checked)
                    boolJours.put("SUNDAY",TRUE);
                else
                    boolJours.put("SUNDAY",FALSE);
                break;
        }

    }

    private void initHashMap() {
        boolJours =new HashMap<String, Short>();
        boolJours.put("MONDAY",FALSE);
        boolJours.put("TUESDAY",FALSE);
        boolJours.put("WEDNESDAY",FALSE);
        boolJours.put("THURSDAY",FALSE);
        boolJours.put("FRIDAY",FALSE);
        boolJours.put("SATURDAY",FALSE);
        boolJours.put("SUNDAY",FALSE);
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
            Intent i = new Intent(getApplicationContext(), MenuActivity.class);
            startActivity(i);

        }
        return super.onOptionsItemSelected(item);
    }

}