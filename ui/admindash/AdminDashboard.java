package ui.admindash;

import dao.*;
import models.*;
import ui.login.loginwindow;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class AdminDashboard extends JFrame {

    private CardLayout card;
    private JPanel mainPanel;
    private JPanel sidebar;

    private final adminDAO adminDao = new adminDAO();                 
    private final studentDAO studentDao = new studentDAO();
    private final instructorDAO instructorDao = new instructorDAO();
    private final courseDAO courseDao = new courseDAO();
    private final sectionDAO sectionDao = new sectionDAO();
    private final adminsettingsDAO settingsDao = new adminsettingsDAO(); 

    public AdminDashboard() {
        setTitle("Admin Dashboard");
        setExtendedState(MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        buildUI();
        setVisible(true);
    }
    // Sidebar Button
    private JButton sidebarBtn(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("Arial", Font.BOLD, 18));
        b.setForeground(Color.WHITE);
        b.setBackground(new Color(140, 40, 200));
        b.setHorizontalAlignment(SwingConstants.LEFT);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        return b;
    }

    // BUILD UI using
    private void buildUI() {

        sidebar = new JPanel(new GridLayout(20, 1, 2, 2));
        sidebar.setBackground(new Color(90, 0, 150));
        sidebar.setPreferredSize(new Dimension(220, getHeight()));

        JButton studentBtn = sidebarBtn("Manage Students");
        JButton instructorBtn = sidebarBtn("Manage Instructors");
        JButton adminBtn = sidebarBtn("Manage Admins");
        JButton courseBtn = sidebarBtn("Manage Courses");
        JButton sectionBtn = sidebarBtn("Manage Sections");
        JButton regLockBtn = sidebarBtn("Registration Lock");
        JButton maintainBtn = sidebarBtn("Maintenance Mode");
        JButton notifBtn = sidebarBtn("Notifications");
        JButton logoutBtn = sidebarBtn("Logout");

        sidebar.add(studentBtn);
        sidebar.add(instructorBtn);
        sidebar.add(adminBtn);
        sidebar.add(courseBtn);
        sidebar.add(sectionBtn);
        sidebar.add(new JLabel());
        sidebar.add(regLockBtn);
        sidebar.add(maintainBtn);
        sidebar.add(notifBtn);
        sidebar.add(new JLabel());
        sidebar.add(logoutBtn);

        add(sidebar, BorderLayout.WEST);

        // Toggle Sidebar
        JButton toggle = new JButton("☰");
        toggle.setBounds(10, 10, 40, 40);
        toggle.setBackground(new Color(153, 51, 255));
        toggle.setForeground(Color.WHITE);
        toggle.setFocusPainted(false);
        toggle.setBorderPainted(false);
        toggle.addActionListener(e -> {
            sidebar.setVisible(!sidebar.isVisible());
            revalidate();
        });

        getLayeredPane().add(toggle, JLayeredPane.PALETTE_LAYER);

        // Main Panel
        mainPanel = new JPanel();
        card = new CardLayout();
        mainPanel.setLayout(card);

        mainPanel.add(buildStudentsPanel(), "students");
        mainPanel.add(buildInstructorsPanel(), "instructors");
        mainPanel.add(buildAdminsPanel(), "admins");
        mainPanel.add(buildCoursesPanel(), "courses");
        mainPanel.add(buildSectionsPanel(), "sections");
        mainPanel.add(buildRegLockPanel(), "registration");
        mainPanel.add(buildMaintenancePanel(), "maintenance");
        mainPanel.add(buildNotificationPanel(), "notifications");

        add(mainPanel, BorderLayout.CENTER);
        studentBtn.addActionListener(e -> card.show(mainPanel, "students"));
        instructorBtn.addActionListener(e -> card.show(mainPanel, "instructors"));
        adminBtn.addActionListener(e -> card.show(mainPanel, "admins"));
        courseBtn.addActionListener(e -> card.show(mainPanel, "courses"));
        sectionBtn.addActionListener(e -> card.show(mainPanel, "sections"));
        regLockBtn.addActionListener(e -> card.show(mainPanel, "registration"));
        maintainBtn.addActionListener(e -> card.show(mainPanel, "maintenance"));
        notifBtn.addActionListener(e -> card.show(mainPanel, "notifications"));

        logoutBtn.addActionListener(e -> {
            dispose();
            new loginwindow();
        });
    }
    // student panel
    private JPanel buildStudentsPanel() {

        JPanel p = new JPanel(new BorderLayout());

        JLabel title = new JLabel("Manage Students", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        p.add(title, BorderLayout.NORTH);

        DefaultTableModel model = new DefaultTableModel(
                new String[]{"ID", "Roll", "Name", "Program", "Sem", "Hostel", "UserID"}, 0);

        JTable table = new JTable(model);
        refreshStudents(model);

        JPanel bottom = new JPanel();
        JButton add = new JButton("Add");
        JButton edit = new JButton("Edit");
        JButton delete = new JButton("Delete");
        JButton refresh = new JButton("Refresh");

        bottom.add(add);
        bottom.add(edit);
        bottom.add(delete);
        bottom.add(refresh);

        add.addActionListener(e -> showStudentForm(null, model));
        edit.addActionListener(e -> editSelectedStudent(table, model));
        delete.addActionListener(e -> deleteSelectedStudent(table, model));
        refresh.addActionListener(e -> refreshStudents(model));

        p.add(new JScrollPane(table), BorderLayout.CENTER);
        p.add(bottom, BorderLayout.SOUTH);

        return p;
    }

    private void refreshStudents(DefaultTableModel model) {
        model.setRowCount(0);
        for (student s : adminDao.getAllStudents()) {
            model.addRow(new Object[]{
                    s.getStudentId(), s.getRollNo(), s.getName(),
                    s.getProgramType(), s.getSemester(),
                    s.getHostel(), s.getUserId()
            });
        }
    }

    private void editSelectedStudent(JTable table, DefaultTableModel model) {
        int row = table.getSelectedRow();
        if (row == -1) return;

        student s = new student(
                (int) model.getValueAt(row, 0),
                (int) model.getValueAt(row, 6),
                (String) model.getValueAt(row, 1),
                (String) model.getValueAt(row, 2),
                (String) model.getValueAt(row, 3),
                (int) model.getValueAt(row, 4),
                (String) model.getValueAt(row, 5)
        );

        showStudentForm(s, model);
    }

    private void deleteSelectedStudent(JTable table, DefaultTableModel model) {
        int row = table.getSelectedRow();
        if (row == -1) return;

        int studentId = (int) model.getValueAt(row, 0);
        int userId = (int) model.getValueAt(row, 6);

        if (adminDao.deleteStudent(studentId, userId)) {
            JOptionPane.showMessageDialog(this, "Student Deleted");
            refreshStudents(model);
        }
    }

    private void showStudentForm(student s, DefaultTableModel model) {

        JTextField username = new JTextField();
        JTextField email = new JTextField();
        JTextField password = new JTextField();

        JTextField roll = new JTextField();
        JTextField name = new JTextField();
        JComboBox<String> program = new JComboBox<>(new String[]{"BTECH", "MTECH", "PHD"});
        JTextField semester = new JTextField();
        JTextField hostel = new JTextField();

        if (s != null) {
            // Cannot edit user account
            username.setText("—");
            username.setEnabled(false);
            email.setText("—");
            email.setEnabled(false);
            password.setText("—");
            password.setEnabled(false);

            roll.setText(s.getRollNo());
            name.setText(s.getName());
            program.setSelectedItem(s.getProgramType());
            semester.setText(String.valueOf(s.getSemester()));
            hostel.setText(s.getHostel());
        }

        Object[] form = {
                "Username:", username,
                "Email:", email,
                (s == null ? "Password:" : "Password (unchanged):"), password,
                "Roll No:", roll,
                "Name:", name,
                "Program:", program,
                "Semester:", semester,
                "Hostel (optional):", hostel
        };

        int result = JOptionPane.showConfirmDialog(
                this, form,
                (s == null ? "Add Student" : "Edit Student"),
                JOptionPane.OK_CANCEL_OPTION
        );

        if (result != JOptionPane.OK_OPTION) return;

        if (s == null) {
            // Add mode
            String hostelVal = hostel.getText().trim();
            if (hostelVal.isEmpty()) hostelVal = null;

            boolean ok = adminDao.createStudent(
                    username.getText().trim(),
                    email.getText().trim(),
                    password.getText().trim(),
                    roll.getText().trim(),
                    name.getText().trim(),
                    (String) program.getSelectedItem(),
                    Integer.parseInt(semester.getText().trim()),
                    hostelVal
            );

            if (ok) {
                JOptionPane.showMessageDialog(this, "Student Added");
                refreshStudents(model);
            } else {
                JOptionPane.showMessageDialog(this, "Error adding student");
            }

        } else {
            // Edit mode
            s.setRollNo(roll.getText().trim());
            s.setName(name.getText().trim());
            s.setProgramType((String) program.getSelectedItem());
            s.setSemester(Integer.parseInt(semester.getText().trim()));
            s.setHostel(hostel.getText().trim());

            if (adminDao.updateStudent(s)) {
                JOptionPane.showMessageDialog(this, "Student Updated");
                refreshStudents(model);
            } else {
                JOptionPane.showMessageDialog(this, "Error updating student");
            }
        }
    }
    // instructr pamel
    private JPanel buildInstructorsPanel() {

        JPanel p = new JPanel(new BorderLayout());

        JLabel title = new JLabel("Manage Instructors", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        p.add(title, BorderLayout.NORTH);

        DefaultTableModel model = new DefaultTableModel(
                new String[]{"ID", "Name", "Dept", "UserID"}, 0);

        JTable table = new JTable(model);
        refreshInstructors(model);

        JPanel bottom = new JPanel();
        JButton add = new JButton("Add");
        JButton edit = new JButton("Edit");
        JButton delete = new JButton("Delete");
        JButton refresh = new JButton("Refresh");

        bottom.add(add);
        bottom.add(edit);
        bottom.add(delete);
        bottom.add(refresh);

        add.addActionListener(e -> showInstructorForm(null, model));
        edit.addActionListener(e -> editSelectedInstructor(table, model));
        delete.addActionListener(e -> deleteSelectedInstructor(table, model));
        refresh.addActionListener(e -> refreshInstructors(model));

        p.add(new JScrollPane(table), BorderLayout.CENTER);
        p.add(bottom, BorderLayout.SOUTH);

        return p;
    }

    private void refreshInstructors(DefaultTableModel model) {
        model.setRowCount(0);
        for (instructor i : adminDao.getAllInstructors()) {
            model.addRow(new Object[]{
                    i.getInstructorId(), i.getName(), i.getDepartment(), i.getUserId()
            });
        }
    }

    private void editSelectedInstructor(JTable table, DefaultTableModel model) {
        int row = table.getSelectedRow();
        if (row == -1) return;

        instructor ins = new instructor(
                (int) model.getValueAt(row, 0),
                (int) model.getValueAt(row, 3),
                (String) model.getValueAt(row, 1),
                (String) model.getValueAt(row, 2)
        );

        showInstructorForm(ins, model);
    }

    private void deleteSelectedInstructor(JTable table, DefaultTableModel model) {
        int row = table.getSelectedRow();
        if (row == -1) return;

        int insId = (int) model.getValueAt(row, 0);
        int userId = (int) model.getValueAt(row, 3);

        if (adminDao.deleteInstructor(insId, userId)) {
            JOptionPane.showMessageDialog(this, "Instructor Deleted");
            refreshInstructors(model);
        }
    }

    private void showInstructorForm(instructor ins, DefaultTableModel model) {

        JTextField username = new JTextField();
        JTextField email = new JTextField();
        JTextField password = new JTextField();

        JTextField name = new JTextField();
        JTextField dep = new JTextField();

        if (ins != null) {
            username.setText("—");
            username.setEnabled(false);
            email.setText("—");
            email.setEnabled(false);
            password.setText("—");
            password.setEnabled(false);

            name.setText(ins.getName());
            dep.setText(ins.getDepartment());
        }

        Object[] form = {
                "Username:", username,
                "Email:", email,
                (ins == null ? "Password:" : "Password (unchanged):"), password,
                "Name:", name,
                "Department:", dep
        };

        int result = JOptionPane.showConfirmDialog(
                this, form,
                (ins == null ? "Add Instructor" : "Edit Instructor"),
                JOptionPane.OK_CANCEL_OPTION
        );

        if (result != JOptionPane.OK_OPTION) return;

        if (ins == null) {
            boolean ok = adminDao.createInstructor(
                    username.getText(), email.getText(), password.getText(),
                    name.getText(), dep.getText()
            );

            if (ok) {
                JOptionPane.showMessageDialog(this, "Instructor Added");
                refreshInstructors(model);
            }
        } else {
            ins.setName(name.getText());
            ins.setDepartment(dep.getText());

            if (adminDao.updateInstructor(ins)) {
                JOptionPane.showMessageDialog(this, "Instructor Updated");
                refreshInstructors(model);
            }
        }
    }


    private JPanel buildAdminsPanel() {

    JPanel p = new JPanel(new BorderLayout());

    JLabel title = new JLabel("Manage Admins", SwingConstants.CENTER);
    title.setFont(new Font("Arial", Font.BOLD, 22));
    p.add(title, BorderLayout.NORTH);
    DefaultTableModel model = new DefaultTableModel(
        new String[]{"User ID", "Username", "Email"}, 0
    );

    JTable table = new JTable(model);
    refreshAdmins(model);

    JPanel bottom = new JPanel();
    JButton add = new JButton("Add");
    JButton delete = new JButton("Delete");
    JButton refresh = new JButton("Refresh");

    bottom.add(add);
    bottom.add(delete);
    bottom.add(refresh);

    add.addActionListener(e -> showAdminForm(null, model));
    delete.addActionListener(e -> deleteSelectedAdmin(table, model));
    refresh.addActionListener(e -> refreshAdmins(model));

    p.add(new JScrollPane(table), BorderLayout.CENTER);
    p.add(bottom, BorderLayout.SOUTH);

    return p;
}
private void refreshAdmins(DefaultTableModel model) {
    model.setRowCount(0);

    List<admin> list = adminDao.getAllAdmins();

    for (admin a : list) {
        model.addRow(new Object[]{
            a.getUserId(),
            a.getUsername(),
            a.getEmail()
        });
    }
}

private void deleteSelectedAdmin(JTable table, DefaultTableModel model) {
    int row = table.getSelectedRow();
    if (row == -1) {
        JOptionPane.showMessageDialog(this, "Select a row first");
        return;
    }

    int userId = Integer.parseInt(model.getValueAt(row, 0).toString());

    int confirm = JOptionPane.showConfirmDialog(
        this,
        "Delete this admin?",
        "Confirm",
        JOptionPane.YES_NO_OPTION
    );

    if (confirm == JOptionPane.YES_OPTION) {
        if (adminDao.deleteAdmin(userId)) {
            JOptionPane.showMessageDialog(this, "Admin Deleted SIR");
            refreshAdmins(model);
        } else {
            JOptionPane.showMessageDialog(this, "Error deleting admin SIR");
        }
    }
}

private void showAdminForm(admin adm, DefaultTableModel model) {

    JTextField username = new JTextField();
    JTextField email = new JTextField();
    JPasswordField password = new JPasswordField();

    // If editing an admin
    if (adm != null) {
        username.setText(adm.getUsername());
        username.setEnabled(false);  // cannot edit username

        email.setText(adm.getEmail());
        email.setEnabled(false);     // cannot edit email

        password.setEnabled(false);  // password stays unchanged
    }

    Object[] form = {
        "Username:", username,
        "Email:", email,
        (adm == null ? "Password:" : "Password (unchanged):"), password
    };

    int result = JOptionPane.showConfirmDialog(
        this, form,
        (adm == null ? "Add Admin" : "Edit Admin"),
        JOptionPane.OK_CANCEL_OPTION
    );

    if (result != JOptionPane.OK_OPTION) return;

    if (adm == null) {
        // ADD ADMIN
        boolean ok = adminDao.createAdmin(
            username.getText(),
            email.getText(),
            new String(password.getPassword())
        );

        if (ok) {
            JOptionPane.showMessageDialog(this, "Admin Added");
            refreshAdmins(model);
        } else {
            JOptionPane.showMessageDialog(this, "Failed to Add Admin");
        }
    } else {
        // EDIT ADMIN — Only username/email not editable; password unchanged
        boolean ok = adminDao.updateAdmin(adm);
        if (ok) {
            JOptionPane.showMessageDialog(this, "Admin Updated");
            refreshAdmins(model);
        } else {
            JOptionPane.showMessageDialog(this, "Failed to Update Admin");
        }
    }
}

    // courses pamel
    private JPanel buildCoursesPanel() {

        JPanel p = new JPanel(new BorderLayout());
        JLabel t = new JLabel("Manage Courses", SwingConstants.CENTER);
        t.setFont(new Font("Arial", Font.BOLD, 20));
        p.add(t, BorderLayout.NORTH);

        DefaultTableModel model = new DefaultTableModel(
                new String[]{
                        "ID", "Code", "Title", "Credits",
                        "Dept", "Allowed", "MinSem", "CGPA"
                }, 0);

        JTable table = new JTable(model);
        loadCourses(model);

        JButton add = new JButton("Add");
        JButton edit = new JButton("Edit");
        JButton del = new JButton("Delete");
        JButton refresh = new JButton("Refresh");

        add.addActionListener(e -> showCourseForm(null, model));

        edit.addActionListener(e -> {
            int r = table.getSelectedRow();
            if (r == -1) {
                JOptionPane.showMessageDialog(this, "Select a row first");
                return;
            }
            int id = (int) model.getValueAt(r, 0);
            course c = courseDao.getCourse(id);
            showCourseForm(c, model);
        });

        del.addActionListener(e -> {
            int r = table.getSelectedRow();
            if (r == -1) {
                JOptionPane.showMessageDialog(this, "Select a course first");
                return;
            }

            int id = (int) model.getValueAt(r, 0);

            int ok = JOptionPane.showConfirmDialog(
                    this,
                    "Delete this course? ALL prerequisites referencing it will be deleted.",
                    "Confirm",
                    JOptionPane.YES_NO_OPTION
            );
            if (ok == JOptionPane.YES_OPTION) {
                if (courseDao.deleteCourse(id)) {
                    JOptionPane.showMessageDialog(this, "Course Deleted");
                    loadCourses(model);
                } else {
                    JOptionPane.showMessageDialog(this, "Failed!");
                }
            }
        });

        refresh.addActionListener(e -> loadCourses(model));

        JPanel bottom = new JPanel();
        bottom.add(add);
        bottom.add(edit);
        bottom.add(del);
        bottom.add(refresh);

        p.add(new JScrollPane(table), BorderLayout.CENTER);
        p.add(bottom, BorderLayout.SOUTH);

        return p;
    }

    private void loadCourses(DefaultTableModel model) {
        model.setRowCount(0);
        for (course c : courseDao.getAllCourses()) {
            model.addRow(new Object[]{
                    c.getCourseId(), c.getCode(), c.getTitle(), c.getCredits(),
                    c.getDepartment(), c.getAllowedPrograms(),
                    c.getMinSemester(), c.getMinCgpa()
            });
        }
    }

    private void showCourseForm(course c, DefaultTableModel model) {

        JTextField code = new JTextField();
        JTextField title = new JTextField();
        JTextField credits = new JTextField();
        JTextField dept = new JTextField();
        JTextField allowed = new JTextField();
        JTextField minSem = new JTextField();
        JTextField minCgpa = new JTextField();

        // Load all courses for prerequisites
        List<course> all = courseDao.getAllCourses();
        List<Integer> existing = (c == null)
                ? new ArrayList<>()
                : courseDao.getPrerequisites(c.getCourseId());

        JCheckBox[] boxes = new JCheckBox[all.size()];
        JPanel prePanel = new JPanel(new GridLayout(all.size(), 1));
        prePanel.setBorder(BorderFactory.createTitledBorder("Prerequisites (Optional)"));

        for (int i = 0; i < all.size(); i++) {
            course oc = all.get(i);
            boxes[i] = new JCheckBox(oc.getCode() + " - " + oc.getTitle());
            boxes[i].putClientProperty("course_id", oc.getCourseId());

            if (existing.contains(oc.getCourseId()))
                boxes[i].setSelected(true);

            prePanel.add(boxes[i]);
        }

        // Fill existing course
        if (c != null) {
            code.setText(c.getCode());
            title.setText(c.getTitle());
            credits.setText(String.valueOf(c.getCredits()));
            dept.setText(c.getDepartment());
            allowed.setText(c.getAllowedPrograms());
            minSem.setText(String.valueOf(c.getMinSemester()));
            minCgpa.setText(String.valueOf(c.getMinCgpa()));
        }

        Object[] form = {
                "Code:", code,
                "Title:", title,
                "Credits:", credits,
                "Department:", dept,
                "Allowed Programs:", allowed,
                "Min Semester:", minSem,
                "Min CGPA:", minCgpa,
                prePanel
        };

        int result = JOptionPane.showConfirmDialog(
                this, form,
                (c == null ? "Add Course" : "Edit Course"),
                JOptionPane.OK_CANCEL_OPTION
        );

        if (result != JOptionPane.OK_OPTION) return;

        course newC = new course(
                (c == null ? -1 : c.getCourseId()),
                code.getText().trim(),
                title.getText().trim(),
                Integer.parseInt(credits.getText().trim()),
                dept.getText().trim(),
                allowed.getText().trim(),
                Integer.parseInt(minSem.getText().trim()),
                Double.parseDouble(minCgpa.getText().trim())
        );

        if (c == null) {
            int newId = courseDao.addCourse(newC);

            if (newId != -1) {
                // save prerequisites
                for (JCheckBox b : boxes) {
                    if (b.isSelected()) {
                        int pid = (int) b.getClientProperty("course_id");
                        courseDao.addPrerequisite(newId, pid);
                    }
                }
                JOptionPane.showMessageDialog(this, "Course Added");
                loadCourses(model);
            }

        } else {
            // update
            if (courseDao.updateCourse(newC)) {

                // reset all prereqs
                courseDao.deleteAllPrerequisites(c.getCourseId());

                for (JCheckBox b : boxes) {
                    if (b.isSelected()) {
                        int pid = (int) b.getClientProperty("course_id");
                        courseDao.addPrerequisite(c.getCourseId(), pid);
                    }
                }
                JOptionPane.showMessageDialog(this, "Course Updated");
                loadCourses(model);
            }
        }
    }

    // section panel
    private JPanel buildSectionsPanel() {

        JPanel p = new JPanel(new BorderLayout());

        JLabel t = new JLabel("Manage Sections", SwingConstants.CENTER);
        t.setFont(new Font("Arial", Font.BOLD, 22));
        p.add(t, BorderLayout.NORTH);

        DefaultTableModel model = new DefaultTableModel(
                new String[]{
                        "Section ID", "Course ID", "Instructor ID",
                        "Day/Time", "Room", "Capacity", "Sem", "Year"
                }, 0);

        JTable table = new JTable(model);
        refreshSections(model);

        JPanel bottom = new JPanel();
        JButton add = new JButton("Add");
        JButton delete = new JButton("Delete");
        JButton refresh = new JButton("Refresh");

        bottom.add(add);
        bottom.add(delete);
        bottom.add(refresh);

        add.addActionListener(e -> showSectionForm(model));
        delete.addActionListener(e -> deleteSection(table, model));

        refresh.addActionListener(e -> refreshSections(model));

        p.add(new JScrollPane(table), BorderLayout.CENTER);
        p.add(bottom, BorderLayout.SOUTH);

        return p;
    }


    private void refreshSections(DefaultTableModel model) {
        model.setRowCount(0);
        for (section s : sectionDao.getAllSections()) {
            model.addRow(new Object[]{
                    s.getSectionId(), s.getCourseId(), s.getInstructorId(),
                    s.getDayTime(), s.getRoom(), s.getCapacity(),
                    s.getSemester(), s.getYear()
            });
        }
    }
    private void deleteSection(JTable table, DefaultTableModel model) {
    int row = table.getSelectedRow();
    if (row == -1) {
        JOptionPane.showMessageDialog(this, "Select a section first.");
        return;
    }

    int sectionId = (int) model.getValueAt(row, 0);

    // Check if students are enrolled are there because according me once student excited for subject admin should not remove (Nirvan policy)
    List<Integer> used = sectionDao.getEnrollmentIds(sectionId);
    if (!used.isEmpty()) {
        JOptionPane.showMessageDialog(
                this,
                "Cannot delete: Students are enrolled.\nEnrollments: " + used.size()
        );
        return;
    }
    int confirm = JOptionPane.showConfirmDialog(
            this,
            "Delete section " + sectionId + "?",
            "Confirm",
            JOptionPane.YES_NO_OPTION
    );
    if (confirm == JOptionPane.YES_OPTION) {
        boolean ok = sectionDao.deleteSection(sectionId);
        if (ok) {
            JOptionPane.showMessageDialog(this, "Section deleted.");
            refreshSections(model);
        } else {
            JOptionPane.showMessageDialog(this, "Delete failed.", "Error", JOptionPane.ERROR_MESSAGE);
        }}}


    private void showSectionForm(DefaultTableModel model) {

    JDialog d = new JDialog(this, "Add Section", true);
    d.setSize(400, 400);
    d.setLocationRelativeTo(this);
    d.setLayout(new GridLayout(0, 2, 10, 10));
    JComboBox<Integer> courseBox = new JComboBox<>();
    for (course c : courseDao.getAllCourses()) courseBox.addItem(c.getCourseId());
    JComboBox<Integer> instructorBox = new JComboBox<>();
    for (instructor i : instructorDao.getAllInstructors()) instructorBox.addItem(i.getInstructorId());
    JTextField dayTime = new JTextField();
    JTextField room = new JTextField();
    JTextField capacity = new JTextField();
    JTextField semester = new JTextField();
    JTextField year = new JTextField();
    d.add(new JLabel("Course ID:"));    d.add(courseBox); // maine internet se dekha hai error aa raha tha remove karne ke liye
    d.add(new JLabel("Instructor ID:")); d.add(instructorBox);
    d.add(new JLabel("Day/Time:"));      d.add(dayTime);
    d.add(new JLabel("Room:"));          d.add(room);
    d.add(new JLabel("Capacity:"));      d.add(capacity);
    d.add(new JLabel("Semester:"));      d.add(semester);
    d.add(new JLabel("Year:"));          d.add(year);
    JButton save = new JButton("Save");
    d.add(save);
    save.addActionListener(e -> {
        try {
            int cId = (Integer) courseBox.getSelectedItem();
            int iId = (Integer) instructorBox.getSelectedItem();

            boolean okay = sectionDao.createSection(
                    cId, iId,
                    dayTime.getText(),
                    room.getText(),
                    Integer.parseInt(capacity.getText()),
                    Integer.parseInt(semester.getText()),
                    Integer.parseInt(year.getText())
            );
            if (okay) {
                JOptionPane.showMessageDialog(d, "Section Created Successfully!");
                refreshSections(model);
                d.dispose();
            } else {
                JOptionPane.showMessageDialog(d, "Failed to Create Section!", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(d, "Invalid Input: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    });

    d.setVisible(true);}

// registration lock panel
    private JPanel buildRegLockPanel() {

        JPanel p = new JPanel(new BorderLayout());

        JLabel t = new JLabel("Registration Lock", SwingConstants.CENTER);
        t.setFont(new Font("Arial", Font.BOLD, 22));
        p.add(t, BorderLayout.NORTH);

        JCheckBox lockBox =
                new JCheckBox("Enable Registration Lock",
                        settingsDao.isRegistrationLocked());

        JButton save = new JButton("Save");
        save.addActionListener(e -> {

            boolean val = lockBox.isSelected();

            boolean ok = settingsDao.setRegistrationLocked(val);
            if (ok)
                JOptionPane.showMessageDialog(this, "Registration Lock Updated.");
            else
                JOptionPane.showMessageDialog(this, "Failed to Update!");
        });

        p.add(lockBox, BorderLayout.CENTER);
        p.add(save, BorderLayout.SOUTH);

        return p;
    }



   //maintenance panel
    private JPanel buildMaintenancePanel() {

        JPanel p = new JPanel(new BorderLayout());

        JLabel t = new JLabel("Maintenance Mode", SwingConstants.CENTER);
        t.setFont(new Font("Arial", Font.BOLD, 22));
        p.add(t, BorderLayout.NORTH);

        JCheckBox box =
                new JCheckBox("Enable Maintenance Mode",
                        settingsDao.isMaintenanceMode());

        JButton save = new JButton("Save");
        save.addActionListener(e -> {

            boolean ok = settingsDao.setMaintenanceMode(box.isSelected());
            if (ok)
                JOptionPane.showMessageDialog(this, "Maintenance Mode Updated.");
            else
                JOptionPane.showMessageDialog(this, "Failed!");
        });

        p.add(box, BorderLayout.CENTER);
        p.add(save, BorderLayout.SOUTH);

        return p;
    }
    // notification panel
    private JPanel buildNotificationPanel() {

    JPanel p = new JPanel(new BorderLayout());

    JLabel t = new JLabel("Notifications (Student & Instructor)", SwingConstants.CENTER);
    t.setFont(new Font("Arial", Font.BOLD, 22));
    p.add(t, BorderLayout.NORTH);

    // Retrieve existing notifications
    String stuText = settingsDao.getStudentAnnouncement();
    String insText = settingsDao.getInstructorAnnouncement();

    // Editable text areas
    JTextArea stuArea = new JTextArea(stuText);
    JTextArea insArea = new JTextArea(insText);

    stuArea.setBorder(BorderFactory.createTitledBorder("Student Notification"));
    insArea.setBorder(BorderFactory.createTitledBorder("Instructor Notification"));

    JPanel mid = new JPanel(new GridLayout(1, 2));
    mid.add(new JScrollPane(stuArea));
    mid.add(new JScrollPane(insArea));

    // Buttons panel
    JPanel buttons = new JPanel();

    JButton save = new JButton("Save");
    JButton clearStu = new JButton("Clear Student");
    JButton clearIns = new JButton("Clear Instructor");
    JButton clearAll = new JButton("Clear Both");

    // save
    save.addActionListener(e -> {
        boolean ok1 = settingsDao.setStudentAnnouncement(stuArea.getText());
        boolean ok2 = settingsDao.setInstructorAnnouncement(insArea.getText());
        if (ok1 && ok2)
            JOptionPane.showMessageDialog(this, "Notifications Updated SIR!");
        else
            JOptionPane.showMessageDialog(this, "Error updating notifications SIR!");
    });

    // clear student
    clearStu.addActionListener(e -> {
        stuArea.setText("");
        settingsDao.setStudentAnnouncement("");
        JOptionPane.showMessageDialog(this, "Student Notification Cleared SIR");
    });

    // clear instructor
    clearIns.addActionListener(e -> {
        insArea.setText("");
        settingsDao.setInstructorAnnouncement("");
        JOptionPane.showMessageDialog(this, "Instructor Notification Cleared SIR");
    });

    // clear both
    clearAll.addActionListener(e -> {
        stuArea.setText("");
        insArea.setText("");
        settingsDao.setStudentAnnouncement("");
        settingsDao.setInstructorAnnouncement("");
        JOptionPane.showMessageDialog(this, "Both Notifications Cleared SIR");
    });

    buttons.add(save);
    buttons.add(clearStu);
    buttons.add(clearIns);
    buttons.add(clearAll);

    p.add(mid, BorderLayout.CENTER);
    p.add(buttons, BorderLayout.SOUTH);

    return p;
}

}
