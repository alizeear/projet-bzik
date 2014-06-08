package com.bzik.rest;

import com.bzik.model.Course;
import java.util.Date;
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
@Path("/course/")
public class CourseFacadeREST extends AbstractFacade<Course> {

    @PersistenceContext(unitName = "bzik-allPU")
    private EntityManager em;

    public CourseFacadeREST() {
        super(Course.class);
    }

    @POST
    @Override
    @Consumes({"application/xml", "application/json"})
    public void create(Course entity) {
        super.create(entity);
    }

    @PUT
    @Path("{id}")
    @Consumes({"application/xml", "application/json"})
    public void edit(@PathParam("id") Integer id, Course entity) {
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
    public Course find(@PathParam("id") Integer id) {
        return super.find(id);
    }

    @GET
    @Override
    @Produces({"application/xml", "application/json"})
    public List<Course> findAll() {
        return super.findAll();
    }

    @GET
    @Path("{from}/{to}")
    @Produces({"application/xml", "application/json"})
    public List<Course> findRange(@PathParam("from") Integer from, @PathParam("to") Integer to) {
        return super.findRange(new int[]{from, to});
    }

    @GET
    @Path("checkNotStarted/{id}")
    @Produces({"application/json"})
    public String checkCourseNotStarted(@PathParam("id") Integer id) {
        Date today = new Date();
        Course course = em.find(Course.class, id);
        long diffMilliseconds = course.getDate().getTime() - today.getTime();
        if (diffMilliseconds <= 0) {
            return "false";
        }
        return "true";
    }

    @GET
    @Path("getServerDate")
    @Produces({"application/json"})
    public String getServerDate() {
        long time = System.currentTimeMillis();
        return "" + time + "";
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
