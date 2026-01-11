package com.example.gestionnairebudget.database;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

/**
 * ===========================================================
 * CLASSE : AppDatabase
 * ===========================================================
 *
 *  ROLE :
 * Cette classe configure la BASE DE DONNEES locale de l'application.
 * Elle utilise Room, une bibliotheque qui facilite SQLite sur Android.
 *
 *  TABLES INCLUSES :
 * 1. users                     -> Informations des utilisateurs
 * 2. categories_personnalisees -> Categories de budget personnalisees
 * 3. depenses                  -> Toutes les depenses enregistrees
 * 4. rapports                  -> Rapports generes automatiquement
 *
 *  PATTERN SINGLETON :
 * Une SEULE instance de la base de donnees pour toute l'application.
 * Cela evite les conflits et economise la memoire.
 *
 *  ERREURS COURANTES :
 * - Oublier d'ajouter une entite dans @Database -> Crash au demarrage
 * - Ne pas incrementer "version" apres modification -> Donnees perdues
 * - Creer plusieurs instances -> Conflits de donnees
 */

@Database(
        entities = {
                UserEntity.class,
                CategoriePersonnaliseeEntity.class,
                DepenseEntity.class,
                RapportEntity.class
        },
        version = 2,
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {

    // ===========================================================
    // SINGLETON : Une seule instance pour toute l'application
    // ===========================================================

    /**
     * Instance unique de la base de donnees
     * "private static" = partagee par toute l'application
     * "volatile" = evite les problemes multi-threads
     */
    private static volatile AppDatabase instance;

    // ===========================================================
    // DAOs : Objets d'acces aux donnees
    // ===========================================================

    public abstract UserDao userDao();

    public abstract CategorieDao categorieDao();

    public abstract DepenseDao depenseDao();

    public abstract RapportDao rapportDao();

    // ===========================================================
    // METHODE SINGLETON : Recuperer l'instance
    // ===========================================================

    /**
     * Recuperer l'instance unique de la base de donnees
     *
     *  FONCTIONNEMENT :
     * 1. Si l'instance n'existe pas -> Creation
     * 2. Sinon -> Retour de l'instance existante
     *
     * @param context Contexte Android
     * @return Instance unique de la BDD
     */
    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "gestionnaire_budget_v2_db"
                    )
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }

    // ===========================================================
    // NOTES POUR DEBUTANTS
    // ===========================================================

    /**
     *  POURQUOI UTILISER ROOM ?
     *
     * Sans Room :
     * - Beaucoup de SQL
     * - Risque d'erreurs
     * - Code difficile a maintenir
     *
     * Avec Room :
     * - Code clair
     * - Verification a la compilation
     * - Moins de bugs
     *
     *  REGLE IMPORTANTE :
     * JAMAIS de requetes BDD sur le thread UI !
     * Toujours utiliser Executors.
     */
}
