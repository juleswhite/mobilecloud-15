The following are instructions for running the client portion of the Video Upload app:

1) To test the app on an Android Device:

	a) Change the dataUrl you have created in Assignment 2:
		
		-> If you have followed the Hints given for Assignment 2, then you may be using this code for getting dataUrl:
		
		    private String getDataUrl(long videoId){
			    String base_url = getUrlBaseForLocalServer();
				String url =  base_url + "/video/" + videoId + "/data";
				return url;
            }
			
		-> Change the base_url to the IPV4 Address of your Computer where the server is running:
		
		    - Open your Android device and start up Mobile Wifi hotspot.
		        - Make sure to check "Enable Data access over Mobile Network" on your device.
			- Connect your Computer to your Android device by Wifi.
			- Get IPV4 Address of your Computer:
			
				- e.g., for Windows:
					 Open CommandPrompt of your Computer and type "ipconfig" and look for the IPV4 Address(XXX.XXX.XXX.XXX)
			
			- Now change the getDataUrl(..) method to something like this:
				
				private String getDataUrl(long videoId){
					String base_url =  "http://XXX.XXX.XXX.XXX:8080"; (where XXX.XXX.XXX.XXX is your IPV4 address.)
					String url =  base_url + "/video/" + videoId + "/data";
					return url;
				}
			
			- Changes to the Server is done.
			
	b) Change the constant SERVER_URL in vandy/mooc/utils/Constants.java in the client app:
		    
			SERVER_URL = "http://XXX.XXX.XXX.XXX:8080"; (where XXX.XXX.XXX.XXX is your IPV4 address.);
 
 	
2)  To test the app on an Android Emulator:

    a) No need to change the Server code of Assignment 2.  The code to getDataUrl of a Video should be this:
	        
			 private String getDataUrl(long videoId){
			    String base_url = getUrlBaseForLocalServer();
				String url =  base_url + "/video/" + videoId + "/data";
				return url;
            }
	
	b) Change the constant SERVER_URL in vandy/mooc/utils/Constants.java in the client app:
		    
			SERVER_URL = "http://10.0.2.2:8080";
			
	   For Genymotion/VirtualBox emulators:
		
			SERVER_URL = "http://192.168.56.1:8080";
    
    
	c) To get the Video from Gallery:
	    -> Open DDMS Perspective.
		-> Go to File Explorer.
		-> Store the video in  /storage/Downloads.
		-> Launch the App and try to get Video from Gallery. If it doesn't show up in Gallery, then restart the Emulator and check again.
				
	

			    
			          
