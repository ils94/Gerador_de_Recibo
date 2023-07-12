package com.droidev.geradorderecibo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class PIXSalvarActivity extends AppCompatActivity {

    private EditText editTextPIXBB;
    private EditText editTextPIXCaixa;
    private EditText editTextPIXNubank;

    private TinyDB tinyDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pixsalvar);

        setTitle("Salvar Chaves PIX");

        tinyDB = new TinyDB(PIXSalvarActivity.this);

        // Initialize UI components
        editTextPIXBB = findViewById(R.id.editTextPIXBB);
        editTextPIXCaixa = findViewById(R.id.editTextPIXCaixa);
        editTextPIXNubank = findViewById(R.id.editTextPIXNubank);

        Button buttonSalvarPIX = findViewById(R.id.buttonSalvarPIX);

        buttonSalvarPIX.setOnClickListener(view -> {

            String PIXBBString = editTextPIXBB.getText().toString();
            String PIXCaixaString = editTextPIXCaixa.getText().toString();
            String PIXNubankString = editTextPIXNubank.getText().toString();

            tinyDB.remove("PIXBB");
            tinyDB.remove("PIXCaixa");
            tinyDB.remove("PIXNubank");

            tinyDB.putString("PIXBB", PIXBBString);
            tinyDB.putString("PIXCaixa", PIXCaixaString);
            tinyDB.putString("PIXNubank", PIXNubankString);

            Toast.makeText(PIXSalvarActivity.this, "Chave PIX salvas.", Toast.LENGTH_SHORT).show();

            PIXSalvarActivity.this.finish();

        });

        carregarChavesPIX();

    }

    public void carregarChavesPIX() {

        editTextPIXBB.setText(tinyDB.getString("PIXBB"));
        editTextPIXCaixa.setText(tinyDB.getString("PIXCaixa"));
        editTextPIXNubank.setText(tinyDB.getString("PIXNubank"));
    }
}