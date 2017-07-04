package acacia;

import static org.eclipse.jetty.servlets.CrossOriginFilter.ALLOWED_HEADERS_PARAM;
import static org.eclipse.jetty.servlets.CrossOriginFilter.ALLOWED_METHODS_PARAM;
import static org.eclipse.jetty.servlets.CrossOriginFilter.ALLOWED_ORIGINS_PARAM;
import static org.eclipse.jetty.servlets.CrossOriginFilter.ALLOW_CREDENTIALS_PARAM;

import java.util.EnumSet;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;

import org.eclipse.jetty.servlets.CrossOriginFilter;

import acacia.health.SearchHealthCheck;
import acacia.resources.FindUser;
import acacia.resources.ListClasses;
import acacia.services.QueryExecutor;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class AcaciaApplication extends Application<AcaciaConfiguration> {

	public static void main(String[] args) throws Exception {
		new AcaciaApplication().run(args);
	}
	
	@Override
	public String getName() {
		return "acacia-training";
	}

    @Override
    public void initialize(Bootstrap<AcaciaConfiguration> bootstrap) {
        // TODO
    }
	
	@Override
	public void run(AcaciaConfiguration configuration, Environment environment) throws Exception {
		/*
		 * Enabling CORS (Cross-Origin Resource Sharing)
		 * Sources:	https://stackoverflow.com/a/25801822
		 * 			https://gist.github.com/yunspace/07d80a9ac32901f1e149#gistcomment-1468074
		 */
		System.setProperty("sun.net.http.allowRestrictedHeaders", "true");
		FilterRegistration.Dynamic filter = environment.servlets().addFilter("CORSFilter", CrossOriginFilter.class);
		filter.setInitParameter(ALLOWED_METHODS_PARAM, "OPTIONS,POST,GET");
		filter.setInitParameter(ALLOWED_ORIGINS_PARAM, "*");
		filter.setInitParameter(ALLOWED_HEADERS_PARAM, "Origin,Content-Type,Accept,X-Requested-With");
		filter.setInitParameter(ALLOW_CREDENTIALS_PARAM, "true");
		filter.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");

		QueryExecutor qe = configuration.getQueryExecutorFactory().build();
		environment.jersey().register(new ListClasses(qe));
		environment.jersey().register(new FindUser(qe));
		environment.healthChecks().register("search", new SearchHealthCheck());
	}

}