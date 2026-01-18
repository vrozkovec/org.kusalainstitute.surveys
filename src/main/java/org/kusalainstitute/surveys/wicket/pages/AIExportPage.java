package org.kusalainstitute.surveys.wicket.pages;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.util.ListModel;
import org.jdbi.v3.core.Jdbi;
import org.kusalainstitute.surveys.dao.MatchDao;
import org.kusalainstitute.surveys.dao.PersonDao;
import org.kusalainstitute.surveys.dao.PostSurveyDao;
import org.kusalainstitute.surveys.dao.PreSurveyDao;
import org.kusalainstitute.surveys.pojo.Person;
import org.kusalainstitute.surveys.pojo.PersonMatch;
import org.kusalainstitute.surveys.wicket.app.SurveyApplication;
import org.kusalainstitute.surveys.wicket.model.SituationAnalysisModel;
import org.kusalainstitute.surveys.wicket.model.SituationAnalysisModel.MatchedPairData;
import org.kusalainstitute.surveys.wicket.pages.base.BasePage;
import org.kusalainstitute.surveys.wicket.panel.AIExportPanel;

/**
 * Page displaying survey data in AI-friendly plain text format for easy copy-paste. Contains three
 * panels: ImprovementSummaryTextPanel, RatingDistributionAllTextPanel, and
 * EnumDistributionSummaryTextPanel.
 */
public class AIExportPage extends BasePage
{

	/** Available cohorts from POST survey persons (correct data). */
	private IModel<List<String>> availableCohortsModel;

	/** Currently selected cohorts for filtering. */
	private IModel<List<String>> selectedCohortsModel;

	/**
	 * Creates a new AIExportPage.
	 */
	public AIExportPage()
	{
		super();
	}

	@Override
	protected void onInitialize()
	{
		// Load available cohorts from POST persons only (correct cohort data)
		Jdbi jdbi = SurveyApplication.get().getJdbi();
		List<String> availableCohorts = jdbi.withHandle(handle -> {
			PersonDao personDao = handle.attach(PersonDao.class);
			return personDao.findAllPostCohorts();
		});

		// Initialize models - all cohorts selected by default
		availableCohortsModel = new ListModel<>(availableCohorts);
		selectedCohortsModel = new ListModel<>(new ArrayList<>(availableCohorts));

		super.onInitialize();
	}

	@Override
	protected Component newContentPanel(String id)
	{
		return new AIExportPanel(id, new LoadableDetachableModel<>()
		{
			@Override
			protected SituationAnalysisModel load()
			{
				return loadAnalysisModel();
			}
		}, availableCohortsModel, selectedCohortsModel);
	}

	/**
	 * Loads the analysis model from the database, filtered by selected cohorts.
	 *
	 * @return populated SituationAnalysisModel
	 */
	private SituationAnalysisModel loadAnalysisModel()
	{
		Jdbi jdbi = SurveyApplication.get().getJdbi();
		List<String> selectedCohorts = selectedCohortsModel.getObject();

		return jdbi.withHandle(handle -> {
			MatchDao matchDao = handle.attach(MatchDao.class);
			PersonDao personDao = handle.attach(PersonDao.class);
			PreSurveyDao preSurveyDao = handle.attach(PreSurveyDao.class);
			PostSurveyDao postSurveyDao = handle.attach(PostSurveyDao.class);

			// Filter matches by POST person's cohort (correct cohort data)
			List<PersonMatch> matches = selectedCohorts.isEmpty()
				? List.of()
				: matchDao.findByPostPersonCohorts(selectedCohorts);
			List<MatchedPairData> pairs = new ArrayList<>();

			for (PersonMatch match : matches)
			{
				var prePersonOpt = personDao.findById(match.getPrePersonId());
				var postPersonOpt = personDao.findById(match.getPostPersonId());
				var preOpt = preSurveyDao.findByPersonId(match.getPrePersonId());
				var postOpt = postSurveyDao.findByPersonId(match.getPostPersonId());

				if (prePersonOpt.isPresent() && preOpt.isPresent() && postOpt.isPresent())
				{
					Person personPre = prePersonOpt.get();
					Person personPost = postPersonOpt.get();

					pairs.add(new MatchedPairData(personPre.getName(), personPre.getId(), personPost.getCohort(),
						preOpt.get(), postOpt.get()));
				}
			}

			return SituationAnalysisModel.buildFromMatchedPairs(pairs);
		});
	}
}
