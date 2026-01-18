package org.kusalainstitute.surveys.wicket.panel;

import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.feedback.FencedFeedbackPanel;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.kusalainstitute.surveys.dao.MatchDao.MatchStatistics;
import org.kusalainstitute.surveys.service.MatchingService;
import org.kusalainstitute.surveys.wicket.app.SurveyApplication;
import org.kusalainstitute.surveys.wicket.model.MatchRowData;
import org.kusalainstitute.surveys.wicket.model.UnmatchedPersonData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxButton;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;

/**
 * Wicket panel for managing survey matches. Displays current matches and allows manual matching of
 * unmatched records.
 */
public class MatchManagementPanel extends GenericPanel<Void>
{

	private static final Logger LOG = LoggerFactory.getLogger(MatchManagementPanel.class);

	private Long selectedPreId;
	private Long selectedPostId;
	private String matchNotes;

	private WebMarkupContainer matchesContainer;
	private WebMarkupContainer unmatchedPreContainer;
	private WebMarkupContainer unmatchedPostContainer;
	private WebMarkupContainer statsContainer;
	private WebMarkupContainer selectionContainer;

	private IModel<List<MatchRowData>> matchesModel;
	private IModel<List<UnmatchedPersonData>> unmatchedPreModel;
	private IModel<List<UnmatchedPersonData>> unmatchedPostModel;
	private IModel<MatchStatistics> statsModel;

	/**
	 * Creates a new MatchManagementPanel.
	 *
	 * @param id
	 *            the wicket component id
	 */
	public MatchManagementPanel(String id)
	{
		super(id);
	}

	@Override
	protected void onInitialize()
	{
		super.onInitialize();

		// Create models that load data on demand
		matchesModel = new LoadableDetachableModel<>()
		{
			@Override
			protected List<MatchRowData> load()
			{
				return getMatchingService().getAllMatchesWithData();
			}
		};

		unmatchedPreModel = new LoadableDetachableModel<>()
		{
			@Override
			protected List<UnmatchedPersonData> load()
			{
				return getMatchingService().getUnmatchedPreWithData();
			}
		};

		unmatchedPostModel = new LoadableDetachableModel<>()
		{
			@Override
			protected List<UnmatchedPersonData> load()
			{
				return getMatchingService().getUnmatchedPostWithData();
			}
		};

		statsModel = new LoadableDetachableModel<>()
		{
			@Override
			protected MatchStatistics load()
			{
				return getMatchingService().getStatistics();
			}
		};

		// Feedback panel
		add(new FencedFeedbackPanel("feedback", this).setOutputMarkupId(true));

		// Statistics section
		addStatisticsSection();

		// Manual match form section
		addManualMatchSection();

		// Current matches table
		addMatchesTable();

		// Unmatched lists
		addUnmatchedLists();
	}

	/**
	 * Adds the statistics summary section.
	 */
	private void addStatisticsSection()
	{
		statsContainer = new WebMarkupContainer("statsContainer");
		statsContainer.setOutputMarkupId(true);

		statsContainer.add(new Label("totalMatches", new PropertyModel<>(statsModel, "totalMatches")));
		statsContainer.add(new Label("unmatchedPreCount", new LoadableDetachableModel<Integer>()
		{
			@Override
			protected Integer load()
			{
				return unmatchedPreModel.getObject().size();
			}
		}));
		statsContainer.add(new Label("unmatchedPostCount", new LoadableDetachableModel<Integer>()
		{
			@Override
			protected Integer load()
			{
				return unmatchedPostModel.getObject().size();
			}
		}));

		add(statsContainer);
	}

	/**
	 * Adds the manual match form section.
	 */
	private void addManualMatchSection()
	{
		Form<Void> form = new Form<>("matchForm");

		selectionContainer = new WebMarkupContainer("selectionContainer");
		selectionContainer.setOutputMarkupId(true);

		// Selected PRE display
		selectionContainer.add(new Label("selectedPreLabel", new LoadableDetachableModel<String>()
		{
			@Override
			protected String load()
			{
				if (selectedPreId == null)
				{
					return "(none selected)";
				}
				return unmatchedPreModel.getObject().stream()
					.filter(p -> p.personId().equals(selectedPreId))
					.findFirst()
					.map(UnmatchedPersonData::getDisplayText)
					.orElse("(unknown)");
			}
		}));

		// Selected POST display
		selectionContainer.add(new Label("selectedPostLabel", new LoadableDetachableModel<String>()
		{
			@Override
			protected String load()
			{
				if (selectedPostId == null)
				{
					return "(none selected)";
				}
				return unmatchedPostModel.getObject().stream()
					.filter(p -> p.personId().equals(selectedPostId))
					.findFirst()
					.map(UnmatchedPersonData::getDisplayText)
					.orElse("(unknown)");
			}
		}));

		// Notes textarea
		selectionContainer.add(new TextArea<>("notes", new PropertyModel<>(this, "matchNotes")));

		// Create match button
		selectionContainer.add(new BootstrapAjaxButton("createMatchBtn", Model.of("Create Match"), Buttons.Type.Primary)
		{
			@Override
			protected void onSubmit(AjaxRequestTarget target)
			{
				createManualMatch(target);
			}

			@Override
			protected void onConfigure()
			{
				super.onConfigure();
				setEnabled(selectedPreId != null && selectedPostId != null);
			}
		});

		form.add(selectionContainer);
		add(form);
	}

