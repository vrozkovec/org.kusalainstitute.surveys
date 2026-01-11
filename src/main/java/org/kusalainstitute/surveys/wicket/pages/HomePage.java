package org.kusalainstitute.surveys.wicket.pages;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.wicket.Component;
import org.apache.wicket.model.LoadableDetachableModel;
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
import org.kusalainstitute.surveys.wicket.panel.SituationAnalysisPanel;

/**
 * Home page for signed-in users
 */
public class HomePage extends BasePage
{
	/**
	 * Constructor.
	 */
	public HomePage()
	{
		super();
	}

	@Override
	protected void onInitialize()
	{
		super.onInitialize();

	}

	@Override
	protected Component newContentPanel(String id)
	{
		return new SituationAnalysisPanel(id, new LoadableDetachableModel<>()
		{
			@Override
			protected SituationAnalysisModel load()
			{
				return loadAnalysisModel();
			}
		});
	}

	/**
	 * Loads the analysis model from the database.
	 *
	 * @return populated SituationAnalysisModel
	 */
	private SituationAnalysisModel loadAnalysisModel()
	{
		Jdbi jdbi = SurveyApplication.get().getJdbi();

		return jdbi.withHandle(handle ->
		{
			MatchDao matchDao = handle.attach(MatchDao.class);
			PersonDao personDao = handle.attach(PersonDao.class);
			PreSurveyDao preSurveyDao = handle.attach(PreSurveyDao.class);
			PostSurveyDao postSurveyDao = handle.attach(PostSurveyDao.class);

			List<PersonMatch> matches = matchDao.findAll();
			List<MatchedPairData> pairs = new ArrayList<>();

			for (PersonMatch match : matches)
			{
				Optional<Person> prePersonOpt = personDao.findById(match.getPrePersonId());
				Optional<PreSurveyResponse> preOpt = preSurveyDao.findByPersonId(match.getPrePersonId());
				Optional<PostSurveyResponse> postOpt = postSurveyDao.findByPersonId(match.getPostPersonId());

				if (prePersonOpt.isPresent() && preOpt.isPresent() && postOpt.isPresent())
				{
					Person person = prePersonOpt.get();
					pairs.add(new MatchedPairData(person.getName(), person.getId(), preOpt.get(), postOpt.get()));
				}
			}

			return SituationAnalysisModel.buildFromMatchedPairs(pairs);
		});
	}
}