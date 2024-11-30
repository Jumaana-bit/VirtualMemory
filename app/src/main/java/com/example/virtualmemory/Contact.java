package com.example.virtualmemory;

public class Contact {
    private long id;
    private String name;
    private String relationship;
    private String email;
    private String phoneNumber;
    private String category;
    private String voiceMemoPath;  // New field for voice memo path

    // Constructor updated to include voiceMemoPath
    public Contact(long id, String name, String relationship, String email, String phoneNumber, String category, String voiceMemoPath) {
        this.id = id;
        this.name = name;
        this.relationship = relationship;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.category = category;
        this.voiceMemoPath = voiceMemoPath;
    }

    // Getters and Setters for the new field
    public String getVoiceMemoPath() {
        return voiceMemoPath;
    }

    public void setVoiceMemoPath(String voiceMemoPath) {
        this.voiceMemoPath = voiceMemoPath;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getRelationship() {
        return relationship;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getCategory() {
        return category;
    }
}
