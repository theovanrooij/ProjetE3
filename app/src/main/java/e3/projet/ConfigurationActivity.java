package e3.projet;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.Button;
import android.os.Bundle;
import android.widget.TextView;


public class ConfigurationActivity extends Activity implements View.OnClickListener {


    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    private TextView ip;
    private TextView user ;
    private TextView password ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle("");
        setContentView(R.layout.activity_configuration);

        Button buttonRetourMain = findViewById(R.id.buttonReturn);

        buttonRetourMain.setOnClickListener(this);

        Button buttonSubmit = findViewById(R.id.buttonSubmit);

        buttonSubmit.setOnClickListener(this);

        ip = (TextView) findViewById(R.id.editIP);
        user = (TextView) findViewById(R.id.editUtilisateur);
        password = (TextView) findViewById(R.id.editPassword);

        pref = getApplicationContext().getSharedPreferences("SSH", 0); // 0 - for private mode
        String ipSave = pref.getString("ip", null); // getting String
        String userSave = pref.getString("user", null); // getting String
        String passwordSave = pref.getString("password", null); // getting String

        ip.setText(ipSave);
        user.setText(userSave);
        password.setText(passwordSave);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonReturn:
                // Retour sur la main activity
                finish();
                break;
            case R.id.buttonSubmit:

                editor = pref.edit();
                String newIp = ip.getText().toString();
                String newUser = user.getText().toString();
                String newPassword = password.getText().toString();
                editor.putString("ip",newIp);
                editor.putString("user",newUser);
                editor.putString("password",newPassword);

                boolean connexionBoolean = CommandRaspberry.sendCommandRaspberry(getApplicationContext(),"ls");

                if (connexionBoolean) {
                    // return true si il a pu executer la commande, ie si la connexion est fonctionnelle
                    editor.putBoolean("connexion",true);
                } else {
                    editor.putBoolean("connexion",false);
                }
                editor.apply();
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
                finish();
                break;
        }
    }

}