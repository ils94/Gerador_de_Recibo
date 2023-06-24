package com.droidev.geradorderecibo;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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
    private EditText editTextAdditionalInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PDFGeneration pdfGeneration = new PDFGeneration();
        Miscs miscs = new Miscs();

        // Initialize UI components
        editTextName = findViewById(R.id.editTextName);
        editTextValue = findViewById(R.id.editTextValue);
        editTextValorPorExtenso = findViewById(R.id.editTextValorPorExtenso);
        editTextDate = findViewById(R.id.editTextDate);
        checkBox1 = findViewById(R.id.checkBox1);
        checkBox2 = findViewById(R.id.checkBox2);
        checkBox3 = findViewById(R.id.checkBox3);
        checkBox4 = findViewById(R.id.checkBox4);
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
                checkBox4));

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
}
