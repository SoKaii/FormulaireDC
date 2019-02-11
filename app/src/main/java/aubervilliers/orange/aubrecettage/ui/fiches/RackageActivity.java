package aubervilliers.orange.aubrecettage.ui.fiches;

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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import aubervilliers.orange.aubrecettage.R;
import aubervilliers.orange.aubrecettage.model.Question;
import aubervilliers.orange.aubrecettage.model.Recette;
import aubervilliers.orange.aubrecettage.ui.ExportActivity;
import aubervilliers.orange.aubrecettage.ui.RecapActivity;

public class RackageActivity extends AppCompatActivity {

    private static final String TAG = "CablageActivity";

    private Recette recette;
    private String numTicket = "";
    private String nomSalle = "";
    private String callBaie = "";
    private String numEquip = "";

    private LinearLayout ll;
    private List<String> titleList = new ArrayList<>();
    private List<RadioButton> yesBtList = new ArrayList<>();
    private List<RadioButton> noBtList = new ArrayList<>();
    private List<EditText> commentList = new ArrayList<>();
    private List<Boolean> isOpenQuestionList = new ArrayList<>();
    private List<Boolean> isObligatoryQuestionList = new ArrayList<>();
    private List<Question> questions = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_rackage);

        Button buttonNext = findViewById(R.id.saveNext);

        Intent intent = getIntent();

        if (intent != null) {

            if (intent.hasExtra("nTicket")) {
                numTicket = intent.getStringExtra("nTicket");
                nomSalle = intent.getStringExtra("nomSalle");
                callBaie = intent.getStringExtra("callBaie");
                numEquip = intent.getStringExtra("numEquip");

            }

            TextView nTicket = findViewById(R.id.numTicket);
            nTicket.setText(numTicket);

            TextView nSalle = findViewById(R.id.nomSalle);
            nSalle.setText(nomSalle);

            TextView cBaie = findViewById(R.id.callBaie);
            cBaie.setText(callBaie);

            TextView nEquip = findViewById(R.id.numEquip);
            nEquip.setText(numEquip);
        }

        ll = findViewById(R.id.questions);
        addQuestion("L'équipement est bien présent dans la 26E/Transplan ?", true, true);
        addQuestion("Les commentaire 'UO' & '#' sont présents dans le ticket ?", true, true);
        addQuestion("Les informations équipement/constructeur/modèle sont en cohérence avec la demande ?", true, true);
        addQuestion("La localisation Salle/Baie/U est en cohérence avec la demande ?", true, true);
        addQuestion("La présence de l’étiquetage 26E/ hostname /n° serie / et son emplacement est correct ?", true, true);
        addQuestion("Le n°26E/hostname/n°serie  des équipements décrits dans le document de référence est en cohérence avec la demande ?", true, true);
        addQuestion("La redondance courant fort est correctement réalisée ?", true, true);
        addQuestion("Les câbles CFO sont-ils correctement installés ?", true, true);
        addQuestion("Les rails et vis sont correctement installés ?", true, true);
        addQuestion("Le rackage respecte t-il le sens de soufflage  en cohérence  avec les consignes aéraulique ?",true,true);
        addQuestion("Autres anomalies constatées : ", false, false);

        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getInfos();
                boolean allAnsweredQuestions = true;
                for (Question question : questions) {
                    if (question.isObligatoryQuestion() && !question.isButtonYesSelected() && !question.isButtonNoSelected()) {
                        Log.v(TAG, "Question non répondu: " + question.getQuestionLabel());
                        allAnsweredQuestions = false;
                        break;
                    }
                }
                if (!allAnsweredQuestions) {
                    AlertDialog.Builder notAnswered = new AlertDialog.Builder(RackageActivity.this);
                    notAnswered.setMessage("Vous n'avez pas complété toutes les questions")
                            .setPositiveButton("FERMER", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setTitle("Erreur")
                            .create();
                    notAnswered.show();
                } else {
                    Intent intent1 = new Intent(RackageActivity.this, RecapActivity.class);
                    intent1.putExtra(ExportActivity.EXTRA_RECETTE_KEY, recette);
                    startActivity(intent1);
                }

            }
        });

    }

    private void addQuestion(String title, boolean hasRadioButtons, boolean obligatoryQuestion) {
        View questionLayout = View.inflate(this, R.layout.layout_question, null);
        titleList.add(title);
        TextView tv = questionLayout.findViewById(R.id.questionTitle);
        tv.setText(title);
        yesBtList.add((RadioButton) questionLayout.findViewById(R.id.questionYes));
        noBtList.add((RadioButton) questionLayout.findViewById(R.id.questionNo));
        commentList.add((EditText) questionLayout.findViewById(R.id.questionComment));
        if (!hasRadioButtons) {
            questionLayout.findViewById(R.id.questionRadioGroup).setVisibility(View.GONE);
            questionLayout.findViewById(R.id.questionCommentTV).setVisibility(View.GONE);
        }
        isOpenQuestionList.add(!hasRadioButtons);
        isObligatoryQuestionList.add(obligatoryQuestion);
        ll.addView(questionLayout);
    }

    private void getInfos() {
        int index = 0;
        questions = new ArrayList<>();
        Log.v(TAG, "getInfos");
        for (String title : titleList) {
            Question question = new Question();
            question.setQuestionLabel(title);
            boolean openQuestion = isOpenQuestionList.get(index);
            boolean obligatoryQuestion = isObligatoryQuestionList.get(index);
            question.setOpenQuestion(openQuestion);
            question.setObligatoryQuestion(obligatoryQuestion);
            if (!openQuestion) {
                RadioButton yesButton = yesBtList.get(index);
                RadioButton noButton = noBtList.get(index);
                Log.v(TAG, "question: " + title + " button yes selected: " + yesButton.isChecked());
                question.setButtonYesSelected(yesButton.isChecked());
                question.setButtonNoSelected(noButton.isChecked());
            }
            EditText commentEt = commentList.get(index);
            question.setCommentary(commentEt.getText().toString());
            questions.add(question);
            index++;
        }

        recette = new Recette("Câblage simple", numTicket, nomSalle, callBaie, numEquip, questions);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.
                INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(this.getWindow().getDecorView().getRootView().getWindowToken(), 0);
        return true;
    }
}
