package main;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class StudentApi {
    private int id;
    private String name;

    @JsonProperty("marks")
    private List<Integer> grades;

    public StudentApi() {}

    public StudentApi(String name) {
        this.name = name;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public List<Integer> getGrades() { return grades; }
    public void setGrades(List<Integer> grades) { this.grades = grades; }

    public void addGrade(int grade) {
        this.grades.add(grade);
    }
}
