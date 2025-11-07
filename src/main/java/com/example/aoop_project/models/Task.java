package com.example.aoop_project.models;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Task {
    private final StringProperty task = new SimpleStringProperty();
    private final BooleanProperty done = new SimpleBooleanProperty();

    public Task(String task, boolean done) {
        this.task.set(task);
        this.done.set(done);
    }

    // getters / setters
    public String getTask() { return task.get(); }
    public void setTask(String value) { task.set(value); }
    public StringProperty taskProperty() { return task; }

    public boolean isDone() { return done.get(); }
    public void setDone(boolean value) { done.set(value); }
    public BooleanProperty doneProperty() { return done; }
}