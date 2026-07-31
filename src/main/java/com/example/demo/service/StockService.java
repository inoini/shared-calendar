package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.Stock;
import com.example.demo.repository.StockRepository;

@Service
public class StockService {

    @Autowired
    private StockRepository stockRepository;

    // ==========================
    // 一覧取得
    // ==========================
    public List<Stock> findAll() {
        return stockRepository.findAll();
    }

    // ==========================
    // 1件取得
    // ==========================
    public Stock findById(Long id) {
        return stockRepository.findById(id).orElse(null);
    }

    // ==========================
    // 保存
    // ==========================
    public void save(Stock stock) {
        stockRepository.save(stock);
    }

    // ==========================
    // 削除
    // ==========================
    public void delete(Long id) {
        stockRepository.deleteById(id);
    }

    // ==========================
    // 資材名検索
    // ==========================
    public Stock findByItemName(String itemName) {

        Optional<Stock> stock =
                stockRepository.findByItemName(itemName);

        return stock.orElse(null);
    }

    // ==========================
    // 在庫を減らす
    // ==========================
    public void useStock(String itemName, int amount) {

        Stock stock = findByItemName(itemName);

        if (stock == null) {
            return;
        }

        int remain = stock.getQuantity() - amount;

        if (remain < 0) {
            remain = 0;
        }

        stock.setQuantity(remain);

        stockRepository.save(stock);
    }

    // ==========================
    // 不足資材
    // ==========================
    public List<Stock> getLowStocks() {

        return stockRepository.findAll()
                .stream()
                .filter(stock ->
                        stock.getQuantity()
                        <= stock.getMinimumStock())
                .toList();
    }

    // ==========================
    // 余剰資材
    // ==========================
    public List<Stock> getOverStocks() {

        return stockRepository.findAll()
                .stream()
                .filter(stock ->
                        stock.getQuantity()
                        >= stock.getMinimumStock() * 3)
                .toList();
    }

}