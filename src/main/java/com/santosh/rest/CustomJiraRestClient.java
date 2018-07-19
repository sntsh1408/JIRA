package com.santosh.rest;


import java.net.URI;

import com.atlassian.jira.rest.client.api.GetCreateIssueMetadataOptionsBuilder;
import com.atlassian.jira.rest.client.api.IssueRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClientFactory;
import com.atlassian.jira.rest.client.api.domain.BasicIssue;
import com.atlassian.jira.rest.client.api.domain.BasicProject;
import com.atlassian.jira.rest.client.api.domain.CimIssueType;
import com.atlassian.jira.rest.client.api.domain.CimProject;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.SearchResult;
import com.atlassian.jira.rest.client.api.domain.User;
import com.atlassian.jira.rest.client.api.domain.input.IssueInputBuilder;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import com.atlassian.jira.rest.client.api.domain.EntityHelper;
import com.atlassian.util.concurrent.Promise;

public class CustomJiraRestClient {

    private static final String JIRA_URL = "http://localhost:6060";
    private static final String JIRA_ADMIN_USERNAME = "sonu.sntsh";
    private static final String JIRA_ADMIN_PASSWORD = "123456";
    private static String jiraIssueType = "Bug";
    static String jiraSecLevel = "Private";

    public static void main(String[] args) throws Exception {
    	
        System.out.println(String.format("Logging in to %s with username '%s' and password '%s'", JIRA_URL, JIRA_ADMIN_USERNAME, JIRA_ADMIN_PASSWORD));
        JiraRestClientFactory factory = new AsynchronousJiraRestClientFactory();
        URI uri = new URI(JIRA_URL);
        JiraRestClient client = factory.createWithBasicHttpAuthentication(uri, JIRA_ADMIN_USERNAME, JIRA_ADMIN_PASSWORD);

        Promise<User> promise = client.getUserClient().getUser("sonu.sntsh");
        User user = promise.claim();

        for (BasicProject project : client.getProjectClient().getAllProjects().claim()) {
            System.out.println(project.getKey() + ": " + project.getName());
        }

        Promise<SearchResult> searchJqlPromise = client.getSearchClient().searchJql("project = PROJ");

        for (Issue issue : searchJqlPromise.claim().getIssues()) {
            System.out.println(issue.getSummary());
            
        }
        
        System.out.println(String.format("Your admin user's email address is: %s\r\n", user.getEmailAddress()));

       
        System.out.println("Example complete. Now exiting.");
        
        try {
        	//IssueType issueType = new IssueType(self, id, name, isSubtask, description, iconUri);
        	IssueRestClient issueClient = client.getIssueClient();
            Iterable<CimProject> metadataProjects = issueClient.getCreateIssueMetadata( new GetCreateIssueMetadataOptionsBuilder().withProjectKeys("PROJ").withExpandedIssueTypesFields().build() ).claim();
            CimProject project = metadataProjects.iterator().next();
            CimIssueType issueType = EntityHelper.findEntityByName(project.getIssueTypes(), jiraIssueType);
            IssueInputBuilder issueInputBuilder = new IssueInputBuilder(project, issueType, "Test summary");
            BasicIssue basicCreatedIssue = issueClient.createIssue(issueInputBuilder.build()).claim();
            System.out.println(basicCreatedIssue.getKey());
            // Sec level - error
            //issueInputBuilder = new IssueInputBuilder(project, issueType, "Test summary").setFieldValue("seclevel", jiraSecLevel);
            basicCreatedIssue = issueClient.createIssue(issueInputBuilder.build()).claim();
            System.out.println(basicCreatedIssue.getKey());
    		
    		/*IssueInputBuilder updateInputBuilder = new IssueInputBuilder();
    		updateInputBuilder.setAssignee(santosh);
    		issueClient.updateIssue(issueKey, updateInputBuilder.build());
    		System.out.println("Updated issue:" + issueKey);*/

        } finally {
            client.close();
        }
        
        
        
    }
}