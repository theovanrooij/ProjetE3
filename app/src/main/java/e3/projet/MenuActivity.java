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


public class MenuActivity extends Activity implements OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle("Menu");

        setContentView(R.layout.activty_menu);

        Button buttonLaunch = findViewById(R.id.buttonLaunch);
        Button buttonConfiguration = findViewById(R.id.buttonConfiguration);
        Button buttonAjoutAlarme = findViewById(R.id.buttonAjoutAlarme);
        Button buttonSuivi = findViewById(R.id.buttonSuivi);

        buttonLaunch.setOnClickListener(this);
        buttonConfiguration.setOnClickListener(this);
        buttonAjoutAlarme.setOnClickListener(this);
        buttonSuivi.setOnClickListener(this);

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
                Intent intentLaunch = new Intent(getApplicationContext(), ManualLaunchActivity.class);
                startActivity(intentLaunch);
                break;
            case R.id.buttonAjoutAlarme:
                Intent intentAlarm = new Intent(getApplicationContext(), CreateAlarmActivity.class);
                startActivity(intentAlarm);
                finish();
                break;
            case R.id.buttonSuivi:
                Intent intentSuivi = new Intent(getApplicationContext(), SuiviPressageActivity.class);
                startActivity(intentSuivi);
                break;
        }
    }

}