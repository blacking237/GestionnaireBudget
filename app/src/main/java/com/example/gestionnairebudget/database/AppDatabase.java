package com.example.gestionnairebudget.database;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

/**
 * BASE DE DONNÉES PRINCIPALE
 *
 * Cette classe configure la base de données Room
 * Pattern Singleton : une seule instance pour toute l'application
 */
@Database(entities = {DepenseEntity.class, RapportEntity.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    // Instance unique (Singleton)
    private static AppDatabase instance;

    // Méthodes abstraites pour obtenir les DAOs
    public abstract DepenseDao depenseDao();
    public abstract RapportDao rapportDao();


    /**
     * Obtenir l'instance de la base de données (Singleton)
     * CONDITION : Si l'instance n'existe pas, on la crée
     */
    public static synchronized AppDatabase getInstance(Context context) {
        // CONDITION : Vérifier si l'instance existe déjà
        if (instance == null) {
            // Créer la base de données
            instance = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "gestionnaire_budget_db"  // Nom de la BDD
                    )
                    .fallbackToDestructiveMigration()  // En cas de changement de version
                    .build();
        }

        return instance;
    }
}