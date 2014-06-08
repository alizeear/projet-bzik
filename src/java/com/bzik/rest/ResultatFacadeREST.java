package com.bzik.rest;

import com.bzik.model.Course;
import com.bzik.model.Resultat;
import com.bzik.model.Utilisateur;
import java.io.IOException;
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
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

/**
 *
 * @author Alizée Arnaud, Vincent Perillat, Thomas Lelièvre
 */
@Stateless
@Path("/resultat/")
public class ResultatFacadeREST extends AbstractFacade<Resultat> {

    @PersistenceContext(unitName = "bzik-allPU")
    private EntityManager em;

    public ResultatFacadeREST() {
        super(Resultat.class);
    }

    @POST
    @Override
    @Consumes({"application/json"})
    public void create(Resultat entity) {
        super.create(entity);
    }

    @PUT
    @Path("{id}")
    @Consumes({"application/json"})
    public void edit(@PathParam("id") Integer id, Resultat entity) {
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
    public Resultat find(@PathParam("id") Integer id) {
        return super.find(id);
    }

    @GET
    @Path("last/{idCoureur}&{idCourse}")
    @Produces({"application/json"})
    public Resultat findLastByCoureurIdAndCourseId(@PathParam("idCoureur") Integer idCoureur, @PathParam("idCourse") Integer idCourse) {
        Course myCourse = em.find(Course.class, idCourse);
        Utilisateur myUtilisateur = em.find(Utilisateur.class, idCoureur);
        return (Resultat) em.createQuery(
                "SELECT r FROM Resultat r WHERE r.course = :idCourse AND r.utilisateur = :idCoureur ORDER BY r.idResultat DESC")
                .setParameter("idCourse", myCourse)
                .setParameter("idCoureur", myUtilisateur)
                .setMaxResults(1)
                .getSingleResult();
    }

    @GET
    @Path("last/{idCoureur}")
    @Produces({"application/json"})
    public Resultat findLastResultatByUtilisateurId(@PathParam("idCoureur") Integer idCoureur) {
        Utilisateur myUtilisateur = em.find(Utilisateur.class, idCoureur);
        Resultat resultat = (Resultat) em.createQuery(
                "SELECT r FROM Resultat r WHERE r.utilisateur = :utilisateur ORDER BY r.idResultat DESC")
                .setParameter("utilisateur", myUtilisateur)
                .setMaxResults(1)
                .getSingleResult();
        return resultat;
    }

    @GET
    @Path("getRank/{idRun}&{column}&{order}")
    @Produces({"application/json"})
    public String getRank(@PathParam("idRun") Integer idRun,
            @PathParam("column") String column,
            @PathParam("order") String order) throws IOException {

        Course test = em.find(Course.class, idRun);
        List resultat_1;
        if (column.equals("temps")) {
            resultat_1 = (List) em.createQuery(
                    "SELECT r FROM Resultat r "
                    + "WHERE r.idResultat IN ("
                    + "SELECT MAX(r1.idResultat) as maxResultatId "
                    + "FROM Resultat r1 "
                    + "WHERE r1.course = :test "
                    + "AND r1.vitesse >= 0 "
                    + "AND r1.utilisateur IN (SELECT r2.utilisateur "
                    + "FROM Resultat r2 "
                    + "WHERE r2.course = :test "
                    + "AND r2.vitesse < 0) "
                    + "GROUP BY r1.utilisateur) "
                    + "ORDER BY r." + column + " " + order
            )
                    .setParameter("test", test)
                    .getResultList();
        } else {
            resultat_1 = (List) em.createQuery(
                    "SELECT r FROM Resultat r "
                    + "WHERE r.idResultat IN ("
                    + "SELECT MAX(r1.idResultat) as maxResultatId "
                    + "FROM Resultat r1 "
                    + "WHERE r1.course = :test "
                    + "AND r1.vitesse >= 0 "
                    + "GROUP BY r1.utilisateur) "
                    + "ORDER BY r." + column + " " + order)
                    .setParameter("test", test)
                    .getResultList();
        }
        ObjectMapper mapper = new ObjectMapper();
        String s = mapper.writerWithType(new TypeReference<List<Resultat>>() {
        }).writeValueAsString(resultat_1);
        return s;
    }

    @GET
    @Override
    @Produces({"application/json"})
    public List<Resultat> findAll() {
        return super.findAll();
    }

    @GET
    @Path("{from}/{to}")
    @Produces({"application/json"})
    public List<Resultat> findRange(@PathParam("from") Integer from, @PathParam("to") Integer to) {
        return super.findRange(new int[]{from, to});
    }

    @GET
    @Path("count")
    @Produces("text/plain")
    public String countREST() {
        return String.valueOf(super.count());
    }

    @GET
    @Path("setCourseFinished/{utilisateurId}&{courseId}")
    @Produces({"application/json"})
    public void setCourseFinished(@PathParam("utilisateurId") Integer utilisateurId, @PathParam("courseId") Integer courseId) {
        try {
            Course myCourse = em.find(Course.class, courseId);
            Utilisateur myUtilisateur = em.find(Utilisateur.class, utilisateurId);
            Resultat finishedResultat = new Resultat();
            finishedResultat.setCourse(myCourse);
            finishedResultat.setUtilisateur(myUtilisateur);
            finishedResultat.setDistance(-1);
            finishedResultat.setVitesse(-1);
            finishedResultat.setTemps(-1);
            super.create(finishedResultat);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @GET
    @Path("isCourseFinished/{utilisateurId}&{courseId}")
    @Produces({"application/json"})
    public String isCourseFinished(@PathParam("utilisateurId") Integer utilisateurId, @PathParam("courseId") Integer courseId) {
        String result = "false";
        try {
            Resultat lastResultat = findLastByCoureurIdAndCourseId(utilisateurId, courseId);
            if (lastResultat.getDistance() == -1 && lastResultat.getTemps() == -1 && lastResultat.getVitesse() == -1) {
                result = "true";
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

}
