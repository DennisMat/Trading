package com.dennis;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ib.controller.ApiController;
import com.dennis.Util;

public class App 
{

	private static Log logger = LogFactory.getLog(App.class);
	public static void main( String[] args ) 
	{
		Variables.loadVariables(null);
		ApiController apiController = Util.obtainApiController();
		boolean wait=true;
		while(wait)  {			
			Variables.loadVariables(apiController);
			if(Variables.isTest) {
				wait=false;
				break;
			}else if(Util.isExchangeOpen()) {
				wait=false;
				break;
			}
			logger.info("waiting...");
			int waitTime=10*60*1000;//10 min
			try {Thread.sleep(waitTime);} catch (InterruptedException e) {}				
		}
		//this line of code is repeated because this line may be called after a long time.
		apiController = Util.obtainApiController();
		logger.info("Session started.");
		Variables.loadVariables(apiController);
		
		Trading t = new Trading();
		t.doStuff(apiController);


		//apiController.logout();// do not use this. the request token is invalidated
		logger.info("App ended");
	}

	
}





