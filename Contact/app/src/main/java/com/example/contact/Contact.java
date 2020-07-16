package com.example.contact;

import java.io.Serializable;

public class Contact implements Serializable {

    private String Sexe;
    private String nom;
    private String prenom;
    private String tel;
    private String anniversaire;
    private String mail;
    private String addresse;
    private String codePostal;
    private String imagePath;
    private static int nbContact = 0;

    public Contact(String sexe, String nom, String prenom, String tel, String anniversaire, String mail, String addresse, String codePostal, String imagePath) {
        Sexe = sexe;
        this.nom = nom;
        this.prenom = prenom;
        this.tel = tel;
        this.anniversaire = anniversaire;
        this.mail = mail;
        this.addresse = addresse;
        this.codePostal = codePostal;
        this.imagePath = imagePath;
        nbContact++;
    }

    public static int getNbContact() {
        return nbContact;
    }

    public static void setNbContact(int nbContact) {
        Contact.nbContact = nbContact;
    }

    public String getSexe() {
        return Sexe;
    }

    public void setSexe(String sexe) {
        Sexe = sexe;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getAnniversaire() {
        return anniversaire;
    }

    public void setAnniversaire(String anniversaire) {
        this.anniversaire = anniversaire;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getAddresse() {
        return addresse;
    }

    public void setAddresse(String addresse) {
        this.addresse = addresse;
    }

    public String getCodePostal() {
        return codePostal;
    }

    public void setCodePostal(String codePostal) {
        this.codePostal = codePostal;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}
