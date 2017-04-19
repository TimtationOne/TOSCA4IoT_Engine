package org.tosca4iot.webinterface;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tosca4iot.logic.RequestProcessor;


@RestController
public class RestEndpoint {
	@Autowired
	private RequestProcessor reqpro;
	private static final Logger logger = LoggerFactory.getLogger(RestEndpoint.class);
	
	
	@RequestMapping(value={"/execute"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	  public String executeBuildPlan(@RequestBody String body, HttpServletRequest req)
	    throws IOException
	  {
	    logger.debug("Recieved request with body" + body);
	    
	    reqpro.process(body);

	    return "Request Sent";
	  }


}
