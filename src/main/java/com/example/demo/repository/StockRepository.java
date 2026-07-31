package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Stock;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {

    // 資材名で検索
    Optional<Stock> findByItemName(String itemName);

    // 指定数量以下の在庫を取得
    List<Stock> findByQuantityLessThanEqual(Integer quantity);

}