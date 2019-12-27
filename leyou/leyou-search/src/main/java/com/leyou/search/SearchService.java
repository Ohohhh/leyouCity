package com.leyou.search;

import com.fasterxml.jackson.core.type.TypeReference;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.leyou.client.BrandClient;
import com.leyou.client.CategoryClient;
import com.leyou.client.GoodsClient;
import com.leyou.client.SpeClient;
import com.leyou.item.pojo.*;
import com.leyou.pojo.Goods;
import com.leyou.pojo.SearchRequest;
import com.leyou.pojo.SearchResult;
import com.leyou.repository.GoodsRepository;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.LongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.UnmappedTerms;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SearchService {

    @Autowired
    private CategoryClient categoryClient;
    @Autowired
    private BrandClient brandClient;
    @Autowired
    private GoodsClient goodsClient;
    @Autowired
    private SpeClient speClient;
    @Autowired
    private GoodsRepository goodsRepository;
    // 序列化工具
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public Goods buildGoods(Spu spu) throws IOException {

        // 创建goods对象
        Goods goods = new Goods();

        // 查询品牌
        Brand brand = this.brandClient.queryBrandNameById(spu.getBrandId());

        // 查询分类名称
        List<String> names = this.categoryClient.queryNamesByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));

        // 查询spu下的所有sku
        List<Sku> skus = this.goodsClient.querySkuBySpuId(spu.getId());
        List<Long> prices = new ArrayList<>();
        // 存放sku的必要字段
        List<Map<String, Object>> skuMapList = new ArrayList<>();
        // 遍历skus，获取价格集合
        skus.forEach(sku -> {
            prices.add(sku.getPrice());
            Map<String, Object> skuMap = new HashMap<>();
            skuMap.put("id", sku.getId());
            skuMap.put("title", sku.getTitle());
            skuMap.put("price", sku.getPrice());
            // 从sku数据库中拿多张图片中的第一张
            skuMap.put("image", StringUtils.isBlank(sku.getImages()) ? "" : StringUtils.split(sku.getImages(), ",")[0]);
            skuMapList.add(skuMap);
        });

        // 查询出所有的搜索规格参数
        List<SpecParam> params = this.speClient.queryParamByGid(null, spu.getCid3(), null, true);
        // 通过cid3 查询spuDetail。获取规格参数值
        SpuDetail spuDetail = this.goodsClient.querySpuDetailBySpuId(spu.getId());
        // 获取通用的规格参数  并进行反序列化    {"1":"其它","2":"G9青春版（全网通版）","3":2016.0,"5":143,"6":"其它",...}
        Map<String, Object> genericSpecMap = MAPPER.readValue(spuDetail.getGeneric_spec(), new TypeReference<Map<String, Object>>() {
        });
        // 获取特殊的规格参数    并进行反序列化  {"4":["白色","金色","玫瑰金"],"12":["3GB"],"13":["16GB"]}
        Map<String, List<Object>> specialSpecMap = MAPPER.readValue(spuDetail.getSpecial_spec(), new TypeReference<Map<String, List<Object>>>() {
        });
        // 定义map接收{规格参数名，规格参数值}
        Map<String, Object> paramMap = new HashMap<>();
        params.forEach(param -> {
            // 判断是否通用规格参数
            if (param.getGeneric()) {
                // 获取通用规格参数值
                String value = genericSpecMap.get(param.getId().toString()).toString();
                // 判断是否是数值类型
                if (param.getNumeric()) {
                    // 如果是数值的话，判断该数值落在那个区间
                    value = chooseSegment(value, param);
                }
                // 把参数名和值放入结果集中
                paramMap.put(param.getName(), value);
            } else {
                List<Object> value = specialSpecMap.get(param.getId().toString());
                paramMap.put(param.getName(), value);
            }
        });

        // 设置参数
        goods.setId(spu.getId());
        goods.setCid1(spu.getCid1());
        goods.setCid2(spu.getCid2());
        goods.setCid3(spu.getCid3());
        goods.setBrandId(spu.getBrandId());
        goods.setCreateTime(spu.getCreateTime());
        goods.setSubTitle(spu.getSubTitle());
        // 拼接all字段
        goods.setAll(spu.getTitle() + "  " + StringUtils.join(names, " ") + "  " + brand.getName());
        // 获取spu下所有sku价格
        goods.setPrice(prices);
        // 获取所有sku的json字符串
        goods.setSkus(MAPPER.writeValueAsString(skuMapList));
        // 获取所有参数
        goods.setSpecs(paramMap);

        return goods;
    }

    private String chooseSegment(String value, SpecParam p) {
        double val = NumberUtils.toDouble(value);
        String result = "其它";
        // 保存数值段
        for (String segment : p.getSegments().split(",")) {
            String[] segs = segment.split("-");
            // 获取数值范围
            double begin = NumberUtils.toDouble(segs[0]);
            double end = Double.MAX_VALUE;
            if (segs.length == 2) {
                end = NumberUtils.toDouble(segs[1]);
            }
            // 判断是否在范围内
            if (val >= begin && val < end) {
                if (segs.length == 1) {
                    result = segs[0] + p.getUnit() + "以上";
                } else if (begin == 0) {
                    result = segs[1] + p.getUnit() + "以下";
                } else {
                    result = segment + p.getUnit();
                }
                break;
            }
        }
        return result;
    }

    public SearchResult search(SearchRequest request) {
        String key = request.getKey();
        // 判断是否有搜索条件，如果没有，直接返回null。不允许搜索全部商品
        if (StringUtils.isBlank(key)) {
            return null;
        }

        // 构建查询条件
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();

        // 1、对key进行全文检索查询 匹配查询  .operator   and   or   分词过后确定是与还是或的关系
//        QueryBuilder basicQuery = QueryBuilders.matchQuery("all", key).operator(Operator.AND);
        // 改为 bool查询  通过选中的规格参数查询
        BoolQueryBuilder boolQueryBuilder = builBoolQueryBuilders(request);
        queryBuilder.withQuery(boolQueryBuilder);

        // 2、通过sourceFilter设置返回的结果字段,前面为需要显示的字段 id、skus、subTitle   后面为不显示的字段
        queryBuilder.withSourceFilter(new FetchSourceFilter(
                new String[]{"id", "skus", "subTitle"}, null));

        // 3、分页  -1 是因为默认是从1 开始
        // 准备分页参数
        int page = request.getPage();
        int size = request.getSize();
        queryBuilder.withPageable(PageRequest.of(page - 1, size));
        // 4. 排序
        String sortBy = request.getSortBy();
        Boolean descending = request.getDescending();
        if (StringUtils.isNotBlank(sortBy)) {
            // 不为空  进行排序
            queryBuilder.withSort(SortBuilders.fieldSort(sortBy).order(descending ? SortOrder.DESC : SortOrder.ASC));
        }
        // 添加分类和品牌的聚合 按词条聚合
        // 把聚合名称 抽取出来
        String categoryAggName = "categories";
        String brandAggName = "brands";
        queryBuilder.addAggregation(AggregationBuilders.terms(categoryAggName).field("cid3"));
        queryBuilder.addAggregation(AggregationBuilders.terms(brandAggName).field("brandId"));

        // 查询，获取结果
        AggregatedPage<Goods> pageInfo = (AggregatedPage<Goods>) this.goodsRepository.search(queryBuilder.build());

        // 获取相应的聚合结果并解析成相应的类型
        List<Map<String, Object>> categories = getCategoryAggResult(pageInfo.getAggregation(categoryAggName));
        List<Brand> brands = getBrandAggResult(pageInfo.getAggregation(brandAggName));
        //  分类只有一个的时候才参数聚合   因为不同分类的商品，其规格是不同的。
        List<Map<String, Object>> spec = null;
        if (categories.size() == 1) {
            spec = getSpecAggResult((Long) categories.get(0).get("id"), boolQueryBuilder);
        }

        // 封装结果并返回
        return new SearchResult(pageInfo.getTotalElements(), pageInfo.getTotalPages(), pageInfo.getContent(), categories, brands, spec);
    }

    /**
     * 构建bool查询
     * @param request
     */
    private BoolQueryBuilder builBoolQueryBuilders(SearchRequest request) {
        /*
        GET /heima/_search
            {
             "query":{
                "bool":{
        	        "must":{ "match": { "all": "",operator:"and"}},
        	        "filter":{
                        "iter":{}
         */
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(QueryBuilders.matchQuery("all",request.getKey()).operator(Operator.AND));
        // 添加过滤条件  过滤参数为map 遍历map集合
        for (Map.Entry<String, Object> entry : request.getFilter().entrySet()) {
            String key = entry.getKey();
            // 判断
            if (StringUtils.equals(key,"品牌")){
                key = "brandId";
            }else if(StringUtils.equals(key,"分类")){
                key = "cid3";
            }else {
                key = "specs."+ key + ".keyword";
            }
            boolQueryBuilder.filter(QueryBuilders.termQuery(key,entry.getValue()));
        }
        return boolQueryBuilder;
    }

    /**
     * 根据查询条件聚合规格参数
     *
     * @param cid
     * @param basicQuery
     * @return
     */
    private List<Map<String, Object>> getSpecAggResult(Long cid, QueryBuilder basicQuery) {
        // 构建本地查询器构建器
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        // 添加查询条件
        queryBuilder.withQuery(basicQuery);
        // 根据分类id查询要聚合的规格参数
        List<SpecParam> params = this.speClient.queryParamByGid(null, cid, null, true);
        // 添加聚合 遍历每种规格参数
        params.forEach(param -> {
            queryBuilder.addAggregation(AggregationBuilders.terms(param.getName()).field("specs." + param.getName() + ".keyword"));
        });
        // 添加结果集过滤 不需要查询结果集
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{}, null));
        // 执行聚合查询
        AggregatedPage<Goods> goodsPage = (AggregatedPage<Goods>) this.goodsRepository.search(queryBuilder.build());
        // 定义收集聚合的结果集
        List<Map<String, Object>> specMapList = new ArrayList<>();
        // 解析聚合查询的结果集
        Map<String, Aggregation> aggregationMap = goodsPage.getAggregations().asMap();
        // 遍历aggregationMap
        for (Map.Entry<String, Aggregation> entry : aggregationMap.entrySet()) {
            // 存放  String：规格参数的名字    Object：options
            Map<String, Object> map = new HashMap<>();
            map.put("k", entry.getKey());
            // 存放收集规格参数值
            List<Object> options = new ArrayList<>();
            // 获取聚合  需要转化
            StringTerms terms = (StringTerms)entry.getValue();
            // 获取桶
            terms.getBuckets().forEach(bucket -> {
                // 把桶中key放入规格参数集合
                options.add(bucket.getKeyAsString());
            });
            map.put("options", options);
            specMapList.add(map);
        }
        return specMapList;
    }

    /**
     * 解析品牌的聚合结果集
     *
     * @param aggregation
     * @return
     */
    private List<Brand> getBrandAggResult(Aggregation aggregation) {
        // 获取桶（需要先转换）  拿到key  就是brandId  在通过brandId 查找brand  存放到List<Brand>里
        LongTerms terms = (LongTerms) aggregation;
        return terms.getBuckets().stream().map(bucket -> {
            return this.brandClient.queryBrandNameById(bucket.getKeyAsNumber().longValue());
        }).collect(Collectors.toList());
    }

    /**
     * 解析分类的聚合结果集
     *
     * @param aggregation
     * @return
     */
    private List<Map<String, Object>> getCategoryAggResult(Aggregation aggregation) {
        // 获取桶（先强转聚合）里的分类id  查找到分类name 存放到 List<Map<String, Object>> 去
        LongTerms terms = (LongTerms) aggregation;
        return terms.getBuckets().stream().map(bucket -> {
            // 处理桶中的key转化为Map<String, Object>
            HashMap<String, Object> map = new HashMap<>();
            // 获取分类id
            Long id = bucket.getKeyAsNumber().longValue();
            // 通过分类id查找分类名字
            List<String> names = this.categoryClient.queryNamesByIds(Arrays.asList(id));
            map.put("id", id);
            map.put("name", names.get(0));
            return map;
        }).collect(Collectors.toList());
    }

    /**
     * 消费方监听到消息队列新增和删除后的处理
     * @param id
     */
    public void save(Long id) throws IOException {
        Spu spu = goodsClient.querySpuById(id);
        Goods goods = buildGoods(spu);
        goodsRepository.save(goods);
    }

    public void delete(Long id) {
        goodsRepository.deleteById(id);
    }
}
