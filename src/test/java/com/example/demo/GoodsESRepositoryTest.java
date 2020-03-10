package com.example.demo;

import com.alibaba.fastjson.JSON;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @Author: pengbenlei
 * @Date: 2020/3/9 15:33
 * @Description:
 */
@SpringBootTest
public class GoodsESRepositoryTest {

    @Autowired
    private GoodsRepository goodsRepository;

    @Test
    public void addDocument() {
        Goods goods = new Goods(1L, "大米1S", "手机",
                "大米", 3499.00, "https://img13.360buyimg.com/n1/s450x450_jfs/t1/79993/29/9874/153231/5d7809f4E8f387bff/1dc9e1b6b262f0fb.jpg");
        Goods goods0 = goodsRepository.save(goods);
        System.out.println(JSON.toJSONString(goods0));
    }
}
