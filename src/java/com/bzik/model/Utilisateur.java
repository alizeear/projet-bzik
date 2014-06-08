package com.bzik.model;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;


/**
 *
 * @author lp
 */
@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class Utilisateur extends Personne implements Serializable{
    
    private String avatar;
    private int etatConnexion;
    
    @JoinTable(name = "courses_personne")
    @ManyToMany // fait la relation entre les classes courses et utilisateurs
    private List<Course> listeCourses;
    
    @OneToMany
    private List<Resultat> resultats;
    
    public Utilisateur(){
        //System.out.println("Dans le constructeur de la classe utilisateur");
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public List<Course> getListeCourses() {
        return listeCourses;
    }

    public void setListeCourses(List<Course> listeCourses) {
        this.listeCourses = listeCourses;
    }

    public List<Resultat> getResultats() {
        return resultats;
    }

    public void setResultats(List<Resultat> resultats) {
        this.resultats = resultats;
    }

    public int getEtatConnexion() {
        return etatConnexion;
    }

    public void setEtatConnexion(int etatConnexion) {
        this.etatConnexion = etatConnexion;
    }
    
    
    
}