package dk.reload.atlassian.plugins.project_template;

import com.atlassian.jira.project.template.hook.ConfigureData;
import com.atlassian.jira.project.template.hook.ConfigureResponse;
import com.atlassian.jira.project.template.hook.ValidateData;
import com.atlassian.jira.project.template.hook.ValidateResponse;
import com.atlassian.jira.project.template.hook.AddProjectHook;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.issue.fields.config.FieldConfigScheme;
import com.atlassian.jira.issue.context.JiraContextNode;
import com.atlassian.jira.issue.fields.config.manager.FieldConfigSchemeManager;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.context.manager.JiraContextTreeManager;
import com.atlassian.jira.issue.customfields.CustomFieldUtils;
import com.atlassian.jira.project.ProjectCategory;
import com.atlassian.jira.project.ProjectManager;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.Collections;

public class ReloadAddProjectHook implements AddProjectHook
{
    @Override
    public ValidateResponse validate(final ValidateData validateData)
    {
        ValidateResponse validateResponse = ValidateResponse.create();
        if (validateData.projectKey().equals("TEST"))
        {
            validateResponse.addErrorMessage("Invalid Project Key");
        }

        return validateResponse;
    }

    @Override
    public ConfigureResponse configure(final ConfigureData configureData)
    {

        FieldConfigSchemeManager fieldConfigSchemeManager = ComponentAccessor.getFieldConfigSchemeManager();
        changeSchemeOfProject(configureData.project(), fieldConfigSchemeManager.getFieldConfigScheme(new Long(10100)));
        ConfigureResponse configureResponse = ConfigureResponse.create().setRedirect("/issues/");

        return configureResponse;
    }

    private void changeSchemeOfProject(Project jiraProject, FieldConfigScheme newIssueTypeScheme) {
		Long[] currentProjectId = new Long[] { jiraProject.getId() };
		JiraContextTreeManager jiraContextTreeManager = ComponentAccessor
				.getComponentOfType(JiraContextTreeManager.class);
		List<JiraContextNode> removeContext = CustomFieldUtils.buildJiraIssueContexts(false, null,
				currentProjectId, jiraContextTreeManager);
		FieldConfigSchemeManager fieldConfigSchemeManager = ComponentAccessor.getComponent(FieldConfigSchemeManager.class);
		fieldConfigSchemeManager.removeSchemeAssociation(removeContext, ComponentAccessor
				.getFieldManager().getIssueTypeField());
		List<Project> associatedProjectObjects = newIssueTypeScheme.getAssociatedProjectObjects();
		if (associatedProjectObjects == null) {
			associatedProjectObjects = Collections.emptyList();
		}
		Set<Project> projectList = new HashSet<Project>(associatedProjectObjects);
		projectList.add(jiraProject);
		Long[] projectIds = new Long[projectList.size()];
		int i = 0;
		for (Project currentProject : projectList) {
			projectIds[i] = currentProject.getId();
			++i;
		}

        /*
		List<ProjectCategory> associatedProjectCategoryObjects = newIssueTypeScheme
				.getAssociatedProjectCategoryObjects();
		if (associatedProjectCategoryObjects == null) {
			associatedProjectCategoryObjects = Collections.emptyList();
		}
		Set<ProjectCategory> projectCategories = new HashSet<ProjectCategory>(
				associatedProjectCategoryObjects);
		Long[] projectCategoryIds = new Long[projectCategories.size()];
		i = 0;
		for (ProjectCategory category : projectCategories) {
			projectCategoryIds[i] = category.getId();
			++i;
		}*/

		List<JiraContextNode> contexts = CustomFieldUtils.buildJiraIssueContexts(
				newIssueTypeScheme.isGlobal(), projectIds,
				ComponentAccessor.getProjectManager());

		fieldConfigSchemeManager.updateFieldConfigScheme(newIssueTypeScheme, contexts,
				newIssueTypeScheme.getField());

		ComponentAccessor.getFieldManager().refresh();
	}
}
