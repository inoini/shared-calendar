package com.example.demo.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;


@Entity
public class Crop {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    // 作物名
    private String cropName;


    // 品種
    private String variety;


    // 植付日
    private String plantingDate;


    // 収穫予定日
    private String harvestDate;


    // 圃場
    private String fieldName;


    // 状態
    private String status;


    // メモ
    private String memo;



    public Long getId() {
        return id;
    }


    public void setId(Long id) {
        this.id = id;
    }



    public String getCropName() {
        return cropName;
    }


    public void setCropName(String cropName) {
        this.cropName = cropName;
    }



    public String getVariety() {
        return variety;
    }


    public void setVariety(String variety) {
        this.variety = variety;
    }



    public String getPlantingDate() {
        return plantingDate;
    }


    public void setPlantingDate(String plantingDate) {
        this.plantingDate = plantingDate;
    }



    public String getHarvestDate() {
        return harvestDate;
    }


    public void setHarvestDate(String harvestDate) {
        this.harvestDate = harvestDate;
    }



    public String getFieldName() {
        return fieldName;
    }


    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }



    public String getStatus() {
        return status;
    }


    public void setStatus(String status) {
        this.status = status;
    }



    public String getMemo() {
        return memo;
    }


    public void setMemo(String memo) {
        this.memo = memo;
    }

}