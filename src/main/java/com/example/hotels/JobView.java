package com.example.hotels;

public class JobView {
    public String job_code;
    public String name;


    public JobView (String job_code, String name){
        this.job_code = job_code;
        this.name = name;

    }
    public String getName() {return name;}
    public String getJob_code() {return job_code;}

    public void setJob_code(String job_code) { this.job_code = job_code; }
    public void setName(String name) { this.name = name; }
}
