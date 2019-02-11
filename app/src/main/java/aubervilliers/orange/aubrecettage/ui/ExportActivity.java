package aubervilliers.orange.aubrecettage.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Image;
import com.itextpdf.text.BaseColor;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import aubervilliers.orange.aubrecettage.R;
import aubervilliers.orange.aubrecettage.model.Question;
import aubervilliers.orange.aubrecettage.model.Recette;

public class ExportActivity extends Activity {

    public static final String TAG = "ExportActivity";
    public static final String EXTRA_RECETTE_KEY = "extra-recette-key";
    public static final String EXTRA_STATE_KEY = "state_key";
    public static final int EXPORT_STATE_MAIL = 1;
    public static final int EXPORT_STATE_PDF = 2;
    public static final int EXPORT_STATE_MAIL_PDF = 3;
    static final int PICK_CONTACT_REQUEST = 1;
    Document document = new Document();
    private String pdfFileName;
    private String objetMail;
    private String mailRecipient;
    private Recette recette;
    private EditText mailObject;
    private EditText mailTo;
    private EditText editText;
    private LinearLayout linearMail;
    private CheckBox cbSave;
    private CheckBox cbSend;
    private File file;
    private int extraState = 1;

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_export);

        cbSave = findViewById(R.id.CBSave);
        cbSend = findViewById(R.id.CBSend);
        linearMail = findViewById(R.id.linearSend);
        mailObject = findViewById(R.id.mailObject);
        mailTo = findViewById(R.id.mailTo);
        editText = findViewById(R.id.nomFichier);
        final View exportButton = findViewById(R.id.exportButton);
        exportButton.setVisibility(View.INVISIBLE);

        final Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra(EXTRA_RECETTE_KEY))
                recette = (Recette) intent.getSerializableExtra(EXTRA_RECETTE_KEY);
            Toast.makeText(this,
                    "Intent " + recette.getQuestion1().getCommentary() + " récupéré",
                    Toast.LENGTH_LONG).show();
        }
        cbSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cbSend.isChecked())
                    linearMail.setVisibility(View.VISIBLE);
                else
                    linearMail.setVisibility((View.GONE));

                if (cbSave.isChecked() || cbSend.isChecked())
                    exportButton.setVisibility(View.VISIBLE);
                else
                    exportButton.setVisibility(View.GONE);
            }
        });

        cbSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cbSave.isChecked() || cbSend.isChecked())
                    exportButton.setVisibility(View.VISIBLE);
                else
                    exportButton.setVisibility(View.GONE);
            }
        });

        exportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isStoragePermissionGranted())
                    exportPDFAndSendEmailIfNecessary();

            }
        });
    }

    private void exportPDFAndSendEmailIfNecessary() {
        objetMail = mailObject.getText().toString();
        mailRecipient = mailTo.getText().toString();
        String fileName = editText.getText().toString();
        pdfFileName = Environment.getExternalStorageDirectory() + "/" + fileName + ".pdf";
        if (cbSend.isChecked()) {
            extraState = EXPORT_STATE_MAIL;
            exportPDF();
            sendMail();
        } else if (cbSave.isChecked()) {
            extraState = EXPORT_STATE_PDF;
            exportPDF();

            Intent confirmation = new Intent(ExportActivity.this, ConfirmationActivity.class);
            confirmation.putExtra(EXTRA_STATE_KEY, extraState);
            confirmation.putExtra(ConfirmationActivity.EXTRA_FILE_KEY, file);
            confirmation.putExtra("path", pdfFileName);
            startActivity(confirmation);
        } else if (!cbSave.isChecked() && !cbSend.isChecked()) {
            AlertDialog.Builder cbEmpty = new AlertDialog.Builder(ExportActivity.this);
            cbEmpty.setMessage("Aucun choix d'exportation détecté")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setTitle("Erreur")
                    .create();
            cbEmpty.show();
        }
    }

    public void exportPDF() {
        try {
            PdfWriter.getInstance(document, new FileOutputStream(pdfFileName));
            int index = 1;
            boolean spaceQuestion;
            int compteurYes = 0;
            int compteurNo = 0;
            document.open();
            document.addAuthor("AubRecettage");
            document.addCreator("AubRecettage");
            BaseColor orange = new BaseColor(255,102,0);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            Bitmap bitmap = BitmapFactory.decodeResource(getBaseContext().getResources(), R.drawable.littleorange);
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream);
            Image img = Image.getInstance(stream.toByteArray());
            img.scaleAbsolute(50,50);
            img.setAlignment(Image.ALIGN_RIGHT);

            Paragraph titleParagraph = new Paragraph();
            Chunk title = new Chunk("Recette du ticket n°" + recette.getTicketNumber(),
                    FontFactory.getFont(FontFactory.TIMES_BOLD, 18,orange));
            title.setUnderline(0.2f, -2f);
            titleParagraph.add(title);
            titleParagraph.add("\n\n");
            titleParagraph.setAlignment(Element.ALIGN_CENTER);

            PdfPTable tableTitle = new PdfPTable(3);
            tableTitle.setWidthPercentage(100);
            tableTitle.setWidths(new int[]{15,70,15});

            PdfPCell logo2Cell = new PdfPCell();
            logo2Cell.setBorderColor(BaseColor.WHITE);

            PdfPCell titleCell = new PdfPCell();
            titleCell.setMinimumHeight(80);
            titleCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            titleCell.addElement(titleParagraph);
            titleCell.setBorderColor(BaseColor.WHITE);

            PdfPCell logoCell = new PdfPCell();
            logoCell.setVerticalAlignment(Element.ALIGN_RIGHT);
            logoCell.addElement(img);
            logoCell.setBorderColor(BaseColor.WHITE);

            tableTitle.addCell(logo2Cell);
            tableTitle.addCell(titleCell);
            tableTitle.addCell(logoCell);

            document.add(tableTitle);
            
            //ca ne marche pas !!!!
            //String imagepath = "file:///app/res/drawable/logoorange.jpg";
            //Image image = Image.getInstance(imagepath);
            //Chunk c = new Chunk(image, 0, -24);
            //titleParagraph.add(c);

            new Font(Font.FontFamily.TIMES_ROMAN, 12);
            Font intituler = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD,orange);
            Paragraph infosTicketParagraph = new Paragraph();
            Chunk numeroTicket = new Chunk("Numéro du ticket : ", intituler);
            Chunk numeroTicketInfo = new Chunk(recette.getTicketNumber() + "\n" );
            Chunk nomSalle = new Chunk("Nom de la salle : ", intituler);
            Chunk nomSalleInfo = new Chunk(recette.getRoomName() + "\n" );
            Chunk CallepinageEquipement = new Chunk("Callepinage de l'équipement : ", intituler);
            Chunk CallepinageEquipementInfo = new Chunk( recette.getBaieCall() + "\n");
            Chunk numero26e = new Chunk("N° 26E de l'équipement : ", intituler);
            Chunk numero26eInfo = new Chunk( recette.getEquipNumber() + "\n\n");
            infosTicketParagraph.add(numeroTicket);
            infosTicketParagraph.add(numeroTicketInfo);
            infosTicketParagraph.add(nomSalle);
            infosTicketParagraph.add(nomSalleInfo);
            infosTicketParagraph.add(CallepinageEquipement);
            infosTicketParagraph.add(CallepinageEquipementInfo);
            infosTicketParagraph.add(numero26e);
            infosTicketParagraph.add(numero26eInfo);

            Paragraph recapParagraph = new Paragraph();
            Chunk ci2a = new Chunk("Numéro de CI2A du ticket : ", intituler);
            Chunk ci2aInfo = new Chunk( recette.getRecap().getCI2Anum() + "\n");
            Chunk validation = new Chunk("Validation orange : ", intituler);
            Chunk validationInfo = new Chunk(recette.getRecap().getValidOrange() + "\n");
            Chunk referent = new Chunk("Référent Orange : ", intituler);
            Chunk referentInfo = new Chunk(recette.getRecap().getReferentOrange() + "\n\n");
            recapParagraph.add(ci2a);
            recapParagraph.add(ci2aInfo);
            recapParagraph.add(validation);
            recapParagraph.add(validationInfo);
            recapParagraph.add(referent);
            recapParagraph.add(referentInfo);

            PdfPTable tableInfo = new PdfPTable(2);
            tableInfo.setWidthPercentage(100);

            PdfPCell infosTicketCell = new PdfPCell();
            infosTicketCell.setMinimumHeight(50);
            infosTicketCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            infosTicketCell.addElement(infosTicketParagraph);

            PdfPCell recapCell = new PdfPCell();
            recapCell.setMinimumHeight(50);
            recapCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            recapCell.addElement(recapParagraph);

            tableInfo.addCell(infosTicketCell);
            tableInfo.addCell(recapCell);
            document.add(tableInfo);
            Paragraph spaces = new Paragraph("\n");
            document.add(spaces);

            for (Question question : recette.getTabQuestions()) {
                if (!question.isOpenQuestion()) {
                    if (question.isButtonYesSelected()) {
                        compteurYes ++;
                    } else if (question.isButtonNoSelected()) {
                        compteurNo++;
                    }
                }
            }

            ByteArrayOutputStream checkStream = new ByteArrayOutputStream();
            Bitmap checkBitmap = BitmapFactory.decodeResource(getBaseContext().getResources(), R.drawable.tic);
            checkBitmap.compress(Bitmap.CompressFormat.JPEG,100,checkStream);
            Image imgCheck = Image.getInstance(checkStream.toByteArray());
            imgCheck.scaleAbsolute(20,20);
            imgCheck.setAlignment(Image.ALIGN_RIGHT);

            ByteArrayOutputStream uncheckStream = new ByteArrayOutputStream();
            Bitmap uncheckBitmap = BitmapFactory.decodeResource(getBaseContext().getResources(), R.drawable.no);
            uncheckBitmap.compress(Bitmap.CompressFormat.JPEG,100,uncheckStream);
            Image imgUncheck = Image.getInstance(uncheckStream.toByteArray());
            imgUncheck.scaleAbsolute(20,20);
            imgUncheck.setAlignment(Image.ALIGN_RIGHT);

            //check table
            PdfPTable tableCheck = new PdfPTable(2);
            tableCheck.setWidthPercentage(18);
            tableCheck.setWidths(new int[]{4,14});
            tableCheck.setHorizontalAlignment(50);
            Paragraph checkParagraphe = new Paragraph();
            Chunk checkText = new Chunk(": " + compteurYes);
            checkParagraphe.add(checkText);

            PdfPCell imgCheckCell = new PdfPCell();
            imgCheckCell.setMinimumHeight(30);
            imgCheckCell.setVerticalAlignment(Element.ALIGN_RIGHT);
            imgCheckCell.addElement(imgCheck);
            imgCheckCell.setBorderColor(BaseColor.WHITE);

            PdfPCell textCheckCell = new PdfPCell();
            textCheckCell.setMinimumHeight(30);
            textCheckCell.setVerticalAlignment(Element.ALIGN_LEFT);
            textCheckCell.addElement(checkParagraphe);
            textCheckCell.setBorderColor(BaseColor.WHITE);

            tableCheck.addCell(imgCheckCell);
            tableCheck.addCell(textCheckCell);
            document.add(tableCheck);

            //uncheck table
            PdfPTable tableUncheck = new PdfPTable(2);
            tableUncheck.setWidthPercentage(18);
            tableUncheck.setWidths(new int[]{4,14});
            tableUncheck.setHorizontalAlignment(50);
            Paragraph uncheckParagraphe = new Paragraph();
            Chunk uncheck = new Chunk(": " + compteurNo);
            uncheckParagraphe.add(uncheck);

            PdfPCell imgUncheckCell = new PdfPCell();
            imgUncheckCell.setMinimumHeight(30);
            imgUncheckCell.setVerticalAlignment(Element.ALIGN_RIGHT);
            imgUncheckCell.addElement(imgUncheck);
            imgUncheckCell.setBorderColor(BaseColor.WHITE);

            PdfPCell textUncheckCell = new PdfPCell();
            textUncheckCell.setMinimumHeight(30);
            textUncheckCell.setVerticalAlignment(Element.ALIGN_LEFT);
            textUncheckCell.addElement(uncheckParagraphe);
            textUncheckCell.setBorderColor(BaseColor.WHITE);

            tableUncheck.addCell(imgUncheckCell);
            tableUncheck.addCell(textUncheckCell);
            document.add(tableUncheck);
            document.add(spaces);

            for (Question question : recette.getTabQuestions()) {
                spaceQuestion = false;
                if (!question.isOpenQuestion()) {
                    if (question.isButtonYesSelected()) {
                        Chunk questions = new Chunk(index +") " + question.getQuestionLabel() + "");
                        questions.setUnderline(0.1f, -2f);
                        document.add(questions);
                        document.add(new Paragraph("Validation : Oui"));
                        spaceQuestion = true;
                    } else if (question.isButtonNoSelected()) {
                        Chunk questions = new Chunk(index +") " + question.getQuestionLabel() + "");
                        questions.setUnderline(0.1f, -2f);
                        document.add(questions);
                        document.add(new Paragraph("Validation : Non"));
                        spaceQuestion = true;
                    }
                }
                else
                {
                    if (!question.getCommentary().equals("")) {
                        Chunk questions = new Chunk(index +") " + question.getQuestionLabel() + "");
                        questions.setUnderline(0.1f, -2f);
                        document.add(questions);
                        spaceQuestion = true;
                    }
                }

                if (!question.getCommentary().equals("")) {
                    document.add(new Paragraph("Commentaire : " + question.getCommentary()));
                }
                if (Boolean.TRUE.equals(spaceQuestion))
                {
                    document.add(new Paragraph("\n"));
                }

                index ++;
            }
            Chunk synthese = new Chunk("Synthèse des commentaires");
            synthese.setUnderline(0.1f, -2f);
            document.add(synthese);

            index = 1;
            for (Question question : recette.getTabQuestions()) {
                if (!question.getCommentary().equals("")) {
                    document.add(new Paragraph(index + ") " + question.getCommentary()));
                }
                index++;
            }

            document.close();
            Toast.makeText(this, "File has been written to :" + pdfFileName,
                    Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Log.e("ExportPDF", e.getMessage(), e);
            Toast.makeText(this,
                    "Error, unable to write to file\n" + e.getMessage(),
                    Toast.LENGTH_LONG).show();

            AlertDialog.Builder notAnswered = new AlertDialog.Builder(ExportActivity.this);
            notAnswered.setMessage(e.getMessage())
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
    }

    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Permission is granted");
                return true;
            } else {
                Log.v(TAG, "Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG, "Permission is granted");
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.v(TAG, "Permission: " + permissions[0] + "was " + grantResults[0]);
            exportPDFAndSendEmailIfNecessary();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_CONTACT_REQUEST) {
            Intent confirmation = new Intent(ExportActivity.this, ConfirmationActivity.class);
            confirmation.putExtra(EXTRA_STATE_KEY, extraState);
            confirmation.putExtra(ConfirmationActivity.EXTRA_FILE_KEY, file);
            confirmation.putExtra("path", pdfFileName);
            startActivity(confirmation);
        }
    }

    private void sendMail() {
        if (cbSave.isChecked()) {
            extraState = EXPORT_STATE_MAIL_PDF;
        }

        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{mailRecipient});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, objetMail);
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Veuillez trouver en pièce jointe, le résultat de la recette au format PDF du ticket n° " + recette.getTicketNumber());

        file = new File(pdfFileName);
        if (!file.exists() || !file.canRead()) {
            Log.e(TAG, "The following file does not exist: " + pdfFileName);
            return;
        }
        Uri uri = Uri.fromFile(file);
        emailIntent.putExtra(Intent.EXTRA_STREAM, uri);
        Log.d("PICK_CONTACT_REQUEST1", "pick = " + PICK_CONTACT_REQUEST);
        startActivityForResult(Intent.createChooser(emailIntent, "Pick an Email provider"), PICK_CONTACT_REQUEST);
        Log.d("PICK_CONTACT_REQUEST2", "pick = " + PICK_CONTACT_REQUEST);
        hideKeyboard(ExportActivity.this);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.
                INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(this.getWindow().getDecorView().getRootView().getWindowToken(), 0);
        return true;
    }
}