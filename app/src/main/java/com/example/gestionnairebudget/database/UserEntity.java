package com.example.gestionnairebudget.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * ENTITÉ UTILISATEUR
 *
 * Stocke les informations de chaque utilisateur
 */
@Entity(tableName = "users")
public class UserEntity {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String nom;
    private String email;
    private String motDePasse;  // À hasher en production !
    private String profession;
    private String ville;

    // Budget mensuel de l'utilisateur
    private double budgetMensuel;

    // Date de dernière modification du budget
    private long dateModificationBudget;

    // Date de création du compte
    private long dateCreation;

    // Statut du compte
    private boolean estActif;


    // ========== CONSTRUCTEUR ==========
    public UserEntity(String nom, String email, String motDePasse,
                      String profession, String ville) {
        this.nom = nom;
        this.email = email;
        this.motDePasse = motDePasse;
        this.profession = profession;
        this.ville = ville;
        this.budgetMensuel = 0.0;
        this.dateModificationBudget = 0;
        this.dateCreation = System.currentTimeMillis();
        this.estActif = true;
    }


    // ========== GETTERS ET SETTERS ==========

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMotDePasse() {
        return motDePasse;
    }

    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public String getVille() {
        return ville;
    }

    public void setVille(String ville) {
        this.ville = ville;
    }

    public double getBudgetMensuel() {
        return budgetMensuel;
    }

    public void setBudgetMensuel(double budgetMensuel) {
        this.budgetMensuel = budgetMensuel;
    }

    public long getDateModificationBudget() {
        return dateModificationBudget;
    }

    public void setDateModificationBudget(long dateModificationBudget) {
        this.dateModificationBudget = dateModificationBudget;
    }

    public long getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(long dateCreation) {
        this.dateCreation = dateCreation;
    }

    public boolean isEstActif() {
        return estActif;
    }

    public void setEstActif(boolean estActif) {
        this.estActif = estActif;
    }


    /**
     * Vérifie si l'utilisateur peut modifier son budget
     * (1 mois doit s'être écoulé depuis la dernière modification)
     */
    public boolean peutModifierBudget() {
        if (dateModificationBudget == 0) {
            return true; // Première fois
        }

        long maintenant = System.currentTimeMillis();
        long unMoisEnMillis = 30L * 24 * 60 * 60 * 1000; // 30 jours

        return (maintenant - dateModificationBudget) >= unMoisEnMillis;
    }
}