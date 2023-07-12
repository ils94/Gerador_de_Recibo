package com.droidev.geradorderecibo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class PIXSalvarActivity extends AppCompatActivity {

    private EditText editTextPIXQRCodeBB;
    private EditText editTextPIXQRCodeCaixa;
    private EditText editTextPIXQRCodeNubank;

    private EditText editTextPIXChaveBB;
    private EditText editTextPIXChaveCaixa;
    private EditText editTextPIXChaveNubank;

    private TinyDB tinyDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pixsalvar);

        setTitle("Salvar Chaves PIX");

        tinyDB = new TinyDB(PIXSalvarActivity.this);

        // Initialize UI components
        editTextPIXQRCodeBB = findViewById(R.id.editTextPIXQRCodeBB);
        editTextPIXQRCodeCaixa = findViewById(R.id.editTextPIXQRCodeCaixa);
        editTextPIXQRCodeNubank = findViewById(R.id.editTextPIXQRCodeNubank);

        editTextPIXChaveBB = findViewById(R.id.editTextPIXBBChave);
        editTextPIXChaveCaixa = findViewById(R.id.editTextPIXCaixaChave);
        editTextPIXChaveNubank = findViewById(R.id.editTextPIXNubankChave);

        Button buttonSalvarPIX = findViewById(R.id.buttonSalvarPIX);

        buttonSalvarPIX.setOnClickListener(view -> {

            String PIXQRCodeBBString = editTextPIXQRCodeBB.getText().toString();
            String PIXQRCodeCaixaString = editTextPIXQRCodeCaixa.getText().toString();
            String PIXQRCodeNubankString = editTextPIXQRCodeNubank.getText().toString();

            String PIXChaveBBString = editTextPIXChaveBB.getText().toString();
            String PIXChaveCaixaString = editTextPIXChaveCaixa.getText().toString();
            String PIXChaveNubankString = editTextPIXChaveNubank.getText().toString();

            tinyDB.remove("PIXQRCodeBB");
            tinyDB.remove("PIXQRCodeCaixa");
            tinyDB.remove("PIXQRCodeNubank");

            tinyDB.remove("PIXChaveBB");
            tinyDB.remove("PIXChaveCaixa");
            tinyDB.remove("PIXChaveNubank");

            tinyDB.putString("PIXQRCodeBB", PIXQRCodeBBString);
            tinyDB.putString("PIXQRCodeCaixa", PIXQRCodeCaixaString);
            tinyDB.putString("PIXQRCodeNubank", PIXQRCodeNubankString);

            tinyDB.putString("PIXChaveBB", PIXChaveBBString);
            tinyDB.putString("PIXChaveCaixa", PIXChaveCaixaString);
            tinyDB.putString("PIXChaveNubank", PIXChaveNubankString);

            Toast.makeText(PIXSalvarActivity.this, "Chave PIX salvas.", Toast.LENGTH_SHORT).show();

            PIXSalvarActivity.this.finish();

        });

        carregarChavesPIX();

    }

    public void carregarChavesPIX() {

        editTextPIXQRCodeBB.setText(tinyDB.getString("PIXQRCodeBB"));
        editTextPIXQRCodeCaixa.setText(tinyDB.getString("PIXQRCodeCaixa"));
        editTextPIXQRCodeNubank.setText(tinyDB.getString("PIXQRCodeNubank"));

        editTextPIXChaveBB.setText(tinyDB.getString("PIXChaveBB"));
        editTextPIXChaveCaixa.setText(tinyDB.getString("PIXChaveCaixa"));
        editTextPIXChaveNubank.setText(tinyDB.getString("PIXChaveNubank"));
    }
}