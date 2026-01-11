package com.example.gestionnairebudget;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gestionnairebudget.database.AppDatabase;
import com.example.gestionnairebudget.database.UserEntity;

import java.util.concurrent.Executors;

public class LoginActivity extends AppCompatActivity {

    private EditText editEmail, editMotDePasse;
    private Button btnConnexion, btnInscription;

    private AppDatabase database;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        database = AppDatabase.getInstance(this);
        prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        initialiserVues();
        configurerBoutons();
    }

    private void initialiserVues() {
        editEmail = findViewById(R.id.editEmail);
        editMotDePasse = findViewById(R.id.editMotDePasse);
        btnConnexion = findViewById(R.id.btnConnexion);
        btnInscription = findViewById(R.id.btnInscription);
    }

    private void configurerBoutons() {

        btnConnexion.setOnClickListener(v -> connexion());

        btnInscription.setOnClickListener(v ->
                startActivity(new Intent(this, InscriptionActivity.class))
        );
    }

    private void connexion() {
        String email = editEmail.getText().toString().trim();
        String motDePasse = editMotDePasse.getText().toString().trim();

        if (email.isEmpty() || motDePasse.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }

        Executors.newSingleThreadExecutor().execute(() -> {
            UserEntity user = database.userDao().connexion(email, motDePasse);

            runOnUiThread(() -> {
                if (user == null) {
                    Toast.makeText(this, "Email ou mot de passe incorrect", Toast.LENGTH_LONG).show();
                } else {
                    prefs.edit()
                            .putInt("userId", user.getId())
                            .apply();

                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                }
            });
        });
    }
}
