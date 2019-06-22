package com.znv.fssrqs.elasticsearch;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
//import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Component("elasticSearchClient")
@ConfigurationProperties(prefix = "es")
@Data
@Slf4j
public class ElasticSearchClient 
{

    private  String username;

    private  String password;

    private  String host;

    private  Integer port;

    private String clusterName;
    
    //private RestHighLevelClient client = null;

    private RestClient restClient = null;

	private TransportClient client = null;
    
    public ElasticSearchClient getInstance()
    {
    	try 
    	{
    		createInstance();
		} catch (Exception e) 
    	{
			throw e;
		}
    	return this;
    }
    
    private void createInstance()
    {

    	if(this.restClient == null)
    	{
    		synchronized (this)
    		{
				if(this.restClient == null)
				{
			        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
			        credentialsProvider.setCredentials(AuthScope.ANY,
			                new UsernamePasswordCredentials(username, password));
			        RestClientBuilder restClientBuilder = RestClient.builder(new HttpHost(host, port))
			                .setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
			                    @Override
			                    public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
			                        return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
			                    }
			                });
			        this.restClient = restClientBuilder.build();
//			        this.client = new RestHighLevelClient(restClientBuilder);

					try {
						Settings settings = Settings.builder()
								.put("cluster.name", clusterName).build();
						this.client = new PreBuiltTransportClient(settings)
								.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(host), 9300));
					} catch (UnknownHostException e) {
						log.error("es创建client异常", e);
					}
				}
			}
    	}
    }

}
