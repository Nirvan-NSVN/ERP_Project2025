package dao;

import database.ERPDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class statsDAO {
    public List<Double> getWeightedScores(int sectionId) { // yeh maine list mein store kar liye aur sab methods ko use karne ke liye
        List<Double> scores = new ArrayList<>();

        String sql =
            "SELECT e.enrollment_id, " +
            "SUM( (m.marks_obtained / NULLIF(cc.max_marks,0)) * cc.weightage ) AS total " +
            "FROM student_component_marks m " +
            "JOIN enrollments e ON m.enrollment_id = e.enrollment_id " +
            "JOIN course_components cc ON m.component_id = cc.component_id " +
            "WHERE e.section_id = ? " +
            "GROUP BY e.enrollment_id";
        try(Connection c = ERPDB.getERPConnection();
             PreparedStatement ps = c.prepareStatement(sql)){

            ps.setInt(1, sectionId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) scores.add(rs.getDouble("total"));

        } catch (Exception e) { e.printStackTrace(); }

        return scores;
    }

    public List<Double> getComponentScores(int sectionId, int componentId) {
        List<Double> scores = new ArrayList<>();
        String sql =
            "SELECT m.marks_obtained " +
            "FROM student_component_marks m " +
            "JOIN enrollments e ON m.enrollment_id = e.enrollment_id " +
            "WHERE e.section_id = ? AND m.component_id = ?";

        try(Connection c =ERPDB.getERPConnection();
             PreparedStatement ps = c.prepareStatement(sql)){
            ps.setInt(1, sectionId);
            ps.setInt(2, componentId);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) scores.add(rs.getDouble("marks_obtained"));

        } catch(Exception e){e.printStackTrace();}
        return scores;
    }

    //  MEAN bhaiya direcyt formula hai kya batao
    public double getMean(List<Double> list) {
        if (list.isEmpty()) return 0;
        double sum = 0;
        for (double d : list) sum += d;
        return sum / list.size();
    }

    public double getMedian(List<Double> list){
        if (list.isEmpty()) return 0;
        Collections.sort(list);
        int n = list.size();
        if (n % 2 == 0)
            return (list.get(n/2 - 1) + list.get(n/2)) / 2.0;
        return list.get(n/2);
    }

    public double getMin(List<Double> list){
        if (list.isEmpty()) return 0;
        return Collections.min(list);
    }


    public double getMax(List<Double> list){
        if (list.isEmpty()) return 0;
        return Collections.max(list);
    }

    public double getStdDev(List<Double> list) {
        if (list.isEmpty()) return 0;
        double m = getMean(list);
        double sum = 0;
        for (double d : list) sum += Math.pow(d - m, 2);
        return Math.sqrt(sum / list.size());
    }
}
