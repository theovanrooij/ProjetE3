package e3.projet;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

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




        //textView.setText(user+"@"+ip+" : "+password+" | "+idAlarm+" intent | bdd | ");

        Calendar calendar = Calendar.getInstance();
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE), 0);
        //textView.setText(calendar.get(Calendar.YEAR) + "/"+calendar.get(Calendar.MONTH)+"/"+calendar.get(Calendar.DAY_OF_MONTH)+" "+calendar.get(Calendar.HOUR_OF_DAY)+":"+calendar.get(Calendar.MINUTE));

        Calendar sCalendar = Calendar.getInstance();
        String dayLongName = sCalendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());
       // textView.setText(dayLongName);

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
            textViewBottom.setText("Lundi, Mardi, Mercredi");
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
            
            switchButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // your code
                    int idAlarm = v.getId();
                    Log.d("MyAlarmBelal", "Set : "+idAlarm);
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
                    Log.d("MyAlarmBelal", "Delete : "+idAlarm);
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

}