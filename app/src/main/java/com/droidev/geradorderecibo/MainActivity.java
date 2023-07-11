package com.droidev.geradorderecibo;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
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

            String PIX = tinyDB.getString("PIX");

            if (!PIX.equals("")) {

                Intent myIntent = new Intent(MainActivity.this, QRCodeActivity.class);
                myIntent.putExtra("content", PIX);
                startActivity(myIntent);
            } else {

                Toast.makeText(MainActivity.this, "Nenhuma chave PIX salva.", Toast.LENGTH_SHORT).show();
            }
        }

        if (id == R.id.cadastrarchave) {

            salvarPIX(MainActivity.this);
        }

        return false;
    }

    public void salvarPIX(Context context) {

        EditText chavePIX = new EditText(context);
        chavePIX.setHint("Chave PIX");
        chavePIX.setInputType(InputType.TYPE_CLASS_TEXT);

        LinearLayout lay = new LinearLayout(context);
        lay.setOrientation(LinearLayout.VERTICAL);
        lay.addView(chavePIX);

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setCancelable(false)
                .setTitle("Salvar chave PIX")
                .setMessage("Insira sua chave PIX abaixo.")
                .setPositiveButton("Salvar", null)
                .setNegativeButton("Cancelar", null)
                .setNeutralButton("Limpar", null)
                .setView(lay)
                .show();

        chavePIX.setText(tinyDB.getString("PIX"));

        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        Button negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        Button neutralButton = dialog.getButton(AlertDialog.BUTTON_NEUTRAL);

        positiveButton.setOnClickListener(v -> {

            if (!chavePIX.getText().toString().isEmpty()) {

                String PIXString = chavePIX.getText().toString();

                tinyDB.remove("PIX");

                tinyDB.putString("PIX", PIXString);

                Toast.makeText(context, "Chave PIX salva.", Toast.LENGTH_SHORT).show();

                dialog.dismiss();
            } else {

                Toast.makeText(context, "Erro, campo vazio.", Toast.LENGTH_SHORT).show();
            }

        });

        negativeButton.setOnClickListener(v -> {

            dialog.dismiss();

        });

        neutralButton.setOnClickListener(v -> {

            chavePIX.setText("");

        });

    }

}
