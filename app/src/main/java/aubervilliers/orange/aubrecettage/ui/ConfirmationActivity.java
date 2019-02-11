package aubervilliers.orange.aubrecettage.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;

import aubervilliers.orange.aubrecettage.R;

public class ConfirmationActivity extends AppCompatActivity {


    public final static String EXTRA_FILE_KEY = "extra-file-key";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_confirmation);
        TextView validExport = findViewById(R.id.confirmationExport);
        Button restartApp = findViewById(R.id.restartApp);
        Button exitApp = findViewById(R.id.exit);

        final Intent intent = getIntent();
        int exportState = intent.getIntExtra(ExportActivity.EXTRA_STATE_KEY, ExportActivity.EXPORT_STATE_MAIL);
        String path = intent.getStringExtra("path");
        File file = (File) intent.getSerializableExtra(ConfirmationActivity.EXTRA_FILE_KEY);

        if (exportState == ExportActivity.EXPORT_STATE_MAIL) {
            validExport.setText("Mail envoyé");
            file.delete();
        } else if (exportState == ExportActivity.EXPORT_STATE_PDF) {
            validExport.setText("Fichier PDF enregistré sous " + path);
        } else if (exportState == ExportActivity.EXPORT_STATE_MAIL_PDF) {
            validExport.setText("Mail envoyé & Fichier PDF enregistré sous " + path);
        }

        restartApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
        });

        exitApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }
}
