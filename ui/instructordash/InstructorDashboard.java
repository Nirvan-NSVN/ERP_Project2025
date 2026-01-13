package ui.instructordash;

import auth.UserSession;
import dao.*;
import database.ERPDB;
import models.instructor;
import models.section;
import models.course;
import models.coursecomponent;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.*;
import java.util.List;

public class InstructorDashboard extends JFrame {

    private CardLayout card;
    private JPanel mainPanel;
    private JPanel leftSidebar;

    private boolean sidebarOpen = true;

    private final adminDAO adminDao = new adminDAO();
    private boolean maintenanceOn = false;

    private instructorDAO instDAO = new instructorDAO();
    private sectionDAO secDAO = new sectionDAO();
    private coursecomponentDAO compDAO = new coursecomponentDAO();
    private studentcomponentmarksDAO marksDAO = new studentcomponentmarksDAO();
    private statsDAO statsDAO = new statsDAO();
    private semesterresultDAO resultDao = new semesterresultDAO();
    private courseDAO courseDao = new courseDAO();

    private int instructorId;
    private instructor currentInstructor;

    private JComboBox<String> sectionDrop;
    private JTable marksTable;
    private DefaultTableModel marksModel;

    private List<Integer> componentIds = new ArrayList<>();
    private List<String> componentNames = new ArrayList<>();

    public InstructorDashboard() {
        setTitle("Instructor Dashboard");
        setExtendedState(MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        maintenanceOn = adminDao.isMaintenanceMode();

        loadInstructor();
        buildUI();
        setVisible(true);
    }
    private void loadInstructor() {
        int userId = UserSession.getUserId();
        currentInstructor = instDAO.getInstructorByUserId(userId);

        if (currentInstructor == null) {
            JOptionPane.showMessageDialog(this, "Instructor not found.");
            dispose();
            return;
        }
        instructorId = currentInstructor.getInstructorId();
    }
    private JButton sidebarBtn(String text) {
        JButton b = new JButton(text);
        b.setBackground(new Color(140, 40, 200));
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Arial", Font.BOLD, 18));
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setHorizontalAlignment(SwingConstants.LEFT);
        b.setPreferredSize(new Dimension(160, 60));
        return b;
    }
    private JPanel maintenanceBanner() {
        JPanel banner = new JPanel(new BorderLayout());
        banner.setBackground(new Color(255, 200, 120));
        banner.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel lbl = new JLabel("⚠ SYSTEM IS UNDER MAINTENANCE — Actions are disabled.", SwingConstants.CENTER);
        lbl.setFont(new Font("Arial", Font.BOLD, 18));

        banner.add(lbl, BorderLayout.CENTER);
        return banner;
    }

    private void disableAllButtons(JPanel parent) {
        Component[] comps = parent.getComponents();
        for (Component c : comps) {
            if (c instanceof JButton) ((JButton) c).setEnabled(false);
            if (c instanceof JPanel) disableAllButtons((JPanel) c);
        }
    }
    private void buildUI() {

        JPanel sidebarHeader = new JPanel(new BorderLayout());
        sidebarHeader.setBackground(new Color(140, 40, 200));
        sidebarHeader.setPreferredSize(new Dimension(220, 60));

        JPanel btnPanel = new JPanel(new GridLayout(10, 1, 5, 5));
        btnPanel.setBackground(new Color(90, 0, 150));

        JButton profileBtn = sidebarBtn("My Profile");
        JButton sectionsBtn = sidebarBtn("My Sections");
        JButton marksBtn = sidebarBtn("Enter Marks");
        JButton compBtn = sidebarBtn("Manage Components");
        JButton statsBtn = sidebarBtn("Class Stats");
        JButton adminNotifBtn = sidebarBtn("Admin Notifications");
        JButton instAnnounceBtn = sidebarBtn("Post to Students");
        JButton logoutBtn = sidebarBtn("Logout");

        btnPanel.add(profileBtn);
        btnPanel.add(sectionsBtn);
        btnPanel.add(marksBtn);
        btnPanel.add(compBtn);
        btnPanel.add(statsBtn);
        btnPanel.add(adminNotifBtn);
        btnPanel.add(instAnnounceBtn);
        btnPanel.add(logoutBtn);

        leftSidebar = new JPanel(new BorderLayout());
        leftSidebar.setBackground(new Color(90, 0, 150));
        leftSidebar.add(sidebarHeader, BorderLayout.NORTH);
        leftSidebar.add(btnPanel, BorderLayout.CENTER);
        leftSidebar.setPreferredSize(new Dimension(220, getHeight()));

        mainPanel = new JPanel();
        card = new CardLayout();
        mainPanel.setLayout(card);

        // PANELS WITH MAINTENANCE BLOCKING
        mainPanel.add(wrapWithMaintenance(buildProfilePanel()), "profile");
        mainPanel.add(wrapWithMaintenance(buildSectionsPanel()), "sections");
        mainPanel.add(wrapWithMaintenance(buildMarksPanel()), "marks");
        mainPanel.add(wrapWithMaintenance(new componentmanagerpanel(instructorId)), "components");
        mainPanel.add(wrapWithMaintenance(buildStatsPanel()), "stats");
        mainPanel.add(wrapWithMaintenance(buildAdminNotificationPanel()), "admin_notif");
        mainPanel.add(wrapWithMaintenance(buildInstructorAnnouncementPanel()), "inst_announce");

        add(leftSidebar, BorderLayout.WEST);
        add(mainPanel, BorderLayout.CENTER);

        // NAVIGATION
        profileBtn.addActionListener(e -> card.show(mainPanel, "profile"));
        sectionsBtn.addActionListener(e -> card.show(mainPanel, "sections"));
        marksBtn.addActionListener(e -> card.show(mainPanel, "marks"));
        compBtn.addActionListener(e -> card.show(mainPanel, "components"));
        statsBtn.addActionListener(e -> card.show(mainPanel, "stats"));
        adminNotifBtn.addActionListener(e -> card.show(mainPanel, "admin_notif"));
        instAnnounceBtn.addActionListener(e -> card.show(mainPanel, "inst_announce"));

        logoutBtn.addActionListener(e -> {
            dispose();
            new ui.login.loginwindow();
        });

        JButton floatToggle = new JButton("☰");
        floatToggle.setFont(new Font("Arial", Font.BOLD, 18));
        floatToggle.setBackground(new Color(153, 51, 255));
        floatToggle.setForeground(Color.WHITE);
        floatToggle.setBorderPainted(false);
        floatToggle.addActionListener(e -> toggleSidebar());

        getLayeredPane().add(floatToggle, JLayeredPane.PALETTE_LAYER);
        floatToggle.setBounds(8, 8, 40, 36);
    }
    private JPanel wrapWithMaintenance(JPanel panel) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(panel, BorderLayout.CENTER);

        if (maintenanceOn) {
            JPanel banner = maintenanceBanner();
            wrapper.add(banner, BorderLayout.NORTH);
            disableAllButtons(panel);
        }

        return wrapper;
    }

