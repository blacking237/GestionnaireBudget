package com.example.gestionnairebudget;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
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

/**
 * GESTIONNAIRE DE BUDGET COMPLET
 * Avec base de données SQLite et rapports automatiques
 */
public class MainActivity extends AppCompatActivity {

    // ========== VARIABLES D'INTERFACE ==========
    private EditText editBudget;
    private Spinner spinnerCategorie;
    private EditText editDesignation;
    private EditText editMontant;
    private Button btnDefinirBudget;
    private Button btnAjouterDepense;
    private Button btnVoirHistorique;
    private Button btnGenererRapport;
    private Button btnVoirRapports;
    private TextView textSituation;
    private LinearLayout layoutCategories;
    private TextView textHistorique;

    // ========== VARIABLES DE DONNÉES ==========
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

        // Initialiser la base de données
        database = AppDatabase.getInstance(this);

        // Initialiser SharedPreferences pour sauvegarder le budget
        prefs = getSharedPreferences("BudgetPrefs", MODE_PRIVATE);

        // Charger le budget sauvegardé
        chargerBudgetSauvegarde();

        // Initialiser les catégories
        initialiserCategories();

        // Connecter les vues
        connecterVues();

        // Configurer le Spinner
        configurerSpinner();

        // Configurer les boutons
        configurerBoutons();

