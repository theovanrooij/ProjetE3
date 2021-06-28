package e3.projet;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.view.View.OnClickListener;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;


public class SuiviPressageActivity extends Activity implements OnClickListener {

    Timer timer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("");
        setContentView(R.layout.activty_suivi);

        Button buttonRetourMain = findViewById(R.id.buttonReturn);

        buttonRetourMain.setOnClickListener(this);

        Button buttonActualiser = findViewById(R.id.buttonActualiser);

        buttonActualiser.setOnClickListener(this);

        this.updateStatut();
    }

    private void updateStatut(){
        JSONObject JSON = CommandRaspberry.getJSON(getApplicationContext());

        int nbActuel =0;
        int nbVoulu = 0;
        String statut = "";

        try {
            JSON = JSON.getJSONObject("suivi");
            nbActuel = JSON.getInt("nbActuel");
            nbVoulu = JSON.getInt("nbVoulu");
            statut = JSON.getString("statut");
        } catch (Exception e) {
            Log.d("ProjetE3", "Erreur de lecture du fichier JSON : " + e.getMessage());
        }


        if (JSON != null) {
            float tauxFloat = ((float) nbActuel /(float) nbVoulu) * 100;
            int taux = (int) tauxFloat;
            ProgressBar remplissage = (ProgressBar) findViewById(R.id.indeterminateBar);
            if (Build.VERSION.SDK_INT >= 24) {
                remplissage.setProgress(taux, true);
            } else {
                remplissage.setProgress(taux);
            }
            TextView remplissageText = (TextView) findViewById(R.id.textProgress);
            remplissageText.setText("Avancement global : " + taux + "%\n" + nbActuel + " oranges sur " + nbVoulu + " pressees\n Statut de l'orange en cours : "+statut);
        } else {
            TextView remplissageText = (TextView) findViewById(R.id.textProgress);
            remplissageText.setText("Aucun pressage en cours");

        }
    }

    @Override
    public void onClick(View v) {
        // Retour sur la main activity

        switch (v.getId()) {
            case R.id.buttonReturn:
                // Retour sur la main activity
                finish();
                break;
            case R.id.buttonActualiser:
                this.updateStatut();
                break;
        }

    }

}