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

        // Initialize UI components
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

        // Set OnClickListener for the Generate PDF button
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

        // Create a Calendar instance to store the selected date
        Calendar selectedDate = Calendar.getInstance();

        // Create a DatePickerDialog.OnDateSetListener to handle the selected date
        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, dayOfMonth) -> {
            // Update the selected date
            selectedDate.set(Calendar.YEAR, year);
            selectedDate.set(Calendar.MONTH, month);
            selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            // Format the selected date and display it
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            String formattedDate = dateFormat.format(selectedDate.getTime());
            editTextDate.setText(formattedDate);
        };

        // Set an OnClickListener on the editTextDate to show the DatePickerDialog
        editTextDate.setOnClickListener(v -> {

            // Hide the keyboard
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(editTextDate.getWindowToken(), 0);

            // Get the current date values
            int year = selectedDate.get(Calendar.YEAR);
            int month = selectedDate.get(Calendar.MONTH);
            int dayOfMonth = selectedDate.get(Calendar.DAY_OF_MONTH);

            // Create and show the DatePickerDialog
            DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this, dateSetListener, year, month, dayOfMonth);
            datePickerDialog.show();
        });

        // Set the editTextDate as non-focusable and non-editable
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

            // Create the radio button options
            String[] radioButtonOptions = {"Banco do Brasil", "Caixa Econômica Federal", "Nubank"};

            // Set up the radio buttons
            builder.setSingleChoiceItems(radioButtonOptions, selectedRadioButton[0],
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            selectedRadioButton[0] = which;
                        }
                    });

            // Set up the buttons
            builder.setPositiveButton("Abrir", null);
            builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Handle the Cancel button click
                    dialog.dismiss();
                }
            });

            // Create and show the AlertDialog
            final AlertDialog dialog = builder.create();
            dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialogInterface) {
                    // Handle the Open button click
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(view -> {
                        if (selectedRadioButton[0] != -1) {
                            // Perform code based on the selected radio button
                            switch (selectedRadioButton[0]) {
                                case 0:
                                    // Code for Option 1

                                    abrirIntentPIX("PIXBB", "Banco do Brasil");

                                    break;
                                case 1:
                                    // Code for Option 2

                                    abrirIntentPIX("PIXCaixa", "Caixa Econômica Federal");

                                    break;
                                case 2:
                                    // Code for Option 3

                                    abrirIntentPIX("PIXNubank", "Nubank");

                                    break;
                            }
                        } else {
                            // No radio button selected
                            Toast.makeText(MainActivity.this, "Você deve selecionar um dos três bancos.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });

            dialog.show();

        }

        if (id == R.id.cadastrarchave) {

            Intent myIntent = new Intent(MainActivity.this, PIXSalvarActivity.class);
            startActivity(myIntent);
        }

        return false;
    }

    public void abrirIntentPIX(String PIXBanco, String bancoNome) {

        String s = tinyDB.getString(PIXBanco);

        if (!s.equals("")) {
            Intent myIntent = new Intent(MainActivity.this, QRCodeActivity.class);
            myIntent.putExtra("content", s);
            myIntent.putExtra("nome", bancoNome);
            startActivity(myIntent);
        } else {
            Toast.makeText(MainActivity.this, "Nenhuma chave PIX " + bancoNome + " salva.", Toast.LENGTH_SHORT).show();
        }

    }

}
