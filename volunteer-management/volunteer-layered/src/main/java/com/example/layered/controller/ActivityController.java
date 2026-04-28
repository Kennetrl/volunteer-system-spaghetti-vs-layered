package com.example.layered.controller;

import com.example.layered.dto.CreateActivityRequest;
import com.example.layered.dto.UpdateActivityRequest;
import com.example.layered.service.ActivityService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class ActivityController {

    private final ActivityService activityService;

    public ActivityController(ActivityService activityService) {
        this.activityService = activityService;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("activities", activityService.findAll());
        if (!model.containsAttribute("createActivityRequest")) {
            model.addAttribute("createActivityRequest", new CreateActivityRequest());
        }
        return "index";
    }

    @PostMapping("/activities")
    public String create(@Valid @ModelAttribute("createActivityRequest") CreateActivityRequest request,
                         BindingResult bindingResult,
                         Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("activities", activityService.findAll());
            return "index";
        }
        activityService.create(request);
        return "redirect:/";
    }

    @GetMapping("/activities/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        var activity = activityService.findById(id);
        var request = new UpdateActivityRequest();
        request.setName(activity.getName());
        request.setDescription(activity.getDescription());
        request.setMaxCapacity(activity.getMaxCapacity());

        model.addAttribute("activity", activity);
        if (!model.containsAttribute("updateActivityRequest")) {
            model.addAttribute("updateActivityRequest", request);
        }
        return "edit";
    }

    @PostMapping("/activities/{id}/update")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("updateActivityRequest") UpdateActivityRequest request,
                         BindingResult bindingResult,
                         Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("activity", activityService.findById(id));
            return "edit";
        }
        activityService.update(id, request);
        return "redirect:/";
    }

    @PostMapping("/activities/{id}/delete")
    public String delete(@PathVariable Long id) {
        activityService.delete(id);
        return "redirect:/";
    }

    @PostMapping("/activities/{id}/add-participant")
    public String addParticipant(@PathVariable Long id) {
        activityService.addParticipant(id);
        return "redirect:/";
    }

    @PostMapping("/activities/{id}/remove-participant")
    public String removeParticipant(@PathVariable Long id) {
        activityService.removeParticipant(id);
        return "redirect:/";
    }
}