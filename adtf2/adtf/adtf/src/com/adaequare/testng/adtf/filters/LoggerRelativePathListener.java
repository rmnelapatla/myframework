package com.adaequare.testng.adtf.filters;

import java.io.File;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class LoggerRelativePathListener implements ServletContextListener {

	@Override
	public void contextDestroyed(ServletContextEvent event) {
	}

	@Override
	public void contextInitialized(ServletContextEvent event) {

		ServletContext context = event.getServletContext();
		System.setProperty("LOG_FILE_PATH", context.getRealPath("")
				+ File.separator + "data" + File.separator + "reports"
				+ File.separator + "log.html");
		System.out.println("Log4j path :" + context.getRealPath("")
				+ File.separator + "data" + File.separator + "reports"
				+ File.separator + "log.html");
	}

}