	/**
	 * Adds the current matches table.
	 */
	private void addMatchesTable()
	{
		matchesContainer = new WebMarkupContainer("matchesContainer");
		matchesContainer.setOutputMarkupId(true);

		matchesContainer.add(new ListView<>("matches", matchesModel)
		{
			@Override
			protected void populateItem(ListItem<MatchRowData> item)
			{
				MatchRowData row = item.getModelObject();

				item.add(new Label("cohort", row.cohort()));
				item.add(new Label("matchType", row.getMatchTypeLabel())
					.add(AttributeModifier.append("class", "badge " + row.getMatchTypeBadgeClass())));
				item.add(new Label("preName", row.preName()));
				item.add(new Label("preEmail", row.preEmail()));
				item.add(new Label("preDate", row.getFormattedPreTimestamp()));
				item.add(new Label("postName", row.postName()));
				item.add(new Label("postEmail", row.postEmail()));
				item.add(new Label("postDate", row.getFormattedPostTimestamp()));
				item.add(new Label("notes", row.notes()));
			}
		});

		add(matchesContainer);
	}

	/**
	 * Adds the unmatched person lists.
	 */
	private void addUnmatchedLists()
	{
		// Unmatched PRE list
		unmatchedPreContainer = new WebMarkupContainer("unmatchedPreContainer");
		unmatchedPreContainer.setOutputMarkupId(true);

		unmatchedPreContainer.add(new ListView<>("unmatchedPre", unmatchedPreModel)
		{
			@Override
			protected void populateItem(ListItem<UnmatchedPersonData> item)
			{
				UnmatchedPersonData person = item.getModelObject();

				// Add CSS class for selection and warning state
				if (person.personId().equals(selectedPreId))
				{
					item.add(AttributeModifier.append("class", "table-primary"));
				}
				else if (person.requiresManual())
				{
					item.add(AttributeModifier.append("class", "table-warning"));
				}

				item.add(new Label("cohort", person.cohort()));
				item.add(new Label("name", person.name()));
				item.add(new Label("email", person.email()));
				item.add(new Label("date", person.getFormattedTimestamp()));

				item.add(new AjaxLink<Void>("selectBtn")
				{
					@Override
					public void onClick(AjaxRequestTarget target)
					{
						selectPrePerson(target, person.personId());
					}
				});
			}
		});

		add(unmatchedPreContainer);

		// Unmatched POST list
		unmatchedPostContainer = new WebMarkupContainer("unmatchedPostContainer");
		unmatchedPostContainer.setOutputMarkupId(true);

		unmatchedPostContainer.add(new ListView<>("unmatchedPost", unmatchedPostModel)
		{
			@Override
			protected void populateItem(ListItem<UnmatchedPersonData> item)
			{
				UnmatchedPersonData person = item.getModelObject();

				// Add CSS class for selection and warning state
				if (person.personId().equals(selectedPostId))
				{
					item.add(AttributeModifier.append("class", "table-primary"));
				}
				else if (person.requiresManual())
				{
					item.add(AttributeModifier.append("class", "table-warning"));
				}

				item.add(new Label("cohort", person.cohort()));
				item.add(new Label("name", person.name()));
				item.add(new Label("email", person.email()));
				item.add(new Label("date", person.getFormattedTimestamp()));

				item.add(new AjaxLink<Void>("selectBtn")
				{
					@Override
					public void onClick(AjaxRequestTarget target)
					{
						selectPostPerson(target, person.personId());
					}
				});
			}
		});

		add(unmatchedPostContainer);
	}

	/**
	 * Handles PRE person selection.
	 *
	 * @param target
	 *            the AJAX request target
	 * @param personId
	 *            the selected person ID
	 */
	private void selectPrePerson(AjaxRequestTarget target, Long personId)
	{
		selectedPreId = personId;
		refreshSelectionUI(target);
	}

	/**
	 * Handles POST person selection.
	 *
	 * @param target
	 *            the AJAX request target
	 * @param personId
	 *            the selected person ID
	 */
	private void selectPostPerson(AjaxRequestTarget target, Long personId)
	{
		selectedPostId = personId;
		refreshSelectionUI(target);
	}

	/**
	 * Refreshes the selection-related UI components.
	 *
	 * @param target
	 *            the AJAX request target
	 */
	private void refreshSelectionUI(AjaxRequestTarget target)
	{
		target.add(unmatchedPreContainer);
		target.add(unmatchedPostContainer);
		target.add(selectionContainer);
	}

	/**
	 * Creates a manual match from the current selection.
	 *
	 * @param target
	 *            the AJAX request target
	 */
	private void createManualMatch(AjaxRequestTarget target)
	{
		if (selectedPreId == null || selectedPostId == null)
		{
			error("Please select both a PRE and POST person.");
			target.add(get("feedback"));
			return;
		}

		try
		{
			getMatchingService().createAndPersistManualMatch(
				selectedPreId,
				selectedPostId,
				"admin",
				matchNotes);

			success("Match created successfully!");

			// Clear selection
			selectedPreId = null;
			selectedPostId = null;
			matchNotes = null;

			// Detach and refresh all models
			matchesModel.detach();
			unmatchedPreModel.detach();
			unmatchedPostModel.detach();
			statsModel.detach();

			// Refresh UI
			target.add(matchesContainer);
			target.add(unmatchedPreContainer);
			target.add(unmatchedPostContainer);
			target.add(selectionContainer);
			target.add(statsContainer);
			target.add(get("feedback"));

			LOG.info("Created manual match via UI");
		}
		catch (Exception e)
		{
			LOG.error("Failed to create manual match", e);
			error("Failed to create match: " + e.getMessage());
			target.add(get("feedback"));
		}
	}

	/**
	 * Gets the matching service from the application injector.
	 *
	 * @return the matching service
	 */
	private MatchingService getMatchingService()
	{
		return SurveyApplication.get().getInjector().getInstance(MatchingService.class);
	}
}
