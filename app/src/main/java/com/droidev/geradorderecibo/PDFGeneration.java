package com.droidev.geradorderecibo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PDFGeneration {

    Miscs miscs = new Miscs();

    public void generatePDF(Context context, EditText editTextName, EditText editTextValue, EditText editTextValorPorExtenso, EditText editTextDate, EditText editTextAdditionalInfo, CheckBox checkBox1, CheckBox checkBox2, CheckBox checkBox3, CheckBox checkBox4, CheckBox checkBox5) {
        // Get the input values
        String name = editTextName.getText().toString();
        String value = editTextValue.getText().toString();
        String porExtenso = editTextValorPorExtenso.getText().toString();

        name = name.replaceAll("\\s+", " ").trim();
        value = value.replace(".", ",");

        if (!value.contains(",")) {

            value = value + ",00";
        }

        porExtenso = porExtenso.replaceAll("\\s+", " ").trim();

        boolean checkbox1 = checkBox1.isChecked();
        boolean checkbox2 = checkBox2.isChecked();
        boolean checkbox3 = checkBox3.isChecked();
        boolean checkbox4 = checkBox4.isChecked();

        String description = "Fui remunerado pelo estimado cliente denominado " + name + " por meio do valor de R$" + value + " (" + porExtenso.toLowerCase() + "), como contrapartida aos serviços prestados, os quais são apresentados de forma descriminada abaixo:";

        String garantiaServico = "Garantia do serviço: Comunicamos aos interessados que, após a realização da aplicação do produto, estaremos prontos para fornecer um reforço adicional, caso necessário, ou mediante solicitação do contratante. Cabe ressaltar que o serviço em questão possui uma garantia total de 1 mês, conforme estipulado no Artigo 26 do Código de Defesa do Consumidor, que estabelece o seguinte: O direito de reclamar por vícios aparentes ou de fácil constatação expira em um prazo de 30 dias, quando se tratar do fornecimento de serviços e produtos não duráveis.";

        String modeloRenovacao = "Proposta de Reestruturação do Modelo de Serviço: Abordagem de Prestação de Serviço Unitária. O serviço em questão será executado em uma única ocasião, ficando a critério do contratante a solicitação ou não de sua renovação. A garantia será fornecida quando necessária ou mediante solicitação expressa do contratante.";

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

        TextPaint paint = textoConfig(context, 14, R.color.black);

        TextPaint paint2 = textoConfig(context, 12, R.color.black);

        TextPaint paint3 = textoConfig(context, 16, R.color.black);

        TextPaint paint4 = textoConfig(context, 30, R.color.blue);

        // Set up the logo dimensions and margins
        float margin = 32;
        float availableWidth = canvas.getWidth() - 2 * margin;

        // Load the logo image from resources
        Bitmap logoBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.logo);

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
        StaticLayout descriptionLayout = new StaticLayout(description, paint, (int) availableWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);

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
        StaticLayout garantiaLayout = new StaticLayout(garantiaServico, paint2, (int) availableWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);

        // Draw the description using the StaticLayout
        canvas.save();
        canvas.translate(margin, textStartY);
        garantiaLayout.draw(canvas);
        canvas.restore();

        // Calculate the vertical position for the next line
        textStartY += garantiaLayout.getHeight() + 10;

        // Create a StaticLayout instance for the description
        StaticLayout renovacaoLayout = new StaticLayout(modeloRenovacao, paint2, (int) availableWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);

        // Draw the description using the StaticLayout
        canvas.save();
        canvas.translate(margin, textStartY);
        renovacaoLayout.draw(canvas);
        canvas.restore();

        // Calculate the vertical position for the next line
        textStartY += renovacaoLayout.getHeight() + 40;

        linhaData(canvas, paint3, textStartY, editTextDate);

        linhaDeAssinatura(context, canvas, textStartY, checkBox5);

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

            TextPaint paint5 = textoConfig(context, 30, R.color.black);

            // Define the text properties for the centered text
            String tituloText = "Observações";

            // Calculate the x-coordinate where the text should start
            int tituloTextX = canvas.getWidth() / 2; // Centered horizontally

            // Draw the centered text
            paint5.setTextAlign(Paint.Align.CENTER);
            canvas.drawText(tituloText, tituloTextX, 50, paint5);

            // Create a StaticLayout instance for the description
            StaticLayout additionalInfoLayout = new StaticLayout(additionalInfo, paint, (int) availableWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);

            textStartY += 50;

            // Draw the description using the StaticLayout
            canvas.save();
            canvas.translate(margin, textStartY);
            additionalInfoLayout.draw(canvas);
            canvas.restore();

            textStartY += additionalInfoLayout.getHeight() + 50;

            linhaData(canvas, paint3, textStartY, editTextDate);

            linhaDeAssinatura(context, canvas, textStartY, checkBox5);

            // Finish the page
            document.finishPage(page);
        }

        String timeStamp = new SimpleDateFormat("dd-MM-yyyy-HH-mm-ss", Locale.getDefault()).format(new Date());
        String fileName = "Recibo_" + name.replace(" ", "_") + "_" + timeStamp + ".pdf";

        // Create a file to save the PDF
        File pdfFile = new File(context.getCacheDir(), fileName);

        try {
            // Write the document content to the file
            FileOutputStream outputStream = new FileOutputStream(pdfFile);
            document.writeTo(outputStream);

            // Close the document and file
            document.close();
            outputStream.close();

            // Share the PDF file
            Uri pdfUri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".fileprovider", pdfFile);
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("application/pdf");
            shareIntent.putExtra(Intent.EXTRA_STREAM, pdfUri);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            context.startActivity(Intent.createChooser(shareIntent, "Share PDF"));

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "Error ao gerar PDF", Toast.LENGTH_SHORT).show();
        }
    }

    public TextPaint textoConfig(Context context, int size, int color) {

        TextPaint paint = new TextPaint();
        paint.setColor(ContextCompat.getColor(context, color));
        paint.setTextSize(size);

        return paint;
    }

    private void linhaDeAssinatura(Context context, Canvas canvas, float Y, CheckBox checkBox) {

        boolean checkbox = checkBox.isChecked();

        TextPaint paint = textoConfig(context, 20, R.color.black);

        TextPaint paint2 = textoConfig(context, 12, R.color.black);

        // Define the line properties
        float lineY = Y + 20 + 25; // Calculate the y-coordinate for the line

        if (checkbox) {

            // Load the signature image from resources
            Bitmap signatureBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.assinatura);

            // Define the desired width and height for the scaled image
            int desiredWidth = 150; // Set your desired width
            int desiredHeight = 50; // Set your desired height

            // Scale the signature image to the desired width and height
            Bitmap scaledSignatureBitmap = Bitmap.createScaledBitmap(signatureBitmap, desiredWidth, desiredHeight, false);

            // Calculate the position of the line and the image
            float imageY = lineY - scaledSignatureBitmap.getHeight() + 15; // Position the image above the line

            // Calculate the x-coordinate to center the image
            int imageX = (canvas.getWidth() - scaledSignatureBitmap.getWidth()) / 2;

            // Draw the scaled signature image above the line
            RectF imageRect = new RectF(imageX, imageY, imageX + scaledSignatureBitmap.getWidth(), imageY + scaledSignatureBitmap.getHeight());
            canvas.drawBitmap(scaledSignatureBitmap, null, imageRect, null);
        }

        // Draw the line
        canvas.drawLine(32, lineY, canvas.getWidth() - 32, lineY, paint);

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

    private void linhaData(Canvas canvas, TextPaint paint, float Y, EditText editTextDate) {

        // Get the current date
        String currentDate = miscs.dataHoje(editTextDate.getText().toString());

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
