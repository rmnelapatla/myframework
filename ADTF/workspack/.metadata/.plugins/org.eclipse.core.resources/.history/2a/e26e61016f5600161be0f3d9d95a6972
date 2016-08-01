package com.adaequare.testng.adtf.filters;

import java.io.File;
import java.io.FilenameFilter;

public class XMLFilter implements FilenameFilter {

	@Override
	public boolean accept(File dir, String name) {

		if (name.equalsIgnoreCase("Categories.xml")) {
			return false;
		}else if (name.toLowerCase().endsWith(".xml")) {
			return true;
		} else {
			return false;
		}
	}

}
