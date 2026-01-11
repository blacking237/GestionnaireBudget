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
 * GENERATEUR DE RAPPORTS
 *
 * Cree automatiquement des rapports :
 * - Journaliers (chaque jour)
 * - Hebdomadaires (chaque dimanche soir a 23h59)
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
     * Genere le rapport des depenses d'aujourd'hui
     */
    public RapportEntity genererRapportJournalier() {
        Calendar cal = Calendar.getInstance();
        int jour = cal.get(Calendar.DAY_OF_MONTH);
        int mois = cal.get(Calendar.MONTH) + 1;
        int annee = cal.get(Calendar.YEAR);
        int semaine = cal.get(Calendar.WEEK_OF_YEAR);

        List<DepenseEntity> depensesJour =
                database.depenseDao().getDepensesParJour(jour, mois, annee);

        double totalJour = 0.0;
        for (DepenseEntity d : depensesJour) {
            totalJour += d.getMontant();
        }

        double budgetJournalier = budgetMensuel / 30.0;
        double surplus = budgetJournalier - totalJour;

        double evolutionPourcentage = 0.0;
        cal.add(Calendar.DAY_OF_MONTH, -1);

        int jourHier = cal.get(Calendar.DAY_OF_MONTH);
        int moisHier = cal.get(Calendar.MONTH) + 1;
        int anneeHier = cal.get(Calendar.YEAR);

        Double totalHier =
                database.depenseDao().getTotalJour(jourHier, moisHier, anneeHier);

        if (totalHier != null && totalHier > 0) {
            evolutionPourcentage =
                    ((totalJour - totalHier) / totalHier) * 100.0;
        }

        StringBuilder rapport = new StringBuilder();
        rapport.append("\uD83D\uDCCA RAPPORT JOURNALIER\n");
        rapport.append("================================\n");
        rapport.append("\uD83D\uDCC5 Date : ")
                .append(getCurrentDate()).append("\n\n");

        rapport.append("\uD83D\uDCB0 RESUME FINANCIER\n");
        rapport.append("Budget journalier : ")
                .append(String.format("%.0f", budgetJournalier))
                .append(" FCFA\n");
        rapport.append("Depenses du jour : ")
                .append(String.format("%.0f", totalJour))
                .append(" FCFA\n");

        if (surplus >= 0) {
            rapport.append("\u2705 Surplus : ")
                    .append(String.format("%.0f", surplus))
                    .append(" FCFA\n");
        } else {
            rapport.append("\u274C Deficit : ")
                    .append(String.format("%.0f", Math.abs(surplus)))
                    .append(" FCFA\n");
        }

        rapport.append("\n\uD83D\uDCCA EVOLUTION\n");
        if (totalHier != null) {
            if (evolutionPourcentage > 0) {
                rapport.append("\uD83D\uDCC8 +")
                        .append(String.format("%.1f", evolutionPourcentage))
                        .append("% par rapport a hier\n");
            } else if (evolutionPourcentage < 0) {
                rapport.append("\uD83D\uDCC9 ")
                        .append(String.format("%.1f", evolutionPourcentage))
                        .append("% par rapport a hier\n");
            } else {
                rapport.append("\u27A1 Meme niveau qu'hier\n");
            }
        }

        rapport.append("\n\uD83D\uDCDD DETAILS\n");
        rapport.append("Nombre de transactions : ")
                .append(depensesJour.size()).append("\n");

        rapport.append("\n\uD83D\uDCB3 PAR CATEGORIE\n");
        rapport.append(analyserParCategorie(depensesJour));

        SimpleDateFormat sdf =
                new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

        cal = Calendar.getInstance();

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
     */
    public RapportEntity genererRapportHebdomadaire() {
        Calendar cal = Calendar.getInstance();
        int semaine = cal.get(Calendar.WEEK_OF_YEAR);
        int annee = cal.get(Calendar.YEAR);
        int mois = cal.get(Calendar.MONTH) + 1;
        int jour = cal.get(Calendar.DAY_OF_MONTH);

        List<DepenseEntity> depensesSemaine =
                database.depenseDao().getDepensesParSemaine(semaine, annee);

        double totalSemaine = 0.0;
        for (DepenseEntity d : depensesSemaine) {
            totalSemaine += d.getMontant();
        }

        double budgetHebdo = budgetMensuel / 4.0;
        double surplus = budgetHebdo - totalSemaine;

        double evolutionPourcentage = 0.0;
        Double totalSemainePrecedente =
                database.depenseDao().getTotalSemaine(semaine - 1, annee);

        if (totalSemainePrecedente != null && totalSemainePrecedente > 0) {
            evolutionPourcentage =
                    ((totalSemaine - totalSemainePrecedente)
                            / totalSemainePrecedente) * 100.0;
        }

        StringBuilder rapport = new StringBuilder();
        rapport.append("\uD83D\uDCCA RAPPORT HEBDOMADAIRE\n");
        rapport.append("================================\n");
        rapport.append("\uD83D\uDCC5 Semaine ")
                .append(semaine).append(" - ").append(annee).append("\n");
        rapport.append("\uD83D\uDCC5 Genere le : ")
                .append(getCurrentDate()).append("\n\n");

        rapport.append("\uD83D\uDCB0 RESUME FINANCIER\n");
        rapport.append("Budget hebdomadaire : ")
                .append(String.format("%.0f", budgetHebdo))
                .append(" FCFA\n");
        rapport.append("Depenses semaine : ")
                .append(String.format("%.0f", totalSemaine))
                .append(" FCFA\n");

        if (surplus >= 0) {
            rapport.append("\u2705 Surplus : ")
                    .append(String.format("%.0f", surplus))
                    .append(" FCFA\n");
        } else {
            rapport.append("\u274C Deficit : ")
                    .append(String.format("%.0f", Math.abs(surplus)))
                    .append(" FCFA\n");
        }

        rapport.append("\n\uD83D\uDCCA EVOLUTION\n");
        if (totalSemainePrecedente != null) {
            if (evolutionPourcentage > 0) {
                rapport.append("\uD83D\uDCC8 +")
                        .append(String.format("%.1f", evolutionPourcentage))
                        .append("% vs semaine precedente\n");
            } else if (evolutionPourcentage < 0) {
                rapport.append("\uD83D\uDCC9 ")
                        .append(String.format("%.1f", evolutionPourcentage))
                        .append("% vs semaine precedente\n");
            }
        }

        rapport.append("\n\uD83D\uDCDD DETAILS\n");
        rapport.append("Transactions : ")
                .append(depensesSemaine.size()).append("\n");
        rapport.append("Moyenne journaliere : ")
                .append(String.format("%.0f", totalSemaine / 7.0))
                .append(" FCFA\n");

        rapport.append("\n\uD83D\uDCB3 PAR CATEGORIE\n");
        rapport.append(analyserParCategorie(depensesSemaine));

        SimpleDateFormat sdf =
                new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

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

        List<DepenseEntity> depensesMois =
                database.depenseDao().getDepensesParMois(mois, annee);

        double totalMois = 0.0;
        for (DepenseEntity d : depensesMois) {
            totalMois += d.getMontant();
        }

        double surplus = budgetMensuel - totalMois;

        double evolutionPourcentage = 0.0;
        int moisPrecedent = (mois == 1) ? 12 : mois - 1;
        int anneePrecedente = (mois == 1) ? annee - 1 : annee;

        Double totalMoisPrecedent =
                database.depenseDao().getTotalMois(moisPrecedent, anneePrecedente);

        if (totalMoisPrecedent != null && totalMoisPrecedent > 0) {
            evolutionPourcentage =
                    ((totalMois - totalMoisPrecedent)
                            / totalMoisPrecedent) * 100.0;
        }

        StringBuilder rapport = new StringBuilder();
        rapport.append("\uD83D\uDCCA RAPPORT MENSUEL\n");
        rapport.append("================================\n");
        rapport.append("\uD83D\uDCC5 Mois : ")
                .append(getNomMois(mois)).append(" ").append(annee).append("\n");
        rapport.append("\uD83D\uDCC5 Genere le : ")
                .append(getCurrentDate()).append("\n\n");

        rapport.append("\uD83D\uDCB0 RESUME FINANCIER\n");
        rapport.append("Budget mensuel : ")
                .append(String.format("%.0f", budgetMensuel))
                .append(" FCFA\n");
        rapport.append("Depenses du mois : ")
                .append(String.format("%.0f", totalMois))
                .append(" FCFA\n");

        if (surplus >= 0) {
            rapport.append("\u2705 Surplus : ")
                    .append(String.format("%.0f", surplus))
                    .append(" FCFA\n");
        } else {
            rapport.append("\u274C Deficit : ")
                    .append(String.format("%.0f", Math.abs(surplus)))
                    .append(" FCFA\n");
        }

        rapport.append("Budget utilise : ")
                .append(String.format("%.1f",
                        (totalMois / budgetMensuel) * 100.0))
                .append("%\n");

        rapport.append("\n\uD83D\uDCCA EVOLUTION\n");
        if (totalMoisPrecedent != null) {
            if (evolutionPourcentage > 0) {
                rapport.append("\uD83D\uDCC8 +")
                        .append(String.format("%.1f", evolutionPourcentage))
                        .append("% vs mois precedent\n");
            } else if (evolutionPourcentage < 0) {
                rapport.append("\uD83D\uDCC9 ")
                        .append(String.format("%.1f", evolutionPourcentage))
                        .append("% vs mois precedent\n");
            }
        }

        rapport.append("\n\uD83D\uDCDD DETAILS\n");
        rapport.append("Transactions : ")
                .append(depensesMois.size()).append("\n");
        rapport.append("Moyenne journaliere : ")
                .append(String.format("%.0f", totalMois / 30.0))
                .append(" FCFA\n");

        rapport.append("\n\uD83D\uDCB3 PAR CATEGORIE\n");
        rapport.append(analyserParCategorie(depensesMois));

        SimpleDateFormat sdf =
                new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

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
     * Analyse des depenses par categorie
     */
    private String analyserParCategorie(List<DepenseEntity> depenses) {
        String[] categories = new String[10];
        double[] totaux = new double[10];
        int nbCategories = 0;

        for (DepenseEntity d : depenses) {
            String cat = d.getCategorie();
            double montant = d.getMontant();

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

        for (int i = 0; i < nbCategories - 1; i++) {
            for (int j = i + 1; j < nbCategories; j++) {
                if (totaux[j] > totaux[i]) {
                    double t = totaux[i];
                    totaux[i] = totaux[j];
                    totaux[j] = t;

                    String c = categories[i];
                    categories[i] = categories[j];
                    categories[j] = c;
                }
            }
        }

        StringBuilder resultat = new StringBuilder();
        for (int i = 0; i < nbCategories; i++) {
            resultat.append("- ")
                    .append(categories[i])
                    .append(" : ")
                    .append(String.format("%.0f", totaux[i]))
                    .append(" FCFA\n");
        }

        return resultat.toString();
    }

    private String getCurrentDate() {
        SimpleDateFormat sdf =
                new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        return sdf.format(new Date());
    }

    private String getNomMois(int mois) {
        String[] noms = {
                "Janvier", "Fevrier", "Mars", "Avril", "Mai", "Juin",
                "Juillet", "Aout", "Septembre", "Octobre", "Novembre", "Decembre"
        };
        return noms[mois - 1];
    }
}
