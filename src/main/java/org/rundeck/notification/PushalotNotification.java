package org.rundeck.notification;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import org.apache.log4j.Logger;

import com.dtolabs.rundeck.core.plugins.Plugin;
import com.dtolabs.rundeck.core.plugins.configuration.PropertyScope;
import com.dtolabs.rundeck.plugins.descriptions.PluginDescription;
import com.dtolabs.rundeck.plugins.descriptions.PluginProperty;
import com.dtolabs.rundeck.plugins.notification.NotificationPlugin;

/**
* Pushalot Notification Plugin
*/
@Plugin(service = "Notification", name = "Pushalot")
@PluginDescription(title = "Pushalot", description = "Send notification messages to Pushalot")
public class PushalotNotification implements NotificationPlugin {
	
	public static final Logger log = Logger.getLogger(PushalotNotification.class);
	
	public static final String PUSHALOT_API_URL = "https://pushalot.com/api/sendmessage";
	public static final int RESULT_OK = 200;
	
	 @PluginProperty(title= "Override Authorization API Token", name = "authorizationJobApiToken", description = "Job specific token. Comma separated values.")
     private String authorizationJobApiToken;
	
	 @PluginProperty(title= "Authorization API Token", name = "authorizationApiToken", description = "Authorization API Token. Comma separated values.", scope = PropertyScope.Project)
     private String authorizationApiToken;
	
	 @PluginProperty(name = "Important notification", description = "Mark as important", defaultValue="false")
     private boolean isImportant;
	 
	 @PluginProperty(name = "Silent notification", description = "Silent delivery", defaultValue="false")
     private boolean isSilent;
	 
	public boolean postNotification(String trigger, Map executionData, Map config) {
			if (isBlank(authorizationApiToken) && isBlank(authorizationJobApiToken)) {
				log.error("Unable to send Pushalot notification without an authorizationApiToken");
				throw new IllegalStateException("authorizationApiToken must be set");
			}

		String token = !isBlank(authorizationJobApiToken) ? authorizationJobApiToken : authorizationApiToken;
		
		String jobString = ((Map)executionData.get("job")).get("name").toString();
		String jobUrl = executionData.get("href").toString();
		String projectString = executionData.get("project").toString();
		
		PushalotNotificationDetail notification = new PushalotNotificationDetail();
		notification.setBody(getNotificationMessage(trigger, jobString, projectString));
		notification.setTitle(getNotificationTitle(trigger, jobString));
		notification.setUrl(jobUrl);
		notification.setUrlTitle("View Job execution output view.");
		System.err.print(jobUrl);
		
		notification.setImportant(isImportant);
		notification.setSilent(isSilent);
		
		boolean result = true;
			String[] tokens = token.split(",");
			for(String t : tokens){
				try {
					result &= sendPostRequest(t, notification) == RESULT_OK;
				} catch (IOException e) {
					log.error(e.getMessage(), e);
				}
			}
		
		return result;
	}

	  private boolean isBlank(String string) {
          return null == string || "".equals(string.trim());
  }
  
  private String getNotificationMessage(String trigger, String job, String project) {
          String notificationMessage = null;
          if("start".equalsIgnoreCase(trigger)){
        	  notificationMessage = "Rundeck job " + job + " for project " + project + " has begun";
          } else if ("success".equalsIgnoreCase(trigger))   {
        	  notificationMessage = "Rundeck job " + job + " for project " + project + " has finished successfully";
          } else if ("failure".equalsIgnoreCase(trigger)) {
        	  notificationMessage = "Rundeck job " + job + " for project " + project + " has failed";
          }
          return notificationMessage;
  }
  
  private String getNotificationTitle(String trigger, String job) {
      String notificationMessage = null;
      if("start".equalsIgnoreCase(trigger)){
    	  notificationMessage = job + " has begun";
      } else if ("success".equalsIgnoreCase(trigger))   {
    	  notificationMessage = job + " has finished successfully";
      } else if ("failure".equalsIgnoreCase(trigger)) {
    	  notificationMessage = job + " has failed";
      }
      return notificationMessage;
}
	
	private int sendPostRequest(String token, PushalotNotificationDetail notification) throws IOException {
 
		URL obj = new URL(PUSHALOT_API_URL);
		HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

		//add request header
		con.setRequestMethod("POST");
		con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		
		notification.setToken(token);
		
		// Send post request
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(notification.toString());
		wr.flush();
		wr.close();
 
		return con.getResponseCode();
	}

	
	class PushalotNotificationDetail {
		private String token;
		
		private String title;
		private String urlTitle;
		private String url;
		private String body;
		private boolean isImportant;
		private boolean isSilent;
		
		public String getToken() {
			return token;
		}
		
		public String getBody() {
			return body;
		}
		
		public String getTitle() {
			return title;
		}
		
		public String getUrl() {
			return url;
		}
		
		public String getUrlTitle() {
			return urlTitle;
		}
		
		public boolean isImportant() {
			return isImportant;
		}
		
		public boolean isSilent() {
			return isSilent;
		}

		public void setToken(String token) {
			this.token = token;
		}
		
		public void setBody(String body) {
			this.body = body;
		}
		
		public void setTitle(String title) {
			this.title = title;
		}
		
		public void setUrl(String url) {
			this.url = url;
		}
		
		public void setImportant(boolean isImportant) {
			this.isImportant = isImportant;
		}
		
		public void setSilent(boolean isSilent) {
			this.isSilent = isSilent;
		}
		
		public void setUrlTitle(String urlTitle) {
			this.urlTitle = urlTitle;
		}
		
		public String toString(){
			StringBuilder build = new StringBuilder();
			build.append("AuthorizationToken=");
			build.append(token);
			
			if(!isBlank(body)){
				build.append("&Body=");
				build.append(body);
			}
			
			if(!isBlank(title)){
				build.append("&Title=");
				build.append(title);
			}
			
			if(!isBlank(url)){
				build.append("&Link=");
				build.append(url);
			}
			
			if(!isBlank(urlTitle)){
				build.append("&LinkTitle=");
				build.append(urlTitle);
			}
			
			if(isImportant){
				build.append("&IsImportant=True");
			}
			
			if(isSilent){
				build.append("&IsSilent=True");
			}
			
			return build.toString();
		}
	}
}
