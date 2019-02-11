package aubervilliers.orange.aubrecettage.ui;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import aubervilliers.orange.aubrecettage.R;
import aubervilliers.orange.aubrecettage.model.Recap;
import aubervilliers.orange.aubrecettage.model.Recette;
import aubervilliers.orange.aubrecettage.ui.fiches.CablageActivity;

public class RecapActivity extends AppCompatActivity {

    private static final String TAG = "RecapActivity";

    private Recette recette;
    private Recap recap;

    private DatePickerDialog datePickerDialog;
    private RadioButton validYes;
    private RadioButton validNo;
    private  boolean radioButtonIsChecked = false;

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_recap);

        Button mSavePDF = findViewById(R.id.SavePDF);
        final Button dateRecette = findViewById(R.id.dateRecetteI);
        final EditText nCI2A = findViewById(R.id.nCI2A);
        final EditText refOrange = findViewById(R.id.refORange);
        validYes = findViewById(R.id.valid_yes);
        validNo = findViewById(R.id.valid_no);
        final Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);
        datePickerDialog = new DatePickerDialog(this, new MyDateSetListener(dateRecette), year, month, day);


        dateRecette.setText(R.string.click_to_add);
        Intent intent = getIntent();

        if (intent != null) {
            if (intent.hasExtra(ExportActivity.EXTRA_RECETTE_KEY))
                recette = (Recette) intent.getSerializableExtra(ExportActivity.EXTRA_RECETTE_KEY);
            Toast.makeText(this,
                    "Intent " + recette.getRecetteType() + " récupéré",
                    Toast.LENGTH_LONG).show();
        }

        dateRecette.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard(RecapActivity.this);
                datePickerDialog.show();
            }
        });


        mSavePDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RecapActivity.this, ExportActivity.class);

                recap = new Recap(nCI2A.getText().toString(), dateRecette.getText().toString(), refOrange.getText().toString());
                if (validYes.isChecked())
                {
                    recap.setValidOrange("OUI");
                    radioButtonIsChecked = true;
                }
                else if (validNo.isChecked())
                {
                    recap.setValidOrange("NON");
                    radioButtonIsChecked = true;
                }

                if (!radioButtonIsChecked) {
                    AlertDialog.Builder notAnswered = new AlertDialog.Builder(RecapActivity.this);
                    notAnswered.setMessage("Le champ obligatoire 'Validation' n'a pas été renseigné")
                            .setPositiveButton("FERMER", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setTitle("Erreur")
                            .create();
                    notAnswered.show();
                }
                else
                {
                    recette.setRecap(recap);
                    intent.putExtra(ExportActivity.EXTRA_RECETTE_KEY, recette);
                    startActivity(intent);
                }



            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.
                INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(this.getWindow().getDecorView().getRootView().getWindowToken(), 0);
        return true;
    }

    class MyDateSetListener implements DatePickerDialog.OnDateSetListener {

        private Button button;

        MyDateSetListener(Button button) {
            this.button = button;
        }

        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            button.setText(new StringBuilder().append(dayOfMonth).append("/")
                    .append(month + 1).append("/").append(year));
        }
    }
}