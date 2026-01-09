package com.example.gestionnairebudget.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * ENTITÉ ROOM : Depense
 *
 * Cette classe représente une TABLE dans la base de données
 * Chaque objet = une LIGNE dans la table
 */
@Entity(tableName = "depenses")
public class DepenseEntity {

    // ========== COLONNES DE LA TABLE ==========

    @PrimaryKey(autoGenerate = true)
    private int id;  // ID auto-incrémenté

    private String categorie;
    private String designation;
    private double montant;
    private long timestamp;  // Date en millisecondes
    private String dateFormatee;  // Date lisible (ex: "09/01/2026 14:30")

    // Informations de période
    private int jour;    // Jour du mois (1-31)
    private int semaine; // Numéro de semaine dans l'année (1-52)
    private int mois;    // Mois (1-12)
    private int annee;   // Année (ex: 2026)


    // ========== CONSTRUCTEUR ==========
    public DepenseEntity(String categorie, String designation, double montant,
                         long timestamp, String dateFormatee,
                         int jour, int semaine, int mois, int annee) {
        this.categorie = categorie;
        this.designation = designation;
        this.montant = montant;
        this.timestamp = timestamp;
        this.dateFormatee = dateFormatee;
        this.jour = jour;
        this.semaine = semaine;
        this.mois = mois;
        this.annee = annee;
    }


    // ========== GETTERS ET SETTERS ==========
    // Room en a besoin pour lire/écrire dans la BDD

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCategorie() {
        return categorie;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public double getMontant() {
        return montant;
    }

    public void setMontant(double montant) {
        this.montant = montant;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getDateFormatee() {
        return dateFormatee;
    }

    public void setDateFormatee(String dateFormatee) {
        this.dateFormatee = dateFormatee;
    }

    public int getJour() {
        return jour;
    }

    public void setJour(int jour) {
        this.jour = jour;
    }

    public int getSemaine() {
        return semaine;
    }

    public void setSemaine(int semaine) {
        this.semaine = semaine;
    }

    public int getMois() {
        return mois;
    }

    public void setMois(int mois) {
        this.mois = mois;
    }

    public int getAnnee() {
        return annee;
    }

    public void setAnnee(int annee) {
        this.annee = annee;
    }
}