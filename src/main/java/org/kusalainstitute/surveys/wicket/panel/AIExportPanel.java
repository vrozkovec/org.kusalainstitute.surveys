package org.kusalainstitute.surveys.wicket.panel;

import java.util.List;

import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;
import org.kusalainstitute.surveys.wicket.model.SituationAnalysisModel;

import name.berries.wicket.bootstrap.form.BootstrapMultiSelect;

/**
 * Panel containing all AI-friendly text export panels. Displays ImprovementSummaryTextPanel,
 * RatingDistributionAllTextPanel, and EnumDistributionSummaryTextPanel for easy copy-paste into AI
 * systems.
 */
public class AIExportPanel extends GenericPanel<SituationAnalysisModel>
{

	/** Model containing available cohorts for filtering. */
	private final IModel<List<String>> availableCohortsModel;

	/** Model containing selected cohorts for filtering. */
	private final IModel<List<String>> selectedCohortsModel;

	/** Container for AJAX refresh of panel content. */
	private WebMarkupContainer contentContainer;

	/**
	 * Creates a new AIExportPanel.
	 *
	 * @param id
	 *            the wicket component id
	 * @param model
	 *            the model containing the analysis data
	 * @param availableCohortsModel
	 *            model containing available cohorts for filtering
	 * @param selectedCohortsModel
	 *            model containing selected cohorts for filtering
	 */
	public AIExportPanel(String id, IModel<SituationAnalysisModel> model, IModel<List<String>> availableCohortsModel,
		IModel<List<String>> selectedCohortsModel)
	{
		super(id, model);
		this.availableCohortsModel = availableCohortsModel;
		this.selectedCohortsModel = selectedCohortsModel;
	}

	@Override
	protected void onInitialize()
	{
		super.onInitialize();

		// Add cohort filter form
		Form<Void> filterForm = new Form<>("filterForm");
		add(filterForm);

		BootstrapMultiSelect<String> cohortSelect = new BootstrapMultiSelect<>("cohortFilter", selectedCohortsModel,
			availableCohortsModel);

		cohortSelect.add(OnChangeAjaxBehavior.onChange(t -> {
			getModel().detach();
			t.add(contentContainer);
		}));
		filterForm.add(cohortSelect);

		// Content container for AJAX refresh
		contentContainer = new WebMarkupContainer("contentContainer");
		contentContainer.setOutputMarkupId(true);
		add(contentContainer);

		contentContainer.add(new ImprovementSummaryTextPanel("improvementSummary", getModel()));
		contentContainer.add(new RatingDistributionAllTextPanel("ratingDistribution", getModel()));
		contentContainer.add(new EnumDistributionSummaryTextPanel("enumDistribution", getModel()));
	}
}
