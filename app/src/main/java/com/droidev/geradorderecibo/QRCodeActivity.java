package com.droidev.geradorderecibo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);

        imageView = findViewById(R.id.qrcode);

        bancoNome = findViewById(R.id.bancoNome);

        setTitle("QR Code PIX");

        Intent intent = getIntent();
        String content = intent.getStringExtra("content");
        String banco = intent.getStringExtra("nome");

        bancoNome.setText(banco);


        try {

            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();

            Bitmap bitmap = barcodeEncoder.encodeBitmap(content, BarcodeFormat.QR_CODE, 512, 512);

            salvarQRCode(bitmap);

            imageView.setImageBitmap(bitmap);

        } catch (WriterException e) {
            e.printStackTrace();
        }
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