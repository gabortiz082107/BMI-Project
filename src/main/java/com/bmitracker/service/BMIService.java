package com.bmitracker.service;

import com.bmitracker.dao.Crud;
import com.bmitracker.model.BMIRecord;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service (business logic) layer.
 *
 * SOLID in action:
 *  - Single Responsibility: only validation + orchestration live here;
 *    persistence detail stays in BMIRecordDAO.
 *  - Dependency Inversion: this class depends on the Crud<BMIRecord>
 *    interface, NOT the concrete BMIRecordDAO class. A test double or a
 *    different storage engine can be injected via the constructor
 *    without changing a single line here (also enables unit testing).
 *  - Open/Closed: new business rules can be added here without
 *    modifying the DAO or GUI layers.
 */
public class BMIService {

    private final Crud<BMIRecord> bmiRepository;

    public BMIService(Crud<BMIRecord> bmiRepository) {
        this.bmiRepository = bmiRepository;
    }

    public boolean addRecord(BMIRecord record) {
        return bmiRepository.create(record);
    }

    public BMIRecord getRecord(int id) {
        return bmiRepository.read(id);
    }

    public List<BMIRecord> getAllRecords() {
        return bmiRepository.readAll();
    }

    public boolean updateRecord(BMIRecord record) {
        return bmiRepository.update(record);
    }

    public boolean deleteRecord(int id) {
        return bmiRepository.delete(id);
    }

    /** Business rule: search by name (case-insensitive). */
    public List<BMIRecord> searchByName(String query) {
        String q = query.toLowerCase().trim();
        return bmiRepository.readAll().stream()
                .filter(r -> r.getFullName().toLowerCase().contains(q))
                .collect(Collectors.toList());
    }

    /** Business rule: filter records by BMI category. */
    public List<BMIRecord> getByCategory(String category) {
        return bmiRepository.readAll().stream()
                .filter(r -> r.getCategory().equalsIgnoreCase(category))
                .collect(Collectors.toList());
    }
}
