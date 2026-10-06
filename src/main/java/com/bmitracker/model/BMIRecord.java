package com.bmitracker.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Concrete BMIRecord class — one person's BMI tracking record.
 * Demonstrates INHERITANCE (extends Person), POLYMORPHISM (overrides
 * getRole() and toString()), COLLECTIONS (weight history list), and
 * ABSTRACTION (BMI math and category logic are hidden behind methods
 * so callers never compute it themselves — single source of truth).
 */
public class BMIRecord extends Person {

    private double heightCm;
    private double weightKg;
    private final List<Double> weightHistoryKg = new ArrayList<>();

    public BMIRecord(int id, String firstName, String lastName, String email,
                      double heightCm, double weightKg) {
        super(id, firstName, lastName, email);
        setHeightCm(heightCm);
        setWeightKg(weightKg);
    }

    @Override
    public String getRole() {
        return "Client";
    }

    // ----- Encapsulated accessors with validation -----
    public double getHeightCm() {
        return heightCm;
    }

    public void setHeightCm(double heightCm) {
        if (heightCm < 50 || heightCm > 272) {
            throw new IllegalArgumentException("Height must be between 50 and 272 cm.");
        }
        this.heightCm = heightCm;
    }

    public double getWeightKg() {
        return weightKg;
    }

    public void setWeightKg(double weightKg) {
        if (weightKg < 2 || weightKg > 650) {
            throw new IllegalArgumentException("Weight must be between 2 and 650 kg.");
        }
        this.weightKg = weightKg;
        weightHistoryKg.add(weightKg); // keep a running history as weight changes
    }

    public List<Double> getWeightHistoryKg() {
        return Collections.unmodifiableList(weightHistoryKg);
    }

    /**
     * Core BMI formula: weight(kg) / height(m)^2.
     * Kept here (not in the DB) so there is a single authoritative
     * calculation — the stored table never has a chance to go stale
     * relative to height/weight.
     */
    public double getBmi() {
        double heightM = heightCm / 100.0;
        return weightKg / (heightM * heightM);
    }

    /** WHO standard adult BMI categories. */
    public String getCategory() {
        double bmi = getBmi();
        if (bmi < 18.5) return "Underweight";
        if (bmi < 25.0) return "Normal";
        if (bmi < 30.0) return "Overweight";
        return "Obese";
    }

    /** POLYMORPHISM: overrides Person's toString with BMI-specific detail. */
    @Override
    public String toString() {
        return String.format("%s | Height: %.1fcm | Weight: %.1fkg | BMI: %.1f (%s)",
                super.toString(), heightCm, weightKg, getBmi(), getCategory());
    }
}
