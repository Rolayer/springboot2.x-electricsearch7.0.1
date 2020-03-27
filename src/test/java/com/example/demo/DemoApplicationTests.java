package com.example.demo;

import com.alibaba.fastjson.JSON;
import org.elasticsearch.action.get.MultiGetRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.metrics.avg.InternalAvg;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.web.PageableDefault;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class DemoApplicationTests {
    @Autowired
    GoodsRepository goodsRepository;
    @Test
    void contextLoads() {
    }
//    @Autowired
//    private ElasticsearchTemplate elasticsearchTemplate;




    @Test
    public void addDocument() {
        Goods goods = new Goods(1L, "大米1S", "手机",
                "大米", 3499.00, "https://img13.360buyimg.com/n1/s450x450_jfs/t1/79993/29/9874/153231/5d7809f4E8f387bff/1dc9e1b6b262f0fb.jpg");
        Goods goods0 = goodsRepository.save(goods);
        System.out.println(JSON.toJSONString(goods0));
    }

    /**
     * 批量新增
     */
    @Test
    public void createDocumentList() {
        List<Goods> list = new ArrayList<>();
        list.add(new Goods(2L, "坚果手机R1", " 手机", "锤子", 3699.00, "http://image.leyou.com/123.jpg"));
        list.add(new Goods(3L, "华为META10", " 手机", "华为", 4499.00, "http://image.leyou.com/3.jpg"));
        // 接收对象集合，实现批量新增
        goodsRepository.saveAll(list);
    }

    @Test
    public void findDocument() {
        // 查询全部，并安装价格降序排序
        Iterable<Goods> goodsIterable = this.goodsRepository.findAll(Sort.by(Sort.Direction.DESC, "price"));
        goodsIterable.forEach(goods -> System.out.println(JSON.toJSONString(goods)));
    }

    @Test
    public void indexList() {
        List<Goods> list = new ArrayList<>();
        list.add(new Goods(1L, "小米手机7", "手机", "小米", 3299.00, "http://image.leyou.com/13123.jpg"));
        list.add(new Goods(2L, "坚果手机R1", "手机", "锤子", 3699.00, "http://image.leyou.com/13123.jpg"));
        list.add(new Goods(3L, "华为META10", "手机", "华为", 4499.00, "http://image.leyou.com/13123.jpg"));
        list.add(new Goods(4L, "小米Mix2S", "手机", "小米", 4299.00, "http://image.leyou.com/13123.jpg"));
        list.add(new Goods(5L, "荣耀V10", "手机", "华为", 2799.00, "http://image.leyou.com/13123.jpg"));
        // 接收对象集合，实现批量新增
        goodsRepository.saveAll(list);
    }

    @Test
    public void queryByPriceBetween(){
        List<Goods> list = this.goodsRepository.findByPriceBetween(0, 3500.00);
        for (Goods goods: list) {
            System.out.println(goods.getTitle());
        }
    }
    @Test
    public void testQuery(){
        MatchQueryBuilder queryBuilder = QueryBuilders.matchQuery("id", "1");
        Iterable<Goods> items =goodsRepository.search(queryBuilder);
        for (Goods item : items) {
            System.out.println(JSON.toJSONString(item));
        }
    }

    @Test
    void findByTitle()
    {
        Goods goods= goodsRepository.findByTitle("米");
        System.out.println(JSON.toJSONString(goods));
    }


    @Test
    void findByTitleLike()
    {
        List<Goods> goods= goodsRepository.findByTitleContaining("小");
        for (Goods item : goods) {
            System.out.println(JSON.toJSONString(item));
        }
    }
    @Test
    public void testNativeQuery(){
        // 构建查询条件
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        // 添加基本的分词查询
        queryBuilder.withQuery(QueryBuilders.termQuery("category", "手机"));

        // 初始化分页参数
        int page = 0;
        int size = 20;
        // 设置分页参数
        queryBuilder.withPageable(PageRequest.of(page, size));

        // 执行搜索，获取结果
        Page<Goods> items = goodsRepository.search(queryBuilder.build());
        // 打印总条数
        System.out.println(items.getTotalElements());
        // 打印总页数
        System.out.println(items.getTotalPages());
        // 每页大小
        System.out.println(items.getSize());
        // 当前页
        System.out.println(items.getNumber());
        for (Goods item : items) {
            System.out.println(JSON.toJSONString(item));
        }
    }
    @Test
    public void testSort(){
        // 构建查询条件
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        // 添加基本的分词查询
        queryBuilder.withQuery(QueryBuilders.termQuery("category", "手机"));

        // 排序
        queryBuilder.withSort(SortBuilders.fieldSort("id").order(SortOrder.ASC));

        // 执行搜索，获取结果
        Page<Goods> items = goodsRepository.search(queryBuilder.build());
        // 打印总条数
        System.out.println(items.getTotalElements());
        for (Goods item : items) {
            System.out.println(JSON.toJSONString(item));
        }
    }
    @Test
    /**
     * 按照品牌brand进行分组 统计各品牌的总数
     * */
    public void testAgg(){
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        // 不查询任何结果
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{""}, null));
        // 1、添加一个新的聚合，聚合类型为terms，聚合名称为brands，聚合字段为brand
        queryBuilder.addAggregation(
                AggregationBuilders.terms("brands").field("brand"));
        // 2、查询,需要把结果强转为AggregatedPage类型
        AggregatedPage<Goods> aggPage = (AggregatedPage<Goods>) goodsRepository.search(queryBuilder.build());
        // 3、解析
        // 3.1、从结果中取出名为brands的那个聚合，
        // 因为是利用String类型字段来进行的term聚合，所以结果要强转为StringTerm类型
        StringTerms agg = (StringTerms) aggPage.getAggregation("brands");
        // 3.2、获取桶
        List<StringTerms.Bucket> buckets = agg.getBuckets();
        // 3.3、遍历
        for (StringTerms.Bucket bucket : buckets) {
            // 3.4、获取桶中的key，即品牌名称
            System.out.println(bucket.getKeyAsString());
            // 3.5、获取桶中的文档数量
            System.out.println(bucket.getDocCount());
        }
    }
    @Test
    /**
     * 嵌套聚合，求平均值
     * */
    public void testSubAgg(){
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        // 不查询任何结果
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{""}, null));
        // 1、添加一个新的聚合，聚合类型为terms，聚合名称为brands，聚合字段为brand
        queryBuilder.addAggregation(
                AggregationBuilders.terms("brands").field("brand")
                        .subAggregation(AggregationBuilders.avg("priceAvg").field("price")) // 在品牌聚合桶内进行嵌套聚合，求平均值
        );
        // 2、查询,需要把结果强转为AggregatedPage类型
        AggregatedPage<Goods> aggPage = (AggregatedPage<Goods>) goodsRepository.search(queryBuilder.build());
        // 3、解析
        // 3.1、从结果中取出名为brands的那个聚合，
        // 因为是利用String类型字段来进行的term聚合，所以结果要强转为StringTerm类型
        StringTerms agg = (StringTerms) aggPage.getAggregation("brands");
        // 3.2、获取桶
        List<StringTerms.Bucket> buckets = agg.getBuckets();
        // 3.3、遍历
        for (StringTerms.Bucket bucket : buckets) {
            // 3.4、获取桶中的key，即品牌名称  3.5、获取桶中的文档数量
            System.out.println(bucket.getKeyAsString() + "，共" + bucket.getDocCount() + "台");

            // 3.6.获取子聚合结果：
            InternalAvg avg = (InternalAvg) bucket.getAggregations().asMap().get("priceAvg");
            System.out.println("平均售价：" + avg.getValue());
        }
    }

}
