package com.liferay.demo.hh.model;

public class Vacancy {
  private String id;
  private String name;
  private String publishedAt="";
  private String departmentName="";
  private String salary="з/п не указана";

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {

    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getPublishedAt() {
    return publishedAt;
  }

  public void setPublishedAt(String publishedAt) {
    this.publishedAt = publishedAt;
  }

  public String getDepartmentName() {
    return departmentName;
  }

  public void setDepartmentName(String departmentName) {
    this.departmentName = departmentName;
  }

  public String getSalary() {
    return salary;
  }

  public void setSalary(String salary) {
    this.salary = salary;
  }
}
