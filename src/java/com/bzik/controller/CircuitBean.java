package com.bzik.controller;

import com.bzik.model.Circuit;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.faces.application.FacesMessage;
import javax.enterprise.context.RequestScoped;
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
public class CircuitBean {

    @PersistenceContext
    private EntityManager em;
    @Resource
    private UserTransaction tx;
    private Circuit circuit = new Circuit();
    private List<Circuit> listeCircuits;
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

    public void updateCircuit(Circuit circuit) {
        setCircuit(circuit);
    }

    public void updateCircuit() {
        try {
            tx.begin();
            em.merge(circuit);
            tx.commit();

            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "L'enregistrement a bien fonctionné", null);
            FacesContext.getCurrentInstance().addMessage(null, message);
        } catch (Exception ex) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Echec de l'enregistrement", null);
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
        circuit = null;
        circuit = new Circuit();
        activeTab = 3;
        ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
        HttpSession session = (HttpSession) context.getSession(true);
        session.setAttribute("activeTab", activeTab);
    }

    public void deleteCircuit(Circuit circuit) {
        try {
            String pathUpload = FacesContext.getCurrentInstance().getExternalContext().getInitParameter("imgVideoEmplacement");
            File file = new File(pathUpload + circuit.getUrlVideo());
            Files.deleteIfExists(file.toPath());

            tx.begin();
            em.remove(em.merge(circuit));
            tx.commit();

            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Le circuit a bien été supprimé", null);
            FacesContext.getCurrentInstance().addMessage(null, message);
        } catch (Exception ex) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Une erreur s'est produite", null);
            FacesContext.getCurrentInstance().addMessage(null, message);
        }

        this.listeCircuits = null; // Pour rafraichir la liste lors d'une suppression
    }

    public void upload(FileUploadEvent event) throws IOException {
        // on donne le chemin du dossier dans le projet
        String pathUpload = FacesContext.getCurrentInstance().getExternalContext().getInitParameter("imgVideoEmplacement");
        // on donne un nom aléatoire à l'image
        //String nomVideo=Math.random()+".avi";
        String nomVideo = UUID.randomUUID().toString() + ".mp4";
        // on donne le nom de l'image à l'objet cycliste pour l'enregistrement
        this.circuit.setUrlVideo(nomVideo);
        // où on veut que le fichier aille
        File avatar = new File(pathUpload + File.separator + nomVideo);
        // récupère les octes des fichiers
        InputStream fichier = event.getFile().getInputstream();
        // on copie dans le dossier de destination, on remplace si deux fois le même nom
        Files.copy(fichier, avatar.toPath(), StandardCopyOption.REPLACE_EXISTING);
        // message de validation
        FacesMessage msg = new FacesMessage("Enregistrement de la vidéo réussi", event.getFile().getFileName());
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public Circuit getCircuit() {
        return this.circuit;
    }

    public void setCircuit(Circuit circuit) {
        this.circuit = circuit;
    }

    public List<Circuit> getListeCircuits() {
        Query query = em.createQuery("SELECT c FROM Circuit c");
        listeCircuits = query.getResultList();
        return listeCircuits;
    }

    public void setListeCircuits(List<Circuit> listeCircuits) {
        this.listeCircuits = listeCircuits;
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
        System.out.println(activeTab);
        return activeTab;
    }

    public void setActiveTab(int activeTab) {
        this.activeTab = activeTab;
    }

}
