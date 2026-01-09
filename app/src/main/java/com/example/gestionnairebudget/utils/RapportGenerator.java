package com.example.gestionnairebudget.utils;

import com.example.gestionnairebudget.database.AppDatabase;
import com.example.gestionnairebudget.database.DepenseEntity;
import com.example.gestionnairebudget.database.RapportEntity;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * GÉNÉRATEUR DE RAPPORTS
 *
 * Crée automatiquement des rapports :
 * - Journaliers (chaque jour)
 * - Hebdomadaires (chaque dimanche soir à 23h59)
 * - Mensuels (dernier jour du mois)
 */
public class RapportGenerator {

    private AppDatabase database;
    private double budgetMensuel;

    public RapportGenerator(AppDatabase database, double budgetMensuel) {
        this.database = database;
        this.budgetMensuel = budgetMensuel;
    }


    /**
     * RAPPORT JOURNALIER
     * Génère le rapport des dépenses d'aujourd'hui
     */
    public RapportEntity genererRapportJournalier() {
        Calendar cal = Calendar.getInstance();
        int jour = cal.get(Calendar.DAY_OF_MONTH);
        int mois = cal.get(Calendar.MONTH) + 1;
        int annee = cal.get(Calendar.YEAR);
        int semaine = cal.get(Calendar.WEEK_OF_YEAR);

        // Récupérer les dépenses du jour
        List<DepenseEntity> depensesJour = database.depenseDao().getDepensesParJour(jour, mois, annee);

        // Calculer le total
        double totalJour = 0.0;
        for (DepenseEntity d : depensesJour) {
            totalJour += d.getMontant();
        }

        // Budget journalier = budget mensuel / 30
        double budgetJournalier = budgetMensuel / 30.0;
        double surplus = budgetJournalier - totalJour;

        // Calculer l'évolution par rapport à hier
        double evolutionPourcentage = 0.0;
        cal.add(Calendar.DAY_OF_MONTH, -1);  // Hier
        int jourHier = cal.get(Calendar.DAY_OF_MONTH);
        int moisHier = cal.get(Calendar.MONTH) + 1;
        int anneeHier = cal.get(Calendar.YEAR);

        Double totalHier = database.depenseDao().getTotalJour(jourHier, moisHier, anneeHier);
        if (totalHier != null && totalHier > 0) {
            evolutionPourcentage = ((totalJour - totalHier) / totalHier) * 100.0;
        }

        // Générer le contenu du rapport
        StringBuilder rapport = new StringBuilder();
        rapport.append("📊 RAPPORT JOURNALIER\n");
        rapport.append("═══════════════════════════\n");
        rapport.append("📅 Date : ").append(getCurrentDate()).append("\n\n");

        rapport.append("💰 RÉSUMÉ FINANCIER\n");
        rapport.append("Budget journalier : ").append(String.format("%.0f", budgetJournalier)).append(" FCFA\n");
        rapport.append("Dépenses du jour : ").append(String.format("%.0f", totalJour)).append(" FCFA\n");

        if (surplus >= 0) {
            rapport.append("✅ Surplus : ").append(String.format("%.0f", surplus)).append(" FCFA\n");
        } else {
            rapport.append("❌ Déficit : ").append(String.format("%.0f", Math.abs(surplus))).append(" FCFA\n");
        }

        rapport.append("\n📊 ÉVOLUTION\n");
        if (totalHier != null) {
            if (evolutionPourcentage > 0) {
                rapport.append("📈 +").append(String.format("%.1f", evolutionPourcentage)).append("% par rapport à hier\n");
            } else if (evolutionPourcentage < 0) {
                rapport.append("📉 ").append(String.format("%.1f", evolutionPourcentage)).append("% par rapport à hier\n");
            } else {
                rapport.append("➡️ Même niveau qu'hier\n");
            }
        }

        rapport.append("\n📝 DÉTAILS\n");
        rapport.append("Nombre de transactions : ").append(depensesJour.size()).append("\n");

        // Analyser par catégorie
        rapport.append("\n💳 PAR CATÉGORIE\n");
        rapport.append(analyserParCategorie(depensesJour));

        // Créer l'entité rapport
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

        cal = Calendar.getInstance();  // Remettre à aujourd'hui
        jour = cal.get(Calendar.DAY_OF_MONTH);
        mois = cal.get(Calendar.MONTH) + 1;
        annee = cal.get(Calendar.YEAR);

        return new RapportEntity(
                "JOURNALIER",
                System.currentTimeMillis(),
                sdf.format(new Date()),
                jour,
                semaine,
                mois,
                annee,
                budgetJournalier,
                totalJour,
                surplus,
                depensesJour.size(),
                rapport.toString(),
                evolutionPourcentage
        );
    }


