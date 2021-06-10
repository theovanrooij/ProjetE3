package e3.projet;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.os.Bundle;
import android.widget.EditText;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;


public class ManualLaunchActivity extends Activity implements OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle("");
        setContentView(R.layout.activty_manual_launch);

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
                Intent i = new Intent(getApplicationContext(), MenuActivity.class);
                startActivity(i);
                finish();
                break;

            case R.id.buttonSubmit:
                EditText editOranges = (EditText) findViewById(R.id.editOranges);
                String nbOranges = editOranges.getText().toString();

                Log.d("Projet",nbOranges);


                // Execution de la commande
                CommandRaspberry.sendCommandOrangeRaspberry(getApplicationContext(),nbOranges);

                Intent intentSuivi = new Intent(getApplicationContext(), SuiviPressageActivity.class);
                startActivity(intentSuivi);
                finish();
                break;
        }
    }

}