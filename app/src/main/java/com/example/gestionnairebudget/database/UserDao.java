package com.example.gestionnairebudget.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

/**
 * DAO pour les utilisateurs
 */
@Dao
public interface UserDao {

    /**
     * Insérer un nouvel utilisateur
     */
    @Insert
    long inserer(UserEntity user);

    /**
     * Mettre à jour un utilisateur
     */
    @Update
    void mettreAJour(UserEntity user);

    /**
     * Récupérer un utilisateur par email
     */
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    UserEntity getUserParEmail(String email);

    /**
     * Vérifier si un email existe déjà
     */
    @Query("SELECT COUNT(*) FROM users WHERE email = :email")
    int emailExiste(String email);

    /**
     * Connexion : vérifier email et mot de passe
     */
    @Query("SELECT * FROM users WHERE email = :email AND motDePasse = :motDePasse LIMIT 1")
    UserEntity connexion(String email, String motDePasse);

    /**
     * Récupérer un utilisateur par ID
     */
    @Query("SELECT * FROM users WHERE id = :userId")
    UserEntity getUserParId(int userId);
}