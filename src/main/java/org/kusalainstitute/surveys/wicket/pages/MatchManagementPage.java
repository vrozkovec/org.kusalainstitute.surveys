package org.kusalainstitute.surveys.wicket.pages;

import org.apache.wicket.Component;
import org.kusalainstitute.surveys.wicket.pages.base.BasePage;
import org.kusalainstitute.surveys.wicket.panel.MatchManagementPanel;

import de.agilecoders.wicket.core.util.CssClassNames;

/**
 * Page for managing survey matches. Displays current matches and allows manual matching of
 * unmatched records.
 */
public class MatchManagementPage extends BasePage
{

	/**
	 * Creates a new MatchManagementPage.
	 */
	public MatchManagementPage()
	{
		super();
	}

	@Override
	protected Component newContentPanel(String id)
	{
		return new MatchManagementPanel(id);
	}

	@Override
	protected String getContainerCssClass()
	{
		return CssClassNames.Grid.containerFluid;
	}
}
