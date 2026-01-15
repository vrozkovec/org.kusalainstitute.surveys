package org.kusalainstitute.surveys.wicket.panel;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.kusalainstitute.surveys.wicket.model.SituationAnalysisModel;
import org.kusalainstitute.surveys.wicket.model.SituationAnalysisModel.RatingDistribution;

/**
 * Panel displaying rating distribution before and after for speaking situations. Shows horizontal
 * bar chart with PRE (gray) and POST (green) bars for each rating level (1-5). Includes dropdown
 * to select specific situation or view all combined.
 */
public class RatingDistributionPanel extends GenericPanel<SituationAnalysisModel>
{

	private String selectedSituation = "All Situations";
	private WebMarkupContainer distributionContainer;

	/**
	 * Creates a new RatingDistributionPanel.
	 *
	 * @param id
	 *            the wicket component id
	 * @param model
	 *            the model containing the analysis data
	 */
	public RatingDistributionPanel(String id, IModel<SituationAnalysisModel> model)
	{
		super(id, model);
	}

	@Override
	protected void onInitialize()
	{
		super.onInitialize();

		// Build situation choices
		List<String> choices = new ArrayList<>();
		choices.add("All Situations");
		choices.addAll(SituationAnalysisModel.SITUATION_NAMES);

		// Dropdown for situation selection
		DropDownChoice<String> situationChoice = new DropDownChoice<>("situationSelect",
			new PropertyModel<>(this, "selectedSituation"), choices);
		situationChoice.add(new AjaxFormComponentUpdatingBehavior("change")
		{
			@Override
			protected void onUpdate(AjaxRequestTarget target)
			{
				target.add(distributionContainer);
			}
		});
		add(situationChoice);

		// Container for the distribution display (refreshed on selection change)
		distributionContainer = new WebMarkupContainer("distributionContainer");
		distributionContainer.setOutputMarkupId(true);
		add(distributionContainer);

		// Rating rows (1-5)
		distributionContainer.add(new ListView<>("ratingRows", List.of(1, 2, 3, 4, 5))
		{
			@Override
			protected void populateItem(ListItem<Integer> item)
			{
				int rating = item.getModelObject();
				RatingDistribution dist = getCurrentDistribution();
				int maxCount = Math.max(dist.getMaxCount(), 1); // Avoid division by zero

				int preCount = dist.preCounts().get(rating - 1);
				int postCount = dist.postCounts().get(rating - 1);

				int prePercent = preCount * 100 / maxCount;
				int postPercent = postCount * 100 / maxCount;

				item.add(new Label("ratingNumber", String.valueOf(rating)));
				item.add(new Label("ratingLabel", getRatingLabel(rating)));

				// Pre bar
				WebMarkupContainer preBar = new WebMarkupContainer("preBar");
				preBar.add(AttributeModifier.replace("style", "width:" + prePercent + "%"));
				item.add(preBar);
				item.add(new Label("preCount", String.valueOf(preCount)));

				// Post bar
				WebMarkupContainer postBar = new WebMarkupContainer("postBar");
				postBar.add(AttributeModifier.replace("style", "width:" + postPercent + "%"));
				item.add(postBar);
				item.add(new Label("postCount", String.valueOf(postCount)));
			}
		});

		// Situation name label
		distributionContainer.add(new Label("situationLabel", new Model<String>()
		{
			@Override
			public String getObject()
			{
				return selectedSituation;
			}
		}));
	}

	/**
	 * Gets the current rating distribution based on selected situation.
	 *
	 * @return the RatingDistribution for current selection
	 */
	private RatingDistribution getCurrentDistribution()
	{
		SituationAnalysisModel data = getModelObject();

		if ("All Situations".equals(selectedSituation))
		{
			return data.getRatingDistributionAll();
		}

		int index = SituationAnalysisModel.SITUATION_NAMES.indexOf(selectedSituation);
		if (index >= 0)
		{
			return data.getRatingDistribution(index);
		}

		return data.getRatingDistributionAll();
	}

	/**
	 * Returns a human-readable label for a rating value.
	 *
	 * @param rating
	 *            the rating (1-5)
	 * @return descriptive label
	 */
	private String getRatingLabel(int rating)
	{
		return switch (rating)
		{
			case 1 -> "Can't at all";
			case 2 -> "Struggle";
			case 3 -> "Okay";
			case 4 -> "Good";
			case 5 -> "Very good";
			default -> "";
		};
	}
}
