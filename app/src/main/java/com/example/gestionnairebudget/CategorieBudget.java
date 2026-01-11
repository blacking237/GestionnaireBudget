package com.example.gestionnairebudget;

/**
 * CLASSE CategorieBudget
 *
 * Represente une categorie de depenses avec :
 * - Nom de la categorie
 * - Pourcentage alloue
 * - Montant alloue
 * - Montant utilise
 * - Couleur selon l'utilisation
 */
public class CategorieBudget {

    // ========== ATTRIBUTS ==========
    private String nom;
    private double pourcentageAlloue;
    private double montantAlloue;
    private double montantUtilise;

    // ========== CONSTRUCTEUR ==========
    public CategorieBudget(String nom, double pourcentageAlloue) {
        this.nom = nom;
        this.pourcentageAlloue = pourcentageAlloue;
        this.montantAlloue = 0.0;
        this.montantUtilise = 0.0;
    }

    // ========== METHODES DE CALCUL ==========
    public void calculerMontantAlloue(double budgetTotal) {
        this.montantAlloue = budgetTotal * (pourcentageAlloue / 100.0);
    }

    public void ajouterDepense(double montant) {
        this.montantUtilise += montant;
    }

    public double getPourcentageUtilise() {
        if (montantAlloue == 0) {
            return 0.0;
        }
        return (montantUtilise / montantAlloue) * 100.0;
    }

    public double getMontantRestant() {
        return montantAlloue - montantUtilise;
    }

    /**
     * Couleur logique (texte)
     */
    public String getCouleur() {
        double pourcentage = getPourcentageUtilise();

        if (pourcentage < 50) {
            return "VERT";
        } else if (pourcentage < 70) {
            return "ORANGE";
        } else {
            return "ROSE";
        }
    }

    /**
     * Code couleur Android
     */
    public int getCouleurCode() {
        double pourcentage = getPourcentageUtilise();

        if (pourcentage < 50) {
            return 0xFF4CAF50; // Vert
        } else if (pourcentage < 70) {
            return 0xFFFF9800; // Orange
        } else {
            return 0xFFFFB6C1; // Rose
        }
    }

    /**
     * Emoji selon l'etat (Unicode safe)
     */
    public String getEmoji() {
        double pourcentage = getPourcentageUtilise();

        if (pourcentage < 50) {
            return "\u2705";
        } else if (pourcentage < 70) {
            return "\u26A0";
        } else if (pourcentage < 100) {
            return "\uD83D\uDEA8";
        } else {
            return "\u274C";
        }
    }

    public boolean estEnDepassement() {
        return montantUtilise > montantAlloue;
    }

    // ========== GETTERS ==========
    public String getNom() {
        return nom;
    }

    public double getPourcentageAlloue() {
        return pourcentageAlloue;
    }

    public double getMontantAlloue() {
        return montantAlloue;
    }

    public double getMontantUtilise() {
        return montantUtilise;
    }

    // ========== SETTERS ==========
    public void setMontantUtilise(double montantUtilise) {
        this.montantUtilise = montantUtilise;
    }

    public void reinitialiser() {
        this.montantUtilise = 0.0;
    }

    // ========== AFFICHAGE ==========
    public String afficherResume() {
        return String.format(
                "%s %s (%.0f%%)\nAlloue: %.0f FCFA | Utilise: %.0f FCFA | Reste: %.0f FCFA\nUtilisation: %.1f%%",
                getEmoji(),
                nom,
                pourcentageAlloue,
                montantAlloue,
                montantUtilise,
                getMontantRestant(),
                getPourcentageUtilise()
        );
    }
}

/**
 * CLASSE UTILITAIRE : CategoriesManager
 */
class CategoriesManager {

    public static CategorieBudget[] creerCategoriesParDefaut() {
        return new CategorieBudget[]{
                new CategorieBudget("Projets", 15.0),
                new CategorieBudget("Sante", 5.0),
                new CategorieBudget("Nutrition", 30.0),
                new CategorieBudget("Loyer", 20.0),
                new CategorieBudget("Internet", 3.0),
                new CategorieBudget("Loisirs", 8.0),
                new CategorieBudget("Sport", 5.0),
                new CategorieBudget("Famille", 7.0),
                new CategorieBudget("Autres", 2.0),
                new CategorieBudget("Transport", 5.0)
        };
    }

    public static boolean verifierTotal(CategorieBudget[] categories) {
        double total = 0.0;

        for (int i = 0; i < categories.length; i++) {
            total += categories[i].getPourcentageAlloue();
        }

        return Math.abs(total - 100.0) < 0.01;
    }
}
