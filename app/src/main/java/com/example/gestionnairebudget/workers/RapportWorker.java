package com.example.gestionnairebudget.workers;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import com.example.gestionnairebudget.database.AppDatabase;
import com.example.gestionnairebudget.database.RapportEntity;
import com.example.gestionnairebudget.utils.RapportGenerator;
import java.util.Calendar;

/**
 * WORKER POUR GÉNÉRATION AUTOMATIQUE DES RAPPORTS
 *
 * Ce Worker est exécuté automatiquement par Android
 * pour générer les rapports périodiques
 */
public class RapportWorker extends Worker {

    public RapportWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        try {
            // Récupérer le budget depuis SharedPreferences
            SharedPreferences prefs = getApplicationContext()
                    .getSharedPreferences("BudgetPrefs", Context.MODE_PRIVATE);
            double budgetMensuel = Double.longBitsToDouble(prefs.getLong("budgetMensuel", 0));

            // CONDITION : Vérifier que le budget est défini
            if (budgetMensuel <= 0) {
                return Result.success();  // Pas de budget, on ne fait rien
            }

            // Obtenir la base de données
            AppDatabase database = AppDatabase.getInstance(getApplicationContext());

            // Créer le générateur de rapports
            RapportGenerator generator = new RapportGenerator(database, budgetMensuel);

            // Obtenir le type de rapport à générer
            String typeRapport = getInputData().getString("TYPE_RAPPORT");

            RapportEntity rapport = null;

            // CONDITION : Selon le type de rapport demandé
            if ("JOURNALIER".equals(typeRapport)) {
                rapport = generator.genererRapportJournalier();
            } else if ("HEBDOMADAIRE".equals(typeRapport)) {
                rapport = generator.genererRapportHebdomadaire();
            } else if ("MENSUEL".equals(typeRapport)) {
                rapport = generator.genererRapportMensuel();
            }

            // Sauvegarder le rapport dans la base de données
            if (rapport != null) {
                database.rapportDao().inserer(rapport);
            }

            return Result.success();

        } catch (Exception e) {
            e.printStackTrace();
            return Result.failure();
        }
    }
}