package vandy.mooc.view;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import vandy.mooc.R;
import vandy.mooc.common.Utils;


public class SettingsActivity extends Activity
          {

	
	public static final String KEY_PREFERENCE_PROTOCOL =
			"pref_key_protocol";
	
	public static final String KEY_PREFERENCE_IP_ADDRESS =
			"pref_key_ip_address";
	
	public static final String KEY_PREFERENCE_PORT =
			"pref_key_port";
	
	public static final String KEY_PREFERENCE_USER_NAME =
			"pref_key_username";
	
	public static final String KEY_PREFERENCE_PASSWORD =
			"pref_key_password";
	
	
	
	
	@Override 
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
           
        // Display the fragment as the main content. 
        getFragmentManager()
                .beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit(); 
        
    } 
	

	 
	
	
	public class SettingsFragment extends PreferenceFragment
	       implements OnSharedPreferenceChangeListener{
	   
		private static final String IPADDRESS_PATTERN = 
				"^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
				"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
				"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
				"([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
		
		
		private Pattern pattern;
	    private Matcher matcher;
	    
		
		public SettingsFragment() {
			
		}
		
		
		@Override 
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	 
	        setRetainInstance(true);
	        
	        pattern = Pattern.compile(IPADDRESS_PATTERN);
	     	 
	        // Load the preferences from an XML resource 
	        addPreferencesFromResource(R.xml.preferences);
	        SharedPreferences prefs = 
	        		PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
	        
	        setPreferenceSummary(prefs, KEY_PREFERENCE_PROTOCOL);
	        setPreferenceSummary(prefs, KEY_PREFERENCE_IP_ADDRESS);
	        setPreferenceSummary(prefs, KEY_PREFERENCE_PORT);
	        setPreferenceSummary(prefs, KEY_PREFERENCE_USER_NAME);
	        setPreferenceSummary(prefs, KEY_PREFERENCE_PASSWORD);
	        
	    }

		 public void onResume() { 
		        super.onResume(); 
		        getPreferenceScreen().getSharedPreferences()
		                .registerOnSharedPreferenceChangeListener(this);
		    } 
		 
		    public void onPause() { 
		        super.onPause(); 
		        getPreferenceScreen().getSharedPreferences()
		                .unregisterOnSharedPreferenceChangeListener(this);
		    } 
		
		    
		@Override
		public void onSharedPreferenceChanged(SharedPreferences sharedPrefs, String key) {
			setPreferenceSummary(sharedPrefs, key);
			
		} 
		
		
		
		private void setPreferenceSummary(SharedPreferences prefs, String key){
			
			SharedPreferences.Editor editor = prefs.edit();
			
		    Preference pref = findPreference(key);
			
		    if (TextUtils.equals(key, KEY_PREFERENCE_PROTOCOL)) {
		    	ListPreference lp = (ListPreference) pref;
		    	pref.setSummary(lp.getEntry());
	            
		    }else if(TextUtils.equals(key, KEY_PREFERENCE_IP_ADDRESS)){
		    	EditTextPreference etp = (EditTextPreference) pref;
		    	String ip = etp.getText();
	            
		    	if(TextUtils.isEmpty(ip)){
		    		editor.putString(KEY_PREFERENCE_IP_ADDRESS, "0.0.0.0");
	            	etp.setDefaultValue("0.0.0.0");
	            	Utils.showToast(getActivity(), "IP Address cannot be empty");
	            	
	            }else if(! validateIp(ip)) {
	            	editor.putString(KEY_PREFERENCE_IP_ADDRESS, "0.0.0.0");
	            	etp.setDefaultValue("0.0.0.0");
	            	Utils.showToast(getActivity(), "IP Address is invalid");
	            	
	            }else{
	            	pref.setSummary(etp.getText());
	            }
		    	
		    }else if(TextUtils.equals(key, KEY_PREFERENCE_PORT)){
		    	EditTextPreference etp = (EditTextPreference) pref;
		    	String portNo = etp.getText();
		    	
		    	if(TextUtils.isEmpty(portNo)){
		    		editor.putString(KEY_PREFERENCE_PORT, "8080");
		    		etp.setDefaultValue("8080");
	            	Utils.showToast(getActivity(), "Port number cannot be empty");
		    	
		    	}else if(! validatePortNo(portNo)){
		    		editor.putString(KEY_PREFERENCE_PORT, "8080");
		    		etp.setDefaultValue("8080");
	            	Utils.showToast(getActivity(), "Port number is invalid");
	            	
		    	}else{
	            	pref.setSummary(etp.getText());
	            }
		    	
		    }else if(TextUtils.equals(key, KEY_PREFERENCE_USER_NAME)){
		    	EditTextPreference etp = (EditTextPreference) pref;
		    	String userName = etp.getText();
		    	
		    	if(TextUtils.isEmpty(userName)){
		    		editor.putString(KEY_PREFERENCE_USER_NAME, "admin");
		    		etp.setDefaultValue("admin");
	            	Utils.showToast(getActivity(), "UserName cannot be empty");
		    	
		    	}else{
	            	pref.setSummary(etp.getText());
	            }
		    	
		    	
		    }else if(TextUtils.equals(key, KEY_PREFERENCE_PASSWORD)){
		    	EditTextPreference etp = (EditTextPreference) pref;
		    	String password = etp.getText();
		    	
		    	if(TextUtils.isEmpty(password)){
		    		editor.putString(KEY_PREFERENCE_PASSWORD, "pass");
		    		etp.setDefaultValue("pass");
	            	Utils.showToast(getActivity(), "Password cannot be empty");
		    	
		    	}else{
	            	pref.setSummary(etp.getText());
	            }
		    	
		    }
		    
		    
		    editor.commit();
		    
		}
		
		
		 /**
		    * Validate ip address with regular expression
		    * @param ip ip address for validation
		    * @return true valid ip address, false invalid ip address
		    */
		    public boolean validateIp(final String ip){		  
			  matcher = pattern.matcher(ip);
			  return matcher.matches();	    	    
		    }
		

		    public boolean validatePortNo(String portNo){
		    	 //match a number with optional '-' and decimal.
		    	return portNo.matches("\\d+"); 	    	
		    }
		
		
	    
	}




	
}
