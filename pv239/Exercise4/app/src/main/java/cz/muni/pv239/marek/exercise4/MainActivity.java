package cz.muni.pv239.marek.exercise4;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button openCalcButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prepareUi();
    }

    private void prepareUi() {
        openCalcButton = (Button) findViewById(R.id.openCalcActivityButton);

        openCalcButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), CalcActivity.class);
                startActivity(intent);
            }
        });

        // if the build is not the "full" flavor, the "calculator" button will be disabled
        openCalcButton.setEnabled(BuildConfig.FLAVOR.equals("full"));
    }
}
