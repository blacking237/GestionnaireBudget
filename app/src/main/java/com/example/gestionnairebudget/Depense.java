package com.example.gestionnairebudget;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * CLASSE Depense - VERSION COMPLETE
 *
 * Cette classe represente UNE depense avec :
 * - Categorie
 * - Designation
 * - Montant
 * - Date automatique
 */
public class Depense {

    // ========== ATTRIBUTS ==========
    private String categorie;
    private String designation;
    private double montant;
    private String date;

    // ========== CONSTRUCTEUR ==========
    public Depense(String categorie, String designation, double montant) {
        this.categorie = categorie;
        this.designation = designation;
        this.montant = montant;

        SimpleDateFormat formatDate =
                new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        this.date = formatDate.format(new Date());
    }

    // ========== GETTERS ==========
    public String getCategorie() {
        return categorie;
    }

    public String getDesignation() {
        return designation;
    }

    public double getMontant() {
        return montant;
    }

    public String getDate() {
        return date;
    }

    // ========== SETTERS ==========
    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public void setMontant(double montant) {
        this.montant = montant;
    }

    // ========== AFFICHAGE ==========
    public String afficherDepense() {
        return "\uD83D\uDCDD " + categorie + " - " + designation +
                "\n\uD83D\uDCB5 " + montant + " FCFA" +
                "\n\uD83D\uDCC5 " + date;
    }

    public String afficherCourt() {
        return categorie + " : " + montant + " FCFA (" + date + ")";
    }
}
