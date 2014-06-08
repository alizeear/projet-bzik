package com.bzik.controller;

import com.bzik.model.Course;
import com.bzik.model.Utilisateur;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.json.stream.JsonGenerationException;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.servlet.http.HttpSession;
import javax.transaction.UserTransaction;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

/**
 *
 * @author Alizée Arnaud, Vincent Perillat, Thomas Lelièvre
 */
@Named
@RequestScoped
public class CourseBean {

    @PersistenceContext
    private EntityManager em;
    @Resource
    private UserTransaction tx;
    private Course course = new Course();
    private List<Course> listeCourses;
    private String userPseudo;
    private String userMdp;
    private String adminPseudo;
    private String adminMdp;
    private int activeTab = 0;

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

    public void updateCourse(Course course) {
        setCourse(course);
    }

    public void updateCourse() {
        try {
            tx.begin();
            em.merge(course);
            tx.commit();

            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "L'enregistrement a bien fonctionné", null);
            FacesContext.getCurrentInstance().addMessage(null, message);
        } catch (Exception ex) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Echec de l'enregistrement", null);
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
        course = null;
        course = new Course();
        activeTab = 2;
        ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
        HttpSession session = (HttpSession) context.getSession(true);
        session.setAttribute("activeTab", activeTab);
    }

    public void deleteCourse(Course course) {
        try {
            tx.begin();
            em.remove(em.merge(course));
            tx.commit();

            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "La course a bien été supprimé", null);
            FacesContext.getCurrentInstance().addMessage(null, message);
        } catch (Exception ex) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Une erreur s'est produite : " + ex.getMessage(), null);
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
        this.listeCourses = null; // Pour rafraichir la liste lors d'une suppression
    }

    public Course getCourse() {
        return this.course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    /**
     * Permet la redirection vers la page de simulation de course lorsque un
     * utilisateur entre dans une course
     *
     * @param course Course dans laquelle l'utilisateur entre
     */
    public void redirectToCourse(Course course) {
        try {
            ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
            HttpSession session = (HttpSession) context.getSession(true);
            session.setAttribute("courseId", course.getIdCourse());
            context.redirect(context.getRequestContextPath() + "/video.jsf");
        } catch (IOException ex) {
            Logger.getLogger(CourseBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public List<Course> getListeCourses(boolean bo) {
        Query query = em.createQuery("SELECT c FROM Course c WHERE c.date > CURRENT_TIMESTAMP");
        if (bo == true) {
            query = em.createQuery("SELECT c FROM Course c ");
        }
        listeCourses = query.getResultList();
        return listeCourses;
    }

    public void setListeCourses(List<Course> listeCourses) {
        this.listeCourses = listeCourses;
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

    /**
     * Permet de récupérer le temps restant avant le début d'une course
     *
     * @param courseId ID de la Course
     * @param refreshSession
     * @return Une string formatée (jour hh:mm:ss)
     */
    public String getCourseTimeLeft(int courseId, boolean refreshSession) {
        Date today = new Date();
        course = em.find(Course.class, courseId);
        ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
        HttpSession session = (HttpSession) context.getSession(true);
        if (!refreshSession) {
            boolean countDownStarted = false;
            if (session.getAttribute("countDownStarted") != null) {
                countDownStarted = Boolean.parseBoolean(session.getAttribute("countDownStarted").toString());
            }
            if (!countDownStarted) {
                this.addFirstResultat();
                session.setAttribute("countDownStarted", true);
            }
        } else {
            session.removeAttribute("countDownStarted");
        }
        long diffMilliseconds = course.getDate().getTime() - today.getTime();
        double diffDays = (double) diffMilliseconds / 1000 / 60 / 60 / 24;
        double diffHours = (diffDays - (int) diffDays) * 24;
        double diffMinutes = (diffHours - (int) diffHours) * 60;
        double diffSeconds = (diffMinutes - (int) diffMinutes) * 60;
        if (diffMilliseconds <= 0) {
            return "";
        }

        String countDown = "";
        if ((int) diffDays != 0) {
            countDown += (int) diffDays + " jour(s) ";
        }
        countDown += String.format("%02d", (int) diffHours) + ":"
                + String.format("%02d", (int) diffMinutes) + ":"
                + String.format("%02d", (int) diffSeconds);
        return countDown;
    }

    /**
     * Permet d'ajouter le premier Resultat d'entré en course (Ce premier
     * Resultat ayant la vitesse et le distance = 0 indique l'entré de
     * l'Utilisateur dans la Course)
     */
    private void addFirstResultat() {
        try {
//            DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss:SS");
//            Date startDate = new Date();
//            System.out.println("START TIME : " + dateFormat.format(startDate));

            URL url = new URL("http://172.31.250.233:8080/bzik-all/rest/resultat/");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            System.out.println("Vitesse : " + 0);

            ObjectMapper mapper = new ObjectMapper();

            ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
            HttpSession session = (HttpSession) context.getSession(true);
            Integer utilisateurId = Integer.parseInt(session.getAttribute("utilisateurId").toString());

            Utilisateur myUtilisateur = (Utilisateur) em.createQuery("SELECT u FROM Utilisateur u WHERE u.idPersonne = :idUtilisateur")
                    .setParameter("idUtilisateur", utilisateurId)
                    .setMaxResults(1)
                    .getSingleResult();
            System.out.println("***********************************************************" + course.getLibelle());
            String courseJson = mapper.writeValueAsString(course);
            String utilisateurJson = mapper.writeValueAsString(myUtilisateur);
            String input = "{\"distance\":\"" + 0 + "\","
                    + "\"vitesse\":\"" + 0 + "\","
                    + "\"course\": " + courseJson + ","
                    + "\"utilisateur\": " + utilisateurJson + "}";

            OutputStream os = conn.getOutputStream();
            os.write(input.getBytes());
            os.flush();

            if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED && conn.getResponseCode() != HttpURLConnection.HTTP_NO_CONTENT) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            String output;
            System.out.println("Output from Server .... \n");
            while ((output = br.readLine()) != null) {
                System.out.println(output);
            }
            conn.disconnect();
//            Date endDate = new Date();
//            System.out.println("END TIME : " + dateFormat.format(endDate));
        } catch (JsonGenerationException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (NoResultException e) {
            System.out.println("function addRestResultat : getSingleResult() did not retrieve any entities.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
