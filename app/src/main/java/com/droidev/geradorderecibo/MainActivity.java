package com.droidev.geradorderecibo;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private EditText editTextName;
    private EditText editTextValue;
    private EditText editTextValorPorExtenso;
    private EditText editTextDate;
    private CheckBox checkBox1;
    private CheckBox checkBox2;
    private CheckBox checkBox3;
    private CheckBox checkBox4;
    private CheckBox checkBox5;
    private CheckBox checkBox6;
    private EditText editTextAdditionalInfo;

    private TinyDB tinyDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PDFGeneration pdfGeneration = new PDFGeneration();
        Miscs miscs = new Miscs();

        tinyDB = new TinyDB(MainActivity.this);

        editTextName = findViewById(R.id.editTextName);
        editTextValue = findViewById(R.id.editTextValue);
        editTextValorPorExtenso = findViewById(R.id.editTextValorPorExtenso);
        editTextDate = findViewById(R.id.editTextDate);
        checkBox1 = findViewById(R.id.checkBox1);
        checkBox2 = findViewById(R.id.checkBox2);
        checkBox3 = findViewById(R.id.checkBox3);
        checkBox4 = findViewById(R.id.checkBox4);
        checkBox5 = findViewById(R.id.checkBox5);
        checkBox6 = findViewById(R.id.checkBox6);

        editTextAdditionalInfo = findViewById(R.id.editTextAdditionalInfo);

        Button buttonGeneratePDF = findViewById(R.id.buttonGeneratePDF);

        editTextValue.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

        buttonGeneratePDF.setOnClickListener(v -> pdfGeneration.generatePDF(MainActivity.this,
                editTextName,
                editTextValue,
                editTextValorPorExtenso,
                editTextDate,
                editTextAdditionalInfo,
                checkBox1,
                checkBox2,
                checkBox3,
                checkBox4,
                checkBox5,
                checkBox6));

        Calendar selectedDate = Calendar.getInstance();

        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, dayOfMonth) -> {

            selectedDate.set(Calendar.YEAR, year);
            selectedDate.set(Calendar.MONTH, month);
            selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            // Format the selected date and display it
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            String formattedDate = dateFormat.format(selectedDate.getTime());
            editTextDate.setText(formattedDate);
        };

        editTextDate.setOnClickListener(v -> {

            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(editTextDate.getWindowToken(), 0);

            int year = selectedDate.get(Calendar.YEAR);
            int month = selectedDate.get(Calendar.MONTH);
            int dayOfMonth = selectedDate.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this, dateSetListener, year, month, dayOfMonth);
            datePickerDialog.show();
        });

        editTextDate.setFocusable(false);
        editTextDate.setClickable(true);

        miscs.deleteCache(MainActivity.this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_activity, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.qrcode) {

            final int[] selectedRadioButton = {-1};

            // Create AlertDialog
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(false);
            builder.setTitle("Escolha o banco");

            String[] radioButtonOptions = {"Banco do Brasil", "Caixa Econômica Federal", "Nubank"};

            builder.setSingleChoiceItems(radioButtonOptions, selectedRadioButton[0],
                    (dialog, which) -> selectedRadioButton[0] = which);


            builder.setPositiveButton("Abrir", null);
            builder.setNegativeButton("Fechar", (dialog, which) -> {

                dialog.dismiss();
            });

            final AlertDialog dialog = builder.create();
            dialog.setOnShowListener(dialogInterface -> {

                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(view -> {
                    if (selectedRadioButton[0] != -1) {

                        switch (selectedRadioButton[0]) {
                            case 0:

                                abrirIntentPIX("PIXQRCodeBB", "PIXChaveBB", "Banco do Brasil");

                                break;
                            case 1:

                                abrirIntentPIX("PIXQRCodeCaixa", "PIXChaveCaixa", "Caixa Econômica Federal");

                                break;
                            case 2:

                                abrirIntentPIX("PIXQRCodeNubank", "PIXChaveNubank", "Nubank");

                                break;
                        }
                    } else {
                        // No radio button selected
                        Toast.makeText(MainActivity.this, "Você deve selecionar um dos três bancos.", Toast.LENGTH_SHORT).show();
                    }
                });
            });

            dialog.show();

        }

        if (id == R.id.cadastrarchave) {

            Intent myIntent = new Intent(MainActivity.this, PIXSalvarActivity.class);
            startActivity(myIntent);
        }

        return false;
    }

    public void abrirIntentPIX(String PIXQRCodeBanco, String PIXChaveBanco, String bancoNome) {

        String string1 = tinyDB.getString(PIXQRCodeBanco);
        String string2 = tinyDB.getString(PIXChaveBanco);

        if (!string1.equals("")) {
            Intent myIntent = new Intent(MainActivity.this, QRCodeActivity.class);
            myIntent.putExtra("qrcode", string1);
            myIntent.putExtra("chave", string2);
            myIntent.putExtra("nome", bancoNome);
            startActivity(myIntent);
        } else {
            Toast.makeText(MainActivity.this, "Nenhuma QR Code PIX para esse banco.", Toast.LENGTH_SHORT).show();
        }

    }

}
