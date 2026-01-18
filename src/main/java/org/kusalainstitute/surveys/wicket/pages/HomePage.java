package org.kusalainstitute.surveys.wicket.pages;

import org.apache.wicket.Component;
import org.kusalainstitute.surveys.wicket.pages.base.BasePage;
import org.kusalainstitute.surveys.wicket.panel.HomePanel;

/**
 * Home page with navigation links to all available pages.
 */
public class HomePage extends BasePage
{

	/**
	 * Creates a new HomePage.
	 */
	public HomePage()
	{
		super();
	}

	@Override
	protected Component newContentPanel(String id)
	{
		return new HomePanel(id);
	}
}
