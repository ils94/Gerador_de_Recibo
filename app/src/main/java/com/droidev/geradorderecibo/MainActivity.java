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
        buttonGeneratePDF.setOnClickListener(v -> generatePDF());

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
    }

    private void generatePDF() {
        // Get the input values
        String name = editTextName.getText().toString();
        String value = editTextValue.getText().toString();
        String porExtenso = editTextValorPorExtenso.getText().toString();

        boolean checkbox1 = checkBox1.isChecked();
        boolean checkbox2 = checkBox2.isChecked();
        boolean checkbox3 = checkBox3.isChecked();
        boolean checkbox4 = checkBox4.isChecked();

        String description = "Descrição do serviço: Recebi de " + name + " a quantia de " + value + " (" + porExtenso.toLowerCase() + ") pelo(s) serviço(s) prestado(s) abaixo:";

        String garantiaServico = "Garantia do serviço: Informamos que após a aplicação do produto, Será disponibilizado um reforço, Se necessário, Ou quando solicitado pelo contratante, tendo o serviço a garantia total de 1 mês, conforme estabelece o código de defesa do consumidor no seu Artigo 26 (- “O direito de reclamar pelos vícios aparentes ou de fácil constatação caduca em 30 dias, tratando-se de fornecimento de serviço e de produto não duráveis.”)";

        String modeloRenovacao = "Modelo de renovação do serviço: Modelo de prestação de serviço unitário. O serviço será realizado apenas 1 vez, cabendo ao contratante solicitar ou não renovação do serviço. Garantia se necessário, ou quando solicitada pelo contratante.";

        // Create a new PDF document
        PdfDocument document = new PdfDocument();

        // Create a page with the desired dimensions
        int pageWidth = 612; // 8.5 inches (612pt) assuming 72pt per inch
        int pageHeight = 792; // 11 inches (792pt) assuming 72pt per inch
        int pageNumber = 1;

        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create();
        PdfDocument.Page page = document.startPage(pageInfo);

        // Get the canvas for drawing on the page
        Canvas canvas = page.getCanvas();

        TextPaint paint = textoConfig(20, R.color.black);

        TextPaint paint2 = textoConfig(12, R.color.black);

        TextPaint paint3 = textoConfig(16, R.color.black);

        TextPaint paint4 = textoConfig(30, R.color.blue);

        // Set up the logo dimensions and margins
        float margin = 32;
        float availableWidth = canvas.getWidth() - 2 * margin;

        // Load the logo image from resources
        Bitmap logoBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.logo);

        // Calculate the scaling factor to fit the logo within the available width
        float logoScale = availableWidth / logoBitmap.getWidth();

        // Calculate the scaled height of the logo
        float logoHeight = logoBitmap.getHeight() * logoScale;

        // Draw the logo at the top of the page
        RectF logoRect = new RectF(margin, margin, canvas.getWidth() - margin, margin + logoHeight);
        canvas.drawBitmap(logoBitmap, null, logoRect, null);

        // Draw the receipt information below the logo
        float textStartY = margin + logoHeight + 40;

        // Draw the receipt label
        String receiptLabel = "VALOR TOTAL: R$ " + value;
        canvas.drawText(receiptLabel, margin, textStartY, paint4);
        textStartY += 16; // Adjust the line spacing

        // Create a StaticLayout instance for the description
        StaticLayout descriptionLayout = new StaticLayout((CharSequence) description, (TextPaint) paint, (int) availableWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);

        // Draw the description using the StaticLayout
        canvas.save();
        canvas.translate(margin, textStartY);
        descriptionLayout.draw(canvas);
        canvas.restore();

        // Calculate the vertical position for the next line
        textStartY += descriptionLayout.getHeight() + 30;

        // Draw the checkboxes
        if (checkbox1) {
            canvas.drawText("     • Higienização de caixa d'água.", margin, textStartY, paint);
            textStartY += 20;
        }

        if (checkbox2) {
            canvas.drawText("     • Desratização.", margin, textStartY, paint);
            textStartY += 20;
        }

        if (checkbox3) {
            canvas.drawText("     • Descupinização.", margin, textStartY, paint);
            textStartY += 20;
        }

        if (checkbox4) {
            canvas.drawText("     • Controle de baratas, escorpiões e formigas.", margin, textStartY, paint);
            textStartY += 20;
        }

        // Create a StaticLayout instance for the description
        StaticLayout garantiaLayout = new StaticLayout((CharSequence) garantiaServico, (TextPaint) paint2, (int) availableWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);

        // Draw the description using the StaticLayout
        canvas.save();
        canvas.translate(margin, textStartY);
        garantiaLayout.draw(canvas);
        canvas.restore();

        // Calculate the vertical position for the next line
        textStartY += garantiaLayout.getHeight() + 10;

        // Create a StaticLayout instance for the description
        StaticLayout renovacaoLayout = new StaticLayout((CharSequence) modeloRenovacao, (TextPaint) paint2, (int) availableWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);

        // Draw the description using the StaticLayout
        canvas.save();
        canvas.translate(margin, textStartY);
        renovacaoLayout.draw(canvas);
        canvas.restore();

        // Calculate the vertical position for the next line
        textStartY += renovacaoLayout.getHeight() + 40;

        linhaData(canvas, paint3, textStartY);

        linhaDeAssinatura(canvas, textStartY);

        // Finish the page
        document.finishPage(page);

        // Check if additional info is present
        String additionalInfo = editTextAdditionalInfo.getText().toString().trim();

        if (!additionalInfo.isEmpty()) {

            pageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 2).create();
            page = document.startPage(pageInfo);

            // Get the canvas for drawing on the page
            canvas = page.getCanvas();

            // Draw the receipt information below the logo
            textStartY = 32;

            TextPaint paint5 = textoConfig(30, R.color.black);

            // Define the text properties for the centered text
            String tituloText = "Orçamento de Serviço";

            // Calculate the x-coordinate where the text should start
            int tituloTextX = canvas.getWidth() / 2; // Centered horizontally

            // Draw the centered text
            paint5.setTextAlign(Paint.Align.CENTER);
            canvas.drawText(tituloText, tituloTextX, 50, paint5);

            // Create a StaticLayout instance for the description
            StaticLayout additionalInfoLayout = new StaticLayout((CharSequence) additionalInfo, (TextPaint) paint, (int) availableWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);

            textStartY += 50;

            // Draw the description using the StaticLayout
            canvas.save();
            canvas.translate(margin, textStartY);
            additionalInfoLayout.draw(canvas);
            canvas.restore();

            textStartY += additionalInfoLayout.getHeight() + 50;

            linhaData(canvas, paint3, textStartY);

            linhaDeAssinatura(canvas, textStartY);

            // Finish the page
            document.finishPage(page);
        }

        String timeStamp = new SimpleDateFormat("dd-MM-yyyy-HH-mm-ss", Locale.getDefault()).format(new Date());
        String fileName = "Recibo_" + name.replace(" ", "_") + "_" + timeStamp + ".pdf";

        // Create a file to save the PDF
        File pdfFile = new File(getCacheDir(), fileName);

        try {
            // Write the document content to the file
            FileOutputStream outputStream = new FileOutputStream(pdfFile);
            document.writeTo(outputStream);

            // Close the document and file
            document.close();
            outputStream.close();

            // Share the PDF file
            Uri pdfUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".fileprovider", pdfFile);
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("application/pdf");
            shareIntent.putExtra(Intent.EXTRA_STREAM, pdfUri);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(shareIntent, "Share PDF"));

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error generating PDF", Toast.LENGTH_SHORT).show();
        }
    }


    public String dataHoje(String data) {

        data = data.replace("/01/", " de Janeiro de ")
                .replace("/02/", " de Fevereiro de ")
                .replace("/03/", " de Março de ")
                .replace("/04/", " de Abril de ")
                .replace("/05/", " de Maio de ")
                .replace("/06/", " de Junho de ")
                .replace("/07/", " de Julho de ")
                .replace("/08/", " de Agosto de ")
                .replace("/09/", " de Setembro de ")
                .replace("/10/", " de Outubro de ")
                .replace("/11/", " de Novembro de ")
                .replace("/12/", " de Dezembro de ");

        return "Natal - RN, " + data + ".";
    }

    public TextPaint textoConfig(int size, int color) {

        TextPaint paint = new TextPaint();
        paint.setColor(ContextCompat.getColor(this, color));
        paint.setTextSize(size);

        return paint;
    }

    public void linhaDeAssinatura(Canvas canvas, float Y){

        TextPaint paint = textoConfig(20, R.color.black);

        TextPaint paint2 = textoConfig(12, R.color.black);

        // Define the line properties
        float lineY = Y + 20 + 25; // Calculate the y-coordinate for the line
        float lineEndX = canvas.getWidth() - 32; // End the line at the right margin

        // Draw the line
        canvas.drawLine(32, lineY, lineEndX, lineY, paint);

        // Define the text properties for the centered text
        String centeredText = "Assinatura do prestador de serviço.";

        // Calculate the x-coordinate where the text should start
        int centeredTextX = canvas.getWidth() / 2; // Centered horizontally

        // Calculate the y-coordinate where the text should start
        float centeredTextY = lineY + 2 * 10; // Position below the line

        // Draw the centered text
        paint2.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(centeredText, centeredTextX, centeredTextY, paint2);
    }

    public void linhaData(Canvas canvas, TextPaint paint, float Y){

        // Get the current date
        String currentDate = dataHoje(editTextDate.getText().toString());

        // Set the maximum length of the date string
        int maxDateLength = 35;

        // Truncate the date string if it exceeds the maximum length
        if (currentDate.length() > maxDateLength) {
            currentDate = currentDate.substring(0, maxDateLength);
        }

        // Calculate the width of the date text
        float dateTextWidth = paint.measureText(currentDate);

        // Draw the date aligned to the right, adjusting the x-coordinate
        float dateStartX = canvas.getWidth() - dateTextWidth - 32;

        // Draw the date string
        canvas.drawText(currentDate, dateStartX, Y, paint);
    }
}
