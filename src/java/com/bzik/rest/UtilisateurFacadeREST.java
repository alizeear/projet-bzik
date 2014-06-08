package com.bzik.rest;

import com.bzik.model.Utilisateur;
import java.text.Normalizer;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

/**
 *
 * @author Alizée Arnaud, Vincent Perillat, Thomas Lelièvre
 */
@Stateless
@Path("/utilisateur/")
public class UtilisateurFacadeREST extends AbstractFacade<Utilisateur> {

    @PersistenceContext(unitName = "bzik-allPU")
    private EntityManager em;

    public UtilisateurFacadeREST() {
        super(Utilisateur.class);
    }

    @POST
    @Override
    @Consumes({"application/json"})
    public void create(Utilisateur entity) {
        super.create(entity);
    }

    @PUT
    @Path("{id}")
    @Consumes({"application/json"})
    public void edit(@PathParam("id") Integer id, Utilisateur entity) {
        super.edit(entity);
    }

    @DELETE
    @Path("{id}")
    public void remove(@PathParam("id") Integer id) {
        super.remove(super.find(id));
    }

    @GET
    @Path("{id}")
    @Produces({"application/json"})
    public Utilisateur find(@PathParam("id") Integer id) {
        return super.find(id);
    }

    @GET
    @Path("findByPseudo/{pseudo}")
    @Produces({"application/json"})
    public Utilisateur findByPseudo(@PathParam("pseudo") String pseudo) {
        pseudo = Normalizer.normalize(pseudo, Normalizer.Form.NFD);
        pseudo = pseudo.replaceAll("[^\\p{ASCII}]", "");
        Utilisateur myUtilisateur = (Utilisateur) em.createQuery("SELECT p FROM Personne p WHERE p.pseudo = :pseudo")
                .setParameter("pseudo", pseudo)
                .setMaxResults(1)
                .getSingleResult();
        System.out.println(myUtilisateur.getPseudo());
        return myUtilisateur;
    }

    @GET
    @Override
    @Produces({"application/json"})
    public List<Utilisateur> findAll() {
        return super.findAll();
    }

    @GET
    @Path("{from}/{to}")
    @Produces({"application/json"})
    public List<Utilisateur> findRange(@PathParam("from") Integer from, @PathParam("to") Integer to) {
        return super.findRange(new int[]{from, to});
    }

    @GET
    @Path("count")
    @Produces("text/plain")
    public String countREST() {
        return String.valueOf(super.count());
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

}
