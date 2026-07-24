
package com.example.demo.entity;


import jakarta.persistence.*;
import java.time.LocalDate;


@Entity
public class Sales {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private LocalDate date;


    private String crop;


    private Double amount;


    @Column(columnDefinition="TEXT")
    private String memo;



    public Long getId(){
        return id;
    }


    public LocalDate getDate(){
        return date;
    }


    public void setDate(LocalDate date){
        this.date=date;
    }


    public String getCrop(){
        return crop;
    }


    public void setCrop(String crop){
        this.crop=crop;
    }


    public Double getAmount(){
        return amount;
    }


    public void setAmount(Double amount){
        this.amount=amount;
    }


    public String getMemo(){
        return memo;
    }


    public void setMemo(String memo){
        this.memo=memo;
    }

}