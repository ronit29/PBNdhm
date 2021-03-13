package com.pb.dp.model;

import java.util.List;

public class Subscription
{
    private Purpose purpose;

    private Patient patient;

    private Hiu hiu;

    private List<String> categories;

    private Period period;

    public void setPurpose(Purpose purpose){
        this.purpose = purpose;
    }
    public Purpose getPurpose(){
        return this.purpose;
    }
    public void setPatient(Patient patient){
        this.patient = patient;
    }
    public Patient getPatient(){
        return this.patient;
    }
    public void setHiu(Hiu hiu){
        this.hiu = hiu;
    }
    public Hiu getHiu(){
        return this.hiu;
    }
    public void setCategories(List<String> categories){
        this.categories = categories;
    }
    public List<String> getCategories(){
        return this.categories;
    }
    public void setPeriod(Period period){
        this.period = period;
    }
    public Period getPeriod(){
        return this.period;
    }
}