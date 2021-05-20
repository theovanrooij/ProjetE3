package e3.projet;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.Button;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class ConfigurationActivity extends AppCompatActivity {

    private String ip;
    private String user;
    private String password;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration);

        Button buttonRetourMain = findViewById(R.id.buttonReturn);

        pref = getApplicationContext().getSharedPreferences("SSH", 0); // 0 - for private mode

        buttonRetourMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Retour sur la main activity
                Intent intentReturn = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intentReturn);

            }
        });

        Button buttonSubmit = findViewById(R.id.buttonSubmit);

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editIP = (EditText) findViewById(R.id.editIP);
                ip = editIP.getText().toString();

                EditText editUser = (EditText) findViewById(R.id.editUtilisateur);
                user = editUser.getText().toString();

                EditText editPassword = (EditText) findViewById(R.id.editPassword);
                password = editPassword.getText().toString();

                editor = pref.edit();

                editor.putString("ip",ip);
                editor.putString("user",user);
                editor.putString("password",password);
                editor.apply();

                Intent intentMain = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intentMain);
            }
        });

        ip = pref.getString("ip", null); // getting String
        user = pref.getString("user", null); // getting String
        password = pref.getString("password", null); // getting String

        TextView inputIP = (TextView) findViewById(R.id.editIP);
        inputIP.setText(ip);

        TextView inputUser = (TextView) findViewById(R.id.editUtilisateur);
        inputUser.setText(user);

        TextView inputPassword = (TextView) findViewById(R.id.editPassword);
        inputPassword.setText(password);

    }
}