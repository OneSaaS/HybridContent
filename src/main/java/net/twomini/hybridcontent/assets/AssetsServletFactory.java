package net.twomini.hybridcontent.assets;

import com.google.common.cache.CacheBuilderSpec;
import com.yammer.dropwizard.auth.Authenticator;
import com.yammer.dropwizard.config.Environment;
import net.twomini.hybridcontent.auth.AuthCaller;
import net.twomini.hybridcontent.auth.Authorizer;
import net.twomini.hybridcontent.auth.HttpRequestDetails;

import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * An assets bundle (like {@link com.yammer.dropwizard.assets.AssetsBundle}) that utilizes configuration to provide the
 * ability to override how assets are loaded and cached.  Specifying an override is useful during the development phase
 * to allow assets to be loaded directly out of source directories instead of the classpath and to force them to not be
 * cached by the browser or the server.  This allows developers to edit an asset, save and then immediately refresh the
 * web browser and see the updated assets.  No compilation or copy steps are necessary.
 */
public class AssetsServletFactory {
    private static final String DEFAULT_PATH = "/assets";
    protected static final CacheBuilderSpec DEFAULT_CACHE_SPEC = CacheBuilderSpec.parse("maximumSize=100");

    private final String resourcePath;
    private final CacheBuilderSpec cacheBuilderSpec;
    private final String uriPath;

    /**
     * Creates a new {@link AssetsServletFactory} which serves up static assets from
     * {@code src/main/resources/assets/*} as {@code /assets/*}.
     *
     * @see AssetsServletFactory#AssetsServletFactory(String, CacheBuilderSpec)
     */
    public AssetsServletFactory() {
        this(DEFAULT_PATH, DEFAULT_CACHE_SPEC);
    }

    /**
     * Creates a new {@link AssetsServletFactory} which will configure the service to serve the static files
     * located in {@code src/main/resources/${path}} as {@code /${path}}. For example, given a
     * {@code path} of {@code "/assets"}, {@code src/main/resources/assets/example.js} would be
     * served up from {@code /assets/example.js}.
     *
     * @param path the classpath and URI root of the static asset files
     * @see AssetsServletFactory#AssetsServletFactory(String, CacheBuilderSpec)
     */
    public AssetsServletFactory(String path) {
        this(path, DEFAULT_CACHE_SPEC, path);
    }

    /**
     * Creates a new {@link AssetsServletFactory} which will configure the service to serve the static files
     * located in {@code src/main/resources/${resourcePath}} as {@code /${uriPath}}. For example, given a
     * {@code resourcePath} of {@code "/assets"} and a uriPath of {@code "/js"},
     * {@code src/main/resources/assets/example.js} would be served up from {@code /js/example.js}.
     *
     * @param resourcePath the resource path (in the classpath) of the static asset files
     * @param uriPath      the uri path for the static asset files
     * @see AssetsServletFactory#AssetsServletFactory(String, CacheBuilderSpec)
     */
    public AssetsServletFactory(String resourcePath, String uriPath) {
        this(resourcePath, DEFAULT_CACHE_SPEC, uriPath);
    }

    /**
     * Creates a new {@link AssetsServletFactory} which will configure the service to serve the static files
     * located in {@code src/main/resources/${path}} as {@code /${path}}. For example, given a
     * {@code path} of {@code "/assets"}, {@code src/main/resources/assets/example.js} would be
     * served up from {@code /assets/example.js}.
     *
     * @param resourcePath     the resource path (in the classpath) of the static asset files
     * @param cacheBuilderSpec the spec for the cache builder
     */
    public AssetsServletFactory(String resourcePath, CacheBuilderSpec cacheBuilderSpec) {
        this(resourcePath, cacheBuilderSpec, resourcePath);
    }

    /**
     * Creates a new {@link AssetsServletFactory} which will configure the service to serve the static files
     * located in {@code src/main/resources/${resourcePath}} as {@code /${uriPath}}. For example, given a
     * {@code resourcePath} of {@code "/assets"} and a uriPath of {@code "/js"},
     * {@code src/main/resources/assets/example.js} would be served up from {@code /js/example.js}.
     *
     * @param resourcePath     the resource path (in the classpath) of the static asset files
     * @param cacheBuilderSpec the spec for the cache builder
     * @param uriPath          the uri path for the static asset files
     */
    public AssetsServletFactory(String resourcePath, CacheBuilderSpec cacheBuilderSpec, String uriPath) {
        checkArgument(resourcePath.startsWith("/"), "%s is not an absolute path", resourcePath);
        checkArgument(!"/".equals(resourcePath), "%s is the classpath root", resourcePath);
        this.resourcePath = resourcePath.endsWith("/") ? resourcePath : (resourcePath + '/');
        this.uriPath = uriPath.endsWith("/") ? uriPath : (uriPath + '/');
        this.cacheBuilderSpec = cacheBuilderSpec;
    }

    public void addNewAssetServletToEnvironment(Environment environment, AssetsConfiguration config, Authenticator<HttpRequestDetails,AuthCaller> authenticator, Authorizer authorizer, String authServiceBaseUrl, String serviceBaseURL) {
        // Let the cache spec from the configuration override the one specified in the code
        CacheBuilderSpec spec = (config.getCacheSpec() != null)
                ? CacheBuilderSpec.parse(config.getCacheSpec())
                : cacheBuilderSpec;

        Iterable<Map.Entry<String, String>> overrides = config.getOverrides();

        environment.addServlet(new AssetServlet(resourcePath, spec, uriPath, overrides, authenticator, authorizer, authServiceBaseUrl, serviceBaseURL), uriPath + "*");
    }

}
