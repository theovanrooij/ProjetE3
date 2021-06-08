package e3.projet;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.Button;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class ConfigurationActivity extends Activity implements View.OnClickListener {

    private String ip;
    private String user;
    private String password;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle("");
        setContentView(R.layout.activity_configuration);

        pref = getApplicationContext().getSharedPreferences("SSH", 0); // 0 - for private mode

        Button buttonRetourMain = findViewById(R.id.buttonReturn);

        buttonRetourMain.setOnClickListener(this);

        Button buttonSubmit = findViewById(R.id.buttonSubmit);

        buttonSubmit.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonReturn:
                // Retour sur la main activity
                finish();
                break;
            case R.id.buttonSubmit:
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
                finish();
                break;
        }
    }

}