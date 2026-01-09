package com.example.gestionnairebudget.utils;

import android.content.Context;
import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import com.example.gestionnairebudget.workers.RapportWorker;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

/**
 * PLANIFICATEUR DE RAPPORTS
 *
 * Configure l'exécution automatique des rapports :
 * - Journalier : chaque jour à 23h59
 * - Hebdomadaire : chaque dimanche à 23h59
 * - Mensuel : dernier jour du mois à 23h59
 */
public class RapportScheduler {

    private Context context;

    public RapportScheduler(Context context) {
        this.context = context;
    }


    /**
     * Planifier TOUS les rapports automatiques
     */
    public void planifierTousLesRapports() {
        planifierRapportJournalier();
        planifierRapportHebdomadaire();
        planifierRapportMensuel();
    }


    /**
     * RAPPORT JOURNALIER : Chaque jour à 23h59
     */
    public void planifierRapportJournalier() {
        // Calculer le délai jusqu'à 23h59 aujourd'hui
        long delai = calculerDelaiJusquaHeure(23, 59);

        // Créer les données pour le Worker
        Data inputData = new Data.Builder()
                .putString("TYPE_RAPPORT", "JOURNALIER")
                .build();

        // Créer la requête périodique (toutes les 24 heures)
        PeriodicWorkRequest rapportJournalierRequest =
                new PeriodicWorkRequest.Builder(
                        RapportWorker.class,
                        24,  // Répéter toutes les 24 heures
                        TimeUnit.HOURS
                )
                        .setInitialDelay(delai, TimeUnit.MILLISECONDS)
                        .setInputData(inputData)
                        .build();

        // Enregistrer dans WorkManager
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                "RapportJournalier",
                ExistingPeriodicWorkPolicy.KEEP,  // Garder si existe déjà
                rapportJournalierRequest
        );
    }


    /**
     * RAPPORT HEBDOMADAIRE : Chaque dimanche à 23h59
     */
    public void planifierRapportHebdomadaire() {
        // Calculer le délai jusqu'au prochain dimanche à 23h59
        long delai = calculerDelaiJusquaDimanche();

        Data inputData = new Data.Builder()
                .putString("TYPE_RAPPORT", "HEBDOMADAIRE")
                .build();

        // Répéter toutes les 7 jours (1 semaine)
        PeriodicWorkRequest rapportHebdoRequest =
                new PeriodicWorkRequest.Builder(
                        RapportWorker.class,
                        7,  // Toutes les 7 jours
                        TimeUnit.DAYS
                )
                        .setInitialDelay(delai, TimeUnit.MILLISECONDS)
                        .setInputData(inputData)
                        .build();

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                "RapportHebdomadaire",
                ExistingPeriodicWorkPolicy.KEEP,
                rapportHebdoRequest
        );
    }


    /**
     * RAPPORT MENSUEL : Dernier jour du mois à 23h59
     */
    public void planifierRapportMensuel() {
        // Calculer le délai jusqu'au dernier jour du mois
        long delai = calculerDelaiJusquaDernierJourMois();

        Data inputData = new Data.Builder()
                .putString("TYPE_RAPPORT", "MENSUEL")
                .build();

        // Répéter toutes les 30 jours (approximatif)
        PeriodicWorkRequest rapportMensuelRequest =
                new PeriodicWorkRequest.Builder(
                        RapportWorker.class,
                        30,  // Toutes les 30 jours
                        TimeUnit.DAYS
                )
                        .setInitialDelay(delai, TimeUnit.MILLISECONDS)
                        .setInputData(inputData)
                        .build();

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                "RapportMensuel",
                ExistingPeriodicWorkPolicy.KEEP,
                rapportMensuelRequest
        );
    }


    /**
     * ANNULER tous les rapports automatiques
     */
    public void annulerTousLesRapports() {
        WorkManager workManager = WorkManager.getInstance(context);
        workManager.cancelUniqueWork("RapportJournalier");
        workManager.cancelUniqueWork("RapportHebdomadaire");
        workManager.cancelUniqueWork("RapportMensuel");
    }


    // ========== MÉTHODES UTILITAIRES DE CALCUL ==========

    /**
     * Calculer le délai jusqu'à une heure précise aujourd'hui
     */
    private long calculerDelaiJusquaHeure(int heure, int minute) {
        Calendar maintenant = Calendar.getInstance();
        Calendar cible = Calendar.getInstance();

        cible.set(Calendar.HOUR_OF_DAY, heure);
        cible.set(Calendar.MINUTE, minute);
        cible.set(Calendar.SECOND, 0);
        cible.set(Calendar.MILLISECOND, 0);

        // Si l'heure est déjà passée aujourd'hui, passer à demain
        if (cible.before(maintenant)) {
            cible.add(Calendar.DAY_OF_MONTH, 1);
        }

        return cible.getTimeInMillis() - maintenant.getTimeInMillis();
    }


    /**
     * Calculer le délai jusqu'au prochain dimanche à 23h59
     */
    private long calculerDelaiJusquaDimanche() {
        Calendar maintenant = Calendar.getInstance();
        Calendar prochainDimanche = Calendar.getInstance();

        // Jour de la semaine actuel (1 = Dimanche, 2 = Lundi, ..., 7 = Samedi)
        int jourActuel = maintenant.get(Calendar.DAY_OF_WEEK);

        // CONDITION : Calculer les jours jusqu'à dimanche
        int joursJusquaDimanche;
        if (jourActuel == Calendar.SUNDAY) {
            // Si c'est dimanche, vérifier l'heure
            if (maintenant.get(Calendar.HOUR_OF_DAY) < 23 ||
                    (maintenant.get(Calendar.HOUR_OF_DAY) == 23 && maintenant.get(Calendar.MINUTE) < 59)) {
                joursJusquaDimanche = 0;  // Aujourd'hui
            } else {
                joursJusquaDimanche = 7;  // Dimanche prochain
            }
        } else {
            // Calculer les jours restants jusqu'à dimanche
            joursJusquaDimanche = (Calendar.SUNDAY + 7 - jourActuel) % 7;
            if (joursJusquaDimanche == 0) joursJusquaDimanche = 7;
        }

        prochainDimanche.add(Calendar.DAY_OF_MONTH, joursJusquaDimanche);
        prochainDimanche.set(Calendar.HOUR_OF_DAY, 23);
        prochainDimanche.set(Calendar.MINUTE, 59);
        prochainDimanche.set(Calendar.SECOND, 0);
        prochainDimanche.set(Calendar.MILLISECOND, 0);

        return prochainDimanche.getTimeInMillis() - maintenant.getTimeInMillis();
    }


    /**
     * Calculer le délai jusqu'au dernier jour du mois à 23h59
     */
    private long calculerDelaiJusquaDernierJourMois() {
        Calendar maintenant = Calendar.getInstance();
        Calendar dernierJour = Calendar.getInstance();

        // Obtenir le dernier jour du mois actuel
        int dernierJourMois = maintenant.getActualMaximum(Calendar.DAY_OF_MONTH);
        int jourActuel = maintenant.get(Calendar.DAY_OF_MONTH);

        // CONDITION : Si on est déjà au dernier jour
        if (jourActuel == dernierJourMois) {
            // Vérifier l'heure
            if (maintenant.get(Calendar.HOUR_OF_DAY) < 23 ||
                    (maintenant.get(Calendar.HOUR_OF_DAY) == 23 && maintenant.get(Calendar.MINUTE) < 59)) {
                // C'est aujourd'hui
                dernierJour.set(Calendar.HOUR_OF_DAY, 23);
                dernierJour.set(Calendar.MINUTE, 59);
            } else {
                // Passer au dernier jour du mois prochain
                dernierJour.add(Calendar.MONTH, 1);
                dernierJour.set(Calendar.DAY_OF_MONTH, dernierJour.getActualMaximum(Calendar.DAY_OF_MONTH));
                dernierJour.set(Calendar.HOUR_OF_DAY, 23);
                dernierJour.set(Calendar.MINUTE, 59);
            }
        } else {
            // Aller au dernier jour du mois actuel
            dernierJour.set(Calendar.DAY_OF_MONTH, dernierJourMois);
            dernierJour.set(Calendar.HOUR_OF_DAY, 23);
            dernierJour.set(Calendar.MINUTE, 59);
        }

        dernierJour.set(Calendar.SECOND, 0);
        dernierJour.set(Calendar.MILLISECOND, 0);

        return dernierJour.getTimeInMillis() - maintenant.getTimeInMillis();
    }
}