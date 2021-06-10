package e3.projet;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.view.View.OnClickListener;


public class SuiviPressageActivity extends Activity implements OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("");
        setContentView(R.layout.activty_suivi);

        Button buttonRetourMain = findViewById(R.id.buttonReturn);

        buttonRetourMain.setOnClickListener(this);

        String remplissageInfo = CommandRaspberry.readFile(getApplicationContext(),"suivi");

        Log.d("ProjetE3", "Remplissage"+remplissageInfo);
        if (remplissageInfo != null) {
            String[] splitLine = remplissageInfo.split(":");
            Log.d("ProjetE3", splitLine[0] + " / " + splitLine[1]);
            float tauxFloat = Float.parseFloat(splitLine[1]) / Float.parseFloat(splitLine[0]) * 100;
            int taux = (int) tauxFloat;
            Log.d("ProjetE3", "Taux : " + taux);
            ProgressBar remplissage = (ProgressBar) findViewById(R.id.indeterminateBar);
            if (Build.VERSION.SDK_INT >= 24) {
                remplissage.setProgress(taux, true);
            }

            TextView remplissageText = (TextView) findViewById(R.id.textProgress);
            remplissageText.setText("Avancement global : " + taux + "%\n" + splitLine[1] + " oranges sur " + splitLine[0] + " pressees\n Statut de l'orange en cours : "+splitLine[2]);
        } else {
            TextView remplissageText = (TextView) findViewById(R.id.textProgress);
            remplissageText.setText("Aucun pressage en cours");

        }
    }

    @Override
    public void onClick(View v) {
        // Retour sur la main activity
        finish();

    }

}