    /**
     * RAPPORT HEBDOMADAIRE
     * Génère le rapport de la semaine (dimanche soir)
     */
    public RapportEntity genererRapportHebdomadaire() {
        Calendar cal = Calendar.getInstance();
        int semaine = cal.get(Calendar.WEEK_OF_YEAR);
        int annee = cal.get(Calendar.YEAR);
        int mois = cal.get(Calendar.MONTH) + 1;
        int jour = cal.get(Calendar.DAY_OF_MONTH);

        // Récupérer les dépenses de la semaine
        List<DepenseEntity> depensesSemaine = database.depenseDao().getDepensesParSemaine(semaine, annee);

        double totalSemaine = 0.0;
        for (DepenseEntity d : depensesSemaine) {
            totalSemaine += d.getMontant();
        }

        // Budget hebdomadaire = budget mensuel / 4
        double budgetHebdo = budgetMensuel / 4.0;
        double surplus = budgetHebdo - totalSemaine;

        // Évolution par rapport à la semaine dernière
        double evolutionPourcentage = 0.0;
        Double totalSemainePrecedente = database.depenseDao().getTotalSemaine(semaine - 1, annee);
        if (totalSemainePrecedente != null && totalSemainePrecedente > 0) {
            evolutionPourcentage = ((totalSemaine - totalSemainePrecedente) / totalSemainePrecedente) * 100.0;
        }

        // Générer le rapport
        StringBuilder rapport = new StringBuilder();
        rapport.append("📊 RAPPORT HEBDOMADAIRE\n");
        rapport.append("═══════════════════════════\n");
        rapport.append("📅 Semaine ").append(semaine).append(" de ").append(annee).append("\n");
        rapport.append("📅 Généré le : ").append(getCurrentDate()).append("\n\n");

        rapport.append("💰 RÉSUMÉ FINANCIER\n");
        rapport.append("Budget hebdomadaire : ").append(String.format("%.0f", budgetHebdo)).append(" FCFA\n");
        rapport.append("Dépenses de la semaine : ").append(String.format("%.0f", totalSemaine)).append(" FCFA\n");

        if (surplus >= 0) {
            rapport.append("✅ Surplus : ").append(String.format("%.0f", surplus)).append(" FCFA\n");
        } else {
            rapport.append("❌ Déficit : ").append(String.format("%.0f", Math.abs(surplus))).append(" FCFA\n");
        }

        rapport.append("\n📊 ÉVOLUTION\n");
        if (totalSemainePrecedente != null) {
            if (evolutionPourcentage > 0) {
                rapport.append("📈 +").append(String.format("%.1f", evolutionPourcentage)).append("% vs semaine précédente\n");
            } else if (evolutionPourcentage < 0) {
                rapport.append("📉 ").append(String.format("%.1f", evolutionPourcentage)).append("% vs semaine précédente\n");
            }
        }

        rapport.append("\n📝 DÉTAILS\n");
        rapport.append("Nombre de transactions : ").append(depensesSemaine.size()).append("\n");
        rapport.append("Moyenne journalière : ").append(String.format("%.0f", totalSemaine / 7.0)).append(" FCFA\n");

        rapport.append("\n💳 PAR CATÉGORIE\n");
        rapport.append(analyserParCategorie(depensesSemaine));

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

        return new RapportEntity(
                "HEBDOMADAIRE",
                System.currentTimeMillis(),
                sdf.format(new Date()),
                jour,
                semaine,
                mois,
                annee,
                budgetHebdo,
                totalSemaine,
                surplus,
                depensesSemaine.size(),
                rapport.toString(),
                evolutionPourcentage
        );
    }


