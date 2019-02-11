package aubervilliers.orange.aubrecettage.ui.infostickets;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import aubervilliers.orange.aubrecettage.R;
import aubervilliers.orange.aubrecettage.ui.fiches.CablageActivity;

public class InfosCablageActivity extends AppCompatActivity {

    private EditText mNTicket;
    private EditText mNomSalle;
    private EditText mCallBaie;
    private EditText mNumEquip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_infos_cablage);

        mNTicket = findViewById(R.id.nTicket);
        mNomSalle = findViewById(R.id.nomSalle);
        mCallBaie = findViewById(R.id.callBaie);
        mNumEquip = findViewById(R.id.numEquip);

        Button mButtonStart = findViewById(R.id.start);

        mButtonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String numeroTicket = mNTicket.getText().toString();
                String nomSalle = mNomSalle.getText().toString();
                String callBaie = mCallBaie.getText().toString();
                String numEquip = mNumEquip.getText().toString();

                Intent intent = new Intent(InfosCablageActivity.this, CablageActivity.class);
                intent.putExtra("nTicket", numeroTicket);
                intent.putExtra("nomSalle", nomSalle);
                intent.putExtra("callBaie", callBaie);
                intent.putExtra("numEquip", numEquip);

                startActivity(intent);
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
}

