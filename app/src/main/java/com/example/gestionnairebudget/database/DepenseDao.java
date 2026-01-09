package com.example.gestionnairebudget.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Delete;
import java.util.List;

/**
 * DAO (Data Access Object)
 *
 * Interface qui définit toutes les opérations possibles sur la BDD
 * Room génère automatiquement le code d'implémentation
 */
@Dao
public interface DepenseDao {

    // ========== INSERTION ==========

    /**
     * Insérer une nouvelle dépense
     */
    @Insert
    void inserer(DepenseEntity depense);

    /**
     * Insérer plusieurs dépenses
     */
    @Insert
    void insererTout(List<DepenseEntity> depenses);


    // ========== REQUÊTES DE LECTURE ==========

    /**
     * Récupérer TOUTES les dépenses
     */
    @Query("SELECT * FROM depenses ORDER BY timestamp DESC")
    List<DepenseEntity> getToutesLesDepenses();

    /**
     * Récupérer les dépenses d'un jour spécifique
     */
    @Query("SELECT * FROM depenses WHERE jour = :jour AND mois = :mois AND annee = :annee ORDER BY timestamp DESC")
    List<DepenseEntity> getDepensesParJour(int jour, int mois, int annee);

    /**
     * Récupérer les dépenses d'une semaine
     */
    @Query("SELECT * FROM depenses WHERE semaine = :semaine AND annee = :annee ORDER BY timestamp DESC")
    List<DepenseEntity> getDepensesParSemaine(int semaine, int annee);

    /**
     * Récupérer les dépenses d'un mois
     */
    @Query("SELECT * FROM depenses WHERE mois = :mois AND annee = :annee ORDER BY timestamp DESC")
    List<DepenseEntity> getDepensesParMois(int mois, int annee);

    /**
     * Récupérer les dépenses d'une année
     */
    @Query("SELECT * FROM depenses WHERE annee = :annee ORDER BY timestamp DESC")
    List<DepenseEntity> getDepensesParAnnee(int annee);

    /**
     * Récupérer les dépenses d'une catégorie
     */
    @Query("SELECT * FROM depenses WHERE categorie = :categorie ORDER BY timestamp DESC")
    List<DepenseEntity> getDepensesParCategorie(String categorie);

    /**
     * Récupérer les dépenses d'une catégorie pour un mois donné
     */
    @Query("SELECT * FROM depenses WHERE categorie = :categorie AND mois = :mois AND annee = :annee")
    List<DepenseEntity> getDepensesCategorieParMois(String categorie, int mois, int annee);


    // ========== CALCULS ==========

    /**
     * Calculer le total des dépenses d'un jour
     */
    @Query("SELECT SUM(montant) FROM depenses WHERE jour = :jour AND mois = :mois AND annee = :annee")
    Double getTotalJour(int jour, int mois, int annee);

    /**
     * Calculer le total des dépenses d'une semaine
     */
    @Query("SELECT SUM(montant) FROM depenses WHERE semaine = :semaine AND annee = :annee")
    Double getTotalSemaine(int semaine, int annee);

    /**
     * Calculer le total des dépenses d'un mois
     */
    @Query("SELECT SUM(montant) FROM depenses WHERE mois = :mois AND annee = :annee")
    Double getTotalMois(int mois, int annee);

    /**
     * Calculer le total d'une catégorie pour un mois
     */
    @Query("SELECT SUM(montant) FROM depenses WHERE categorie = :categorie AND mois = :mois AND annee = :annee")
    Double getTotalCategorieParMois(String categorie, int mois, int annee);

    /**
     * Compter le nombre de dépenses d'un jour
     */
    @Query("SELECT COUNT(*) FROM depenses WHERE jour = :jour AND mois = :mois AND annee = :annee")
    int getNombreDepensesJour(int jour, int mois, int annee);

    /**
     * Compter le nombre total de dépenses
     */
    @Query("SELECT COUNT(*) FROM depenses")
    int getNombreTotalDepenses();


    // ========== SUPPRESSION ==========

    /**
     * Supprimer une dépense
     */
    @Delete
    void supprimer(DepenseEntity depense);

    /**
     * Supprimer toutes les dépenses
     */
    @Query("DELETE FROM depenses")
    void supprimerTout();

    /**
     * Supprimer les dépenses d'un mois
     */
    @Query("DELETE FROM depenses WHERE mois = :mois AND annee = :annee")
    void supprimerDepensesMois(int mois, int annee);
}