        // Si le budget est déjà défini, afficher les catégories
        if (budgetDefini) {
            afficherCategories();
            mettreAJourSituation();
        }
    }


    /**
     * Charger le budget sauvegardé
     */
    private void chargerBudgetSauvegarde() {
        budgetMensuel = Double.longBitsToDouble(prefs.getLong("budgetMensuel", 0));
        if (budgetMensuel > 0) {
            budgetDefini = true;
            editBudget.setText(String.valueOf(budgetMensuel));
        }
    }


    /**
     * Initialiser les catégories
     */
    private void initialiserCategories() {
        categories = CategoriesManager.creerCategoriesParDefaut();

        if (budgetDefini) {
            // Calculer les montants alloués
            for (int i = 0; i < categories.length; i++) {
                categories[i].calculerMontantAlloue(budgetMensuel);
            }

            // Charger les dépenses du mois en cours depuis la BDD
            chargerDepensesMoisEnCours();
        }
    }


    /**
     * Charger les dépenses du mois en cours depuis la base de données
     */
    private void chargerDepensesMoisEnCours() {
        Executors.newSingleThreadExecutor().execute(() -> {
            Calendar cal = Calendar.getInstance();
            int mois = cal.get(Calendar.MONTH) + 1;
            int annee = cal.get(Calendar.YEAR);

            List<DepenseEntity> depensesMois = database.depenseDao().getDepensesParMois(mois, annee);

            // Pour chaque catégorie, calculer le total utilisé
            for (int i = 0; i < categories.length; i++) {
                String nomCategorie = categories[i].getNom();
                double totalCategorie = 0.0;

                for (DepenseEntity depense : depensesMois) {
                    if (depense.getCategorie().equals(nomCategorie)) {
                        totalCategorie += depense.getMontant();
                    }
                }

                categories[i].setMontantUtilise(totalCategorie);
            }

            runOnUiThread(() -> {
                afficherCategories();
                mettreAJourSituation();
            });
        });
    }


    /**
     * Connecter les vues
     */
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
    }


    /**
     * Configurer le Spinner
     */
    private void configurerSpinner() {
        String[] nomsCategories = new String[categories.length];

        for (int i = 0; i < categories.length; i++) {
            nomsCategories[i] = categories[i].getNom() + " (" +
                    categories[i].getPourcentageAlloue() + "%)";
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                nomsCategories
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategorie.setAdapter(adapter);
    }


    /**
     * Configurer les boutons
     */
    private void configurerBoutons() {

        btnDefinirBudget.setOnClickListener(v -> definirBudget());

        btnAjouterDepense.setOnClickListener(v -> ajouterDepense());

        btnVoirHistorique.setOnClickListener(v -> afficherHistorique());

        btnGenererRapport.setOnClickListener(v -> afficherMenuRapports());

        btnVoirRapports.setOnClickListener(v -> afficherRapportsSauvegardes());
    }


    /**
     * Définir le budget
     */
    private void definirBudget() {
        String texte = editBudget.getText().toString();

        if (texte.isEmpty()) {
            Toast.makeText(this, "⚠️ Entrez un montant", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            budgetMensuel = Double.parseDouble(texte);

            if (budgetMensuel <= 0) {
                Toast.makeText(this, "❌ Budget doit être > 0", Toast.LENGTH_SHORT).show();
                return;
            }

            budgetDefini = true;

            // Sauvegarder le budget dans SharedPreferences
            prefs.edit().putLong("budgetMensuel", Double.doubleToRawLongBits(budgetMensuel)).apply();

            // Calculer les montants alloués
            for (int i = 0; i < categories.length; i++) {
                categories[i].calculerMontantAlloue(budgetMensuel);
            }

            // Charger les dépenses existantes
            chargerDepensesMoisEnCours();

            // Planifier les rapports automatiques
            scheduler = new RapportScheduler(this);
            scheduler.planifierTousLesRapports();

            Toast.makeText(this, "✅ Budget défini : " + budgetMensuel + " FCFA\n📊 Rapports automatiques activés", Toast.LENGTH_LONG).show();

            afficherCategories();
            mettreAJourSituation();

        } catch (NumberFormatException e) {
            Toast.makeText(this, "❌ Format invalide", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * Ajouter une dépense
     */
    private void ajouterDepense() {
        if (!budgetDefini) {
            Toast.makeText(this, "⚠️ Définissez d'abord votre budget", Toast.LENGTH_SHORT).show();
            return;
        }

        int positionCategorie = spinnerCategorie.getSelectedItemPosition();
        String designation = editDesignation.getText().toString();
        String montantTexte = editMontant.getText().toString();

        if (designation.isEmpty() || montantTexte.isEmpty()) {
            Toast.makeText(this, "⚠️ Remplissez tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double montant = Double.parseDouble(montantTexte);

            if (montant <= 0) {
                Toast.makeText(this, "❌ Montant doit être > 0", Toast.LENGTH_SHORT).show();
                return;
            }

            CategorieBudget categorieSelectionnee = categories[positionCategorie];
            String nomCategorie = categorieSelectionnee.getNom();

            // Ajouter le montant à la catégorie (en mémoire)
            categorieSelectionnee.ajouterDepense(montant);

            // Créer l'entité pour la BDD
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

            DepenseEntity depenseEntity = new DepenseEntity(
                    nomCategorie,
                    designation,
                    montant,
                    System.currentTimeMillis(),
                    sdf.format(new Date()),
                    cal.get(Calendar.DAY_OF_MONTH),
                    cal.get(Calendar.WEEK_OF_YEAR),
                    cal.get(Calendar.MONTH) + 1,
                    cal.get(Calendar.YEAR)
            );

            // Insérer dans la base de données (en arrière-plan)
            Executors.newSingleThreadExecutor().execute(() -> {
                database.depenseDao().inserer(depenseEntity);
            });

            // Vérifier les seuils
            double pourcentageUtilise = categorieSelectionnee.getPourcentageUtilise();
            if (pourcentageUtilise >= 70 && pourcentageUtilise < 100) {
                Toast.makeText(this,
                        "🚨 ALERTE : " + nomCategorie + " a atteint " +
                                String.format("%.1f", pourcentageUtilise) + "% !",
                        Toast.LENGTH_LONG).show();
            } else if (pourcentageUtilise >= 100) {
                Toast.makeText(this,
                        "❌ DÉPASSEMENT : " + nomCategorie + " est à " +
                                String.format("%.1f", pourcentageUtilise) + "% !",
                        Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "✅ Dépense ajoutée : " + montant + " FCFA", Toast.LENGTH_SHORT).show();
            }

            editDesignation.setText("");
            editMontant.setText("");

            afficherCategories();
            mettreAJourSituation();

        } catch (NumberFormatException e) {
            Toast.makeText(this, "❌ Format invalide", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * Afficher les catégories avec couleurs
     */
    private void afficherCategories() {
        layoutCategories.removeAllViews();

        for (int i = 0; i < categories.length; i++) {
            CategorieBudget cat = categories[i];

            TextView tvCategorie = new TextView(this);
            String texte = cat.afficherResume();
            tvCategorie.setText(texte);
            tvCategorie.setTextSize(14);
            tvCategorie.setPadding(20, 15, 20, 15);
            tvCategorie.setTextColor(Color.BLACK);
            tvCategorie.setBackgroundColor(cat.getCouleurCode());

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 0, 0, 10);
            tvCategorie.setLayoutParams(params);

            layoutCategories.addView(tvCategorie);
        }
    }


    /**
     * Afficher l'historique depuis la BDD
     */
    private void afficherHistorique() {
        Executors.newSingleThreadExecutor().execute(() -> {
            Calendar cal = Calendar.getInstance();
            int mois = cal.get(Calendar.MONTH) + 1;
            int annee = cal.get(Calendar.YEAR);

            List<DepenseEntity> depenses = database.depenseDao().getDepensesParMois(mois, annee);

            runOnUiThread(() -> {
                if (depenses.isEmpty()) {
                    textHistorique.setText("📋 HISTORIQUE\n\nAucune dépense ce mois-ci");
                    return;
                }

                StringBuilder historique = new StringBuilder();
                historique.append("📋 HISTORIQUE DU MOIS\n");
                historique.append("════════════════════════\n\n");

                double total = 0.0;
                for (int i = 0; i < depenses.size(); i++) {
                    DepenseEntity d = depenses.get(i);

                    historique.append("🔸 #").append(i + 1).append("\n");
                    historique.append("   📁 ").append(d.getCategorie()).append("\n");
                    historique.append("   📝 ").append(d.getDesignation()).append("\n");
                    historique.append("   💵 ").append(d.getMontant()).append(" FCFA\n");
                    historique.append("   📅 ").append(d.getDateFormatee()).append("\n\n");

                    total += d.getMontant();
                }

                historique.append("════════════════════════\n");
                historique.append("💰 TOTAL : ").append(String.format("%.0f", total)).append(" FCFA\n");
                historique.append("📊 Transactions : ").append(depenses.size());

                textHistorique.setText(historique.toString());
            });
        });
    }


    /**
     * Menu pour générer les rapports manuellement
     */
    private void afficherMenuRapports() {
        String[] options = {"📅 Rapport Journalier", "📊 Rapport Hebdomadaire", "📈 Rapport Mensuel"};

        new AlertDialog.Builder(this)
                .setTitle("Générer un rapport")
                .setItems(options, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            genererRapportManuel("JOURNALIER");
                            break;
                        case 1:
                            genererRapportManuel("HEBDOMADAIRE");
                            break;
                        case 2:
                            genererRapportManuel("MENSUEL");
                            break;
                    }
                })
                .show();
    }


    /**
     * Générer un rapport manuellement
     */
    private void genererRapportManuel(String type) {
        Toast.makeText(this, "⏳ Génération du rapport...", Toast.LENGTH_SHORT).show();

        Executors.newSingleThreadExecutor().execute(() -> {
            RapportGenerator generator = new RapportGenerator(database, budgetMensuel);
            RapportEntity rapport = null;

            if ("JOURNALIER".equals(type)) {
                rapport = generator.genererRapportJournalier();
            } else if ("HEBDOMADAIRE".equals(type)) {
                rapport = generator.genererRapportHebdomadaire();
            } else if ("MENSUEL".equals(type)) {
                rapport = generator.genererRapportMensuel();
            }

            if (rapport != null) {
                database.rapportDao().inserer(rapport);

                String contenu = rapport.getContenuRapport();

                runOnUiThread(() -> {
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("📊 " + type)
                            .setMessage(contenu)
                            .setPositiveButton("OK", null)
                            .show();
                });
            }
        });
    }


    /**
     * Afficher les rapports sauvegardés
     */
    private void afficherRapportsSauvegardes() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<RapportEntity> rapports = database.rapportDao().getTousLesRapports();

            runOnUiThread(() -> {
                if (rapports.isEmpty()) {
                    Toast.makeText(this, "📭 Aucun rapport sauvegardé", Toast.LENGTH_SHORT).show();
                    return;
                }

                String[] titresRapports = new String[rapports.size()];
                for (int i = 0; i < rapports.size(); i++) {
                    RapportEntity r = rapports.get(i);
                    titresRapports[i] = r.getTypeRapport() + " - " + r.getDateFormatee();
                }

                new AlertDialog.Builder(this)
                        .setTitle("📚 Rapports sauvegardés")
                        .setItems(titresRapports, (dialog, which) -> {
                            RapportEntity rapportSelectionne = rapports.get(which);
                            new AlertDialog.Builder(MainActivity.this)
                                    .setTitle("📊 " + rapportSelectionne.getTypeRapport())
                                    .setMessage(rapportSelectionne.getContenuRapport())
                                    .setPositiveButton("OK", null)
                                    .show();
                        })
                        .setNegativeButton("Fermer", null)
                        .show();
            });
        });
    }


    /**
     * Mettre à jour la situation
     */
    private void mettreAJourSituation() {
        Executors.newSingleThreadExecutor().execute(() -> {
            Calendar cal = Calendar.getInstance();
            int mois = cal.get(Calendar.MONTH) + 1;
            int annee = cal.get(Calendar.YEAR);

            Double totalDepenses = database.depenseDao().getTotalMois(mois, annee);
            if (totalDepenses == null) totalDepenses = 0.0;

            int nbDepenses = database.depenseDao().getNombreDepensesJour(
                    cal.get(Calendar.DAY_OF_MONTH), mois, annee
            );

            double reste = budgetMensuel - totalDepenses;

            final double finalTotal = totalDepenses;
            final int finalNb = nbDepenses;

            runOnUiThread(() -> {
                StringBuilder situation = new StringBuilder();
                situation.append("💰 SITUATION GLOBALE\n");
                situation.append("════════════════════════\n\n");
                situation.append("Budget mensuel : ").append(String.format("%.0f", budgetMensuel)).append(" FCFA\n");
                situation.append("Dépenses totales : ").append(String.format("%.0f", finalTotal)).append(" FCFA\n");
                situation.append("────────────────────────\n");

                if (reste < 0) {
                    situation.append("🚨 DÉFICIT : ").append(String.format("%.0f", Math.abs(reste))).append(" FCFA\n");
                } else {
                    situation.append("✅ SURPLUS : ").append(String.format("%.0f", reste)).append(" FCFA\n");
                }

                double pourcentage = (finalTotal / budgetMensuel) * 100.0;
                situation.append("\n📊 Utilisé : ").append(String.format("%.1f", pourcentage)).append("%\n");
                situation.append("📝 Transactions aujourd'hui : ").append(finalNb);

                textSituation.setText(situation.toString());
            });
        });
    }
}