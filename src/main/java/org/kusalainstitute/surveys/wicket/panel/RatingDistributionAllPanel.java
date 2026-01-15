package org.kusalainstitute.surveys.wicket.panel;

import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;
import org.kusalainstitute.surveys.wicket.model.SituationAnalysisModel;
import org.kusalainstitute.surveys.wicket.model.SituationAnalysisModel.RatingDistribution;

/**
 * Panel displaying rating distributions for ALL speaking situations at once. Shows a grid of
 * mini-charts: one for "All Situations" aggregate and one for each of the 11 individual
 * situations. Each mini-chart displays ratings 1-5 with PRE (gray) and POST (green) bars plus
 * delta values.
 */
public class RatingDistributionAllPanel extends GenericPanel<SituationAnalysisModel>
{

	/**
	 * Creates a new RatingDistributionAllPanel.
	 *
	 * @param id
	 *            the wicket component id
	 * @param model
	 *            the model containing the analysis data
	 */
	public RatingDistributionAllPanel(String id, IModel<SituationAnalysisModel> model)
	{
		super(id, model);
	}

	@Override
	protected void onInitialize()
	{
		super.onInitialize();

		SituationAnalysisModel data = getModelObject();
		List<RatingDistribution> allDistributions = data.getAllRatingDistributions();

		// Outer ListView for each situation (12 charts)
		add(new ListView<>("situationCharts", allDistributions)
		{
			@Override
			protected void populateItem(ListItem<RatingDistribution> chartItem)
			{
				RatingDistribution dist = chartItem.getModelObject();
				int maxCount = Math.max(dist.getMaxCount(), 1);

				// Highlight "All Situations" with different styling
				boolean isAggregate = "All Situations".equals(dist.situationName());
				if (isAggregate)
				{
					chartItem.add(AttributeModifier.append("class", "chart-aggregate"));
				}

				// Situation name header
				chartItem.add(new Label("situationName", dist.situationName()));

				// Inner ListView for ratings (1-5)
				chartItem.add(new ListView<>("ratingRows", List.of(1, 2, 3, 4, 5))
				{
					@Override
					protected void populateItem(ListItem<Integer> ratingItem)
					{
						int rating = ratingItem.getModelObject();

						int preCount = dist.preCounts().get(rating - 1);
						int postCount = dist.postCounts().get(rating - 1);

						int prePercent = preCount * 100 / maxCount;
						int postPercent = postCount * 100 / maxCount;

						ratingItem.add(new Label("ratingNumber", String.valueOf(rating)));
						ratingItem.add(new Label("ratingLabel", getRatingLabel(rating)));

						// Pre bar
						WebMarkupContainer preBar = new WebMarkupContainer("preBar");
						preBar.add(AttributeModifier.replace("style", "width:" + prePercent + "%"));
						ratingItem.add(preBar);
						ratingItem.add(new Label("preCount", String.valueOf(preCount)));

						// Post bar
						WebMarkupContainer postBar = new WebMarkupContainer("postBar");
						postBar.add(AttributeModifier.replace("style", "width:" + postPercent + "%"));
						ratingItem.add(postBar);
						ratingItem.add(new Label("postCount", String.valueOf(postCount)));

						// Delta (neutral color, just showing the change)
						int delta = postCount - preCount;
						String deltaText = delta > 0 ? "(+" + delta + ")" : "(" + delta + ")";
						ratingItem.add(new Label("postDelta", deltaText));
					}
				});
			}
		});
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
