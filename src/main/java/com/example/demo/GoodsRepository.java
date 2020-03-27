package com.example.demo;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author: pengbenlei
 * @Date: 2020/3/9 11:40
 * @Description:
 */
@Component
public interface GoodsRepository extends ElasticsearchRepository<Goods,Long> {

    /**
     * 根据价格区间查询
     * @param price1
     * @param price2
     * @return
     */
    List<Goods> findByPriceBetween(double price1, double price2);

    Goods findByTitle(String title);

    List<Goods> findByTitleContaining(String name);
}
