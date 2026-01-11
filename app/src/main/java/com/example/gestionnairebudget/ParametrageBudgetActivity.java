package com.example.gestionnairebudget;

import android.os.Bundle;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import java.util.LinkedHashMap;
import java.util.Map;

public class ParametrageBudgetActivity extends AppCompatActivity {

    private LinearLayout containerCategories;

    // Catégories (%)
    private final Map<String, Integer> categories = new LinkedHashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parametrage_budget);

        // LinearLayout RACINE du ScrollView
        containerCategories = findViewById(android.R.id.content)
                .findViewById(R.id.textCategorieTitre)
                .getRootView()
                .findViewById(android.R.id.content);

        initialiserCategories();
        afficherCategories(100000); // Budget exemple
    }

    // =============================
    // CATÉGORIES PAR DÉFAUT
    // =============================
    private void initialiserCategories() {
        categories.put("Projets", 15);
        categories.put("Santé", 5);
        categories.put("Nutrition", 30);
        categories.put("Loyer", 20);
        categories.put("Internet", 3);
        categories.put("Loisirs", 8);
        categories.put("Sport", 5);
        categories.put("Famille", 7);
        categories.put("Autres", 2);
        categories.put("Transport", 5);
    }

    // =============================
    // AFFICHAGE
    // =============================
    private void afficherCategories(double budget) {
        containerCategories.removeAllViews();

        for (Map.Entry<String, Integer> entry : categories.entrySet()) {
            containerCategories.addView(
                    creerCarteCategorie(
                            entry.getKey(),
                            entry.getValue(),
                            budget
                    )
            );
        }
    }

    // =============================
    // CARTE CATÉGORIE
    // =============================
    private CardView creerCarteCategorie(String nom, int pourcentage, double budget) {

        double alloue = budget * pourcentage / 100;

        CardView card = new CardView(this);
        LinearLayout.LayoutParams cardParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
        cardParams.setMargins(0, 0, 0, 16);
        card.setLayoutParams(cardParams);
        card.setRadius(12);
        card.setCardElevation(4);

        // Couleur selon seuil
        if (pourcentage <= 50) {
            card.setCardBackgroundColor(0xFF4CAF50); // Vert
        } else if (pourcentage <= 70) {
            card.setCardBackgroundColor(0xFFFF9800); // Orange
        } else {
            card.setCardBackgroundColor(0xFFFFCDD2); // Rose
        }

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(24, 24, 24, 24);

        TextView titre = new TextView(this);
        titre.setText(nom + " (" + pourcentage + "%)");
        titre.setGravity(Gravity.CENTER);
        titre.setTextAppearance(this, R.style.CategorieTitre);

        TextView details = new TextView(this);
        details.setText(
                "Alloué : " + (int) alloue + " FCFA | " +
                        "Utilisé : 0 FCFA | " +
                        "Reste : " + (int) alloue + " FCFA"
        );

        layout.addView(titre);
        layout.addView(details);
        card.addView(layout);

        return card;
    }
}
