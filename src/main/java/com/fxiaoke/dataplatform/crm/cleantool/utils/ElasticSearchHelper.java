package com.fxiaoke.dataplatform.crm.cleantool.utils;

import com.github.autoconf.ConfigFactory;
import com.github.autoconf.api.IConfig;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * ElasticSearch Helper Created by wzk on 16/1/19.
 */
@Slf4j
public class ElasticSearchHelper {

  private static final Logger LOG = LoggerFactory.getLogger(ElasticSearchHelper.class);
  private static Client client = null;
  private static String sqlUrl;
  private static long timeout = 10000L;

  static {
    ConfigFactory.getInstance().getConfig("datapt-elasticsearch", ElasticSearchHelper::loadConfig);
  }

  private static void loadConfig(IConfig config) {
    String serverUrl = config.get("servers");
    Preconditions.checkNotNull(serverUrl, "Elasticsearch server url should not be empty!");
    String cluster = config.get("cluster.name");
    timeout = config.getLong("timeoutInMillis", 10000L);
    sqlUrl = config.get("sqlUrl");
    connectElasticSearch(cluster, serverUrl);
  }

  private static void connectElasticSearch(String cluster, String serverUrl) {
    Settings settings = Settings.builder().put("cluster.name", cluster).build();
    TransportClient c = new PreBuiltTransportClient(settings);
    log.info("{}.serverUrl={}", cluster, serverUrl);
    for (String i : Splitter.on(',').trimResults().omitEmptyStrings().split(serverUrl)) {
      int pos = i.indexOf(':');
      String ip = i;
      int port = 9300;
      if (pos > 0) {
        ip = i.substring(0, pos);
        port = Integer.parseInt(i.substring(pos + 1));
      }
      try {
        c.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(ip), port));
      } catch (UnknownHostException e) {
        log.error("Cannot parse address:{}", ip, e);
      }
    }
    if (client != null) {
      client.close();
    }
    client = c;
  }

  public static void bulk(String index, String type, Set<String> items) {
    Preconditions.checkNotNull(index, "Elasticsearch index should not be empty");
    Preconditions.checkNotNull(type, "Elasticsearch type should not be empty");
    if (items == null) {
      return;
    }
    BulkRequestBuilder bulkRequest = client.prepareBulk();
    for (String i : items) {
      bulkRequest.add(client.prepareIndex(index, type).setSource(i));
    }
    if (bulkRequest.numberOfActions() > 0) {
      BulkResponse bulkResponse = bulkRequest.execute().actionGet();
      if (bulkResponse.hasFailures()) {
        LOG.error("Cannot execute bulkIndex, reason: {}, items: {}",
                  bulkResponse.buildFailureMessage(), items);
      } else {
        LOG.info("Saved {} items to ElasticSearch in {}", items.size(), index);
      }
    }
  }

  public static void deleteIndex(String index) {
    Preconditions.checkNotNull(index, "Index should not be empty.");
    try {
      client.admin()
          .indices()
          .prepareDelete(index)
          .execute()
          .actionGet();
    } catch (Exception e) {
      LOG.error("Cannot delete index {}", index, e);
    }
  }

  public static SearchRequestBuilder newBuilder(String index) {
    return client.prepareSearch(index);
  }

  public static SearchResponse search(SearchRequestBuilder builder) {
    return builder == null ? null : builder.execute().actionGet(timeout, TimeUnit.MILLISECONDS);
  }

  public static String getSqlUrl() {
    return sqlUrl;
  }

}
