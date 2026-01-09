package com.example.gestionnairebudget;

/**
 * CLASSE CategorieBudget
 *
 * Représente une catégorie de dépenses avec :
 * - Nom de la catégorie
 * - Pourcentage alloué
 * - Montant alloué (calculé depuis le budget)
 * - Montant utilisé
 * - Couleur selon l'utilisation
 */
public class CategorieBudget {

    // ========== ATTRIBUTS ==========
    private String nom;
    private double pourcentageAlloue;    // Ex: 15% pour Projets
    private double montantAlloue;        // Calculé depuis le budget total
    private double montantUtilise;       // Ce qui a été dépensé


    // ========== CONSTRUCTEUR ==========
    /**
     * Crée une nouvelle catégorie de budget
     *
     * @param nom : nom de la catégorie (ex: "Transport")
     * @param pourcentageAlloue : pourcentage du budget (ex: 5.0 pour 5%)
     */
    public CategorieBudget(String nom, double pourcentageAlloue) {
        this.nom = nom;
        this.pourcentageAlloue = pourcentageAlloue;
        this.montantAlloue = 0.0;
        this.montantUtilise = 0.0;
    }


    // ========== MÉTHODES DE CALCUL ==========

    /**
     * Calcule le montant alloué à partir du budget total
     */
    public void calculerMontantAlloue(double budgetTotal) {
        this.montantAlloue = budgetTotal * (pourcentageAlloue / 100.0);
    }

    /**
     * Ajoute une dépense à cette catégorie
     */
    public void ajouterDepense(double montant) {
        this.montantUtilise += montant;
    }

    /**
     * Calcule le pourcentage utilisé
     */
    public double getPourcentageUtilise() {
        if (montantAlloue == 0) {
            return 0.0;
        }
        return (montantUtilise / montantAlloue) * 100.0;
    }

    /**
     * Calcule le montant restant
     */
    public double getMontantRestant() {
        return montantAlloue - montantUtilise;
    }

    /**
     * Détermine la couleur selon l'utilisation
     * VERT : < 50%
     * ORANGE : 50% - 70%
     * ROSE : >= 70%
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
     * Retourne le code couleur Android
     */
    public int getCouleurCode() {
        double pourcentage = getPourcentageUtilise();

        if (pourcentage < 50) {
            return 0xFF4CAF50;  // Vert
        } else if (pourcentage < 70) {
            return 0xFFFF9800;  // Orange
        } else {
            return 0xFFFFB6C1;  // Rose clair
        }
    }

    /**
     * Retourne un emoji selon l'état
     */
    public String getEmoji() {
        double pourcentage = getPourcentageUtilise();

        if (pourcentage < 50) {
            return "✅";
        } else if (pourcentage < 70) {
            return "⚠️";
        } else if (pourcentage < 100) {
            return "🚨";
        } else {
            return "❌";
        }
    }

    /**
     * Vérifie si la catégorie est en dépassement
     */
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


    // ========== MÉTHODE D'AFFICHAGE ==========

    /**
     * Retourne un résumé de la catégorie
     */
    public String afficherResume() {
        return String.format("%s %s (%.0f%%)\nAlloué: %.0f FCFA | Utilisé: %.0f FCFA | Reste: %.0f FCFA\nUtilisation: %.1f%%",
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
 * Gère toutes les catégories prédéfinies
 */
class CategoriesManager {

    /**
     * Crée et retourne toutes les catégories prédéfinies
     * Total = 100%
     */
    public static CategorieBudget[] creerCategoriesParDefaut() {
        return new CategorieBudget[] {
                new CategorieBudget("Projets", 15.0),
                new CategorieBudget("Santé", 5.0),
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

    /**
     * Vérifie que le total des pourcentages = 100%
     */
    public static boolean verifierTotal(CategorieBudget[] categories) {
        double total = 0.0;

        for (int i = 0; i < categories.length; i++) {
            total += categories[i].getPourcentageAlloue();
        }

        return Math.abs(total - 100.0) < 0.01; // Tolérance de 0.01%
    }
}