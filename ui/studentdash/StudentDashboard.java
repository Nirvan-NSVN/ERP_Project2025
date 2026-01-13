package ui.studentdash;

import auth.UserSession;
import dao.*;
import models.*;
import ui.login.loginwindow;
import ui.common.circleCGPA;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.FlowLayout;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class StudentDashboard extends JFrame {

    private CardLayout card;
    private JPanel mainPanel;
    private JPanel leftSidebar;

    private boolean sidebarOpen = true;
    private int sidebarWidth = 220;

    private student loggedStudent;

    private final studentDAO studentDao = new studentDAO();
    private final sectionDAO sectionDao = new sectionDAO();
    private final catalogDAO catalogDao = new catalogDAO();
    private final adminDAO adminDao = new adminDAO();
    private final enrollmentDAO enrollmentDao = new enrollmentDAO();
    private final registrationDAO regDao = new registrationDAO();
    private final semesterresultDAO resultDao = new semesterresultDAO();
    private final courseDAO courseDao = new courseDAO();

    public StudentDashboard() {

        loggedStudent = studentDao.getStudentByUserId(UserSession.getUserId());
        if (loggedStudent == null) {
            JOptionPane.showMessageDialog(null, "Student not found!");
            return;
        }

        setTitle("Student Dashboard - Welcome " + loggedStudent.getName());
        setExtendedState(MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        buildUI();
        setVisible(true);
    }

    private JButton sidebarBtn(String text) {
        JButton b = new JButton(text);
        b.setBackground(new Color(140, 40, 200));
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Arial", Font.BOLD, 18));
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setHorizontalAlignment(SwingConstants.LEFT);
        b.setPreferredSize(new Dimension(160, 60));
        return b;
    }

    private JPanel buildMaintenanceBanner() {
        boolean maint = adminDao.isMaintenanceMode();
        if (!maint) return null;

        JLabel warn = new JLabel(
                " SYSTEM IS UNDER MAINTENANCE — Some actions are disabled",
                SwingConstants.CENTER
        );
        warn.setOpaque(true);
        warn.setBackground(new Color(255, 180, 180));
        warn.setForeground(Color.BLACK);
        warn.setFont(new Font("Arial", Font.BOLD, 18));
        warn.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel banner = new JPanel(new BorderLayout());
        banner.add(warn, BorderLayout.CENTER);
        return banner;
    }

    private void buildUI() {

        JPanel sidebarHeader = new JPanel(new BorderLayout());
        sidebarHeader.setBackground(new Color(140, 40, 200));
        sidebarHeader.setPreferredSize(new Dimension(sidebarWidth, 60));

        JPanel btnPanel = new JPanel(new GridLayout(10, 1, 5, 5));
        btnPanel.setBackground(new Color(90, 0, 150));

        JButton profileBtn = sidebarBtn("My Profile");
        JButton ttBtn = sidebarBtn("Time Table");
        JButton resultBtn = sidebarBtn("Result");
        JButton catalogBtn = sidebarBtn("Course Catalog");
        JButton regBtn = sidebarBtn("Course Registration");
        JButton logoutBtn = sidebarBtn("Logout");
        JButton notifyBtn = sidebarBtn("Notifications");
        JButton transcriptBtn = sidebarBtn("Download Transcript");

        btnPanel.add(notifyBtn);
        btnPanel.add(profileBtn);
        btnPanel.add(ttBtn);
        btnPanel.add(resultBtn);
        btnPanel.add(catalogBtn);
        btnPanel.add(regBtn);
        btnPanel.add(logoutBtn);
        btnPanel.add(transcriptBtn);

        leftSidebar = new JPanel(new BorderLayout());
        leftSidebar.setBackground(new Color(90, 0, 150));
        leftSidebar.setPreferredSize(new Dimension(sidebarWidth, getHeight()));

        leftSidebar.add(sidebarHeader, BorderLayout.NORTH);
        leftSidebar.add(btnPanel, BorderLayout.CENTER);

        add(leftSidebar, BorderLayout.WEST);

        mainPanel = new JPanel();
        card = new CardLayout();
        mainPanel.setLayout(card);

        mainPanel.add(buildHome(), "home");
        mainPanel.add(buildTimetable(), "timetable");
        mainPanel.add(buildResultPanel(), "result");
        mainPanel.add(buildCatalogPanel(), "catalog");
        mainPanel.add(buildCourseRegistration(), "registration");
        mainPanel.add(buildNotificationPanel(), "notif");

        add(mainPanel, BorderLayout.CENTER);

        profileBtn.addActionListener(e -> card.show(mainPanel, "home"));
        transcriptBtn.addActionListener(e -> downloadTranscriptCSV());
        ttBtn.addActionListener(e -> card.show(mainPanel, "timetable"));
        resultBtn.addActionListener(e -> card.show(mainPanel, "result"));
        catalogBtn.addActionListener(e -> card.show(mainPanel, "catalog"));
        regBtn.addActionListener(e -> card.show(mainPanel, "registration"));
        notifyBtn.addActionListener(e -> card.show(mainPanel, "notif"));

        logoutBtn.addActionListener(e -> {
            dispose();
            new loginwindow();
        });

        JButton floatToggle = new JButton("☰");
        floatToggle.setFont(new Font("Arial", Font.BOLD, 18));
        floatToggle.setBackground(new Color(153, 51, 255));
        floatToggle.setForeground(Color.WHITE);
        floatToggle.setBorderPainted(false);
        floatToggle.setFocusPainted(false);

        floatToggle.addActionListener(e -> toggleSidebar());

        getLayeredPane().add(floatToggle, JLayeredPane.PALETTE_LAYER);
        floatToggle.setBounds(8, 8, 40, 36);
    }

    private void toggleSidebar() {
        sidebarOpen = !sidebarOpen;
        leftSidebar.setVisible(sidebarOpen);
        revalidate();
        repaint();
    }

    // HOME PANEL
    private JPanel buildHome() {

        JPanel p = new JPanel(new BorderLayout());

        JPanel banner = buildMaintenanceBanner();
        if (banner != null) p.add(banner, BorderLayout.NORTH);

        JPanel center = new JPanel(new GridBagLayout());
        JPanel card = new JPanel(new GridLayout(6, 1, 10, 10));
        card.setBackground(new Color(255, 255, 255, 90));

        JLabel title = new JLabel("Welcome " + loggedStudent.getName(), SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 26));

        card.add(title);
        card.add(new JLabel("Program: " + loggedStudent.getProgramType()));
        card.add(new JLabel("Current Semester: " + loggedStudent.getSemester()));
        card.add(new JLabel("Student ID: " + loggedStudent.getStudentId()));
        card.add(new JLabel("User ID: " + loggedStudent.getUserId()));

        center.add(card);
        p.add(center);

        return p;
    }

    private JPanel buildTimetable() {

        JPanel p = new JPanel(new BorderLayout());

        JPanel banner = buildMaintenanceBanner();
        if (banner != null) p.add(banner, BorderLayout.NORTH);

        JLabel t = new JLabel("My Time Table", SwingConstants.CENTER);
        t.setFont(new Font("Arial", Font.BOLD, 26));
        p.add(t, BorderLayout.SOUTH);

        DefaultTableModel model = new DefaultTableModel(
                new String[]{"Course", "Title", "Day/Time", "Room", "Instructor"}, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        JTable table = new JTable(model);
        table.setRowHeight(26);
        p.add(new JScrollPane(table), BorderLayout.CENTER);

        List<enrollment> active = enrollmentDao.getActiveEnrollments(loggedStudent.getStudentId());

        for (enrollment e : active) {
            Object[] row = sectionDao.getTimetableRow(e.getSectionId());
            if (row != null) model.addRow(row);
        }

        return p;
    }

    // resut panel
    private JPanel buildResultPanel() {

        boolean maint = adminDao.isMaintenanceMode();

        JPanel p = new JPanel(new BorderLayout());

        JPanel banner = buildMaintenanceBanner();
        if (banner != null) p.add(banner, BorderLayout.NORTH);

        JLabel t = new JLabel("Results & CGPA", SwingConstants.CENTER);
        t.setFont(new Font("Arial", Font.BOLD, 22));
        p.add(t, BorderLayout.NORTH);

        JPanel top = new JPanel(new FlowLayout());
        JComboBox<Integer> semList = new JComboBox<>();

        for (int s : resultDao.getSemesters(loggedStudent.getStudentId())) {
            semList.addItem(s);
        }

        JButton load = new JButton("Load");
        if (maint) load.setEnabled(false);

        top.add(new JLabel("Semester:"));
        top.add(semList);
        top.add(load);

        p.add(top, BorderLayout.NORTH);

        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.add(new JLabel("Name: " + loggedStudent.getName()));
        info.add(new JLabel("Program: " + loggedStudent.getProgramType()));

        JPanel cg = new JPanel();
        cg.add(new circleCGPA(resultDao.getCGPA(loggedStudent.getStudentId()), "CGPA"));

        JPanel mid = new JPanel(new GridLayout(1, 2));
        mid.add(info);
        mid.add(cg);

        p.add(mid, BorderLayout.CENTER);

        DefaultTableModel model = new DefaultTableModel(
                new String[]{"Course", "Title", "Credit", "Grade", "Point"}, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        JTable table = new JTable(model);
        table.setRowHeight(26);
        p.add(new JScrollPane(table), BorderLayout.SOUTH);

        load.addActionListener(e -> {
            model.setRowCount(0);

            int sem = (int) semList.getSelectedItem();
            List<Object[]> rows = resultDao.getSemesterResult(loggedStudent.getStudentId(), sem);

            double tc = 0, tp = 0;

            for (Object[] r : rows) {
                model.addRow(r);
                double cr = Double.parseDouble(r[2].toString());
                double gp = Double.parseDouble(r[4].toString());
                tc += cr;
                tp += cr * gp;
            }

            model.addRow(new Object[]{"", "", "", "", ""});
            model.addRow(new Object[]{"", "", "", "Final SGPA",
                    String.format("%.2f", tc > 0 ? tp / tc : 0)});
        });

        return p;
    }

    // catalog panel
    private JPanel buildCatalogPanel() {

        JPanel p = new JPanel(new BorderLayout());

        JPanel banner = buildMaintenanceBanner();
        if (banner != null) p.add(banner, BorderLayout.NORTH);

        JLabel t = new JLabel("Course Catalog (Read Only)", SwingConstants.CENTER);
        t.setFont(new Font("Arial", Font.BOLD, 22));
        p.add(t, BorderLayout.NORTH);

        DefaultTableModel model = new DefaultTableModel(
                new String[]{"Code", "Title", "Section", "Instructor", "Day/Time", "Room", "Capacity"}, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        JTable table = new JTable(model);
        table.setRowHeight(26);

        p.add(new JScrollPane(table), BorderLayout.CENTER);

        for (Object[] row : catalogDao.getFullCatalog())
            model.addRow(row);

        return p;
    }

    // course registration panel
    private JPanel buildCourseRegistration() {

        boolean locked = adminDao.isRegistrationLocked();
        boolean maint = adminDao.isMaintenanceMode();

        JPanel p = new JPanel(new BorderLayout());

        JPanel banner = buildMaintenanceBanner();
        if (banner != null) p.add(banner, BorderLayout.NORTH);

        JLabel title = new JLabel("Course Registration", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));

        JPanel messagePanel = new JPanel(new BorderLayout());
        messagePanel.setBackground(new Color(255, 230, 180));
        messagePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTextArea msg = new JTextArea();
        msg.setEditable(false);
        msg.setLineWrap(true);
        msg.setWrapStyleWord(true);
        msg.setFont(new Font("Arial", Font.PLAIN, 15));
        msg.setBackground(new Color(255, 230, 180));

        String text = "";

        if (locked)
            text += "REGISTRATION IS CURRENTLY LOCKED BY ADMIN.\n";

        if (maint)
            text += "SYSTEM IS UNDER MAINTENANCE — registration disabled.\n";

        String announcement = new adminsettingsDAO().getStudentAnnouncement();
        if (announcement != null && !announcement.isBlank())
            text += "\nANNOUNCEMENT:\n" + announcement;

        if (!text.isEmpty()) {
            msg.setText(text);
            messagePanel.add(msg, BorderLayout.CENTER);
            p.add(messagePanel, BorderLayout.NORTH);
        }

    
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));

        int studentId = loggedStudent.getStudentId();

        p.add(title, BorderLayout.BEFORE_FIRST_LINE);
        p.add(new JScrollPane(listPanel), BorderLayout.CENTER);

    
        listPanel.add(new JLabel("ACTIVE COURSES"));
        for (enrollment e : enrollmentDao.getActiveEnrollments(studentId)) {
            int cid = sectionDao.getCourseIdBySection(e.getSectionId());
            course c = courseDao.getCourse(cid);

            JCheckBox box = new JCheckBox(c.getCode() + " - " + c.getTitle(), true);
            box.putClientProperty("courseId", cid);

            box.setEnabled(!locked);   // maintenance will NOT block checkbox (your instruction)

            listPanel.add(box);
        }

        listPanel.add(new JLabel("AVAILABLE NEW COURSES"));
        for (course c : courseDao.getEligibleCourses(
                loggedStudent.getProgramType(),
                loggedStudent.getSemester(),
                resultDao.getCGPA(studentId))) {

            int cid = c.getCourseId();

            if (regDao.hasCompletedOrTakenBefore(studentId, cid)) continue;

            JCheckBox box = new JCheckBox(c.getCode() + " - " + c.getTitle());
            box.putClientProperty("courseId", cid);

            box.setEnabled(!locked);

            listPanel.add(box);
        }

        listPanel.add(new JLabel("COMPLETED COURSES (Read Only)"));
        for (int cid : regDao.getCoursesTakenBefore(studentId)) {

            enrollment last = enrollmentDao.getEnrollmentForCourse(studentId, cid);
            if (last == null) continue;
            if (!last.getStatus().equals("completed")) continue;

            course c = courseDao.getCourse(cid);

            JCheckBox box = new JCheckBox(c.getCode() + " - " + c.getTitle() + " (Completed)");
            box.setEnabled(false);

            listPanel.add(box);
        }

        // save button– maintenance disables only Save
        JButton save = new JButton("Save");
        if (locked || maint) save.setEnabled(false);

        save.addActionListener(e -> saveRegistration(listPanel));

        p.add(save, BorderLayout.SOUTH);

        return p;
    }

    private void saveRegistration(JPanel listPanel) {

        int studentId = loggedStudent.getStudentId();

        List<JCheckBox> boxes = new ArrayList<>();
        collectCheckBoxes(listPanel, boxes);

        Map<Integer, enrollment> latestMap = new HashMap<>();

        for (enrollment en : enrollmentDao.getAllEnrollments(studentId)) {
            int cid = sectionDao.getCourseIdBySection(en.getSectionId());
            latestMap.put(cid, en);
        }

        try {

            for (JCheckBox box : boxes) {

                Integer courseId = (Integer) box.getClientProperty("courseId");
                boolean selected = box.isSelected();
                boolean takenBefore = latestMap.containsKey(courseId);

                if (selected && !takenBefore) {
                    regDao.registerCourse(studentId, courseId);
                    continue;
                }

                if (takenBefore) {

                    enrollment last = latestMap.get(courseId);
                    boolean completed = last.getStatus().equals("completed");
                    boolean active = last.getStatus().equals("active");
                    boolean dropped = last.getStatus().equals("dropped");

                    if (completed) continue;

                    // ACTIVE to select to keep
                    if (active && selected) continue;

                    // ACTIVE to uncheck to drop
                    if (active && !selected) {
                        regDao.dropCourse(studentId, courseId);
                        continue;
                    }

                    // DROPPED to re-register
                    if (dropped && selected) {
                        regDao.registerCourse(studentId, courseId);
                        continue;
                    }
                }
            }

            JOptionPane.showMessageDialog(this, "Registration updated!!!");

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saving registration!!");
        }
    }

    private void collectCheckBoxes(Container container, List<JCheckBox> list) {
        for (Component c : container.getComponents()) {
            if (c instanceof JCheckBox cb) list.add(cb);
            if (c instanceof Container) collectCheckBoxes((Container) c, list);
        }
    }

    // notif panel
    private JPanel buildNotificationPanel() {

        JPanel p = new JPanel(new BorderLayout());

        JPanel banner = buildMaintenanceBanner();
        if (banner != null) p.add(banner, BorderLayout.NORTH);

        JLabel title = new JLabel("Notifications", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        p.add(title, BorderLayout.NORTH);

        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        adminsettingsDAO settingsDao = new adminsettingsDAO();

        String studentMsg = settingsDao.getStudentAnnouncement();
        boolean locked = adminDao.isRegistrationLocked();
        boolean maint = adminDao.isMaintenanceMode();

        int count = 0;

        // locked / maintenance
        if (locked || maint) {
            JPanel box = new JPanel(new BorderLayout());
            box.setBackground(new Color(255, 210, 150));
            box.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            JTextArea area = new JTextArea();
            area.setEditable(false);
            area.setFont(new Font("Arial", Font.PLAIN, 16));
            area.setBackground(new Color(255, 210, 150));

            String msg = "";
            if (locked) msg += "REGISTRATION IS LOCKED \n";
            if (maint) msg += "SYSTEM IS UNDER MAINTENANCE students\n";

            area.setText(msg);
            box.add(area, BorderLayout.CENTER);
            container.add(box);
            container.add(Box.createVerticalStrut(12));

            count++;
        }

        // student announcement
        if (studentMsg != null && !studentMsg.isBlank()) {

            JPanel box = new JPanel(new BorderLayout());
            box.setBackground(new Color(210, 240, 255));
            box.setBorder(BorderFactory.createTitledBorder("Announcement for Students please see"));

            JTextArea area = new JTextArea(studentMsg);
            area.setEditable(false);
            area.setWrapStyleWord(true);
            area.setLineWrap(true);
            area.setFont(new Font("Arial", Font.PLAIN, 16));
            area.setBackground(new Color(210, 240, 255));

            box.add(area, BorderLayout.CENTER);

            container.add(box);
            container.add(Box.createVerticalStrut(12));

            count++;
        }

        if (count == 0) {
            JLabel empty = new JLabel("No notifications at the moment man.", SwingConstants.CENTER);
            empty.setFont(new Font("Arial", Font.ITALIC, 18));
            p.add(empty, BorderLayout.CENTER);
        } else {
            p.add(new JScrollPane(container), BorderLayout.CENTER);
        }

        return p;
    }
    private void downloadTranscriptCSV() {

    try {
        int studentId = loggedStudent.getStudentId();

        
        List<Object[]> rows = resultDao.getTranscriptData(studentId);

        if (rows == null || rows.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No completed courses to generate transcript.");
            return;
        }

        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Save Transcript CSV");
        fc.setSelectedFile(new java.io.File("Transcript_" + studentId + ".csv"));

        int choice = fc.showSaveDialog(this);
        if (choice != JFileChooser.APPROVE_OPTION) return;

        java.io.File file = fc.getSelectedFile();
        java.io.PrintWriter pw = new java.io.PrintWriter(file);

        // Student info
        pw.println("Name," + loggedStudent.getName());
        pw.println("Roll Number," + loggedStudent.getRollNo());
        pw.println();

        // Table Header
        pw.println("Course Code,Course Title,Credits,Grade,Semester,Year");

        // Rows
        for (Object[] r : rows) {
            pw.println(
                    r[0] + "," +     // course code
                    r[1] + "," +     // title
                    r[2] + "," +     // credits
                    r[3] + "," +     // grade
                    r[4] + "," +     // semester
                    r[5]             // year
            );
        }

        pw.close();
        JOptionPane.showMessageDialog(this, "Transcript saved:\n" + file.getAbsolutePath());

    } catch (Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error generating transcript.");
    }
}

}