    private void toggleSidebar() {
        sidebarOpen = !sidebarOpen;
        leftSidebar.setVisible(sidebarOpen);
        revalidate();
        repaint();
    }
    private JPanel buildProfilePanel() {
        JPanel p = new JPanel(new GridBagLayout());
        JPanel c = new JPanel(new GridLayout(5, 1, 10, 10));

        JLabel t = new JLabel("Instructor Profile", SwingConstants.CENTER);
        t.setFont(new Font("Arial", Font.BOLD, 28));

        c.add(t);
        c.add(new JLabel("Name: " + currentInstructor.getName()));
        c.add(new JLabel("Department: " + currentInstructor.getDepartment()));
        c.add(new JLabel("Instructor ID: " + currentInstructor.getInstructorId()));
        c.add(new JLabel("User ID: " + currentInstructor.getUserId()));

        p.add(c);
        return p;
    }
    private JPanel buildSectionsPanel() {
        JPanel p = new JPanel(new BorderLayout());

        JLabel t = new JLabel("My Sections", SwingConstants.CENTER);
        t.setFont(new Font("Arial", Font.BOLD, 26));

        DefaultTableModel m = new DefaultTableModel(
                new String[]{"Section ID", "Course ID", "Semester", "Year", "Capacity", "Room"}, 0
        );
        JTable table = new JTable(m);

        for (section s : secDAO.getSectionsByInstructor(instructorId)) {
            m.addRow(new Object[]{
                    s.getSectionId(),
                    s.getCourseId(),
                    s.getSemester(),
                    s.getYear(),
                    s.getCapacity(),
                    s.getRoom()
            });
        }

        p.add(t, BorderLayout.NORTH);
        p.add(new JScrollPane(table), BorderLayout.CENTER);

        return p;
    }
    // MARK ENTRY PANEL
    private JPanel buildMarksPanel() {
    JPanel p = new JPanel(new BorderLayout());

    JLabel title = new JLabel("Marks Entry", SwingConstants.CENTER);
    title.setFont(new Font("Arial", Font.BOLD, 26));
    p.add(title, BorderLayout.NORTH);

    JPanel top = new JPanel();
    sectionDrop = new JComboBox<>();
    loadSectionsIntoDropdown();

    top.add(new JLabel("Section: "));
    top.add(sectionDrop);

    p.add(top, BorderLayout.BEFORE_FIRST_LINE);

    marksModel = new DefaultTableModel();
    marksTable = new JTable(marksModel);
    marksTable.setRowHeight(25);

    p.add(new JScrollPane(marksTable), BorderLayout.CENTER);

    JPanel bottom = new JPanel();
    JButton save = new JButton("Save Marks");
    JButton importBtn = new JButton("Import CSV");
    JButton exportBtn = new JButton("Export CSV");

    bottom.add(save);
    bottom.add(importBtn);
    bottom.add(exportBtn);

    p.add(bottom, BorderLayout.SOUTH);

    sectionDrop.addActionListener(e -> loadMarksTable());

    //  You missed these!
    save.addActionListener(e -> saveMarks());
    importBtn.addActionListener(e -> importCSV());
    exportBtn.addActionListener(e -> exportCSV());

    return p;
}


