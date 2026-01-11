package com.example.gestionnairebudget;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.gestionnairebudget.database.AppDatabase;
import com.example.gestionnairebudget.database.DepenseEntity;
import com.example.gestionnairebudget.database.RapportEntity;
import com.example.gestionnairebudget.utils.RapportGenerator;
import com.example.gestionnairebudget.utils.RapportScheduler;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;
import android.content.Intent;


/**
 * GESTIONNAIRE DE BUDGET COMPLET
 * Avec base de données SQLite et rapports automatiques
 */
public class MainActivity extends AppCompatActivity {

    private EditText editBudget;
    private Spinner spinnerCategorie;
    private EditText editDesignation;
    private EditText editMontant;
    private Button btnDefinirBudget, btnAjouterDepense,
            btnVoirHistorique, btnGenererRapport, btnVoirRapports;
    private Button btnRetour, btnDeconnexion;

    private TextView textSituation, textHistorique;
    private LinearLayout layoutCategories;

    private double budgetMensuel = 0.0;
    private CategorieBudget[] categories;
    private AppDatabase database;
    private SharedPreferences prefs;
    private RapportScheduler scheduler;
    private boolean budgetDefini = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        database = AppDatabase.getInstance(this);
        prefs = getSharedPreferences("BudgetPrefs", MODE_PRIVATE);

        connecterVues();
        chargerBudgetSauvegarde();
        initialiserCategories();
        configurerSpinner();
        configurerBoutons();

