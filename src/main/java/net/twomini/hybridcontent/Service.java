package net.twomini.hybridcontent;

import net.twomini.hybridcontent.assets.AssetsServletFactory;
import net.twomini.hybridcontent.assets.Ec2ElbHttpsRedirectFilter;
import net.twomini.hybridcontent.auth.*;
import net.twomini.hybridcontent.auth.authservice.AuthServiceProvider;
import net.twomini.hybridcontent.auth.authservice.AuthServiceAuthenticator;
import net.twomini.hybridcontent.auth.dummy.DummyAuthenticator;
import net.twomini.hybridcontent.auth.dummy.DummyProvider;
import net.twomini.hybridcontent.health.DefaultHealthCheck;
import net.twomini.hybridcontent.resources.DefaultResource;
import com.yammer.dropwizard.auth.Authenticator;
import com.yammer.dropwizard.client.HttpClientBuilder;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Environment;
import org.apache.http.client.HttpClient;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

public class Service extends com.yammer.dropwizard.Service<ServiceConfiguration> {

    public static void main(String[] args) throws Exception {
        new Service().run(args);
    }

    @Override
    public void initialize(Bootstrap<ServiceConfiguration> bootstrap) {
        // You can change this name to whatever you want
        bootstrap.setName("HybridContentSite");
    }

    @Override
    public void run(ServiceConfiguration configuration, Environment environment) {

        /*
            This is to redirect HTTP Requests to HTTPS; if you want to do so; we do.
            Conveniently, this will not do anything unless the service is deployed behind an AWS Elastic Load Balancer; so this is safe to leave uncommented in localhost and other server deployments.

            We prefer to host our hybrid content service over HTTPS, redirecting HTTP requests to HTTPS.
            This Filter looks for the AWS Elastic Load Balancer injected HTTP Header "X-Forwarded-Proto", which tells this service if the request came to the load balancer over HTTP or HTTPS, and redirects HTTP requests to HTTPS.

            The pattern "*//*" means execute this Filter for all web requests that start with a "/" after the domain name in the URL.
            You could change the "*//*" pattern to another pattern such as "/secure-stuff" to only redirect HTTP to HTTPS for URLs that have "/secure-stuff" in the URL after the server name such as "http://abc.com/secure-stuff/index.html".

            You can create your own Filter to work with other load balancers, or to embody your own custom logic.
        */
        environment.addFilter(new Ec2ElbHttpsRedirectFilter(), "/*");


        /*
            Dropwizard provides a Managed HTTP Client, which you can give to parts of your code that need to make HTTP or HTTPS requests to other servers.
            We will be giving this our code that calls our Single Sign On server, so verify if users are logged in or not.
         */
        final HttpClient httpClient = new HttpClientBuilder().using(configuration.getHttpClient()).build();


        /*
            This Authenticator instance is used to authenticate your visitors.  By default we use the DummyAuthenticator
            which Always returns that the user is not logged in.  When using the AuthService, you will want to uncomment
            the first line using the AuthServiceAuthenticator, and comment the second using the DummyAuthenticator.
         */
        //Authenticator<HttpServletRequest, AuthCaller> cachingAuthenticator = new AuthServiceAuthenticator(configuration.getAuthServiceBaseUrl(), configuration.getAuthServiceToken(), httpClient);
        Authenticator<HttpServletRequest, AuthCaller> cachingAuthenticator = new DummyAuthenticator();

        /*
            This is a list of rules used to secure static files being served through this service.
            The first parameter is used to match requested URI's.
            The second is a boolean, whether the user is required to be logged in to access matching URI's.
            The third is a String... of roles the user must have, i.e. "roleName" or "roleName1", "roleName2", "roleName3"
         */
        List<AuthUrlRule> urlAuthRules = new ArrayList<AuthUrlRule>();
        // Th following example rule will block requests to URLs that start with "private" unless logged in by a user having the "privateRole" role
        urlAuthRules.add(new AuthUrlRule("private", true, "privateRole"));

        /*
            This Authorizer instance is used by both the static files servlet and the service endpoints to verify that the
            caller has the appropriate roles, or is logged in, based on what is required by the AuthUrlRule or the AuthCaller annotation.
         */
        Authorizer authorizer = new Authorizer(urlAuthRules);

        /*
            This Provider is what adds the ability to secure the service endpoints in the DefaultResource class,
            or any Resource class.  It allows you to use the AuthCaller annotation as a parameter of service endpoint methods,
            see the service endpoints in the DefaultResource class for an example.

            By default we are using the DummyProvider which returns a null for the AuthCaller annotation parameter,
            which means that the user is not logged in.

            When using the Auth Service, uncomment the first line, using the AuthServiceProvider, and comment out the second
            using the DummyProvider.
         */
        //environment.addProvider(new AuthServiceProvider<AuthCaller>(cachingAuthenticator, authorizer, configuration.getServiceBaseURL(), configuration.getAuthServiceBaseUrl()));
        environment.addProvider(new DummyProvider<AuthCaller>());


        /*
            Setting up the servlet that will serve any static files

            "/files/" is a folder location in this project, for static files, that will get included in the binary upon compiling
            This folder can be used to hold static files for hosting.  However, this is not the recommended place for static files.
            The recommended location for static files is specified in the .yml configuration file, and will point to a actual folder
            on the server you are running the service on.  By placing your static files in such a directory on the server,
            you can update them without recompiling and redeploying this project.


            "/" is the root URL folder for serving static files.  Static files from both the "/files/" and the server's folder,
            will be accessible from this URL directory.
            example: http://something.com/mystaticfile.html

            When going to the above example URL, a file named mystaticfile.html from the server's folder would be returned;
            if not found, a file by the same name would be returned from the internal "/files/" folder, if found.
         */
        AssetsServletFactory asf = new AssetsServletFactory("/files/", "/");
        asf.addNewAssetServletToEnvironment(environment, configuration.getAssetsConfiguration(), cachingAuthenticator, authorizer, configuration.getAuthServiceBaseUrl(), configuration.getServiceBaseURL());


        /*
            This is where we add our Restful web service endpoints to this service.
         */
        environment.addResource(new DefaultResource());


        /*
            Dropwizard requires at least one HealthCheck, code that tests to see if the service is working properly.
            This is an empty HealthCheck, to learn more go here: http://dropwizard.codahale.com/manual/core/#health-checks
         */
        environment.addHealthCheck(new DefaultHealthCheck());

    }

}