    private void loadSectionsIntoDropdown() {
        sectionDrop.removeAllItems();
        for (section s : secDAO.getSectionsByInstructor(instructorId)) {
            course c = courseDao.getCourse(s.getCourseId());
            sectionDrop.addItem("Sec " + s.getSectionId() + " | " + c.getCode() + " - " + c.getTitle());
        }
    }
    private int getSelectedSectionId() {
    String s = sectionDrop.getSelectedItem().toString();
    String secId = s.substring(4, s.indexOf("|")).trim();
    return Integer.parseInt(secId);
}

    private void loadMarksTable() {
        if (sectionDrop.getSelectedItem() == null) return;

        int secId = getSelectedSectionId();
        section sec = secDAO.getSection(secId);

        componentIds.clear();
        componentNames.clear();

        //int offeringId = new courseofferingDAO()
          //      .getOffering(sec.getCourseId(), sec.getSemester(), sec.getYear())
            //    .getOfferingId();
        var cod = new courseofferingDAO();
        var offering = cod.getOffering(sec.getCourseId(), sec.getSemester(), sec.getYear());

        if (offering == null) {
            // No offering exists for this section 
            List<Integer> enrList = secDAO.getEnrollmentIds(secId);
            Map<Integer, String[]> names = secDAO.getNamesForSection(secId);
            marksModel.setColumnIdentifiers(new String[] { "Enrollment ID", "Student" });
            marksModel.setRowCount(0);

            for (int enr : enrList) {String[] info = names.get(enr);
            marksModel.addRow(new Object[]{enr,info[0] + " - " + info[1]});
        }
        return;}

int offeringId = offering.getOfferingId();
        for (coursecomponent c : compDAO.getComponentsByOffering(offeringId)) {
            componentIds.add(c.getComponentId());
            componentNames.add(c.getComponentName());
        }

        Map<Integer, Map<Integer, Double>> marks =
                marksDAO.getAllMarksForSection(secId);

        String[] cols = new String[2 + componentIds.size()];
        cols[0] = "Enrollment ID";
        cols[1] = "Student";

        for (int i = 0; i < componentIds.size(); i++)
            cols[i + 2] = componentNames.get(i);

        marksModel.setColumnIdentifiers(cols);
        marksModel.setRowCount(0);

        Map<Integer, String[]> names = secDAO.getNamesForSection(secId);
        List<Integer> enrList = secDAO.getEnrollmentIds(secId);

        for (int enr : enrList) {
            Object[] row = new Object[cols.length];
            row[0] = enr;

            String[] info = names.get(enr);
            row[1] = info[0] + " - " + info[1];

            Map<Integer, Double> studentMarks = marks.getOrDefault(enr, new HashMap<>());
            for (int i = 0; i < componentIds.size(); i++) {
                Double m = studentMarks.get(componentIds.get(i));
                row[i + 2] = (m != null ? m : "");
            }
            marksModel.addRow(row);
        }
        System.out.println("Selected SECTION ID: " + secId);

    }
    private JPanel buildStatsPanel() {

    JPanel p = new JPanel(new BorderLayout());

    JLabel t = new JLabel("Class Statistics", SwingConstants.CENTER);
    t.setFont(new Font("Arial", Font.BOLD, 26));
    p.add(t, BorderLayout.NORTH);

    // SECTION DROPDOWN
    JComboBox<String> drop = new JComboBox<>();
    Map<String, Integer> map = new HashMap<>();

    for (section s : secDAO.getSectionsByInstructor(instructorId)) {
        String key = "Section " + s.getSectionId();
        drop.addItem(key);
        map.put(key, s.getSectionId());
    }

    // TEXT AREA
    JTextArea area = new JTextArea();
    area.setEditable(false);
    area.setFont(new Font("Monospaced", Font.PLAIN, 15));

    // BUTTON
    JButton compute = new JButton("Compute Statistics");

    // TOP PANEL
    JPanel top = new JPanel();
    top.add(new JLabel("Choose Section:"));
    top.add(drop);

    p.add(top, BorderLayout.BEFORE_FIRST_LINE);
    p.add(new JScrollPane(area), BorderLayout.CENTER);
    p.add(compute, BorderLayout.SOUTH);

    // Compute
    compute.addActionListener(e -> {

        if (drop.getSelectedItem() == null) {
            area.setText("No section selected.");
            return;
        }

        int secId = map.get(drop.getSelectedItem().toString());

        List<Double> scores = statsDAO.getWeightedScores(secId);

        if (scores.isEmpty()) {
            area.setText("No marks entered yet for this section.");
            return;
        }

        double mean = statsDAO.getMean(scores);
        double median = statsDAO.getMedian(scores);
        double min = statsDAO.getMin(scores);
        double max = statsDAO.getMax(scores);
        double std = statsDAO.getStdDev(scores);


        StringBuilder sb = new StringBuilder();
        sb.append("CLASS STATISTICS\n");
        sb.append("Section: ").append(secId).append("\n");
        sb.append("------------------------------\n");
        sb.append(String.format("Students Count     : %d\n", scores.size()));
        sb.append(String.format("Mean Score         : %.2f\n", mean));
        sb.append(String.format("Median Score       : %.2f\n", median));
        sb.append(String.format("Minimum Score      : %.2f\n", min));
        sb.append(String.format("Maximum Score      : %.2f\n", max));
        sb.append(String.format("Std Dev            : %.2f\n", std));

        area.setText(sb.toString());
    });

    return p;
}

