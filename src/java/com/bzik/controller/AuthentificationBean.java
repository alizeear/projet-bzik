package com.bzik.controller;

import java.io.IOException;
import java.io.Serializable;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Alizée Arnaud, Vincent Perillat, Thomas Lelièvre
 */
@javax.faces.bean.ManagedBean(name = "authentificationBean")
@SessionScoped
public class AuthentificationBean implements Serializable {

    private String pseudo;
    private String mdp;

    // Injection du bean serviceAuthentification
    @ManagedProperty(value = "#{serviceAuthentification}")
    private ServiceAuthentificationBean serviceAuthentificationBean;

    private String userPseudo;
    private String userMdp;
    private String adminPseudo;
    private String adminMdp;
    private final String redirectBo = "/bzik-all/indexBO.jsf";
    private final String redirectNokFo = "/bzik-all/connectionFO.jsf";
    private final String redirectOkFo = "/bzik-all/indexFO.jsf";

    /**
     * Permet d'authentifier les utilisateurs ou les administrateurs en fonction
     * du booléen passé en paramètre
     *
     * @param isAdmin Un booléen qui définit si la connexion se fait sur
     * l'interface d'admin (true) ou sur l'interface utilisateur (false)
     */
    public void checkAuthentification(boolean isAdmin) {
        FacesMessage message = null;
        ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
        HttpSession session = (HttpSession) context.getSession(true);

        if (session.getAttribute("pseudoBO") != null && session.getAttribute("mdpBO") != null && isAdmin) {
            adminPseudo = session.getAttribute("pseudoBO").toString();
            adminMdp = session.getAttribute("mdpBO").toString();
        } else if (session.getAttribute("pseudoFO") != null && session.getAttribute("mdpFO") != null && !isAdmin) {
            userPseudo = session.getAttribute("pseudoFO").toString();
            userMdp = session.getAttribute("mdpFO").toString();
        } else {
            try {
                if (serviceAuthentificationBean.verifierAuthentification(isAdmin)) {
                    if (!isAdmin) {
                        context.redirect(redirectOkFo);
                    } else {
                        message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Vous êtes connecté", null);
                    }
                } else {
                    if (!isAdmin) {
                        context.redirect(redirectNokFo);
                    } else {
                        context.redirect(redirectBo);
                    }
                    message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Vous ne pouvez pas accéder à cette partie sans être connecté", null);
                }
            } catch (IOException ex) {
                message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur de la redirection", null);
            }
            FacesContext.getCurrentInstance().addMessage(null, message);
        }

    }

    /**
     * Permet de déconnecter l'utilisateur courant de l'application
     *
     * @param isAdmin Un booléen qui définit si la déconnexion se fait sur
     * l'interface d'admin (true) ou sur l'interface utilisateur (false)
     * @return Un String de redirection
     */
    public String logOut(boolean isAdmin) {
        FacesMessage message = null;
        ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
        ((HttpSession) context.getSession(true)).invalidate();
        message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Vous êtes déconnecté", null);
        FacesContext.getCurrentInstance().addMessage(null, message);

        if (isAdmin) {
            return "authentificationBO";
        }
        return "authentificationFO";
    }

    public String getPseudo() {
        return pseudo;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    public String getMdp() {
        return mdp;
    }

    public void setMdp(String mdp) {
        this.mdp = mdp;
    }

    public ServiceAuthentificationBean getServiceAuthentificationBean() {
        return serviceAuthentificationBean;
    }

    public void setServiceAuthentificationBean(ServiceAuthentificationBean serviceAuthentificationBean) {
        this.serviceAuthentificationBean = serviceAuthentificationBean;
    }

}
