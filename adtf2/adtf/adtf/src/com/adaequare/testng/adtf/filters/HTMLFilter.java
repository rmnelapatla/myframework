package com.adaequare.testng.adtf.filters;

import java.io.File;
import java.io.FilenameFilter;

public class HTMLFilter implements FilenameFilter {

	@Override
	public boolean accept(File dir, String name) {

		if (name.equalsIgnoreCase("emailable-report.html")) {
			return false;
		} else if (name.equalsIgnoreCase("index.html")) {
			return false;
		} else if (name.toLowerCase().endsWith(".html")) {
			return true;
		} else {
			return false;
		}
	}

}