    private JPanel buildAdminNotificationPanel() {
        JPanel p = new JPanel(new BorderLayout());

        JLabel title = new JLabel("Admin Notes", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 26));
        p.add(title, BorderLayout.NORTH);

        JTextArea area = new JTextArea();
        area.setEditable(false);

        adminsettingsDAO settings = new adminsettingsDAO();
        String note = settings.getInstructorAnnouncement();

        area.setText(note == null || note.isBlank() ?
                "No Admin Notifications." :
                note);

        p.add(new JScrollPane(area), BorderLayout.CENTER);
        return p;
    }

    private JPanel buildInstructorAnnouncementPanel() {

        JPanel p = new JPanel(new BorderLayout());

        JLabel title = new JLabel("Post Student Announcement", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 26));
        p.add(title, BorderLayout.NORTH);

        adminsettingsDAO settings = new adminsettingsDAO();
        String existing = settings.getStudentAnnouncement();

        JTextArea area = new JTextArea(existing);
        area.setFont(new Font("Arial", Font.PLAIN, 16));
        area.setBorder(BorderFactory.createTitledBorder("Write Announcement"));

        JPanel btns = new JPanel();
        JButton publish = new JButton("Publish");
        JButton update = new JButton("Update");
        JButton delete = new JButton("Delete");

        btns.add(publish);
        btns.add(update);
        btns.add(delete);

        p.add(new JScrollPane(area), BorderLayout.CENTER);
        p.add(btns, BorderLayout.SOUTH);
        publish.addActionListener(e -> {
        String text = area.getText().trim();
        if (text.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Cannot publish empty announcement SIR.");
            return;
        }

        settings.setStudentAnnouncement(text);
        JOptionPane.showMessageDialog(null, "Announcement Published SIR!");
    });

    update.addActionListener(e -> {
        String text = area.getText().trim();
        if (text.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Announcement text is empty SIR.");
            return;
        }

        settings.setStudentAnnouncement(text);
        JOptionPane.showMessageDialog(null, "Announcement Updated SIR!");
    });


    delete.addActionListener(e -> {
        int confirm = JOptionPane.showConfirmDialog(
                null,
                "Are you sure you want to delete the announcement?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            settings.setStudentAnnouncement("");
            area.setText("");
            JOptionPane.showMessageDialog(null, "Announcement Deleted SIR!");
        }
    });

        return p;
    }
    private void saveMarks() {

        int secId = getSelectedSectionId();

        try (Connection con = ERPDB.getERPConnection()) {

            PreparedStatement batch = marksDAO.getBatchSaveStatement(con);
            List<Integer> enrollmentList = new ArrayList<>();

            for (int r = 0; r < marksModel.getRowCount(); r++) {

                int enrollmentId = Integer.parseInt(marksModel.getValueAt(r, 0).toString());
                enrollmentList.add(enrollmentId);

                for (int c = 0; c < componentIds.size(); c++) {

                    Object val = marksModel.getValueAt(r, c + 2);
                    if (val == null || val.toString().isBlank()) continue;

                    double marks = Double.parseDouble(val.toString());
                    int compId = componentIds.get(c);
                    double max = compDAO.getComponent(compId).getMaxMarks();

                    marksDAO.addToBatch(batch,
                            enrollmentId,
                            compId,
                            marks,
                            max,
                            instructorId
                    );
                }
            }

            // First save MARKS to DB
            batch.executeBatch();

            // Then update SGPA/grades for ALL students
            for (int enr : enrollmentList) {
                resultDao.updateAfterMarks(enr);
            }

            JOptionPane.showMessageDialog(this, "Marks Saved & Grades Updated!");

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saving marks");
        }
    }

    private void lockMarksTemporarily() {
        int secId = getSelectedSectionId();
        try {
            new sectionDAO().setTempLock(secId, true);
            JOptionPane.showMessageDialog(this, "Temporary Lock Enabled!");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error locking section.");
        }
    }
    private void unlockMarksTemporarily() {
        int secId = getSelectedSectionId();
        try {
            new sectionDAO().setTempLock(secId, false);
            JOptionPane.showMessageDialog(this, "Temporary Lock Removed!");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error unlocking section.");
        }
    }
    private void importCSV() {

        JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;

        File file = chooser.getSelectedFile();

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {

        String header = br.readLine(); // skip header

        Map<Integer, Map<Integer, Double>> csvMap = new HashMap<>();

        String line;
        while ((line = br.readLine()) != null) {
            String[] arr = line.split(",");

            int enr = Integer.parseInt(arr[0]);

            Map<Integer, Double> compMap = new HashMap<>();
            for (int i = 0; i < componentIds.size(); i++) {
                compMap.put(componentIds.get(i), Double.parseDouble(arr[i + 2]));
            }

            csvMap.put(enr, compMap);
        }

        // Upload in batch
        try (Connection conn = ERPDB.getERPConnection()) {

            PreparedStatement ps = marksDAO.getBatchSaveStatement(conn);

            for (int enr : csvMap.keySet()) {
                Map<Integer, Double> m = csvMap.get(enr);
                for (int comp : m.keySet()) {

                    double marks = m.get(comp);
                    double maxMarks = compDAO.getComponent(comp).getMaxMarks();

                    marksDAO.addToBatch(ps, enr, comp, marks, maxMarks, instructorId);
                }
            }

            ps.executeBatch();
        }

        JOptionPane.showMessageDialog(this, "CSV Imported Successfully!");
        loadMarksTable();

    } catch (Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, "Import Error!");
    }
}
private void exportCSV() {

    JFileChooser chooser = new JFileChooser();
    chooser.setSelectedFile(new File("marks_export.csv"));

    if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;

    File file = chooser.getSelectedFile();

    try (PrintWriter pw = new PrintWriter(file)) {

        // Header
        for (int i = 0; i < marksModel.getColumnCount(); i++) {
            pw.print(marksModel.getColumnName(i));
            if (i < marksModel.getColumnCount() - 1) pw.print(",");
        }
        pw.println();

        // Rows
        for (int r = 0; r < marksModel.getRowCount(); r++) {
            for (int c = 0; c < marksModel.getColumnCount(); c++) {
                pw.print(marksModel.getValueAt(r, c));
                if (c < marksModel.getColumnCount() - 1) pw.print(",");
            }
            pw.println();
        }

        JOptionPane.showMessageDialog(this, "CSV Exported Successfully!");

    } catch (Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, "Export Error!");
    }
}

}

