package com.znv.fssrqs.elasticsearch;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.nio.entity.NStringEntity;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.*;
import org.elasticsearch.client.sniff.Sniffer;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

@Component("elasticSearchClient")
@ConfigurationProperties(prefix = "es")
@Data
@Slf4j
public class ElasticSearchClient {
    //自定义缓存大小
    private static final int DEFAULT_BUFFER_LIMIT_BYTES = 30 * 1024 * 1024;
    private String username;
    private String password;
    private String host;
    private Integer port;
    private String clusterName;
    //private RestHighLevelClient client;
    private RestClient restClient;
    private TransportClient client;
    private Sniffer sniffer;

    public ElasticSearchClient getInstance() {
        try {
            createInstance();
        } catch (Exception e) {
            throw e;
        }
        return this;
    }

    private void createInstance() {
        if (restClient == null) {
            synchronized (this) {
                if (restClient == null) {
                    final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
                    credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, password));
                    RestClientBuilder restClientBuilder = RestClient.builder(new HttpHost(host, port))
                            .setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
                                @Override
                                public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
                                    return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
                                }
                            });
                    restClient = restClientBuilder.build();
                    sniffer = Sniffer.builder(restClient).build();
                    //this.client = new RestHighLevelClient(restClientBuilder);
                    try {
                        Settings settings = Settings.builder().put("cluster.name", clusterName).build();
                        this.client = new PreBuiltTransportClient(settings).addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(host), 9300));
                    } catch (UnknownHostException e) {
                        log.error("create EsRestClient failed:", e);
                        System.exit(1);
                    }
                }
            }
        }
    }


    public Response getRequest(String url) throws IOException {
        Map<String, String> params = Collections.emptyMap();
        if (!url.contains("pretty")) {
            params = Collections.singletonMap("pretty", "true");
        }
        return restClient.performRequest(HttpMethod.GET.name(), url, params);
    }

    public JSONObject getRequest(String url, Map<String, String> params) throws IOException {
        Response response = restClient.performRequest(HttpMethod.GET.name(), url, params);
        try {
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("Code", statusCode);
                return jsonObject;
            }
            log.info("ip=" + response.getHost().getHostName() + ",port=" + response.getHost().getPort());
            return JSONObject.parseObject(EntityUtils.toString(response.getEntity()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public JSONObject postRequest(String url, Map<String, String> params, JSONObject body) {
        HttpEntity entity = new NStringEntity(body.toJSONString(), ContentType.APPLICATION_JSON);
        try {
            Response response = restClient.performRequest(HttpMethod.POST.name(), url, params, entity);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("Code", statusCode);
                return jsonObject;
            }
            log.info("ip=" + response.getHost().getHostName() + ",port=" + response.getHost().getPort());
            return JSONObject.parseObject(EntityUtils.toString(response.getEntity()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public JSONObject postRequest(String url, JSONObject body) {
        Map<String, String> params = Collections.emptyMap();
        return postRequest(url, params, body);
    }

    public JSONObject putRequest(String url, JSONObject body) throws IOException {
        Map<String, String> params = Collections.emptyMap();
        return putRequest(url, params, body);
    }

    public JSONObject putRequest(String url, Map<String, String> params, JSONObject body) throws IOException {
        HttpEntity entity = new NStringEntity(body.toJSONString(), ContentType.APPLICATION_JSON);
        try {
            Response response = restClient.performRequest(HttpMethod.PUT.name(), url, params, entity);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("Code", statusCode);
                return jsonObject;
            }
            log.info("ip=" + response.getHost().getHostName() + ",port=" + response.getHost().getPort());
            return JSONObject.parseObject(EntityUtils.toString(response.getEntity()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public JSONObject getRequest4AsyncResponse(String url, Map<String, String> params) throws IOException {
        HttpAsyncResponseConsumerFactory.HeapBufferedResponseConsumerFactory consumerFactory = new HttpAsyncResponseConsumerFactory.HeapBufferedResponseConsumerFactory(DEFAULT_BUFFER_LIMIT_BYTES);
        try {
            Response response = restClient.performRequest(HttpMethod.GET.name(), url, params, null, consumerFactory);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("Code", statusCode);
                return jsonObject;
            }
            log.info("ip=" + response.getHost().getHostName() + ",port=" + response.getHost().getPort());
            return JSONObject.parseObject(EntityUtils.toString(response.getEntity()));
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public JSONObject getRequest4AsyncResponse(String url) throws IOException {
        Map<String, String> params = Collections.emptyMap();
        return getRequest4AsyncResponse(url, params);
    }

    /**
     * 批量异步提交POST请求
     *
     * @param httpMethod
     * @param indexAndTypeUrl
     * @param httpEntities
     * @throws InterruptedException
     */
    public void batchAsyncRequest(HttpMethod httpMethod, String indexAndTypeUrl, List<HttpEntity> httpEntities) throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(httpEntities.size());
        for (int i = 0; i < httpEntities.size(); i++) {
            restClient.performRequestAsync(httpMethod.name(), indexAndTypeUrl, Collections.<String, String>emptyMap(), httpEntities.get(i), new ResponseListener() {
                        @Override
                        public void onSuccess(Response response) {
                            latch.countDown();
                        }

                        @Override
                        public void onFailure(Exception exception) {
                            latch.countDown();
                        }
                    }
            );
        }
        latch.await();
    }
}
