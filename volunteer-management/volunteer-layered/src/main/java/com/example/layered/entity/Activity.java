package com.example.layered.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "activities_layered")
public class Activity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 500)
    private String description;

    @Column(nullable = false)
    private Integer maxCapacity;

    @Column(nullable = false)
    private Integer currentParticipants;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ActivityStatus status;

    protected Activity() {
    }

    public Activity(String name, String description, Integer maxCapacity) {
        this.name = name;
        this.description = description;
        this.maxCapacity = maxCapacity;
        this.currentParticipants = 0;
        this.status = ActivityStatus.OPEN;
    }

    public boolean isFull() {
        return currentParticipants >= maxCapacity;
    }

    public boolean isClosed() {
        return status == ActivityStatus.CLOSED;
    }

    public boolean hasParticipants() {
        return currentParticipants > 0;
    }

    public void addParticipant() {
        if (isClosed()) {
            throw new IllegalStateException("Activity is already closed");
        }
        if (isFull()) {
            throw new IllegalStateException("Activity is full");
        }
        currentParticipants++;
        if (isFull()) {
            status = ActivityStatus.CLOSED;
        }
    }

    public void removeParticipant() {
        if (!hasParticipants()) {
            throw new IllegalStateException("There are no participants to remove");
        }
        currentParticipants--;
        if (!isFull()) {
            status = ActivityStatus.OPEN;
        }
    }

    public void updateDetails(String name, String description, Integer maxCapacity) {
        if (maxCapacity < currentParticipants) {
            throw new IllegalStateException(
                    "Capacity cannot be lower than current participants (" + currentParticipants + ")"
            );
        }
        this.name = name;
        this.description = description;
        this.maxCapacity = maxCapacity;
        this.status = isFull() ? ActivityStatus.CLOSED : ActivityStatus.OPEN;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public Integer getMaxCapacity() { return maxCapacity; }
    public Integer getCurrentParticipants() { return currentParticipants; }
    public ActivityStatus getStatus() { return status; }
}