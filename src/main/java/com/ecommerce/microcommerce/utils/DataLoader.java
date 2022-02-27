package com.ecommerce.microcommerce.utils;

import com.ecommerce.microcommerce.model.Product;
import com.ecommerce.microcommerce.web.dao.ProductDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DataLoader {
    private ProductDao productDao;

    @Autowired
    public DataLoader(ProductDao productDao) {
        this.productDao = productDao;
        InitDatabase();
    }

    private void InitDatabase() {
        productDao.save(new Product("Ordinateur Portable", 350,120));
        productDao.save(new Product("Aspirateur Robot", 500,200));
        productDao.save(new Product("Table de Ping Pong", 750,400));
    }
}
