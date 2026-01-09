package com.example.gestionnairebudget.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * ENTITÉ ROOM : Rapport
 *
 * Stocke les rapports journaliers, hebdomadaires et mensuels
 */
@Entity(tableName = "rapports")
public class RapportEntity {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String typeRapport;  // "JOURNALIER", "HEBDOMADAIRE", "MENSUEL"
    private long dateCreation;   // Timestamp de création du rapport
    private String dateFormatee; // Date lisible

    // Période concernée
    private int jour;
    private int semaine;
    private int mois;
    private int annee;

    // Données du rapport
    private double budgetTotal;
    private double depensesTotales;
    private double surplus;  // Positif si surplus, négatif si déficit
    private int nombreDepenses;
    private String contenuRapport;  // Le rapport complet en texte

    // Comparaison avec période précédente
    private double evolutionParRapportPeriodePrecedente;  // En pourcentage


    // ========== CONSTRUCTEUR ==========
    public RapportEntity(String typeRapport, long dateCreation, String dateFormatee,
                         int jour, int semaine, int mois, int annee,
                         double budgetTotal, double depensesTotales, double surplus,
                         int nombreDepenses, String contenuRapport,
                         double evolutionParRapportPeriodePrecedente) {
        this.typeRapport = typeRapport;
        this.dateCreation = dateCreation;
        this.dateFormatee = dateFormatee;
        this.jour = jour;
        this.semaine = semaine;
        this.mois = mois;
        this.annee = annee;
        this.budgetTotal = budgetTotal;
        this.depensesTotales = depensesTotales;
        this.surplus = surplus;
        this.nombreDepenses = nombreDepenses;
        this.contenuRapport = contenuRapport;
        this.evolutionParRapportPeriodePrecedente = evolutionParRapportPeriodePrecedente;
    }


    // ========== GETTERS ET SETTERS ==========

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTypeRapport() {
        return typeRapport;
    }

    public void setTypeRapport(String typeRapport) {
        this.typeRapport = typeRapport;
    }

    public long getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(long dateCreation) {
        this.dateCreation = dateCreation;
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

    public double getBudgetTotal() {
        return budgetTotal;
    }

    public void setBudgetTotal(double budgetTotal) {
        this.budgetTotal = budgetTotal;
    }

    public double getDepensesTotales() {
        return depensesTotales;
    }

    public void setDepensesTotales(double depensesTotales) {
        this.depensesTotales = depensesTotales;
    }

    public double getSurplus() {
        return surplus;
    }

    public void setSurplus(double surplus) {
        this.surplus = surplus;
    }

    public int getNombreDepenses() {
        return nombreDepenses;
    }

    public void setNombreDepenses(int nombreDepenses) {
        this.nombreDepenses = nombreDepenses;
    }

    public String getContenuRapport() {
        return contenuRapport;
    }

    public void setContenuRapport(String contenuRapport) {
        this.contenuRapport = contenuRapport;
    }

    public double getEvolutionParRapportPeriodePrecedente() {
        return evolutionParRapportPeriodePrecedente;
    }

    public void setEvolutionParRapportPeriodePrecedente(double evolutionParRapportPeriodePrecedente) {
        this.evolutionParRapportPeriodePrecedente = evolutionParRapportPeriodePrecedente;
    }
}