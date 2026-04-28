package com.example.layered.service;

import com.example.layered.dto.CreateActivityRequest;
import com.example.layered.dto.UpdateActivityRequest;
import com.example.layered.entity.Activity;
import com.example.layered.exception.ActivityNotFoundException;
import com.example.layered.repository.ActivityRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ActivityService {

    private final ActivityRepository activityRepository;

    public ActivityService(ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    @Transactional(readOnly = true)
    public List<Activity> findAll() {
        return activityRepository.findAllByOrderByIdDesc();
    }

    @Transactional(readOnly = true)
    public Activity findById(Long id) {
        return activityRepository.findById(id)
                .orElseThrow(() -> new ActivityNotFoundException(id));
    }

    @Transactional
    public Activity create(CreateActivityRequest request) {
        Activity activity = new Activity(
                request.getName(),
                request.getDescription(),
                request.getMaxCapacity()
        );
        return activityRepository.save(activity);
    }

    @Transactional
    public Activity update(Long id, UpdateActivityRequest request) {
        Activity activity = findById(id);
        activity.updateDetails(request.getName(), request.getDescription(), request.getMaxCapacity());
        return activityRepository.save(activity);
    }

    @Transactional
    public void delete(Long id) {
        Activity activity = findById(id);
        activityRepository.delete(activity);
    }

    @Transactional
    public Activity addParticipant(Long id) {
        Activity activity = findById(id);
        activity.addParticipant();
        return activityRepository.save(activity);
    }

    @Transactional
    public Activity removeParticipant(Long id) {
        Activity activity = findById(id);
        activity.removeParticipant();
        return activityRepository.save(activity);
    }
}