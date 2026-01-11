package com.example.gestionnairebudget;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.gestionnairebudget.database.AppDatabase;
import com.example.gestionnairebudget.database.UserEntity;
import java.util.concurrent.Executors;

/**
 * PAGE D'INSCRIPTION
 * Permet à un nouvel utilisateur de créer un compte
 */
public class InscriptionActivity extends AppCompatActivity {

    private EditText editNom, editEmail, editMotDePasse, editConfirmMotDePasse;
    private EditText editProfession, editVille;
    private Button btnCreerCompte, btnRetourConnexion;

    private AppDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inscription);

        database = AppDatabase.getInstance(this);

        initialiserVues();
        configurerBoutons();
    }

    private void initialiserVues() {
        editNom = findViewById(R.id.editNom);
        editEmail = findViewById(R.id.editEmail);
        editMotDePasse = findViewById(R.id.editMotDePasse);
        editConfirmMotDePasse = findViewById(R.id.editConfirmMotDePasse);
        editProfession = findViewById(R.id.editProfession);
        editVille = findViewById(R.id.editVille);
        btnCreerCompte = findViewById(R.id.btnCreerCompte);
        btnRetourConnexion = findViewById(R.id.btnRetourConnexion);
    }

    private void configurerBoutons() {

        btnCreerCompte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                creerCompte();
            }
        });

        btnRetourConnexion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * Créer un nouveau compte utilisateur
     */
    private void creerCompte() {

        String nom = editNom.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        String motDePasse = editMotDePasse.getText().toString();
        String confirmMotDePasse = editConfirmMotDePasse.getText().toString();
        String profession = editProfession.getText().toString().trim();
        String ville = editVille.getText().toString().trim();

        if (nom.isEmpty() || email.isEmpty() || motDePasse.isEmpty()
                || profession.isEmpty() || ville.isEmpty()) {
            Toast.makeText(this,
                    "\u26A0\uFE0F Remplissez tous les champs",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (!email.contains("@") || !email.contains(".")) {
            Toast.makeText(this,
                    "\u274C Email invalide",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (motDePasse.length() < 6) {
            Toast.makeText(this,
                    "\u274C Mot de passe trop court (min 6 caractères)",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (!motDePasse.equals(confirmMotDePasse)) {
            Toast.makeText(this,
                    "\u274C Les mots de passe ne correspondent pas",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        btnCreerCompte.setEnabled(false);
        btnCreerCompte.setText("Création en cours...");

        Executors.newSingleThreadExecutor().execute(() -> {

            int emailExiste = database.userDao().emailExiste(email);

            if (emailExiste > 0) {
                runOnUiThread(() -> {
                    Toast.makeText(this,
                            "\u274C Cet email est déjà utilisé",
                            Toast.LENGTH_LONG).show();
                    btnCreerCompte.setEnabled(true);
                    btnCreerCompte.setText("\u2705 Créer mon compte");
                });
                return;
            }

            UserEntity nouvelUtilisateur =
                    new UserEntity(nom, email, motDePasse, profession, ville);

            try {
                database.userDao().inserer(nouvelUtilisateur);

                runOnUiThread(() -> {
                    Toast.makeText(this,
                            "\u2705 Compte créé avec succès !\nVeuillez vous connecter.",
                            Toast.LENGTH_LONG).show();
                    finish();
                });

            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(this,
                            "\u274C Erreur lors de la création : " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                    btnCreerCompte.setEnabled(true);
                    btnCreerCompte.setText("\u2705 Créer mon compte");
                });
            }
        });
    }
}
