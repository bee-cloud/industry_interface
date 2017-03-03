package com.fxiaoke.dataplatform.crm.cleantool.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fxiaoke.dataplatform.crm.cleantool.utils.ElasticSearchHelper;
import com.fxiaoke.dataplatform.crm.cleantool.utils.OkhttpUtil;
import com.github.autoconf.ConfigFactory;
import com.github.autoconf.helper.ConfigHelper;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import jetbrick.util.StringUtils;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 处理es相关
 * Created by wzk on 16/4/5.
 */
@Service
public class ElasticsearchService {

    private static final Logger LOG = LoggerFactory.getLogger(ElasticsearchService.class);
    private static final String esMappingId = "ESID";

    @Autowired
    private SparkSubmitService sparkSubmitService;

    public void saveToEs(String sql, String index) {
        Preconditions.checkNotNull(sql, "sql should not be empty.");
        Preconditions.checkNotNull(index, "elasticsearch index should not be empty.");

        // delete index first
        //ElasticSearchHelper.deleteIndex(index);

        String profile = ConfigHelper.getProcessInfo().getProfile();
        List<String> args = Lists.newLinkedList();
        args.add(sql);
        args.add(index);
        args.add(profile);
        args.add(esMappingId);

        try {
            //String classpath = this.getClass().getResource("/").toString();
            //String jar = classpath + "/spark-sql-1.0.0-SNAPSHOT.jar";
            String sparkJar = ConfigFactory.getInstance().getConfig("mlplatform").get("sparkJar",
                    "hdfs:///AUX-LIB/mlplatform/spark-sql-1.0.1-SNAPSHOT.jar");
            sparkSubmitService.submit(sparkJar, "com.fxiaoke.spark.sql.java.SqlExec", args);
        } catch (Throwable e) {
            LOG.error("Cannot submit spark sql {} ", sql, e);
        }
    }

    public List<Map<String, Object>> sqlSearch(String sql) {
        List<Map<String, Object>> list = Lists.newLinkedList();
        try {
            Response response = searchBySql(sql);
            if (!response.isSuccessful()) {
                return list;
            }
            String ss = response.body().string();
            JSONObject object = JSON.parseObject(ss);
            JSONObject outterHits = object.getJSONObject("hits");
            JSONArray hits = outterHits.getJSONArray("hits");

            for (int i = 0; i < hits.size(); i++) {
                JSONObject hit = hits.getJSONObject(i);
                JSONObject source = hit.getJSONObject("_source");
                Map<String, Object> map = Maps.newLinkedHashMap();
                for (String key : source.keySet()) {
                    map.put(key, source.getString(key));
                }
                list.add(map);
            }
        } catch (Exception e) {
            LOG.error("Cannot search by sql from elasticsearch", e);
        }
        return list;
    }

    private Response searchBySql(String sql) throws IOException {
        String baseUrl = ElasticSearchHelper.getSqlUrl();
        String fullUrl = baseUrl + "?sql=" + StringUtils.trim(sql);

        Request request = new Request.Builder().url(fullUrl).build();
        return OkhttpUtil.execute(request);
    }

    /**
     * 必须同时满足keyword和时间范围
     *
     * @param topic     topic, 即es中的type
     * @param keyword   关键词
     * @param timeParam 使用的时间字段
     * @param timeFrom  起始时间戳
     * @param timeTo    截止时间戳
     * @param from      item起始
     * @param size      item条数
     * @return 结果的list
     */
    public List<Map<String, Object>> search(String index, String topic, String keyword, String timeParam, long timeFrom, long timeTo, int from, int size) {
        SearchRequestBuilder builder =
            build(index, topic, keyword, timeParam, timeFrom, timeTo, from, size);
        SearchResponse response = ElasticSearchHelper.search(builder);
        
        List<Map<String, Object>> results = Lists.newArrayList();
        if (response != null && response.getHits() != null) {
            SearchHit[] hits = response.getHits().getHits();
            for (SearchHit hit : hits) {
                results.add(hit.getSource());
            }
        }
        LOG.info("Got {} hits for keyword [{}] in topic [{}]", results.size(), keyword, topic);
        return results;
    }

    /**
     * 必须满足keyword
     *
     * @param topic     topic, 即es中的type
     * @param keyword   关键词
     * @param from      item起始
     * @param size      item条数
     * @return 结果的list
     */
    public List<Map<String, Object>> search(String index, String topic, String keyword, int from, int size) {
        SearchRequestBuilder builder = build(index, topic, keyword, from, size);
        SearchResponse response = ElasticSearchHelper.search(builder);
        List<Map<String, Object>> results = Lists.newArrayList();
        if (response != null && response.getHits() != null) {
            SearchHit[] hits = response.getHits().getHits();
            for (SearchHit hit : hits) {
                results.add(hit.getSource());
            }
        }
        LOG.info("Got {} hits for keyword [{}] in topic [{}]", results.size(), keyword, topic);
        return results;
    }

    /**
     * 必须同时满足keyword和时间范围, 默认返回10条
     *
     * @param topic     topic, 即es中的type
     * @param keyword   关键词
     * @param timeParam 使用的时间字段
     * @param timeFrom  起始时间戳
     * @param timeTo    截止时间戳
     */
    public List<Map<String, Object>> search(String index, String topic, String keyword, String timeParam, long timeFrom, long timeTo) {
        return search(index, topic, keyword, timeParam, timeFrom, timeTo, 0, 10);
    }

    /**
     * 必须同时满足keyword和时间范围
     *
     * @param topic     topic, 即es中的type
     * @param keyword   关键词
     * @param timeParam 使用的时间字段
     * @param timeFrom  起始时间戳
     * @param timeTo    截止时间戳
     * @param from      item起始
     * @param size      item条数
     * @return {@link SearchRequestBuilder}
     */
    private SearchRequestBuilder build(String index, String topic, String keyword, String timeParam, long timeFrom, long timeTo, int from, int size) {
        SearchRequestBuilder builder = ElasticSearchHelper.newBuilder(index);
        if (!Strings.isNullOrEmpty(topic)) {
            builder.setTypes(topic);
        }

        builder.addSort(timeParam, SortOrder.DESC);

        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        boolQuery.must(QueryBuilders.queryStringQuery(keyword));

        if (!Strings.isNullOrEmpty(timeParam) && timeFrom > 0 && timeTo >= timeFrom) {
            RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery(timeParam);
            rangeQuery.gte(timeFrom).lte(timeTo);
            boolQuery.must(rangeQuery);
        }

        builder.setFrom(from);
        builder.setSize(size);

        builder.setQuery(boolQuery);
        return builder;
    }


    /**
     * 必须满足keyword
     *
     * @param topic     topic, 即es中的type
     * @param keyword   关键词
     * @param from      item起始
     * @param size      item条数
     * @return {@link SearchRequestBuilder}
     */
    private SearchRequestBuilder build(String index, String topic, String keyword, int from, int size) {
        SearchRequestBuilder builder = ElasticSearchHelper.newBuilder(index);
        if (!Strings.isNullOrEmpty(topic)) {
            builder.setTypes(topic);
        }
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

        boolQuery.must(QueryBuilders.matchPhraseQuery("company_name", keyword)); //TODO 指定字段
//        boolQuery.must(QueryBuilders.queryStringQuery(keyword));
        builder.setFrom(from);
        builder.setSize(size);
        builder.setQuery(boolQuery);
        return builder;
    }

}
