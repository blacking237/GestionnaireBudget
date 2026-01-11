package com.example.gestionnairebudget.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

/**
 * DAO pour les catégories personnalisées
 */
@Dao
public interface CategorieDao {

    /**
     * Insérer une catégorie
     */
    @Insert
    void inserer(CategoriePersonnaliseeEntity categorie);

    /**
     * Insérer plusieurs catégories
     */
    @Insert
    void insererTout(List<CategoriePersonnaliseeEntity> categories);

    /**
     * Mettre à jour une catégorie
     */
    @Update
    void mettreAJour(CategoriePersonnaliseeEntity categorie);

    /**
     * Récupérer toutes les catégories d'un utilisateur
     */
    @Query("SELECT * FROM categories_personnalisees WHERE userId = :userId AND estActive = 1")
    List<CategoriePersonnaliseeEntity> getCategoriesUtilisateur(int userId);

    /**
     * Récupérer une catégorie par nom et userId
     */
    @Query("SELECT * FROM categories_personnalisees WHERE userId = :userId AND nomCategorie = :nom LIMIT 1")
    CategoriePersonnaliseeEntity getCategorieParNom(int userId, String nom);

    /**
     * Supprimer toutes les catégories d'un utilisateur
     */
    @Query("DELETE FROM categories_personnalisees WHERE userId = :userId")
    void supprimerCategoriesUtilisateur(int userId);

    /**
     * Vérifier si l'utilisateur a des catégories
     */
    @Query("SELECT COUNT(*) FROM categories_personnalisees WHERE userId = :userId AND estActive = 1")
    int nombreCategoriesUtilisateur(int userId);
}