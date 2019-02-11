package aubervilliers.orange.aubrecettage.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import aubervilliers.orange.aubrecettage.R;
import aubervilliers.orange.aubrecettage.ui.infostickets.InfosCablageActivity;
import aubervilliers.orange.aubrecettage.ui.infostickets.InfosProcessJ4Activity;
import aubervilliers.orange.aubrecettage.ui.infostickets.InfosJ4Activity;
import aubervilliers.orange.aubrecettage.ui.infostickets.InfosRackageActivity;

public class ChoixRecetteActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_choix_formulaire);

        Button mbuttonCableSimple = findViewById(R.id.buttonCableSimple);
        Button mbuttonCableComplexe = findViewById(R.id.buttonCableComplexe);
        Button mbuttonRackage = findViewById(R.id.buttonRackage);
        Button mbuttonJ4 = findViewById(R.id.buttonJ4);

        mbuttonCableSimple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChoixRecetteActivity.this, InfosProcessJ4Activity.class);
                startActivity(intent);
            }
        });

        mbuttonRackage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChoixRecetteActivity.this, InfosRackageActivity.class);
                startActivity(intent);
            }
        });

        mbuttonJ4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChoixRecetteActivity.this, InfosJ4Activity.class);
                startActivity(intent);
            }
        });

        mbuttonCableComplexe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChoixRecetteActivity.this, InfosCablageActivity.class);
                startActivity(intent);
            }
        });

    }
}

