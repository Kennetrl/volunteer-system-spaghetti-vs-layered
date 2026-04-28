package com.example.spaghetti;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class ActivityController {

    @PersistenceContext
    private EntityManager entityManager;

    // List all activities (home page)
    @GetMapping("/")
    public String home(Model model) {
        List<Activity> activities = entityManager
                .createQuery("SELECT a FROM Activity a ORDER BY a.id DESC", Activity.class)
                .getResultList();
        model.addAttribute("activities", activities);
        return "index";
    }

    // Create a new activity
    @PostMapping("/activities")
    @Transactional
    public String crearActividad(@RequestParam String name,
                                 @RequestParam String description,
                                 @RequestParam Integer maxCapacity,
                                 Model model) {

        if (name == null || name.trim().isEmpty()) {
            model.addAttribute("error", "El nombre es obligatorio");
            List<Activity> activities = entityManager
                    .createQuery("SELECT a FROM Activity a ORDER BY a.id DESC", Activity.class)
                    .getResultList();
            model.addAttribute("activities", activities);
            return "index";
        }

        if (maxCapacity == null || maxCapacity <= 0) {
            model.addAttribute("error", "La capacidad maxima debe ser mayor a cero");
            List<Activity> activities = entityManager
                    .createQuery("SELECT a FROM Activity a ORDER BY a.id DESC", Activity.class)
                    .getResultList();
            model.addAttribute("activities", activities);
            return "index";
        }

        if (description != null && description.length() > 500) {
            model.addAttribute("error", "La descripcion es muy larga");
            List<Activity> activities = entityManager
                    .createQuery("SELECT a FROM Activity a ORDER BY a.id DESC", Activity.class)
                    .getResultList();
            model.addAttribute("activities", activities);
            return "index";
        }

        Activity activity = new Activity();
        activity.setName(name);
        activity.setDescription(description);
        activity.setMaxCapacity(maxCapacity);
        activity.setCurrentParticipants(0);
        activity.setStatus("OPEN");

        entityManager.persist(activity);

        return "redirect:/";
    }

    // Show edit form
    @GetMapping("/activities/{id}/editar")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        Activity activity = entityManager.find(Activity.class, id);

        if (activity == null) {
            model.addAttribute("error", "Actividad no encontrada");
            List<Activity> activities = entityManager
                    .createQuery("SELECT a FROM Activity a ORDER BY a.id DESC", Activity.class)
                    .getResultList();
            model.addAttribute("activities", activities);
            return "index";
        }

        model.addAttribute("activity", activity);
        return "edit";
    }

    // Update activity (edit name, description, capacity)
    @PostMapping("/activities/{id}/actualizar")
    @Transactional
    public String actualizarActividad(@PathVariable Long id,
                                      @RequestParam String name,
                                      @RequestParam String description,
                                      @RequestParam Integer maxCapacity,
                                      Model model) {

        Activity activity = entityManager.find(Activity.class, id);

        if (activity == null) {
            model.addAttribute("error", "Actividad no encontrada");
            List<Activity> activities = entityManager
                    .createQuery("SELECT a FROM Activity a ORDER BY a.id DESC", Activity.class)
                    .getResultList();
            model.addAttribute("activities", activities);
            return "index";
        }

        if (name == null || name.trim().isEmpty()) {
            model.addAttribute("error", "El nombre es obligatorio");
            model.addAttribute("activity", activity);
            return "edit";
        }

        if (maxCapacity == null || maxCapacity <= 0) {
            model.addAttribute("error", "La capacidad maxima debe ser mayor a cero");
            model.addAttribute("activity", activity);
            return "edit";
        }

        if (description != null && description.length() > 500) {
            model.addAttribute("error", "La descripcion es muy larga");
            model.addAttribute("activity", activity);
            return "edit";
        }

        // Cannot reduce capacity below current participants
        if (maxCapacity < activity.getCurrentParticipants()) {
            model.addAttribute("error",
                    "No se puede reducir la capacidad por debajo de los participantes actuales (" +
                            activity.getCurrentParticipants() + ")");
            model.addAttribute("activity", activity);
            return "edit";
        }

        activity.setName(name);
        activity.setDescription(description);
        activity.setMaxCapacity(maxCapacity);

        // Recalculate status: if capacity went up, reopen; if reached, close
        if (activity.getCurrentParticipants() < activity.getMaxCapacity()) {
            activity.setStatus("OPEN");
        } else {
            activity.setStatus("CLOSED");
        }

        entityManager.merge(activity);

        return "redirect:/";
    }

    // Delete activity
    @PostMapping("/activities/{id}/eliminar")
    @Transactional
    public String eliminarActividad(@PathVariable Long id, Model model) {
        Activity activity = entityManager.find(Activity.class, id);

        if (activity == null) {
            model.addAttribute("error", "Actividad no encontrada");
            List<Activity> activities = entityManager
                    .createQuery("SELECT a FROM Activity a ORDER BY a.id DESC", Activity.class)
                    .getResultList();
            model.addAttribute("activities", activities);
            return "index";
        }

        entityManager.remove(activity);

        return "redirect:/";
    }

    // Add participant to activity (replaces "join")
    @PostMapping("/activities/{id}/anadir")
    @Transactional
    public String anadirParticipante(@PathVariable Long id, Model model) {

        Activity activity = entityManager.find(Activity.class, id);

        if (activity == null) {
            model.addAttribute("error", "Actividad no encontrada");
            List<Activity> activities = entityManager
                    .createQuery("SELECT a FROM Activity a ORDER BY a.id DESC", Activity.class)
                    .getResultList();
            model.addAttribute("activities", activities);
            return "index";
        }

        if ("CLOSED".equals(activity.getStatus())) {
            model.addAttribute("error", "La actividad ya esta cerrada");
            List<Activity> activities = entityManager
                    .createQuery("SELECT a FROM Activity a ORDER BY a.id DESC", Activity.class)
                    .getResultList();
            model.addAttribute("activities", activities);
            return "index";
        }

        if (activity.getCurrentParticipants() >= activity.getMaxCapacity()) {
            activity.setStatus("CLOSED");
            entityManager.merge(activity);
            model.addAttribute("error", "La actividad esta llena");
            List<Activity> activities = entityManager
                    .createQuery("SELECT a FROM Activity a ORDER BY a.id DESC", Activity.class)
                    .getResultList();
            model.addAttribute("activities", activities);
            return "index";
        }

        activity.setCurrentParticipants(activity.getCurrentParticipants() + 1);

        if (activity.getCurrentParticipants().equals(activity.getMaxCapacity())) {
            activity.setStatus("CLOSED");
        }

        entityManager.merge(activity);

        return "redirect:/";
    }
}