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

    public void generatePDF(Context context, EditText editTextName, EditText editTextValue, EditText editTextValorPorExtenso, EditText editTextDate, EditText editTextAdditionalInfo, CheckBox checkBox1, CheckBox checkBox2, CheckBox checkBox3, CheckBox checkBox4, CheckBox checkBox5, CheckBox checkBox6) {

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

        PdfDocument document = new PdfDocument();

        int pageWidth = 612;
        int pageHeight = 792;
        int pageNumber = 1;

        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create();
        PdfDocument.Page page = document.startPage(pageInfo);

        Canvas canvas = page.getCanvas();

        TextPaint paint = textoConfig(context, 14, R.color.black);

        TextPaint paint2 = textoConfig(context, 12, R.color.black);

        TextPaint paint3 = textoConfig(context, 16, R.color.black);

        TextPaint paint4 = textoConfig(context, 30, R.color.blue);

        float margin = 32;
        float availableWidth = canvas.getWidth() - 2 * margin;

        Bitmap logoBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.logo);

        float logoScale = availableWidth / logoBitmap.getWidth();

        float logoHeight = logoBitmap.getHeight() * logoScale;

        RectF logoRect = new RectF(margin, margin, canvas.getWidth() - margin, margin + logoHeight);
        canvas.drawBitmap(logoBitmap, null, logoRect, null);

        float textStartY = margin + logoHeight + 40;

        // Draw the receipt label
        String receiptLabel = "VALOR TOTAL: R$ " + value;
        canvas.drawText(receiptLabel, margin, textStartY, paint4);
        textStartY += 16;

        StaticLayout descriptionLayout = new StaticLayout(description, paint, (int) availableWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);

        canvas.save();
        canvas.translate(margin, textStartY);
        descriptionLayout.draw(canvas);
        canvas.restore();

        textStartY += descriptionLayout.getHeight() + 30;

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

        StaticLayout garantiaLayout = new StaticLayout(garantiaServico, paint2, (int) availableWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);

        canvas.save();
        canvas.translate(margin, textStartY);
        garantiaLayout.draw(canvas);
        canvas.restore();

        textStartY += garantiaLayout.getHeight() + 10;

        StaticLayout renovacaoLayout = new StaticLayout(modeloRenovacao, paint2, (int) availableWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);

        canvas.save();
        canvas.translate(margin, textStartY);
        renovacaoLayout.draw(canvas);
        canvas.restore();

        textStartY += renovacaoLayout.getHeight() + 40;

        linhaData(canvas, paint3, textStartY, editTextDate);

        linhaDeAssinatura(context, canvas, textStartY, checkBox5, checkBox6);

        document.finishPage(page);

        String additionalInfo = editTextAdditionalInfo.getText().toString().trim();

        if (!additionalInfo.isEmpty()) {

            pageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 2).create();
            page = document.startPage(pageInfo);

            canvas = page.getCanvas();

            textStartY = 32;

            TextPaint paint5 = textoConfig(context, 30, R.color.black);

            String tituloText = "Observações";

            int tituloTextX = canvas.getWidth() / 2;

            paint5.setTextAlign(Paint.Align.CENTER);
            canvas.drawText(tituloText, tituloTextX, 50, paint5);

            StaticLayout additionalInfoLayout = new StaticLayout(additionalInfo, paint, (int) availableWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);

            textStartY += 50;

            canvas.save();
            canvas.translate(margin, textStartY);
            additionalInfoLayout.draw(canvas);
            canvas.restore();

            textStartY += additionalInfoLayout.getHeight() + 50;

            linhaData(canvas, paint3, textStartY, editTextDate);

            linhaDeAssinatura(context, canvas, textStartY, checkBox5, checkBox6);

            document.finishPage(page);
        }

        String timeStamp = new SimpleDateFormat("dd-MM-yyyy-HH-mm-ss", Locale.getDefault()).format(new Date());
        String fileName = "Recibo_" + name.replace(" ", "_") + "_" + timeStamp + ".pdf";

        File pdfFile = new File(context.getCacheDir(), fileName);

        try {
            FileOutputStream outputStream = new FileOutputStream(pdfFile);
            document.writeTo(outputStream);

            document.close();
            outputStream.close();

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

    private void linhaDeAssinatura(Context context, Canvas canvas, float Y, CheckBox checkBox1, CheckBox checkBox2) {

        boolean checkbox1 = checkBox1.isChecked();
        boolean checkbox2 = checkBox2.isChecked();

        Bitmap signatureBitmap;

        int position;

        TextPaint paint = textoConfig(context, 20, R.color.black);

        TextPaint paint2 = textoConfig(context, 12, R.color.black);

        float lineY = Y + 20 + 25;

        if (checkbox1) {

            if (checkbox2) {

                signatureBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.extenso);

                position = 10;

            } else {

                signatureBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.rubrica);

                position = 15;
            }

            int desiredWidth = 500;
            int desiredHeight = 50;

            Bitmap scaledSignatureBitmap = Bitmap.createScaledBitmap(signatureBitmap, desiredWidth, desiredHeight, false);

            float imageY = lineY - scaledSignatureBitmap.getHeight() + position;

            int imageX = (canvas.getWidth() - scaledSignatureBitmap.getWidth()) / 2;

            RectF imageRect = new RectF(imageX, imageY, imageX + scaledSignatureBitmap.getWidth(), imageY + scaledSignatureBitmap.getHeight());
            canvas.drawBitmap(scaledSignatureBitmap, null, imageRect, null);
        }

        canvas.drawLine(32, lineY, canvas.getWidth() - 32, lineY, paint);

        String centeredText = "Assinatura do prestador de serviço.";

        int centeredTextX = canvas.getWidth() / 2;

        float centeredTextY = lineY + 2 * 10;

        paint2.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(centeredText, centeredTextX, centeredTextY, paint2);
    }

    private void linhaData(Canvas canvas, TextPaint paint, float Y, EditText editTextDate) {

        String currentDate = miscs.dataHoje(editTextDate.getText().toString());

        int maxDateLength = 35;

        if (currentDate.length() > maxDateLength) {
            currentDate = currentDate.substring(0, maxDateLength);
        }

        float dateTextWidth = paint.measureText(currentDate);

        float dateStartX = canvas.getWidth() - dateTextWidth - 32;

        canvas.drawText(currentDate, dateStartX, Y, paint);
    }

}
