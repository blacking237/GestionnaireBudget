package com.example.gestionnairebudget.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

/**
 * DAO pour les rapports
 */
@Dao
public interface RapportDao {

    // ========== INSERTION ==========

    @Insert
    void inserer(RapportEntity rapport);


    // ========== LECTURE ==========

    /**
     * Récupérer tous les rapports
     */
    @Query("SELECT * FROM rapports ORDER BY dateCreation DESC")
    List<RapportEntity> getTousLesRapports();

    /**
     * Récupérer les rapports journaliers
     */
    @Query("SELECT * FROM rapports WHERE typeRapport = 'JOURNALIER' ORDER BY dateCreation DESC")
    List<RapportEntity> getRapportsJournaliers();

    /**
     * Récupérer les rapports hebdomadaires
     */
    @Query("SELECT * FROM rapports WHERE typeRapport = 'HEBDOMADAIRE' ORDER BY dateCreation DESC")
    List<RapportEntity> getRapportsHebdomadaires();

    /**
     * Récupérer les rapports mensuels
     */
    @Query("SELECT * FROM rapports WHERE typeRapport = 'MENSUEL' ORDER BY dateCreation DESC")
    List<RapportEntity> getRapportsMensuels();

    /**
     * Récupérer le dernier rapport journalier
     */
    @Query("SELECT * FROM rapports WHERE typeRapport = 'JOURNALIER' ORDER BY dateCreation DESC LIMIT 1")
    RapportEntity getDernierRapportJournalier();

    /**
     * Récupérer le dernier rapport hebdomadaire
     */
    @Query("SELECT * FROM rapports WHERE typeRapport = 'HEBDOMADAIRE' ORDER BY dateCreation DESC LIMIT 1")
    RapportEntity getDernierRapportHebdomadaire();

    /**
     * Récupérer le dernier rapport mensuel
     */
    @Query("SELECT * FROM rapports WHERE typeRapport = 'MENSUEL' ORDER BY dateCreation DESC LIMIT 1")
    RapportEntity getDernierRapportMensuel();

    /**
     * Récupérer les rapports d'un mois spécifique
     */
    @Query("SELECT * FROM rapports WHERE mois = :mois AND annee = :annee ORDER BY dateCreation DESC")
    List<RapportEntity> getRapportsParMois(int mois, int annee);


    // ========== SUPPRESSION ==========

    @Query("DELETE FROM rapports")
    void supprimerTout();
}