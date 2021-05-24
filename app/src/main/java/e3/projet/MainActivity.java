package e3.projet;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.provider.AlarmClock;
import android.view.View;
import android.widget.Button;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button buttonAddAlarm = findViewById(R.id.buttonAjoutAlarme);

        buttonAddAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CreateAlarmActivity.class);
                startActivity(intent);
            }
        });

        Button buttonConfiguration = findViewById(R.id.buttonConfiguration);

        buttonConfiguration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ConfigurationActivity.class);
                startActivity(intent);
            }
        });

        TextView textView = (TextView) findViewById(R.id.text);
        SharedPreferences pref = getApplicationContext().getSharedPreferences("SSH", 0); // 0 - for private mode
        String ip = pref.getString("ip", null); // getting String
        String user = pref.getString("user", null); // getting String
        String password = pref.getString("password", null); // getting String

        String idAlarm = getIntent().getStringExtra("EXTRA_ID_ALARM");



        // Récupération des datas dans la base de données :
        DBManager dbManager = new DBManager(this);
        dbManager.open();

        Cursor cursor = dbManager.fetch("4");

        dbManager.close();



        textView.setText(user+"@"+ip+" : "+password+" | "+idAlarm+" intent | bdd | ");

        Calendar calendar = Calendar.getInstance();
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE), 0);
        textView.setText(calendar.get(Calendar.YEAR) + "/"+calendar.get(Calendar.MONTH)+"/"+calendar.get(Calendar.DAY_OF_MONTH)+" "+calendar.get(Calendar.HOUR_OF_DAY)+":"+calendar.get(Calendar.MINUTE));

        Calendar sCalendar = Calendar.getInstance();
        String dayLongName = sCalendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());
        textView.setText(dayLongName);

    }
}