        if (budgetDefini) {
            afficherCategories();
            mettreAJourSituation();
        }
    }

    private void chargerBudgetSauvegarde() {
        budgetMensuel = Double.longBitsToDouble(prefs.getLong("budgetMensuel", 0));
        if (budgetMensuel > 0) {
            budgetDefini = true;
            editBudget.setText(String.valueOf(budgetMensuel));
        }
    }

    private void initialiserCategories() {
        categories = CategoriesManager.creerCategoriesParDefaut();

        if (budgetDefini) {
            for (CategorieBudget cat : categories) {
                cat.calculerMontantAlloue(budgetMensuel);
            }
            chargerDepensesMoisEnCours();
        }
    }

    private void chargerDepensesMoisEnCours() {
        Executors.newSingleThreadExecutor().execute(() -> {
            Calendar cal = Calendar.getInstance();
            int mois = cal.get(Calendar.MONTH) + 1;
            int annee = cal.get(Calendar.YEAR);

            List<DepenseEntity> depenses =
                    database.depenseDao().getDepensesParMois(mois, annee);

            for (CategorieBudget cat : categories) {
                double total = 0;
                for (DepenseEntity d : depenses) {
                    if (d.getCategorie().equals(cat.getNom())) {
                        total += d.getMontant();
                    }
                }
                cat.setMontantUtilise(total);
            }

            runOnUiThread(() -> {
                afficherCategories();
                mettreAJourSituation();
            });
        });
    }

    private void connecterVues() {
        editBudget = findViewById(R.id.editBudget);
        spinnerCategorie = findViewById(R.id.spinnerCategorie);
        editDesignation = findViewById(R.id.editDesignation);
        editMontant = findViewById(R.id.editMontant);
        btnDefinirBudget = findViewById(R.id.btnDefinirBudget);
        btnAjouterDepense = findViewById(R.id.btnAjouterDepense);
        btnVoirHistorique = findViewById(R.id.btnVoirHistorique);
        btnGenererRapport = findViewById(R.id.btnGenererRapport);
        btnVoirRapports = findViewById(R.id.btnVoirRapports);
        textSituation = findViewById(R.id.textSituation);
        layoutCategories = findViewById(R.id.layoutCategories);
        textHistorique = findViewById(R.id.textHistorique);
        btnRetour = findViewById(R.id.btnRetour);
        btnDeconnexion = findViewById(R.id.btnDeconnexion);

    }

    private void configurerSpinner() {
        String[] noms = new String[categories.length];
        for (int i = 0; i < categories.length; i++) {
            noms[i] = categories[i].getNom() + " (" +
                    categories[i].getPourcentageAlloue() + "%)";
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                noms
        );
        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );
        spinnerCategorie.setAdapter(adapter);
    }

    private void configurerBoutons() {
        btnDefinirBudget.setOnClickListener(v -> definirBudget());
        btnAjouterDepense.setOnClickListener(v -> ajouterDepense());
        btnVoirHistorique.setOnClickListener(v -> afficherHistorique());
        btnGenererRapport.setOnClickListener(v -> afficherMenuRapports());
        btnVoirRapports.setOnClickListener(v -> afficherRapportsSauvegardes());

        // 🔙 Bouton RETOUR
        btnRetour.setOnClickListener(v -> {
            onBackPressed(); // Android gère automatiquement la page précédente
        });

// 🔒 Bouton DÉCONNEXION
        btnDeconnexion.setOnClickListener(v -> {

            // Supprimer la session utilisateur
            getSharedPreferences("USER_SESSION", MODE_PRIVATE)
                    .edit()
                    .clear()
                    .apply();

            Toast.makeText(this,
                    "Déconnexion réussie",
                    Toast.LENGTH_SHORT).show();

            // Retour à la page de connexion
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

    }

    private void definirBudget() {
        String txt = editBudget.getText().toString();

        if (txt.isEmpty()) {
            Toast.makeText(this,
                    "\u26A0\uFE0F Entrez un montant",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            budgetMensuel = Double.parseDouble(txt);
            if (budgetMensuel <= 0) {
                Toast.makeText(this,
                        "\u274C Budget doit être > 0",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            budgetDefini = true;
            prefs.edit().putLong("budgetMensuel",
                    Double.doubleToRawLongBits(budgetMensuel)).apply();

            for (CategorieBudget cat : categories) {
                cat.calculerMontantAlloue(budgetMensuel);
            }

            chargerDepensesMoisEnCours();

            scheduler = new RapportScheduler(this);
            scheduler.planifierTousLesRapports();

            Toast.makeText(this,
                    "\u2705 Budget défini : " + budgetMensuel +
                            " FCFA\n\uD83D\uDCCA Rapports automatiques activés",
                    Toast.LENGTH_LONG).show();

        } catch (NumberFormatException e) {
            Toast.makeText(this,
                    "\u274C Format invalide",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void ajouterDepense() {
        if (!budgetDefini) {
            Toast.makeText(this,
                    "\u26A0\uFE0F Définissez d'abord votre budget",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        int pos = spinnerCategorie.getSelectedItemPosition();
        String designation = editDesignation.getText().toString();
        String montantTxt = editMontant.getText().toString();

        if (designation.isEmpty() || montantTxt.isEmpty()) {
            Toast.makeText(this,
                    "\u26A0\uFE0F Remplissez tous les champs",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double montant = Double.parseDouble(montantTxt);
            if (montant <= 0) {
                Toast.makeText(this,
                        "\u274C Montant doit être > 0",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            CategorieBudget cat = categories[pos];
            cat.ajouterDepense(montant);

            Calendar cal = Calendar.getInstance();
            SimpleDateFormat sdf =
                    new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

            DepenseEntity dep = new DepenseEntity(
                    cat.getNom(),
                    designation,
                    montant,
                    System.currentTimeMillis(),
                    sdf.format(new Date()),
                    cal.get(Calendar.DAY_OF_MONTH),
                    cal.get(Calendar.WEEK_OF_YEAR),
                    cal.get(Calendar.MONTH) + 1,
                    cal.get(Calendar.YEAR)
            );

            Executors.newSingleThreadExecutor().execute(() ->
                    database.depenseDao().inserer(dep));

            double pct = cat.getPourcentageUtilise();
            if (pct >= 70 && pct < 100) {
                Toast.makeText(this,
                        "\uD83D\uDEA8 ALERTE : " + cat.getNom() +
                                " " + String.format("%.1f", pct) + "%",
                        Toast.LENGTH_LONG).show();
            } else if (pct >= 100) {
                Toast.makeText(this,
                        "\u274C DÉPASSEMENT : " + cat.getNom(),
                        Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this,
                        "\u2705 Dépense ajoutée",
                        Toast.LENGTH_SHORT).show();
            }

            editDesignation.setText("");
            editMontant.setText("");

            afficherCategories();
            mettreAJourSituation();

        } catch (NumberFormatException e) {
            Toast.makeText(this,
                    "\u274C Format invalide",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void afficherCategories() {
        layoutCategories.removeAllViews();
        for (CategorieBudget cat : categories) {
            TextView tv = new TextView(this);
            tv.setText(cat.afficherResume());
            tv.setPadding(20, 15, 20, 15);
            tv.setTextColor(Color.BLACK);
            tv.setBackgroundColor(cat.getCouleurCode());
            layoutCategories.addView(tv);
        }
    }

    private void afficherHistorique() {
        Executors.newSingleThreadExecutor().execute(() -> {
            Calendar cal = Calendar.getInstance();
            int mois = cal.get(Calendar.MONTH) + 1;
            int annee = cal.get(Calendar.YEAR);

            List<DepenseEntity> depenses =
                    database.depenseDao().getDepensesParMois(mois, annee);

            runOnUiThread(() -> {
                if (depenses.isEmpty()) {
                    textHistorique.setText(
                            "\uD83D\uDCCB HISTORIQUE\n\nAucune dépense ce mois-ci"
                    );
                    return;
                }

                StringBuilder sb = new StringBuilder();
                sb.append("\uD83D\uDCCB HISTORIQUE DU MOIS\n\n");

                double total = 0;
                for (DepenseEntity d : depenses) {
                    sb.append("- ").append(d.getCategorie())
                            .append(" : ").append(d.getMontant()).append(" FCFA\n");
                    total += d.getMontant();
                }

                sb.append("\n\uD83D\uDCB0 TOTAL : ")
                        .append(String.format("%.0f", total)).append(" FCFA");

                textHistorique.setText(sb.toString());
            });
        });
    }

    private void afficherMenuRapports() {
        String[] options = {
                "\uD83D\uDCC5 Rapport Journalier",
                "\uD83D\uDCCA Rapport Hebdomadaire",
                "\uD83D\uDCC8 Rapport Mensuel"
        };

        new AlertDialog.Builder(this)
                .setTitle("Générer un rapport")
                .setItems(options, (d, w) -> {
                    if (w == 0) genererRapportManuel("JOURNALIER");
                    if (w == 1) genererRapportManuel("HEBDOMADAIRE");
                    if (w == 2) genererRapportManuel("MENSUEL");
                })
                .show();
    }

    private void genererRapportManuel(String type) {
        Toast.makeText(this,
                "\u23F3 Génération du rapport...",
                Toast.LENGTH_SHORT).show();

        Executors.newSingleThreadExecutor().execute(() -> {
            RapportGenerator gen =
                    new RapportGenerator(database, budgetMensuel);
            RapportEntity r = null;

            if ("JOURNALIER".equals(type)) r = gen.genererRapportJournalier();
            if ("HEBDOMADAIRE".equals(type)) r = gen.genererRapportHebdomadaire();
            if ("MENSUEL".equals(type)) r = gen.genererRapportMensuel();

            if (r != null) {
                database.rapportDao().inserer(r);
                RapportEntity finalR = r;

                runOnUiThread(() ->
                        new AlertDialog.Builder(this)
                                .setTitle("\uD83D\uDCCA " + finalR.getTypeRapport())
                                .setMessage(finalR.getContenuRapport())
                                .setPositiveButton("OK", null)
                                .show());
            }
        });
    }

    private void afficherRapportsSauvegardes() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<RapportEntity> rapports =
                    database.rapportDao().getTousLesRapports();

            runOnUiThread(() -> {
                if (rapports.isEmpty()) {
                    Toast.makeText(this,
                            "\uD83D\uDCEB Aucun rapport sauvegardé",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                String[] titres = new String[rapports.size()];
                for (int i = 0; i < rapports.size(); i++) {
                    titres[i] = rapports.get(i).getTypeRapport() +
                            " - " + rapports.get(i).getDateFormatee();
                }

                new AlertDialog.Builder(this)
                        .setTitle("\uD83D\uDCD6 Rapports sauvegardés")
                        .setItems(titres, (d, w) ->
                                new AlertDialog.Builder(this)
                                        .setTitle("\uD83D\uDCCA " +
                                                rapports.get(w).getTypeRapport())
                                        .setMessage(
                                                rapports.get(w).getContenuRapport())
                                        .setPositiveButton("OK", null)
                                        .show())
                        .show();
            });
        });
    }

    private void mettreAJourSituation() {
        Executors.newSingleThreadExecutor().execute(() -> {
            Calendar cal = Calendar.getInstance();
            int mois = cal.get(Calendar.MONTH) + 1;
            int annee = cal.get(Calendar.YEAR);

            Double totalFromDb = database.depenseDao().getTotalMois(mois, annee);
            final double total = (totalFromDb == null) ? 0.0 : totalFromDb;

            final double reste = budgetMensuel - total;

            runOnUiThread(() -> {
                String txt =
                        "\uD83D\uDCB0 SITUATION GLOBALE\n\n" +
                                "Budget : " + budgetMensuel + " FCFA\n" +
                                "Dépenses : " + total + " FCFA\n" +
                                (reste < 0
                                        ? "\uD83D\uDEA8 DÉFICIT : "
                                        : "\u2705 SURPLUS : ") +
                                Math.abs(reste) + " FCFA";

                textSituation.setText(txt);
            });
        });
    }

}