package e3.projet;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.os.Bundle;
import android.view.View.OnClickListener;


public class menu extends Activity implements OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle("Menu");

        setContentView(R.layout.menu_layout);

        Button buttonLaunch = findViewById(R.id.buttonLaunch);
        Button buttonConfiguration = findViewById(R.id.buttonConfiguration);
        Button buttonAjoutAlarme = findViewById(R.id.buttonAjoutAlarme);
        Button buttonSuivi = findViewById(R.id.buttonSuivi);


        buttonLaunch.setOnClickListener(this);
        buttonConfiguration.setOnClickListener(this);
        buttonAjoutAlarme.setOnClickListener(this);
        buttonSuivi.setOnClickListener(this);

        Log.d("ProjetE3","Tout est set");

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonConfiguration:
                Intent intentConfiguration = new Intent(getApplicationContext(), ConfigurationActivity.class);
                startActivity(intentConfiguration);
                break;
            case R.id.buttonLaunch:
                // Retour sur la main activity
                Intent intentLaunch = new Intent(getApplicationContext(), manualLaunchActivity.class);
                startActivity(intentLaunch);
                break;
            case R.id.buttonAjoutAlarme:
                Intent intentAlarm = new Intent(getApplicationContext(), CreateAlarmActivity.class);
                startActivity(intentAlarm);
                break;
            case R.id.buttonSuivi:
                Intent intentSuivi = new Intent(getApplicationContext(), SuiviPressage.class);
                startActivity(intentSuivi);
                break;
        }
    }

}