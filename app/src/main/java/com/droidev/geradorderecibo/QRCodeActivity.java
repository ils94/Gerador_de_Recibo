package com.droidev.geradorderecibo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.FileOutputStream;

public class QRCodeActivity extends AppCompatActivity {

    ImageView imageView;
    TextView bancoNome;

    TextView bancoChavePIX;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);

        imageView = findViewById(R.id.qrcode);

        bancoNome = findViewById(R.id.bancoNome);

        bancoChavePIX = findViewById(R.id.bancoChavePIX);

        setTitle("QR Code PIX");

        Intent intent = getIntent();
        String bancoQRCode = intent.getStringExtra("qrcode");
        String bancoChave = intent.getStringExtra("chave");
        String banco = intent.getStringExtra("nome");

        bancoNome.setText(banco);

        if (!bancoChave.equals("")) {

            bancoChavePIX.setText(bancoChave);
        } else {

            bancoChavePIX.setText("Sem chave PIX salva.");
        }

        try {

            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();

            Bitmap bitmap = barcodeEncoder.encodeBitmap(bancoQRCode, BarcodeFormat.QR_CODE, 512, 512);

            salvarQRCode(bitmap);

            imageView.setImageBitmap(bitmap);

        } catch (WriterException e) {
            e.printStackTrace();
        }

        bancoChavePIX.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String textToCopy = bancoChavePIX.getText().toString();

                // Copy the text to the clipboard
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Chave PIX", textToCopy);
                if (clipboard != null) {
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(QRCodeActivity.this, "Chave PIX copiada.", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    public void salvarQRCode(Bitmap bmp) {

        try {
            FileOutputStream fileOutputStream = openFileOutput("QRCode.png", Context.MODE_PRIVATE);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
            fileOutputStream.close();
        } catch (Exception e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }
}