package net.twomini.hybridcontent;

import com.google.common.cache.CacheBuilderSpec;
import com.yammer.dropwizard.client.HttpClientConfiguration;
import com.yammer.dropwizard.config.Configuration;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.twomini.hybridcontent.assets.AssetsConfiguration;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class ServiceConfiguration extends Configuration {

    @NotEmpty
    @JsonProperty
    private String serviceName;

    @NotEmpty
    @JsonProperty
    private String serviceBaseURL;

    @NotEmpty
    @JsonProperty
    private String authServiceToken;

    @NotEmpty
    @JsonProperty
    private String authServiceBaseUrl;

    @Valid
    @NotNull
    @JsonProperty
    private HttpClientConfiguration httpClient = new HttpClientConfiguration();

    @Valid
    @NotNull
    @JsonProperty
    private final AssetsConfiguration assets = new AssetsConfiguration();

    public AssetsConfiguration getAssetsConfiguration() {
        return assets;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getServiceBaseURL() {
        return serviceBaseURL;
    }

    public String getAuthServiceToken() {
        return authServiceToken;
    }

    public String getAuthServiceBaseUrl() {
        return authServiceBaseUrl;
    }

    public HttpClientConfiguration getHttpClient() {
        return httpClient;
    }

}