    /**
     * RAPPORT MENSUEL
     */
    public RapportEntity genererRapportMensuel() {
        Calendar cal = Calendar.getInstance();
        int mois = cal.get(Calendar.MONTH) + 1;
        int annee = cal.get(Calendar.YEAR);
        int jour = cal.get(Calendar.DAY_OF_MONTH);
        int semaine = cal.get(Calendar.WEEK_OF_YEAR);

        List<DepenseEntity> depensesMois = database.depenseDao().getDepensesParMois(mois, annee);

        double totalMois = 0.0;
        for (DepenseEntity d : depensesMois) {
            totalMois += d.getMontant();
        }

        double surplus = budgetMensuel - totalMois;

        // Évolution par rapport au mois précédent
        double evolutionPourcentage = 0.0;
        int moisPrecedent = (mois == 1) ? 12 : mois - 1;
        int anneePrecedente = (mois == 1) ? annee - 1 : annee;

        Double totalMoisPrecedent = database.depenseDao().getTotalMois(moisPrecedent, anneePrecedente);
        if (totalMoisPrecedent != null && totalMoisPrecedent > 0) {
            evolutionPourcentage = ((totalMois - totalMoisPrecedent) / totalMoisPrecedent) * 100.0;
        }

        StringBuilder rapport = new StringBuilder();
        rapport.append("📊 RAPPORT MENSUEL\n");
        rapport.append("═══════════════════════════\n");
        rapport.append("📅 Mois : ").append(getNomMois(mois)).append(" ").append(annee).append("\n");
        rapport.append("📅 Généré le : ").append(getCurrentDate()).append("\n\n");

        rapport.append("💰 RÉSUMÉ FINANCIER\n");
        rapport.append("Budget mensuel : ").append(String.format("%.0f", budgetMensuel)).append(" FCFA\n");
        rapport.append("Dépenses du mois : ").append(String.format("%.0f", totalMois)).append(" FCFA\n");

        if (surplus >= 0) {
            rapport.append("✅ Surplus : ").append(String.format("%.0f", surplus)).append(" FCFA\n");
        } else {
            rapport.append("❌ Déficit : ").append(String.format("%.0f", Math.abs(surplus))).append(" FCFA\n");
        }

        double pourcentageUtilise = (totalMois / budgetMensuel) * 100.0;
        rapport.append("📊 Budget utilisé : ").append(String.format("%.1f", pourcentageUtilise)).append("%\n");

        rapport.append("\n📊 ÉVOLUTION\n");
        if (totalMoisPrecedent != null) {
            if (evolutionPourcentage > 0) {
                rapport.append("📈 +").append(String.format("%.1f", evolutionPourcentage)).append("% vs mois précédent\n");
            } else if (evolutionPourcentage < 0) {
                rapport.append("📉 ").append(String.format("%.1f", evolutionPourcentage)).append("% vs mois précédent\n");
            }
        }

        rapport.append("\n📝 DÉTAILS\n");
        rapport.append("Nombre de transactions : ").append(depensesMois.size()).append("\n");
        rapport.append("Moyenne journalière : ").append(String.format("%.0f", totalMois / 30.0)).append(" FCFA\n");

        rapport.append("\n💳 PAR CATÉGORIE\n");
        rapport.append(analyserParCategorie(depensesMois));

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

        return new RapportEntity(
                "MENSUEL",
                System.currentTimeMillis(),
                sdf.format(new Date()),
                jour,
                semaine,
                mois,
                annee,
                budgetMensuel,
                totalMois,
                surplus,
                depensesMois.size(),
                rapport.toString(),
                evolutionPourcentage
        );
    }


    /**
     * MÉTHODE UTILITAIRE : Analyser les dépenses par catégorie
     */
    private String analyserParCategorie(List<DepenseEntity> depenses) {
        // Tableaux pour stocker les catégories et totaux
        String[] categories = new String[10];
        double[] totaux = new double[10];
        int nbCategories = 0;

        // BOUCLE : Parcourir toutes les dépenses
        for (DepenseEntity d : depenses) {
            String cat = d.getCategorie();
            double montant = d.getMontant();

            // Chercher si la catégorie existe
            int index = -1;
            for (int i = 0; i < nbCategories; i++) {
                if (categories[i].equals(cat)) {
                    index = i;
                    break;
                }
            }

            if (index >= 0) {
                totaux[index] += montant;
            } else if (nbCategories < 10) {
                categories[nbCategories] = cat;
                totaux[nbCategories] = montant;
                nbCategories++;
            }
        }

        // Trier par montant décroissant
        for (int i = 0; i < nbCategories - 1; i++) {
            for (int j = i + 1; j < nbCategories; j++) {
                if (totaux[j] > totaux[i]) {
                    double tempTotal = totaux[i];
                    totaux[i] = totaux[j];
                    totaux[j] = tempTotal;

                    String tempCat = categories[i];
                    categories[i] = categories[j];
                    categories[j] = tempCat;
                }
            }
        }

        // Générer le texte
        StringBuilder resultat = new StringBuilder();
        for (int i = 0; i < nbCategories; i++) {
            resultat.append("• ").append(categories[i]).append(" : ")
                    .append(String.format("%.0f", totaux[i])).append(" FCFA\n");
        }

        return resultat.toString();
    }


    // Méthodes utilitaires
    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        return sdf.format(new Date());
    }

    private String getNomMois(int mois) {
        String[] noms = {"Janvier", "Février", "Mars", "Avril", "Mai", "Juin",
                "Juillet", "Août", "Septembre", "Octobre", "Novembre", "Décembre"};
        return noms[mois - 1];
    }
}