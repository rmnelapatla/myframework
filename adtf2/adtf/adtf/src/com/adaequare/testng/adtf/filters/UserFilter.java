package com.adaequare.testng.adtf.filters;

import java.io.File;
import java.io.FilenameFilter;

public class UserFilter implements FilenameFilter {

	@Override
	public boolean accept(File dir, String name) {
		if (name.equalsIgnoreCase("images")) {
			return false;
		} else 	return true;
		
	}

}
