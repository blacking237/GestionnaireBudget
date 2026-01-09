package com.example.gestionnairebudget;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * CLASSE Depense - VERSION COMPLÈTE
 *
 * Cette classe représente UNE dépense avec :
 * - Catégorie (Transport, Alimentation, etc.)
 * - Désignation (description détaillée)
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
    /**
     * Crée une nouvelle dépense avec date automatique
     *
     * @param categorie : type de dépense (ex: "Transport")
     * @param designation : description (ex: "Taxi pour aller au travail")
     * @param montant : somme dépensée (ex: 2000.0)
     */
    public Depense(String categorie, String designation, double montant) {
        this.categorie = categorie;
        this.designation = designation;
        this.montant = montant;

        // Générer automatiquement la date et l'heure actuelles
        SimpleDateFormat formatDate = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        this.date = formatDate.format(new Date());
    }


    // ========== GETTERS ==========

    /**
     * Retourne la catégorie de la dépense
     */
    public String getCategorie() {
        return categorie;
    }

    /**
     * Retourne la désignation (description)
     */
    public String getDesignation() {
        return designation;
    }

    /**
     * Retourne le montant
     */
    public double getMontant() {
        return montant;
    }

    /**
     * Retourne la date d'enregistrement
     */
    public String getDate() {
        return date;
    }


    // ========== SETTERS ==========

    /**
     * Modifie la catégorie
     */
    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

    /**
     * Modifie la désignation
     */
    public void setDesignation(String designation) {
        this.designation = designation;
    }

    /**
     * Modifie le montant
     */
    public void setMontant(double montant) {
        this.montant = montant;
    }


    // ========== MÉTHODE D'AFFICHAGE ==========
    /**
     * Retourne une description complète de la dépense
     */
    public String afficherDepense() {
        return "📝 " + categorie + " - " + designation +
                "\n💵 " + montant + " FCFA" +
                "\n📅 " + date;
    }


    /**
     * Retourne une version courte (pour les listes)
     */
    public String afficherCourt() {
        return categorie + " : " + montant + " FCFA (" + date + ")";
    }
}