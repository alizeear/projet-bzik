package com.bzik.controller;

import com.bzik.model.Utilisateur;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.servlet.http.HttpSession;
import javax.transaction.UserTransaction;
import org.primefaces.event.FileUploadEvent;

/**
 *
 * @author Alizée Arnaud, Vincent Perillat, Thomas Lelièvre
 */
@Named
@RequestScoped
public class UtilisateurBean {

    @PersistenceContext(unitName = "bzik-allPU")
    private EntityManager em;
    @Resource
    private UserTransaction tx;
    private Utilisateur utilisateur = new Utilisateur();
    private List<Utilisateur> listeUtilisateurs;
    private List<Utilisateur> listeUtilisateursSelected;
    private String userPseudo;
    private String userMdp;
    private int activeTab = 0;
    private String adminPseudo;
    private String adminMdp;

    @PostConstruct
    public void initialisation() {
        ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
        HttpSession session = (HttpSession) context.getSession(true);

        if (session.getAttribute("pseudoBO") != null) {
            adminPseudo = session.getAttribute("pseudoBO").toString();
        }
        if (session.getAttribute("mdpBO") != null) {
            adminMdp = session.getAttribute("mdpBO").toString();
        }
        if (session.getAttribute("pseudoFO") != null) {
            userPseudo = session.getAttribute("pseudoFO").toString();
        }
        if (session.getAttribute("mdpFO") != null) {
            userMdp = session.getAttribute("mdpFO").toString();
        }
    }

    public void updateUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }

    public void updateUtilisateur() {
        try {
            tx.begin();
            em.merge(utilisateur);
            tx.commit();

            FacesMessage message = new FacesMessage("L'enregistrement à bien fonctionné");
            FacesContext.getCurrentInstance().addMessage(null, message);

        } catch (Exception ex) {

            FacesMessage message = new FacesMessage("Echec de l'enregistrement");
            FacesContext.getCurrentInstance().addMessage(null, message);

        }

        utilisateur = null;
        utilisateur = new Utilisateur();
        activeTab = 0;
        ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
        HttpSession session = (HttpSession) context.getSession(true);
        session.setAttribute("activeTab", activeTab);
    }

    public void upload(FileUploadEvent event) throws IOException {
        // on donne le chemin du dossier dans le projet
        String pathUpload = FacesContext.getCurrentInstance().getExternalContext().getInitParameter("imgAvatarsEmplacement");
        // on donne un nom aléatoire à l'image
        //String nomAvatar=Math.random()+".jpg";
        String nomAvatar = UUID.randomUUID().toString() + ".jpg";
        // on donne le nom de l'image à l'objet cycliste pour l'enregistrement
        this.utilisateur.setAvatar(nomAvatar);
        // où on veut que le fichier aille
        File avatar = new File(pathUpload + File.separator + nomAvatar);
        // récupère les octes des fichiers
        InputStream fichier = event.getFile().getInputstream();
        // on copie dans le dossier de destination, on remplace si deux fois le même nom
        Files.copy(fichier, avatar.toPath(), StandardCopyOption.REPLACE_EXISTING);
        // message de validation
        FacesMessage msg = new FacesMessage("Succesful", event.getFile().getFileName() + " is uploaded.");
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void deleteUtilisateur(Utilisateur utilisateur) {
        try {

            String pathUpload = FacesContext.getCurrentInstance().getExternalContext().getInitParameter("imgAvatarsEmplacement");
            File file = new File(pathUpload + utilisateur.getAvatar());

            Files.deleteIfExists(file.toPath());

            tx.begin();
            em.remove(em.merge(utilisateur));
            tx.commit();

            FacesMessage message = new FacesMessage("L'utilisateur à bien été supprimé");
            FacesContext.getCurrentInstance().addMessage(null, message);

        } catch (Exception ex) {

            FacesMessage message = new FacesMessage("Une erreur s'est produite");
            FacesContext.getCurrentInstance().addMessage(null, message);

        }

        this.listeUtilisateurs = null; // Pour rafraichir la liste lors d'une suppression
    }

    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }

    public List<Utilisateur> getListeUtilisateurs() {
        if (listeUtilisateurs == null || listeUtilisateurs.isEmpty()) {
            Query requete = em.createQuery("SELECT p From Personne p INNER JOIN Utilisateur u ON p.idpersonne = u.idpersonne");
            listeUtilisateurs = requete.getResultList();
        }
        return listeUtilisateurs;
    }

    public String getUserPseudo() {
        return userPseudo;
    }

    public void setUserPseudo(String userPseudo) {
        this.userPseudo = userPseudo;
    }

    public String getUserMdp() {
        return userMdp;
    }

    public void setUserMdp(String userMdp) {
        this.userMdp = userMdp;
    }

    public int getActiveTab() {
        return activeTab;
    }

    public void setActiveTab(int activeTab) {
        this.activeTab = activeTab;
    }

    public void setListeUtilisateurs(List<Utilisateur> listeUtilisateurs) {
        this.listeUtilisateurs = listeUtilisateurs;
    }

    public List<Utilisateur> getListeUtilisateursSelected() {
        return listeUtilisateursSelected;
    }

    public void setListeUtilisateursSelected(List<Utilisateur> listeUtilisateursSelected) {
        this.listeUtilisateursSelected = listeUtilisateursSelected;
    }

}
