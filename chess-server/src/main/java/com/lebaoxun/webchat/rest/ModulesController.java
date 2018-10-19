package com.lebaoxun.webchat.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ModulesController {
	
	private Logger logger = LoggerFactory.getLogger(getClass());

	@RequestMapping("/{url}.html")
	public String module(@PathVariable("url") String url){
		logger.debug("url={}",url);
		return "/"+url;
	}
}
