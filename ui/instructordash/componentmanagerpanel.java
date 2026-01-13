package ui.instructordash;

import dao.coursecomponentDAO;
import dao.courseofferingDAO;
import dao.sectionDAO;
import dao.studentcomponentmarksDAO;
import models.coursecomponent;
import models.courseoffering;
import models.section;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class componentmanagerpanel extends JPanel {

    private int instructorId;
    private JComboBox<String> sectionDropdown;
    private DefaultTableModel componentModel;
    private JTable componentTable;

    private sectionDAO sectionDao = new sectionDAO();
    private courseofferingDAO offeringDAO = new courseofferingDAO();
    private coursecomponentDAO componentDAO = new coursecomponentDAO();
    private studentcomponentmarksDAO marksDAO = new studentcomponentmarksDAO();

    public componentmanagerpanel(int instructorId) {
        this.instructorId = instructorId;

        setLayout(new BorderLayout(10,10));
        setBackground(Color.WHITE);

        JLabel title = new JLabel("Manage Components", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        add(title, BorderLayout.NORTH);

        JPanel top = new JPanel(new FlowLayout());
        top.add(new JLabel("Select Section: "));

        sectionDropdown = new JComboBox<>();
        sectionDropdown.setFont(new Font("Arial", Font.PLAIN, 16));
        top.add(sectionDropdown);

        add(top, BorderLayout.BEFORE_FIRST_LINE);

        // load sections taught by instructor
        loadInstructorSections();

        sectionDropdown.addActionListener(e -> loadComponents());

        componentModel = new DefaultTableModel(
                new String[]{"Component ID", "Name", "Weightage", "Max Marks"}, 0
        );
        componentTable = new JTable(componentModel);
        componentTable.setRowHeight(26);

        add(new JScrollPane(componentTable), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout());

        JButton addBtn = new JButton("Add Component");
        JButton delBtn = new JButton("Delete Component");

        addBtn.addActionListener(e -> addComponent());
        delBtn.addActionListener(e -> deleteComponent());

        bottom.add(addBtn);
        bottom.add(delBtn);

        add(bottom, BorderLayout.SOUTH);
    }

    private void loadInstructorSections() {
        sectionDropdown.removeAllItems();
        try {
            List<section> list = sectionDao.getSectionsByInstructor(instructorId);
            for (section s : list) {
                String disp = "Sec " + s.getSectionId() +
                        " | CourseID " + s.getCourseId() +
                        " | Sem " + s.getSemester();
                sectionDropdown.addItem(disp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadComponents() {

        componentModel.setRowCount(0);

        String sel = (String) sectionDropdown.getSelectedItem();
        if (sel == null) return;

        int secId = Integer.parseInt(sel.split(" ")[1]);

        try {
            // find offering
            section sec = sectionDao.getSection(secId);
            courseoffering offering = offeringDAO.getOffering(
                    sec.getCourseId(),
                    sec.getSemester(),
                    sec.getYear()
            );

            if (offering == null) {
                JOptionPane.showMessageDialog(this, "No Offering Found!");
                return;
            }

            List<coursecomponent> comps =
                    componentDAO.getComponentsByOffering(offering.getOfferingId());

            for (coursecomponent c : comps) {
                componentModel.addRow(new Object[]{
                        c.getComponentId(),
                        c.getComponentName(),
                        c.getWeightage(),
                        c.getMaxMarks()
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void addComponent() {

        String sel = (String) sectionDropdown.getSelectedItem();
        if (sel == null) {
            JOptionPane.showMessageDialog(this, "Choose a section first!");
            return;
        }

        int secId = Integer.parseInt(sel.split(" ")[1]);

        try {
            section sec = sectionDao.getSection(secId);
            courseoffering off = offeringDAO.getOffering(
                    sec.getCourseId(),
                    sec.getSemester(),
                    sec.getYear()
            );

            if (off == null) {
                JOptionPane.showMessageDialog(this,"Offering not found!");
                return;
            }

            JTextField nameF = new JTextField();
            JTextField wF = new JTextField();
            JTextField mF = new JTextField();

            JPanel form = new JPanel(new GridLayout(3,2));
            form.add(new JLabel("Name: "));
            form.add(nameF);
            form.add(new JLabel("Weightage: "));
            form.add(wF);
            form.add(new JLabel("Max Marks: "));
            form.add(mF);

            int res = JOptionPane.showConfirmDialog(this, form,
                    "Add Component", JOptionPane.OK_CANCEL_OPTION);

            if (res != JOptionPane.OK_OPTION) return;

            String name = nameF.getText().trim();
            double weight = Double.parseDouble(wF.getText().trim());
            double maxMarks = Double.parseDouble(mF.getText().trim());

            componentDAO.createComponent(
                    off.getOfferingId(),
                    name,
                    weight,
                    instructorId,
                    maxMarks
            );

            loadComponents();

            JOptionPane.showMessageDialog(this, "Component Added!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void deleteComponent() {

        int row = componentTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select component first!");
            return;
        }

        int compId = (int) componentModel.getValueAt(row, 0);

        try {
            boolean ok = componentDAO.deleteComponent(compId);

            if (!ok) {
                JOptionPane.showMessageDialog(this,
                        "Cannot delete! Marks already exist.");
            } else {
                loadComponents();
                JOptionPane.showMessageDialog(this, "Component Deleted!");
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error deleting!");
        }
    }

}
