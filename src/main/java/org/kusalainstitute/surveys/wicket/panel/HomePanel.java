package org.kusalainstitute.surveys.wicket.panel;

import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.kusalainstitute.surveys.wicket.pages.AnswersPage;
import org.kusalainstitute.surveys.wicket.pages.MatchManagementPage;
import org.kusalainstitute.surveys.wicket.pages.SituationAnalysisPage;

/**
 * Home panel with navigation links to all available pages.
 */
public class HomePanel extends Panel
{

	/**
	 * Creates a new HomePanel.
	 *
	 * @param id
	 *            the wicket component id
	 */
	public HomePanel(String id)
	{
		super(id);
	}

	@Override
	protected void onInitialize()
	{
		super.onInitialize();

		add(new BookmarkablePageLink<>("situationAnalysisLink", SituationAnalysisPage.class));
		add(new BookmarkablePageLink<>("answersLink", AnswersPage.class));
		add(new BookmarkablePageLink<>("matchManagementLink", MatchManagementPage.class));
	}
}
