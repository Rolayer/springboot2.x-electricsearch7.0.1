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
        Goods goods = new Goods(1L, "����1S", "�ֻ�",
                "����", 3499.00, "https://img13.360buyimg.com/n1/s450x450_jfs/t1/79993/29/9874/153231/5d7809f4E8f387bff/1dc9e1b6b262f0fb.jpg");
        Goods goods0 = goodsRepository.save(goods);
        System.out.println(JSON.toJSONString(goods0));
    }

    /**
     * ��������
     */
    @Test
    public void createDocumentList() {
        List<Goods> list = new ArrayList<>();
        list.add(new Goods(2L, "����ֻ�R1", " �ֻ�", "����", 3699.00, "http://image.leyou.com/123.jpg"));
        list.add(new Goods(3L, "��ΪMETA10", " �ֻ�", "��Ϊ", 4499.00, "http://image.leyou.com/3.jpg"));
        // ���ն��󼯺ϣ�ʵ����������
        goodsRepository.saveAll(list);
    }

    @Test
    public void findDocument() {
        // ��ѯȫ��������װ�۸�������
        Iterable<Goods> goodsIterable = this.goodsRepository.findAll(Sort.by(Sort.Direction.DESC, "price"));
        goodsIterable.forEach(goods -> System.out.println(JSON.toJSONString(goods)));
    }

    @Test
    public void indexList() {
        List<Goods> list = new ArrayList<>();
        list.add(new Goods(1L, "С���ֻ�7", "�ֻ�", "С��", 3299.00, "http://image.leyou.com/13123.jpg"));
        list.add(new Goods(2L, "����ֻ�R1", "�ֻ�", "����", 3699.00, "http://image.leyou.com/13123.jpg"));
        list.add(new Goods(3L, "��ΪMETA10", "�ֻ�", "��Ϊ", 4499.00, "http://image.leyou.com/13123.jpg"));
        list.add(new Goods(4L, "С��Mix2S", "�ֻ�", "С��", 4299.00, "http://image.leyou.com/13123.jpg"));
        list.add(new Goods(5L, "��ҫV10", "�ֻ�", "��Ϊ", 2799.00, "http://image.leyou.com/13123.jpg"));
        // ���ն��󼯺ϣ�ʵ����������
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
        Goods goods= goodsRepository.findByTitle("��");
        System.out.println(JSON.toJSONString(goods));
    }


    @Test
    void findByTitleLike()
    {
        List<Goods> goods= goodsRepository.findByTitleContaining("С");
        for (Goods item : goods) {
            System.out.println(JSON.toJSONString(item));
        }
    }
    @Test
    public void testNativeQuery(){
        // ������ѯ����
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        // ��ӻ����ķִʲ�ѯ
        queryBuilder.withQuery(QueryBuilders.termQuery("category", "�ֻ�"));

        // ��ʼ����ҳ����
        int page = 0;
        int size = 20;
        // ���÷�ҳ����
        queryBuilder.withPageable(PageRequest.of(page, size));

        // ִ����������ȡ���
        Page<Goods> items = goodsRepository.search(queryBuilder.build());
        // ��ӡ������
        System.out.println(items.getTotalElements());
        // ��ӡ��ҳ��
        System.out.println(items.getTotalPages());
        // ÿҳ��С
        System.out.println(items.getSize());
        // ��ǰҳ
        System.out.println(items.getNumber());
        for (Goods item : items) {
            System.out.println(JSON.toJSONString(item));
        }
    }
    @Test
    public void testSort(){
        // ������ѯ����
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        // ��ӻ����ķִʲ�ѯ
        queryBuilder.withQuery(QueryBuilders.termQuery("category", "�ֻ�"));

        // ����
        queryBuilder.withSort(SortBuilders.fieldSort("id").order(SortOrder.ASC));

        // ִ����������ȡ���
        Page<Goods> items = goodsRepository.search(queryBuilder.build());
        // ��ӡ������
        System.out.println(items.getTotalElements());
        for (Goods item : items) {
            System.out.println(JSON.toJSONString(item));
        }
    }
    @Test
    /**
     * ����Ʒ��brand���з��� ͳ�Ƹ�Ʒ�Ƶ�����
     * */
    public void testAgg(){
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        // ����ѯ�κν��
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{""}, null));
        // 1�����һ���µľۺϣ��ۺ�����Ϊterms���ۺ�����Ϊbrands���ۺ��ֶ�Ϊbrand
        queryBuilder.addAggregation(
                AggregationBuilders.terms("brands").field("brand"));
        // 2����ѯ,��Ҫ�ѽ��ǿתΪAggregatedPage����
        AggregatedPage<Goods> aggPage = (AggregatedPage<Goods>) goodsRepository.search(queryBuilder.build());
        // 3������
        // 3.1���ӽ����ȡ����Ϊbrands���Ǹ��ۺϣ�
        // ��Ϊ������String�����ֶ������е�term�ۺϣ����Խ��ҪǿתΪStringTerm����
        StringTerms agg = (StringTerms) aggPage.getAggregation("brands");
        // 3.2����ȡͰ
        List<StringTerms.Bucket> buckets = agg.getBuckets();
        // 3.3������
        for (StringTerms.Bucket bucket : buckets) {
            // 3.4����ȡͰ�е�key����Ʒ������
            System.out.println(bucket.getKeyAsString());
            // 3.5����ȡͰ�е��ĵ�����
            System.out.println(bucket.getDocCount());
        }
    }
    @Test
    /**
     * Ƕ�׾ۺϣ���ƽ��ֵ
     * */
    public void testSubAgg(){
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        // ����ѯ�κν��
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{""}, null));
        // 1�����һ���µľۺϣ��ۺ�����Ϊterms���ۺ�����Ϊbrands���ۺ��ֶ�Ϊbrand
        queryBuilder.addAggregation(
                AggregationBuilders.terms("brands").field("brand")
                        .subAggregation(AggregationBuilders.avg("priceAvg").field("price")) // ��Ʒ�ƾۺ�Ͱ�ڽ���Ƕ�׾ۺϣ���ƽ��ֵ
        );
        // 2����ѯ,��Ҫ�ѽ��ǿתΪAggregatedPage����
        AggregatedPage<Goods> aggPage = (AggregatedPage<Goods>) goodsRepository.search(queryBuilder.build());
        // 3������
        // 3.1���ӽ����ȡ����Ϊbrands���Ǹ��ۺϣ�
        // ��Ϊ������String�����ֶ������е�term�ۺϣ����Խ��ҪǿתΪStringTerm����
        StringTerms agg = (StringTerms) aggPage.getAggregation("brands");
        // 3.2����ȡͰ
        List<StringTerms.Bucket> buckets = agg.getBuckets();
        // 3.3������
        for (StringTerms.Bucket bucket : buckets) {
            // 3.4����ȡͰ�е�key����Ʒ������  3.5����ȡͰ�е��ĵ�����
            System.out.println(bucket.getKeyAsString() + "����" + bucket.getDocCount() + "̨");

            // 3.6.��ȡ�ӾۺϽ����
            InternalAvg avg = (InternalAvg) bucket.getAggregations().asMap().get("priceAvg");
            System.out.println("ƽ���ۼۣ�" + avg.getValue());
        }
    }

}
