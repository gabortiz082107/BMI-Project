package com.bmitracker.model;

/**
 * Abstract base class representing any person in the system.
 * Demonstrates ABSTRACTION (cannot be instantiated directly, forces
 * subclasses to define getRole()) and ENCAPSULATION (private fields,
 * controlled access through getters/setters with validation).
 */
public abstract class Person {

    private int id;
    private String firstName;
    private String lastName;
    private String email;

    public Person(int id, String firstName, String lastName, String email) {
        this.id = id;
        setFirstName(firstName);
        setLastName(lastName);
        setEmail(email); // goes through validation
    }

    // ----- Encapsulated accessors -----
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new IllegalArgumentException("First name cannot be empty.");
        }
        this.firstName = firstName.trim();
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new IllegalArgumentException("Last name cannot be empty.");
        }
        this.lastName = lastName.trim();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (email == null) {
            throw new IllegalArgumentException("Invalid email format: " + email);
        }
        String trimmedEmail = email.trim();
        if (!trimmedEmail.matches("^[\\w.+-]+@[\\w-]+\\.[a-zA-Z]{2,}$")) {
            throw new IllegalArgumentException("Invalid email format: " + email);
        }
        this.email = trimmedEmail;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    /**
     * ABSTRACT METHOD — every concrete subclass must define its own role.
     * This is the hook that enables POLYMORPHISM when different Person
     * subclasses are treated uniformly through this base type.
     */
    public abstract String getRole();

    @Override
    public String toString() {
        return String.format("[%d] %s (%s) - %s", id, getFullName(), getRole(), email);
    }
}
