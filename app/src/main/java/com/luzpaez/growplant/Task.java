package com.luzpaez.growplant;


public class Task {
    private String id;
    private String plantName;
    private String taskDescription;
    private String dueDate;
    private String imageUrl;
    private boolean isCompleted;

    public Task() {}

    public Task(String id, String plantName, String taskDescription, String dueDate, String imageUrl, boolean isCompleted) {
        this.id = id;
        this.plantName = plantName;
        this.taskDescription = taskDescription;
        this.dueDate = dueDate;
        this.imageUrl = imageUrl;
        this.isCompleted = isCompleted;
    }

    // Getters y Setters
    public String getId() { return id; }
    public String getPlantName() { return plantName; }
    public String getTaskDescription() { return taskDescription; }
    public String getDueDate() { return dueDate; }
    public String getImageUrl() { return imageUrl; }
    public boolean isCompleted() { return isCompleted; }
}
