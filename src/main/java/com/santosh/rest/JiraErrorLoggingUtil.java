package com.santosh.rest;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URI;

import com.atlassian.jira.rest.client.api.GetCreateIssueMetadataOptionsBuilder;
import com.atlassian.jira.rest.client.api.IssueRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClientFactory;
import com.atlassian.jira.rest.client.api.domain.BasicIssue;
import com.atlassian.jira.rest.client.api.domain.CimIssueType;
import com.atlassian.jira.rest.client.api.domain.CimProject;
import com.atlassian.jira.rest.client.api.domain.EntityHelper;
import com.atlassian.jira.rest.client.api.domain.input.IssueInputBuilder;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;

public class JiraErrorLoggingUtil {

    private static final String JIRA_URL = "http://localhost:6060";
    private static final String JIRA_ADMIN_USERNAME = "sonu.sntsh";
    private static final String JIRA_ADMIN_PASSWORD = "123456";
    private static String jiraIssueType = "Bug";
    static String jiraSecLevel = "Private";

    public static void logError(String errorText) throws Exception {
    	
    	System.out.println(String.format("Logging in to %s with username '%s' and password '%s'", JIRA_URL, JIRA_ADMIN_USERNAME, JIRA_ADMIN_PASSWORD));
        JiraRestClientFactory factory = new AsynchronousJiraRestClientFactory();
        URI uri = new URI(JIRA_URL);
        JiraRestClient client = factory.createWithBasicHttpAuthentication(uri, JIRA_ADMIN_USERNAME, JIRA_ADMIN_PASSWORD);
    	
    	Class<JSONService> obj = JSONService.class;

    	// Process @LogJiraError
    	
   		for (Method method : obj.getDeclaredMethods()) {

    			// if method is annotated with @Test
    			if (method.isAnnotationPresent(LogJiraError.class)) {

    				Annotation annotation = method.getAnnotation(LogJiraError.class);
    				LogJiraError test = (LogJiraError) annotation;

    				// if enabled = true  -- validate if logging error is true create bug in JIRA with exceptiion details
    				if (test.enabled()) {
    	    			
    	    			   try {
    	    				   
    	    		        	IssueRestClient issueClient = client.getIssueClient();
    	    		            Iterable<CimProject> metadataProjects = issueClient.getCreateIssueMetadata( new GetCreateIssueMetadataOptionsBuilder().withProjectKeys("PROJ").withExpandedIssueTypesFields().build() ).claim();
    	    		            CimProject project = metadataProjects.iterator().next();
    	    		            CimIssueType issueType = EntityHelper.findEntityByName(project.getIssueTypes(), jiraIssueType);
    	    		            IssueInputBuilder issueInputBuilder = new IssueInputBuilder(project, issueType, errorText);
    	    		            BasicIssue basicCreatedIssue = issueClient.createIssue(issueInputBuilder.build()).claim();
    	    		            System.out.println(basicCreatedIssue.getKey());
    	    		           /* basicCreatedIssue = issueClient.createIssue(issueInputBuilder.build()).claim();
    	    		            System.out.println(basicCreatedIssue.getKey());*/
    	    		    		

    	    		        }catch(Exception ae) {
    	    		        	System.out.println(ae.getMessage());
    	    		        	
    	    		        } finally {
    	    		            client.close();
    	    		        }
    	    		} 

    			}
    		}
    		
    		
    		
    		

    
    	
        
        
    }
}