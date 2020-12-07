package edu.wpi.modula3.what2think.model;

public class SimpleChoice {
    String id;
    String description;
    String dateCreated;
    String dateCompleted;

    public SimpleChoice(){

    }
    public SimpleChoice(String id, String description, String dateCreated, String dateCompleted){
        this.id = id;
        this.description = description;
        this.dateCreated = dateCreated;
        this.dateCompleted = dateCompleted;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getDateCompleted() {
        return dateCompleted;
    }

    public void setDateCompleted(String dateCompleted) {
        this.dateCompleted = dateCompleted;
    }
}
