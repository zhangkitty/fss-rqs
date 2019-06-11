package com.znv.fssrqs.elasticsearch;

import lombok.Data;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component("elasticSearchClient")
@ConfigurationProperties(prefix = "es")
@Data
public class ElasticSearchClient 
{

    private  String username;

    private  String password;

    private  String host;

    private  Integer port;
    
    private RestHighLevelClient client = null;

    private RestClient restClient = null;
    
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

    	if(this.client == null)
    	{
    		synchronized (this) 
    		{
				if(this.client == null)
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
			        this.client = new RestHighLevelClient(restClientBuilder);
				}
			}
    	}
    }

}
