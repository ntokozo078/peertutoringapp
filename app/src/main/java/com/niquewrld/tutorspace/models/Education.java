package com.niquewrld.tutorspace.models;

public class Education {
    private String degree;
    private String institution;
    private String yearRange;

    public Education() {

    }

    public Education(String degree, String institution, String yearRange) {
        this.degree = degree;
        this.institution = institution;
        this.yearRange = yearRange;
    }

    public String getDegree() {
        return degree;
    }

    public String getInstitution() {
        return institution;
    }

    public String getYearRange() {
        return yearRange;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }

    public void setInstitution(String institution) {
        this.institution = institution;
    }

    public void setYearRange(String yearRange) {
        this.yearRange = yearRange;
    }

}
