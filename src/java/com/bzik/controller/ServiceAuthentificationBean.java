package com.bzik.controller;

import com.bzik.model.Personne;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import javax.enterprise.context.SessionScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.servlet.http.HttpSession;
import javax.transaction.UserTransaction;

/**
 *
 * @author Alizée Arnaud, Vincent Perillat, Thomas Lelièvre
 */
@ManagedBean(name = "serviceAuthentification")
@SessionScoped
public class ServiceAuthentificationBean implements Serializable {

    @PersistenceContext
    private EntityManager em;
    @Resource
    private UserTransaction tx;
    private List<Personne> result;
    private Personne personne;
    private boolean authentifie;

    /**
     * Permet de vérifier l'authentification
     * @param isAdmin Un booléen qui définit si la connexion se fait sur
     * l'interface d'admin (true) ou sur l'interface utilisateur (false)
     * @return
     *      Un Booléen
     */
    public boolean verifierAuthentification(boolean isAdmin) {
        Map<String, String> params
                = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String pseudo = "";
        String mdp = "";
        if (!isAdmin) {
            pseudo = params.get("tabViewFO:formConnexionFO:pseudo");
            mdp = params.get("tabViewFO:formConnexionFO:mdp");
        } else {
            pseudo = params.get("tabViewBO:formConnexionBO:pseudo");
            mdp = params.get("tabViewBO:formConnexionBO:mdp");
        }

        if (isAdmin) {
            Query query = em.createQuery(
                    "SELECT a FROM Administrateur a WHERE a.pseudo = :pseudo AND a.mdp = :mdp")
                    .setParameter("pseudo", pseudo)
                    .setParameter("mdp", mdp);
            result = query.getResultList();
        } else {
            Query query = em.createQuery(
                    "SELECT u FROM Utilisateur u WHERE u.pseudo = :pseudo AND u.mdp = :mdp")
                    .setParameter("pseudo", pseudo)
                    .setParameter("mdp", mdp);
            result = query.getResultList();
        }

        if (!result.isEmpty()) {
            ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
            HttpSession session = (HttpSession) context.getSession(true);
            personne = result.get(0);
            if (!isAdmin) {
                session.setAttribute("pseudoFO", pseudo);
                session.setAttribute("mdpFO", mdp);
                session.setAttribute("utilisateurId", personne.getIdPersonne());
            } else {
                session.setAttribute("pseudoBO", pseudo);
                session.setAttribute("mdpBO", mdp);
            }
            return true;
        }
        return false;
    }
}
