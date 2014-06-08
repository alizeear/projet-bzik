package com.bzik.rest;

import com.bzik.model.Circuit;
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
@Path("/circuit/")
public class CircuitFacadeREST extends AbstractFacade<Circuit> {

    @PersistenceContext(unitName = "bzik-allPU")
    private EntityManager em;

    public CircuitFacadeREST() {
        super(Circuit.class);
    }

    @POST
    @Override
    @Consumes({"application/xml", "application/json"})
    public void create(Circuit entity) {
        super.create(entity);
    }

    @PUT
    @Path("{id}")
    @Consumes({"application/xml", "application/json"})
    public void edit(@PathParam("id") Integer id, Circuit entity) {
        super.edit(entity);
    }

    @DELETE
    @Path("{id}")
    public void remove(@PathParam("id") Integer id) {
        super.remove(super.find(id));
    }

    @GET
    @Path("{id}")
    @Produces({"application/xml", "application/json"})
    public Circuit find(@PathParam("id") Integer id) {
        return super.find(id);
    }

    @GET
    @Override
    @Produces({"application/xml", "application/json"})
    public List<Circuit> findAll() {
        return super.findAll();
    }

    @GET
    @Path("{from}/{to}")
    @Produces({"application/xml", "application/json"})
    public List<Circuit> findRange(@PathParam("from") Integer from, @PathParam("to") Integer to) {
        return super.findRange(new int[]{from, to});
    }

    @GET
    @Path("count")
    @Produces("text/plain")
    public String countREST() {
        return String.valueOf(super.count());
    }

    @GET
    @Path("isCircuitDistanceLowerThanUtilisateurDistance/{circuitId}/{distance}")
    @Produces("application/json")
    public String isCircuitDistanceLowerThanUtilisateurDistance(@PathParam("circuitId") Integer circuitId, @PathParam("distance") Integer distance) {
        Circuit myCircuit = em.find(Circuit.class, circuitId);
        if ((myCircuit.getDistance() + 1) >= distance) {
            return "false";
        }
        return "true";
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

}
