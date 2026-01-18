/**
 *
 */
package org.kusalainstitute.surveys.wicket.pages.base;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.lang.Args;
import org.kusalainstitute.surveys.wicket.pages.AIExportPage;
import org.kusalainstitute.surveys.wicket.pages.AnswersPage;
import org.kusalainstitute.surveys.wicket.pages.HomePage;
import org.kusalainstitute.surveys.wicket.pages.MatchManagementPage;
import org.kusalainstitute.surveys.wicket.pages.SituationAnalysisPage;

import cz.newforms.wicket.markup.html.basic.container.AjaxContainer;

import de.agilecoders.wicket.core.markup.html.bootstrap.navbar.Navbar;
import de.agilecoders.wicket.core.markup.html.bootstrap.navbar.Navbar.Position;
import de.agilecoders.wicket.core.markup.html.bootstrap.navbar.NavbarButton;
import de.agilecoders.wicket.core.markup.html.bootstrap.navbar.NavbarComponents;
import de.agilecoders.wicket.core.markup.html.bootstrap.utilities.BackgroundColorBehavior.Color;

/**
 * Base page with navigation
 */
public class BasePage extends SetupPage
{
	private static final String CONTENT_ID = "main-content";


	/**
	 *
	 */
	public BasePage()
	{
	}

	/**
	 * @param parameters
	 */
	public BasePage(PageParameters parameters)
	{
		super(parameters);
	}

	@Override
	protected void onInitialize()
	{
		super.onInitialize();
		add(newContentPanel(CONTENT_ID).setOutputMarkupId(true));
	}

	protected Component newContentPanel(String id)
	{
		return new AjaxContainer(id);
	}

	protected void replaceContent(AjaxRequestTarget target, Component newComponent)
	{
		Args.isTrue(newComponent.getId().equals(CONTENT_ID), "Component id has to be " + CONTENT_ID);
		newComponent.setOutputMarkupPlaceholderTag(true);
		get(CONTENT_ID).replaceWith(newComponent);
		target.add(newComponent);
	}

	@Override
	protected Navbar newNavbar(String id)
	{
		Navbar navbar;
		navbar = new Navbar(id)
		{
			@Override
			protected void onConfigure()
			{
				super.onConfigure();
			}
		};
		navbar.setOutputMarkupId(true);
		navbar.setPosition(Position.TOP);
		navbar.setInverted(true);
		navbar.setBackgroundColor(Color.Dark);

		List<Component> navbarComponents = new ArrayList<>();
		navbarComponents.add(new NavbarButton<HomePage>(HomePage.class, new Model<>("Home")));
		navbarComponents
			.add(new NavbarButton<SituationAnalysisPage>(SituationAnalysisPage.class, new Model<>("Situation Analysis")));
		navbarComponents.add(new NavbarButton<AnswersPage>(AnswersPage.class, new Model<>("Open-Ended Answers")));
		navbarComponents.add(new NavbarButton<AIExportPage>(AIExportPage.class, new Model<>("AI Export")));
		navbarComponents
			.add(new NavbarButton<MatchManagementPage>(MatchManagementPage.class, new Model<>("Match Management")));

		navbar.addComponents(
			NavbarComponents.transform(Navbar.ComponentPosition.LEFT, navbarComponents.toArray(new Component[] { })));

		navbarComponents.clear();
		navbar.addComponents(
			NavbarComponents.transform(Navbar.ComponentPosition.RIGHT, navbarComponents.toArray(new Component[] { })));

		return navbar;
	}

	/**
	 * @param component
	 * @return page
	 */
	public static BasePage get(Component component)
	{
		return (BasePage)component.getPage();
	}

}