package org.kusalainstitute.surveys.wicket.pages;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
import org.kusalainstitute.surveys.pojo.PostSurveyResponse;
import org.kusalainstitute.surveys.pojo.PreSurveyResponse;
import org.kusalainstitute.surveys.wicket.app.SurveyApplication;
import org.kusalainstitute.surveys.wicket.model.SituationAnalysisModel;
import org.kusalainstitute.surveys.wicket.model.SituationAnalysisModel.MatchedPairData;
import org.kusalainstitute.surveys.wicket.pages.base.BasePage;
import org.kusalainstitute.surveys.wicket.panel.OpenEndedQuestionsPanel;

/**
 * Page displaying the open-ended questions with their answers grouped by question.
 */
public class AnswersPage extends BasePage
{

	/** Available cohorts from POST survey persons (correct data). */
	private IModel<List<String>> availableCohortsModel;

	/** Currently selected cohorts for filtering. */
	private IModel<List<String>> selectedCohortsModel;

	/**
	 * Constructor.
	 */
	public AnswersPage()
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
		return new OpenEndedQuestionsPanel(id, new LoadableDetachableModel<>()
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
				Optional<Person> prePersonOpt = personDao.findById(match.getPrePersonId());
				Optional<Person> postPersonOpt = personDao.findById(match.getPostPersonId());
				Optional<PreSurveyResponse> preOpt = preSurveyDao.findByPersonId(match.getPrePersonId());
				Optional<PostSurveyResponse> postOpt = postSurveyDao.findByPersonId(match.getPostPersonId());

				if (prePersonOpt.isPresent() && preOpt.isPresent() && postOpt.isPresent())
				{
					Person personPost = postPersonOpt.get();
					pairs.add(new MatchedPairData(prePersonOpt.get().getName(), prePersonOpt.get().getId(),
						personPost.getCohort(), preOpt.get(), postOpt.get()));
				}
			}

			return SituationAnalysisModel.buildFromMatchedPairs(pairs);
		});
	}
}