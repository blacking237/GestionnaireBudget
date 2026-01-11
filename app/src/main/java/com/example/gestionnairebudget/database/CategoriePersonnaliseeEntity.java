package com.example.gestionnairebudget.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * CATÉGORIES PERSONNALISÉES PAR UTILISATEUR
 *
 * Chaque utilisateur peut définir ses propres catégories
 * et leurs pourcentages
 */
@Entity(tableName = "categories_personnalisees")
public class CategoriePersonnaliseeEntity {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private int userId;  // ID de l'utilisateur propriétaire

    private String nomCategorie;
    private double pourcentageAlloue;
    private double montantAlloue;     // Calculé depuis le budget
    private double montantUtilise;

    private long dateCreation;
    private boolean estActive;


    // ========== CONSTRUCTEUR ==========
    public CategoriePersonnaliseeEntity(int userId, String nomCategorie,
                                        double pourcentageAlloue) {
        this.userId = userId;
        this.nomCategorie = nomCategorie;
        this.pourcentageAlloue = pourcentageAlloue;
        this.montantAlloue = 0.0;
        this.montantUtilise = 0.0;
        this.dateCreation = System.currentTimeMillis();
        this.estActive = true;
    }


    // ========== GETTERS ET SETTERS ==========

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getNomCategorie() {
        return nomCategorie;
    }

    public void setNomCategorie(String nomCategorie) {
        this.nomCategorie = nomCategorie;
    }

    public double getPourcentageAlloue() {
        return pourcentageAlloue;
    }

    public void setPourcentageAlloue(double pourcentageAlloue) {
        this.pourcentageAlloue = pourcentageAlloue;
    }

    public double getMontantAlloue() {
        return montantAlloue;
    }

    public void setMontantAlloue(double montantAlloue) {
        this.montantAlloue = montantAlloue;
    }

    public double getMontantUtilise() {
        return montantUtilise;
    }

    public void setMontantUtilise(double montantUtilise) {
        this.montantUtilise = montantUtilise;
    }

    public long getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(long dateCreation) {
        this.dateCreation = dateCreation;
    }

    public boolean isEstActive() {
        return estActive;
    }

    public void setEstActive(boolean estActive) {
        this.estActive = estActive;
    }
}