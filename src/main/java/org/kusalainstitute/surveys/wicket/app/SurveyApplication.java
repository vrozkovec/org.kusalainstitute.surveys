package org.kusalainstitute.surveys.wicket.app;

import org.apache.wicket.Application;
import org.apache.wicket.markup.head.filter.JavaScriptFilteredIntoFooterHeaderResponse;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.settings.ExceptionSettings;
import org.apache.wicket.util.lang.Bytes;
import org.jdbi.v3.core.Jdbi;
import org.kusalainstitute.surveys.config.SurveysModule;
import org.kusalainstitute.surveys.wicket.pages.HomePage;

import com.google.inject.Guice;
import com.google.inject.Injector;

import de.agilecoders.wicket.core.Bootstrap;
import de.agilecoders.wicket.core.settings.BootstrapSettings;

/**
 * Application object for your web application. If you want to run this application without
 * deploying, run the Start class.
 */
public class SurveyApplication extends WebApplication
{
	/**
	 *
	 */
	public static final String MOUNTPOINT_LOGOUT = "/odhlaseni";

	private Injector injector;

	/**
	 * Construct.
	 */
	public SurveyApplication()
	{
		super();
	}

	/**
	 * @see org.apache.wicket.Application#init()
	 */
	@Override
	public void init()
	{
		super.init();

		// Initialize Guice
		injector = Guice.createInjector(new SurveysModule());

		getCspSettings().blocking().disabled();

		getExceptionSettings().setUnexpectedExceptionDisplay(ExceptionSettings.SHOW_EXCEPTION_PAGE);

		getApplicationSettings().setDefaultMaximumUploadSize(Bytes.megabytes(108));

		/***************************************************
		 * BOOTSTRAP
		 ****************************************************/
		BootstrapSettings settings = new BootstrapSettings();
		settings.setJsResourceFilterName("footer-container");

		Bootstrap.install(this, settings);
		getHeaderResponseDecorators()
			.add(response -> new JavaScriptFilteredIntoFooterHeaderResponse(response, "footer-container"));
	}

	/**
	 * Returns the JDBI instance for database access.
	 *
	 * @return the JDBI instance
	 */
	public Jdbi getJdbi()
	{
		return injector.getInstance(Jdbi.class);
	}

	/**
	 * @see org.apache.wicket.Application#getHomePage()
	 */
	@Override
	public Class<? extends WebPage> getHomePage()
	{
		return HomePage.class;
	}

	/**
	 * @return this application
	 */
	public static SurveyApplication get()
	{
		return (SurveyApplication)Application.get();
	}
}