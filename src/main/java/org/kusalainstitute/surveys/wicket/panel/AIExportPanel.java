package org.kusalainstitute.surveys.wicket.panel;

import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;
import org.kusalainstitute.surveys.wicket.model.SituationAnalysisModel;

/**
 * Panel containing all AI-friendly text export panels.
 * Displays ImprovementSummaryTextPanel, RatingDistributionAllTextPanel,
 * and EnumDistributionSummaryTextPanel for easy copy-paste into AI systems.
 */
public class AIExportPanel extends GenericPanel<SituationAnalysisModel>
{

	/**
	 * Creates a new AIExportPanel.
	 *
	 * @param id
	 *            the wicket component id
	 * @param model
	 *            the model containing the analysis data
	 */
	public AIExportPanel(String id, IModel<SituationAnalysisModel> model)
	{
		super(id, model);
	}

	@Override
	protected void onInitialize()
	{
		super.onInitialize();

		add(new ImprovementSummaryTextPanel("improvementSummary", getModel()));
		add(new RatingDistributionAllTextPanel("ratingDistribution", getModel()));
		add(new EnumDistributionSummaryTextPanel("enumDistribution", getModel()));
	}
}
