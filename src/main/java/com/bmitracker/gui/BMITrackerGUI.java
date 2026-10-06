package com.bmitracker.gui;

import com.bmitracker.dao.BMIRecordDAO;
import com.bmitracker.model.BMIRecord;
import com.bmitracker.service.BMIService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

/**
 * Swing GUI — demonstrates EVENT-DRIVEN PROGRAMMING (ActionListeners,
 * ListSelectionListener) on top of the OOP layers below it. The GUI
 * only talks to BMIService; it never touches SQL/JDBC or computes BMI
 * itself — it just displays what BMIRecord.getBmi()/getCategory() return.
 */
public class BMITrackerGUI extends JFrame {

    private final BMIService bmiService = new BMIService(new BMIRecordDAO());

    private JTextField firstNameField, lastNameField, emailField,
            heightField, weightField, searchField;
    private JTable table;
    private DefaultTableModel tableModel;
    private int selectedId = -1;

    public BMITrackerGUI() {
        setTitle("BMI Tracking System");
        setSize(900, 520);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        add(buildFormPanel(), BorderLayout.NORTH);
        add(buildTablePanel(), BorderLayout.CENTER);
        add(buildButtonPanel(), BorderLayout.SOUTH);

        refreshTable();
    }

    private JPanel buildFormPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 6, 5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Client Details"));

        firstNameField = new JTextField();
        lastNameField = new JTextField();
        emailField = new JTextField();
        heightField = new JTextField();
        weightField = new JTextField();
        searchField = new JTextField();

        panel.add(new JLabel("First Name"));   panel.add(firstNameField);
        panel.add(new JLabel("Last Name"));    panel.add(lastNameField);
        panel.add(new JLabel("Email"));        panel.add(emailField);
        panel.add(new JLabel("Height (cm)"));  panel.add(heightField);
        panel.add(new JLabel("Weight (kg)"));  panel.add(weightField);
        panel.add(new JLabel("Search"));       panel.add(searchField);

        return panel;
    }

    private JScrollPane buildTablePanel() {
        String[] columns = {"ID", "First Name", "Last Name", "Email", "Height (cm)", "Weight (kg)", "BMI", "Category"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        table = new JTable(tableModel);

        // EVENT-DRIVEN: row selection populates the form for editing.
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                int row = table.getSelectedRow();
                selectedId = (int) tableModel.getValueAt(row, 0);
                firstNameField.setText(tableModel.getValueAt(row, 1).toString());
                lastNameField.setText(tableModel.getValueAt(row, 2).toString());
                emailField.setText(tableModel.getValueAt(row, 3).toString());
                heightField.setText(tableModel.getValueAt(row, 4).toString());
                weightField.setText(tableModel.getValueAt(row, 5).toString());
            }
        });

        return new JScrollPane(table);
    }

    private JPanel buildButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout());

        JButton addBtn = new JButton("Add");
        JButton updateBtn = new JButton("Update");
        JButton deleteBtn = new JButton("Delete");
        JButton clearBtn = new JButton("Clear");
        JButton searchBtn = new JButton("Search");
        JButton refreshBtn = new JButton("Refresh");

        // EVENT-DRIVEN: each button wires a lambda ActionListener to a
        // service-layer call — GUI code stays thin and declarative.
        addBtn.addActionListener(this::onAdd);
        updateBtn.addActionListener(this::onUpdate);
        deleteBtn.addActionListener(this::onDelete);
        clearBtn.addActionListener(e -> clearForm());
        searchBtn.addActionListener(this::onSearch);
        refreshBtn.addActionListener(e -> refreshTable());

        panel.add(addBtn);
        panel.add(updateBtn);
        panel.add(deleteBtn);
        panel.add(clearBtn);
        panel.add(searchBtn);
        panel.add(refreshBtn);
        return panel;
    }

    private void onAdd(ActionEvent e) {
        try {
            BMIRecord r = readFormAsRecord(0);
            if (bmiService.addRecord(r)) {
                refreshTable();
                clearForm();
            } else {
                showError("Could not add record. Check that the email is unique.");
            }
        } catch (IllegalArgumentException ex) {
            showError(ex.getMessage() != null ? ex.getMessage() : "Invalid height/weight value.");
        }
    }

    private void onUpdate(ActionEvent e) {
        if (selectedId == -1) {
            showError("Select a record from the table first.");
            return;
        }
        try {
            BMIRecord r = readFormAsRecord(selectedId);
            if (bmiService.updateRecord(r)) {
                refreshTable();
                clearForm();
            } else {
                showError("Update failed.");
            }
        } catch (IllegalArgumentException ex) {
            showError(ex.getMessage() != null ? ex.getMessage() : "Invalid height/weight value.");
        }
    }

    private void onDelete(ActionEvent e) {
        if (selectedId == -1) {
            showError("Select a record from the table first.");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete this record?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            bmiService.deleteRecord(selectedId);
            refreshTable();
            clearForm();
        }
    }

    private void onSearch(ActionEvent e) {
        String query = searchField.getText().trim();
        List<BMIRecord> results = query.isEmpty()
                ? bmiService.getAllRecords()
                : bmiService.searchByName(query);
        populateTable(results);
    }

    private BMIRecord readFormAsRecord(int id) {
        return new BMIRecord(
                id,
                firstNameField.getText().trim(),
                lastNameField.getText().trim(),
                emailField.getText().trim(),
                Double.parseDouble(heightField.getText().trim()),
                Double.parseDouble(weightField.getText().trim())
        );
    }

    private void refreshTable() {
        populateTable(bmiService.getAllRecords());
    }

    private void populateTable(List<BMIRecord> records) {
        tableModel.setRowCount(0);
        for (BMIRecord r : records) {
            tableModel.addRow(new Object[]{
                    r.getId(), r.getFirstName(), r.getLastName(), r.getEmail(),
                    r.getHeightCm(), r.getWeightKg(),
                    String.format("%.1f", r.getBmi()), r.getCategory()
            });
        }
    }

    private void clearForm() {
        selectedId = -1;
        firstNameField.setText("");
        lastNameField.setText("");
        emailField.setText("");
        heightField.setText("");
        weightField.setText("");
        table.clearSelection();
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
