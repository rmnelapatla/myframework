package com.adaequare.testng.adtf.controller;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.openqa.selenium.remote.UnreachableBrowserException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;
import org.testng.TestNG;
import org.testng.xml.XmlSuite;
import org.uncommons.reportng.HTMLReporter;

import automation.scripts.ReportGenerator;

import com.adaequare.testng.adtf.parser.XMLParser;
import com.adaequare.testng.adtf.testng.beans.Configuration;
import com.adaequare.testng.adtf.testng.beans.FailedCases;
import com.adaequare.testng.adtf.testng.beans.Reports;
import com.adaequare.testng.adtf.testng.beans.TestSteps;
import com.adaequare.testng.adtf.testng.beans.TestSuite;
import com.adaequare.testng.adtf.util.AdtfDateUtil;

@Controller
public class TesterController {

	public Logger logger = Logger.getRootLogger();
	@Autowired
	XMLParser xmlParser;

	@RequestMapping(value = "/tester/home.htm", method = RequestMethod.GET)
	public String tester(ModelMap model, HttpServletRequest request) {

		return "tester-home";

	}

	@RequestMapping(value = "/tester/createObjectIdsPage.htm", method = RequestMethod.GET)
	public String createObjectIds(ModelMap model, HttpServletRequest request) {

		String realPath = request.getSession().getServletContext()
				.getRealPath("");

		List<String> projects = xmlParser.getProjects(realPath);
		// this has to be changed after assign project work is done.

		if (projects.isEmpty()) {
			model.put("msg", "Please create project before object creation  ");
			return "objectId-createpage";
		}
		String projectName = projects.toArray()[0].toString();
		// Set<String> modules = xmlParser.getCategories(realPath, projectName);
		// request.getSession().setAttribute("PROJECT_MODULES", modules);

		request.getSession().setAttribute("PROJECT_NAME", projectName);

		return "objectId-createpage";
	}

	@RequestMapping(value = "/tester/saveObjectIds.htm", method = RequestMethod.POST)
	public String saveObjectIds(ModelMap model, HttpServletRequest request) {

		// String module = "";//request.getParameter("project_module");
		//
		// if ((module == null) || (module.trim().length() < 1)) {
		// model.put("msg", " Please select module ");
		// return "objectId-createpage";
		// }

		String realPath = request.getSession().getServletContext()
				.getRealPath("");
		Map<String, String> objectMap = new LinkedHashMap<String, String>();

		Map<String, String[]> params = request.getParameterMap();

		String project_name = request.getParameter("project_name");

		String objName = "";
		String objId = "";

		for (int i = 1; i < params.size(); i++) {

			objName = request.getParameter("objname" + i);
			objId = request.getParameter("objid" + i);

			if ((objName != null) && (objId != null)) {

				if (!("".equals(objName)) && !("".equals(objId))) {
					objectMap.put(objName, objId);
				}
			}
		}

		if (objectMap.size() > 0) {
			// xmlParser.removeObjectIdNodes(realPath, project_name, objectMap);
			int flag = xmlParser.updateObjectReferecesFile(realPath,
					project_name, objectMap);

			if (flag < 0) {
				model.put("msg",
						"Object references not updated, try again later ");
			} else if (flag == 0) {
				model.put("msg", "Object references created successfully.");
			} else if (flag == 1) {
				model.put("msg",
						"Object references already exists, please verify ");
			}

		} else {
			model.put("msg", "Please enter object reference.");
		}

		return "objectId-createpage";
	}

	@RequestMapping(value = "/tester/createMessages.htm", method = RequestMethod.GET)
	public String createMessages(ModelMap model, HttpServletRequest request) {

		String realPath = request.getSession().getServletContext()
				.getRealPath("");

		List<String> projects = xmlParser.getProjects(realPath);
		if (projects.isEmpty()) {
			model.put("msg", "Please create project before message creation  ");
			return "message-createpage";
		}
		// this has to be changed after assign project work is done.
		String projectName = projects.toArray()[0].toString();
		// Set<String> modules = xmlParser.getCategories(realPath, projectName);
		// request.getSession().setAttribute("PROJECT_MODULES", modules);

		request.getSession().setAttribute("PROJECT_NAME", projectName);

		return "message-createpage";
	}

	@RequestMapping(value = "/tester/updateMessages.htm", method = RequestMethod.POST)
	public String updateMessages(ModelMap model, HttpServletRequest request) {

		String realPath = request.getSession().getServletContext()
				.getRealPath("");
		Map<String, String> messageMap = new LinkedHashMap<String, String>();

		Map<String, String[]> params = request.getParameterMap();

		String project_name = request.getParameter("project_name");

		String msgName = "";
		String msgId = "";

		for (int i = 1; i < params.size(); i++) {

			msgName = request.getParameter("msgname" + i);
			msgId = request.getParameter("msgid" + i);

			if ((msgName != null) && (msgId != null)) {

				if (!("".equals(msgName)) && !("".equals(msgId))) {
					messageMap.put(msgName, msgId);
				}
			}
		}
		if (messageMap.size() > 0) {
			// xmlParser.removeMessageNodes(realPath, project_name, messageMap);
			int flag = xmlParser.updateMessageReferenceFile(realPath,
					project_name, messageMap);

			if (flag < 0) {
				model.put("msg",
						"Message references not created, try again later ");
			} else if (flag == 0) {
				model.put("msg", "Messsage reference created successfully. ");
			} else if (flag == 1) {
				model.put("msg",
						"Message references already exists, please verify ");
			}
		} else {
			model.put("msg", "Please enter message reference.");
		}

		return "message-createpage";
	}

	@RequestMapping(value = "/tester/createTestSteps.htm", method = RequestMethod.GET)
	public String createTestSteps(ModelMap model, HttpServletRequest request) {

		String realPath = request.getSession().getServletContext()
				.getRealPath("");

		List<String> projects = xmlParser.getProjects(realPath);
		List<String> actionSet = new ArrayList<String>();
		Set<String> objNameSet = new HashSet<String>();
		Set<String> msgNameSet = new HashSet<String>();

		if (projects.isEmpty()) {
			model.put("msg", "There is no project assigned to you ");
			return "teststeps-createpage";
		}
		// this has to be changed after assign project module is done.
		String projectName = projects.toArray()[0].toString();

		actionSet = xmlParser.getActionList(realPath);
		objNameSet = xmlParser.getObjectReferences(realPath, projectName)
				.keySet();
		msgNameSet = xmlParser.getMessageInfo(realPath, projectName).keySet();

		request.getSession().setAttribute("ACTION_SET", actionSet);
		request.getSession().setAttribute("OBJ_NAMESET", objNameSet);
		request.getSession().setAttribute("MSG_NAMESET", msgNameSet);

		request.getSession().setAttribute("PROJECT_NAME", projectName);

		return "teststeps-createpage";
	}

	@RequestMapping(value = "/tester/createTestCases.htm", method = RequestMethod.GET)
	public String createTestCases(ModelMap model, HttpServletRequest request) {

		String realPath = request.getSession().getServletContext()
				.getRealPath("");

		List<String> projects = xmlParser.getProjects(realPath);

		if (projects.isEmpty()) {
			model.put("msg", "There is no project assigned to you ");
			return "testcase-createpage";
		}
		List<String> testStepsList = new ArrayList<String>();
		// this has to be changed after assign project module is done.
		String projectName = projects.toArray()[0].toString();

		testStepsList = xmlParser.getTestSteps(realPath, projectName);

		List<String> modules = xmlParser.getCategories(realPath, projectName);

		request.getSession().setAttribute("PROJECT_MODULES", modules);
		request.getSession().setAttribute("PROJECT_NAME", projectName);
		request.getSession().setAttribute("TESTSTEPS_LIST", testStepsList);
		request.setAttribute("SELECTED_MODULE", "");
		return "testcase-createpage";
	}

	@RequestMapping(value = "/tester/updateTestCases.htm", method = RequestMethod.POST)
	public String updateTestCases(ModelMap model, HttpServletRequest request) {

		String realPath = request.getSession().getServletContext()
				.getRealPath("");

		String moduleName = request.getParameter("project_module");

		String testcase_name = request.getParameter("testcase_name");

		String project_name = request.getParameter("project_name");

		List<String> modules = xmlParser.getCategories(realPath, project_name);

		List<String> testSteps = new ArrayList<String>();

		if ((moduleName == null) || (moduleName.trim().length() < 1)) {
			model.put("msg", " Please select module ");
			request.setAttribute("SELECTED_MODULE", "");
			return "testcase-createpage";
		} else {
			request.setAttribute("SELECTED_MODULE", moduleName);
		}

		List<String> testStepsList = xmlParser.getTestSteps(realPath,
				project_name);
		request.getSession().setAttribute("TESTSTEPS_LIST", testStepsList);

		if ((testcase_name == null) || (testcase_name.trim().length() < 1)) {
			model.put("msg", "Please enter test case name");
			return "testcase-createpage";
		}

		Pattern pattern = Pattern.compile("^[a-zA-Z0-9_]*$");
		Matcher matcher = pattern.matcher(testcase_name);

		if (!matcher.matches()) {
			model.put("msg", "Only alphanumeric, space and _ allowed");
			return "testcase-createpage";
		}
		testcase_name = testcase_name.toUpperCase().replaceAll("\\s+", "_");
		String tsteps = "";

		Map<String, String[]> params = request.getParameterMap();
		for (int i = 1; i < params.size(); i++) {

			tsteps = request.getParameter("tsteps" + i);

			if ((tsteps != null)) {

				if (!"".equals(tsteps)) {

					testSteps.add(tsteps);
				}
			}
		}
		request.getSession().setAttribute("PROJECT_MODULES", modules);
		request.getSession().setAttribute("PROJECT_NAME", project_name);
		request.getSession().setAttribute("TESTSTEPS_LIST", testStepsList);

		if (xmlParser.checkTestCaseName(realPath, project_name, testcase_name)) {
			model.put("msg", " Test case name already exists, please verify.");
			return "testcase-createpage";
		}

		int flag = xmlParser.updateTestCasesFiles(realPath, project_name,
				moduleName, testSteps, testcase_name);

		if (flag < 0) {
			model.put("msg", "TestCase not created, try again later ");
		} else if (flag == 0) {
			model.put("msg", "TestCase created successfully ");
		} else if (flag == 1) {
			model.put("msg", "TestCase already exists, please verify ");
		}

		return "testcase-createpage";
	}

	@RequestMapping(value = "/tester/updateTestSteps.htm", method = RequestMethod.POST)
	public String updateTestSteps(ModelMap model, HttpServletRequest request) {

		String teststep_name = request.getParameter("teststep_name");

		if ((teststep_name == null) || (teststep_name.trim().length() < 1)) {
			model.put("msg", "Please enter step name.");
			return "teststeps-createpage";
		}

		Pattern pattern = Pattern.compile("^[a-zA-Z0-9_]*$");
		Matcher matcher = pattern.matcher(teststep_name);

		if (!matcher.matches()) {
			model.put("msg", "Only alphanumeric, space and _ allowed");
			return "teststeps-createpage";
		}

		teststep_name = teststep_name.toUpperCase().replaceAll("\\s+", "_");

		String repeat_count = request.getParameter("repeat_count");

		if ((repeat_count == null) || (repeat_count.trim().length() < 1)) {
			model.put("msg", " Please enter repeat count value ");
			return "teststeps-createpage";
		}

		try {
			Integer.parseInt(repeat_count);
		} catch (NumberFormatException e) {
			model.put("msg", " Repeat count must be an integer");
			return "teststeps-createpage";
		}

		String realPath = request.getSession().getServletContext()
				.getRealPath("");

		List<TestSteps> testSteps = new ArrayList<TestSteps>();

		Map<String, String[]> params = request.getParameterMap();

		String project_name = request.getParameter("project_name");

		String compName = "";
		String action = "";
		String msg = "";

		TestSteps steps = null;

		for (int i = 1; i < params.size(); i++) {

			compName = request.getParameter("comp" + i);
			action = request.getParameter("action" + i);
			msg = request.getParameter("msg" + i);

			if ((compName != null) && (action != null) && (msg != null)) {

				if (!("".equals(compName)) && !("".equals(action))
						&& !("".equals(msg))) {
					steps = new TestSteps();
					steps.setActionName(action);
					steps.setComponentName(compName);
					steps.setMessageName(msg);
					testSteps.add(steps);
				}
			}
		}

		if (xmlParser.checkTestStepName(realPath, project_name, teststep_name)) {
			model.put("msg", " Test step name already exists, please verify.");
			return "teststeps-createpage";
		}

		int flag = xmlParser.updateTestSteps(realPath, project_name, testSteps,
				teststep_name, repeat_count);

		if (flag < 0) {
			model.put("msg", "Teststeps  not created, try again later ");
		} else if (flag == 0) {
			model.put("msg", "Teststeps created successfully. ");
		} else if (flag == 1) {
			model.put("msg", "Some Teststeps already exists, please verify ");
		}

		return "teststeps-createpage";
	}

	@RequestMapping(value = "/tester/createTestSuite.htm", method = RequestMethod.GET)
	public String createTestSuite(ModelMap model, HttpServletRequest request) {

		String realPath = request.getSession().getServletContext()
				.getRealPath("");
		List<String> projects = xmlParser.getProjects(realPath);

		if (projects.isEmpty()) {
			model.put("msg", "There is no project assigned to you  ");
			return "testsuite-createpage";
		}

		// this has to be changed after assign project work is done.
		String projectName = projects.toArray()[0].toString();
		Map<String, List<String>> modulesTestList = xmlParser
				.getCategoriesTestList(realPath, projectName);
		request.getSession().setAttribute("PROJECT_MODULES",
				modulesTestList.entrySet());
		request.getSession().setAttribute("PROJECT_NAME", projectName);
		request.getSession().setAttribute("PROJECT_LIST", projects);
		request.getSession().setAttribute("FAILED_FLAG", null);

		return "testsuite-createpage";
	}

	@RequestMapping(value = "/tester/updateTestSuite.htm", method = RequestMethod.POST)
	public String updateTestSuite(ModelMap model, HttpServletRequest request) {

		String realPath = request.getSession().getServletContext()
				.getRealPath("");
		List<String> projects = xmlParser.getProjects(realPath);

		String projectName = request.getParameter("project_name");

		if ((projectName == null) || (projectName.trim().length() < 1)) {
			model.put("msg", "Please select project ");
			return "testsuite-createpage";
		}
		String failedSuite = request.getParameter("failed_suite");

		String testsuite_name = request.getParameter("testsuite_name");

		if ((testsuite_name == null) || (testsuite_name.trim().length() < 1)) {
			model.put("msg", "Please enter suite name");
			return "testsuite-createpage";
		}

		Pattern pattern = Pattern.compile("^[a-zA-Z0-9_]*$");
		Matcher matcher = pattern.matcher(testsuite_name);

		if (!matcher.matches()) {
			model.put("msg", "Only alphanumeric, space and _ allowed");
			return "testsuite-createpage";
		}

		testsuite_name = testsuite_name.toUpperCase();

		Map<String, List<String>> modulesTestList = new LinkedHashMap<String, List<String>>();

		if ((failedSuite == null) || (failedSuite.trim().length() < 1)) {
			modulesTestList = xmlParser.getCategoriesTestList(realPath,
					projectName);

		} else {

			modulesTestList.put(failedSuite, new ArrayList<String>());
		}

		Set<Entry<String, List<String>>> moduleSet = modulesTestList.entrySet();
		List<TestSuite> testSuites = new ArrayList<TestSuite>();
		TestSuite suite = null;

		for (Entry<String, List<String>> entry : moduleSet) {
			String module = entry.getKey();
			String suite_url = module + "_url";
			String suite_sb = module + "_sb";
			String[] testCases = request.getParameterValues(suite_sb);

			if (testCases != null) {
				suite = new TestSuite();
				String url = request.getParameter(suite_url);
				suite.setTestCases(testCases);
				suite.setUrl(url);
				suite.setModule(module);
				testSuites.add(suite);
			}
		}

		request.getSession().setAttribute("PROJECT_MODULES",
				modulesTestList.entrySet());
		request.getSession().setAttribute("PROJECT_NAME", projectName);
		request.getSession().setAttribute("PROJECT_LIST", projects);

		if (xmlParser.checkSuiteName(realPath, projectName, testsuite_name)) {
			model.put("msg", " Test suite already exists, please verify.");
			return "testsuite-createpage";
		}
		if (!testSuites.isEmpty()) {

			int flag = xmlParser.updateTestSuiteInfo(realPath, projectName,
					testsuite_name, testSuites);

			if (flag < 0) {
				model.put("msg", " Test suite not created, try again later.");
			} else if (flag == 0) {
				model.put("msg", "	Test suite <" + testsuite_name
						+ "> created successfully.");
			} else if (flag == 1) {
				model.put("msg", " Test suite already exists, please verify. ");
			}
		} else {
			model.put("msg", "Select atleast one test case.");
		}
		return "testsuite-createpage";
	}

	@RequestMapping(value = "/tester/viewTestSuite.htm", method = RequestMethod.GET)
	public String viewTestSuite(ModelMap model, HttpServletRequest request) {

		String realPath = request.getSession().getServletContext()
				.getRealPath("");
		List<String> projects = xmlParser.getProjects(realPath);

		if (projects.isEmpty()) {
			model.put("msg", "There is no project assigned to you ");
			return "testsuite-viewpage";
		}


		String projectName = projects.toArray()[0].toString();

		Map<String, TestSuite> suiteList = xmlParser.getTestSuites(realPath,
				projectName);

		request.setAttribute("SUITE_LIST", suiteList);
		request.getSession().setAttribute("PROJECT_NAME", projectName);
		request.getSession().setAttribute("PROJECT_LIST", projects);

		return "testsuite-viewpage";
	}

	@RequestMapping(value = "/tester/editTestSuite.htm", method = RequestMethod.POST)
	public String editTestSuite(ModelMap model, HttpServletRequest request) {

		String realPath = request.getSession().getServletContext()
				.getRealPath("");
		List<String> projects = xmlParser.getProjects(realPath);

		if (projects.isEmpty()) {
			model.put("msg",
					"Please create project before configuration creation  ");
			return "testsuite-createpage";
		}
		String projectName = projects.toArray()[0].toString();
		String sel_suite = request.getParameter("suite_name");
		if (sel_suite != null & sel_suite.length() > 1) {
			int flag = xmlParser.removeTestSuite(realPath, projectName,
					sel_suite);

			if (flag == 0)
				model.put("msg", "Test suite <" + sel_suite
						+ "> removed successfully.");
			else
				model.put("msg", "Test suite <" + sel_suite
						+ "> not removed, try again later.");

		}

		Map<String, TestSuite> suiteList = xmlParser.getTestSuites(realPath,
				projectName);
		request.setAttribute("SUITE_LIST", suiteList);
		request.getSession().setAttribute("PROJECT_NAME", projectName);
		request.getSession().setAttribute("PROJECT_LIST", projects);

		return "testsuite-viewpage";
	}

	@RequestMapping(value = "/admin/createConfiguration.htm", method = RequestMethod.GET)
	public String createConfiguration(ModelMap model, HttpServletRequest request) {
		String realPath = request.getSession().getServletContext()
				.getRealPath("");

		List<String> projects = xmlParser.getProjects(realPath);

		if (projects.isEmpty()) {
			model.put("msg",
					"Please create project before configuration creation  ");
			return "testconfig-createpage";
		}

		Map<String, String> osVersions = new LinkedHashMap<String, String>();
		osVersions.put("WINDOWS_7", "Windows 7");
		osVersions.put("WINDOWS_XP", "Windows XP");
		osVersions.put("WINDOWS_VISTA", "Windows Vista");
		osVersions.put("LINUX", "Linux");
		osVersions.put("MAC_OS", "Mac Os");

		Map<String, String> browserTypes = new LinkedHashMap<String, String>();
		browserTypes.put("FIREFOX", "FireFox");
		browserTypes.put("IE", "Internet Explorer");
		browserTypes.put("CHROME", "Google Chrome");

		request.getSession().setAttribute("OPERATING_SYSTEMS", osVersions);
		request.getSession()
				.setAttribute(
						"CHROME_MSG",
						"Make sure chrome driver available at "
								+ System.getProperty("user.home")
								+ " if required download form http://code.google.com/p/chromedriver/downloads/list for  "
								+ System.getProperty("os.name"));
		request.getSession().setAttribute("BROWSER_TYPES", browserTypes);
		request.getSession().setAttribute("PROJECT_LIST", projects);
		request.getSession()
				.setAttribute("PROJECT_NAME", projects.toArray()[0]);

		return "testconfig-createpage";
	}

	@RequestMapping(value = "/admin/viewConfiguration.htm", method = RequestMethod.GET)
	public String viewConfigurations(ModelMap model, HttpServletRequest request) {
		String realPath = request.getSession().getServletContext()
				.getRealPath("");
		List<String> projects = xmlParser.getProjects(realPath);

		if (projects.isEmpty()) {
			model.put("msg",
					"Please create project");
			return "testconfig-viewconfig";
		} else {
			List<Configuration> configs = new ArrayList<Configuration>();
			String project = (String) projects.toArray()[0];
			if (project != null) {
				configs = xmlParser.getConfigurationsList(realPath, project);
				request.setAttribute("CONFIGLIST", configs);
			}

			Map<String, String> browserTypes = new LinkedHashMap<String, String>();
			browserTypes.put("FIREFOX", "FireFox");
			browserTypes.put("IE", "Internet Explorer");
			browserTypes.put("CHROME", "Google Chrome");

			request.setAttribute("BROWSERTYPES", browserTypes);

			request.setAttribute("PROJECTLIST", projects);
			request.setAttribute("PROJECTNAME", projects.toArray()[0]);
		}
		return "testconfig-viewconfig";
	}

	@RequestMapping(value = "/admin/editConfiguration.htm", method = RequestMethod.POST)
	public String editConfigurations(ModelMap model, HttpServletRequest request) {
		String realPath = request.getSession().getServletContext()
				.getRealPath("");
		List<String> projects = xmlParser.getProjects(realPath);
		List<Configuration> configs = new ArrayList<Configuration>();
		
		String project = request.getParameter("project_name");
		if ((project == null) || (project.trim().length() < 1)) {
			model.put("msg", "Please select project ");
			return "testconfig-viewconfig";
		}
		String configName = request.getParameter("config_name");
		if (configName != null && configName.length() > 1) {

			int flag = xmlParser.removeConfiguration(realPath, project,
					configName);
			if (flag == 0)
				model.put("msg", "Configuration of " + configName
						+ " is removed successfully");
			else
				model.put("msg", "Configuration of " + configName
						+ " is not removed, try again later");
		}

		if (project != null) {
			configs = xmlParser.getConfigurationsList(realPath, project);
		}

		Map<String, String> browserTypes = new LinkedHashMap<String, String>();
		browserTypes.put("FIREFOX", "FireFox");
		browserTypes.put("IE", "Internet Explorer");
		browserTypes.put("CHROME", "Google Chrome");

		request.setAttribute("BROWSERTYPES", browserTypes);
		request.setAttribute("CONFIGLIST", configs);
		request.setAttribute("PROJECTLIST", projects);
		request.setAttribute("PROJECTNAME", projects.toArray()[0]);

		return "testconfig-viewconfig";
	}

	@RequestMapping(value = "/admin/updateTestConfig.htm", method = RequestMethod.POST)
	public String updateTestConfig(ModelMap model, HttpServletRequest request) {
		String realPath = request.getSession().getServletContext()
				.getRealPath("");
		String project_name = request.getParameter("project_name");
		if ((project_name == null) || (project_name.trim().length() < 1)) {
			model.put("msg", " Please select project ");
			return "testconfig-createpage";
		}

		String config_name = request.getParameter("config_name");

		if ((config_name == null) || (config_name.trim().length() < 1)) {
			model.put("msg", " Please enter valid configuration name.");
			return "testconfig-createpage";
		}

		config_name = config_name.toUpperCase();
		config_name = config_name.replaceAll("\\.", "");

		String host_name = request.getParameter("host_name");
		if ((host_name == null) || (host_name.trim().length() < 1)) {
			model.put("msg", " Please select selenium server host name ");
			return "testconfig-createpage";
		}

		String browserType = request.getParameter("browserType");

		if ((browserType == null) || (browserType.trim().length() < 1)) {
			model.put("msg", " Please select browser type.");
			return "testconfig-createpage";
		}

		String port_num = request.getParameter("port_num");

		if ((port_num == null) || (port_num.trim().length() < 1)) {
			model.put("msg", " Please enter selenium server  port number ");
			return "testconfig-createpage";
		}

		Integer portNumber = 4444;
		try {
			portNumber = Integer.parseInt(port_num);
		} catch (Exception e) {

		}

		String osVersion = request.getParameter("osVersion");

		if ((osVersion == null) || (osVersion.trim().length() < 1)) {
			model.put("msg", " Please select Operating System  ");
			return "testconfig-createpage";
		}

		String bversion = request.getParameter("bversion");

		if ((bversion == null) || (bversion.trim().length() < 1)) {
			model.put("msg", "Please enter browser version.");
			return "testconfig-createpage";
		}

		if (xmlParser
				.isConfigurationExists(realPath, project_name, config_name)) {
			model.put("msg", "Test configuration already exists, please verify");
			return "testconfig-createpage";
		}

		int flag = xmlParser.updateTestConfiguration(realPath, project_name,
				config_name, host_name, browserType, bversion, osVersion,
				portNumber);
		if (flag < 0) {
			model.put("msg", "Test configuration not created, try again later");
		} else if (flag == 0) {
			model.put("msg", "Test configuration created successfully.");
		} else if (flag == 1) {
			model.put("msg",
					"Test configuration already exists, please verify ");
		}

		return "testconfig-createpage";
	}

	List<Reports> reports;
	Reports report;

	@RequestMapping(value = "/tester/viewReports.htm", method = RequestMethod.GET)
	public String viewReports(ModelMap model, HttpServletRequest request) {

		String realPath = request.getSession().getServletContext()
				.getRealPath("");

		List<String> projects = xmlParser.getProjects(realPath);

		User user = (User) SecurityContextHolder.getContext()
				.getAuthentication().getPrincipal();
		
		if (projects.isEmpty()) {
			model.put("msg",
					"Please create project before configuration creation  ");
			return "view-reports";
		}
		String projectName = projects.toArray()[0].toString();

		String logReport = request.getContextPath() + "/" + "data" + "/"
				+ "reports" + "/" + "log.html";

		request.getSession().setAttribute("FULL_LOG", logReport);
		

		Map<String, List<Reports>> reportsMap = xmlParser.getUserReports(
				request.getContextPath(), realPath, projectName,
				user.getUsername());

		request.getSession().setAttribute("TESNGREPORTS", reportsMap);

		return "view-reports";
	}

	@RequestMapping(value = "/tester/executeTestCases.htm", method = RequestMethod.GET)
	public String executeTestCases(ModelMap model, HttpServletRequest request) {

		String realPath = request.getSession().getServletContext()
				.getRealPath("");

		List<String> projects = xmlParser.getProjects(realPath);

		if (projects.isEmpty()) {
			model.put("msg", "There is no project assigned to you  ");
			return "execute-tests";
		}

		String projectName = projects.toArray()[0].toString();

		request.getSession().setAttribute("PROJECT_NAME", projectName);

		List<String> suiteNameList = new ArrayList<String>(xmlParser
				.getTestSuites(realPath, projectName).keySet());

		List<String> configNameList = new ArrayList<String>(xmlParser
				.getConfigurations(realPath, projectName).keySet());
		Collections.reverse(suiteNameList);

		Collections.reverse(configNameList);

		request.getSession().setAttribute("PROJECT_NAME", projectName);

		request.getSession().setAttribute("CONFIG_LIST", configNameList);
		request.getSession().setAttribute("SUITE_LIST", suiteNameList);

		
		

		return "execute-tests";
	}

	ITestResult iTestResult;
	List<String> reportList = new ArrayList<String>();

	@RequestMapping(value = "/tester/runExectution.htm", method = RequestMethod.POST)
	public void runExectution(ModelMap model, HttpServletRequest request,
			HttpServletResponse response) {
		String realPath = request.getSession().getServletContext()
				.getRealPath("");
		PrintWriter out = null;
		try {

			out = response.getWriter();

			StringBuffer errorMessage = new StringBuffer();
			String execution_type = request.getParameter("etype");
			if ((execution_type == null) || (execution_type == "")) {
				out.write("Please select excution type");
				return;
			}

			User user = (User) SecurityContextHolder.getContext()
					.getAuthentication().getPrincipal();
			List<String> projects = xmlParser.getProjects(realPath);

			String configs = request.getParameter("cname");
			String suites = request.getParameter("sname");

			String isParallel = "false";

			if ("P".equalsIgnoreCase(execution_type)) {
				isParallel = "tests";
				logger.info("Parallel execution started ");
			} else {
				logger.info("Sequential execution started ");
			}

			String projectName = projects.toArray()[0].toString();

			Map<String, TestSuite> testSuiteMap = xmlParser.getTestSuites(
					realPath, projectName);
			Map<String, Configuration> configMap = xmlParser.getConfigurations(
					realPath, projectName);
			List<XmlSuite> xmlSuiteList = new ArrayList<XmlSuite>();

			reportList.clear();
			String timeStamp = String.valueOf(System.currentTimeMillis());
			TestSuite testSuite = null;
			Configuration configuration = null;
			String suite[] = suites.split(",");
			String config[] = configs.split(",");
			for (int i = 0; i < suite.length; i++) {
				configuration = configMap.get(config[i]);
				testSuite = testSuiteMap.get(suite[i]);
				if ((configuration != null) && (testSuite != null)) {

					logger.info("selected choices :" + testSuite
							+ configuration);
					xmlParser.prepareTestNGSuite(user.getUsername(), realPath,
							projectName, testSuite, configuration, suite[i],
							config[i], xmlSuiteList, reportList, timeStamp);
				}
			}

			if (xmlSuiteList.isEmpty()) {
				out.write("	please select suite & configuration ");
				return;
			}

			logger.info(" started execution <" + xmlSuiteList.size()
					+ "> suite file(s) ");

			String outputDir = realPath + File.separator + "data"
					+ File.separator + "projects" + File.separator
					+ projectName + File.separator + "test-output"
					+ File.separator + user.getUsername() + File.separator
					+ timeStamp;

			File reportNG = new File(outputDir + File.separator + "html");
			if (!reportNG.exists()) {
				reportNG.mkdirs();
			}

			TestListenerAdapter testListenerAdapter = new TestListenerAdapter();
			ReportGenerator reportGenerator = new ReportGenerator();
			reportGenerator.setTimeStamp(timeStamp);// to capture Images
			reportGenerator.setContextPath(request.getContextPath());
			// reportGenerator.setContextPath(request.getScheme() + "://" +
			// request.getServerName() + ":" + request.getServerPort() +
			// request.getContextPath());
			HTMLReporter htmlReporter = new HTMLReporter();
			try {
				TestNG testng = new TestNG();
				testng.addListener(reportGenerator);
				testng.addListener(testListenerAdapter);

				testng.addListener(htmlReporter);

				testng.setUseDefaultListeners(false);
				testng.setParallel(isParallel);

				testng.setPreserveOrder(true);
				testng.setRandomizeSuites(false);
				testng.setDataProviderThreadCount(xmlSuiteList.size());

				if (!"false".equalsIgnoreCase(isParallel)) {
					logger.info("PARALLEL EXECUTION STARTED WITH THREAD COUNT "
							+ xmlSuiteList.size());
					testng.setThreadCount(xmlSuiteList.size());
					testng.setSuiteThreadPoolSize(5);

				}
				testng.setXmlSuites(xmlSuiteList);

				testng.setOutputDirectory(outputDir);

				testng.run();

			} catch (Exception e) {

				out.write((e.getMessage() == null) ? " unknown error occured. please verify logcat"
						: e.getMessage());
				for (StackTraceElement se : e.getStackTrace()) {
					logger.error("ClassName : " + se.getClassName());
					logger.error("FileName : " + se.getFileName());
					logger.error("LineNumber : " + se.getLineNumber());
					logger.error("MethodName : " + se.getMethodName());
				}

				return;
			}

			List<ITestResult> configFailsList = testListenerAdapter
					.getConfigurationFailures();

			if (!configFailsList.isEmpty()) {
				iTestResult = configFailsList.get(0);

				logger.error(iTestResult.getThrowable().getMessage());
				if (iTestResult.getThrowable() instanceof UnreachableBrowserException) {
					errorMessage
							.append("selenium server not started at host machine.\n");
					errorMessage.append(" error message:: "
							+ iTestResult.getThrowable().getCause()
									.getMessage());
					out.write(errorMessage.toString());
					return;
				} else {

					try {
						errorMessage.append("	error message:: "
								+ iTestResult.getThrowable().getMessage());
						out.write(errorMessage.toString());
						return;
					} catch (Exception e) {

					}
				}
			}
			errorMessage.append("### EXECUTION  FINISHED  ###");

			List<ITestResult> failedTestList = testListenerAdapter
					.getFailedTests();

			List<String> failedTestCaseList = new ArrayList<String>();

			for (Iterator<ITestResult> iterator = failedTestList.iterator(); iterator
					.hasNext();) {
				try {

					ITestResult iTestResult = (ITestResult) iterator.next();
					failedTestCaseList.add(String.valueOf(iTestResult
							.getParameters()[0]));

				} catch (Exception e) {
					logger.error(e.getMessage());
				}
			}

			if (!failedTestCaseList.isEmpty()) {
				Collections.sort(failedTestCaseList);
				xmlParser.createFailedTestCase(realPath, projectName,
						timeStamp, failedTestCaseList);

				errorMessage.append("  Total " + failedTestList.size()
						+ "Test Cases Failed.\n");
			}

			if (!testListenerAdapter.getSkippedTests().isEmpty()) {
				errorMessage.append("	Total "
						+ testListenerAdapter.getFailedTests().size()
						+ "Test Cases Skipped.\n");
			}

			logger.info(errorMessage.toString());

			out.write(errorMessage.toString());
			return;

		} catch (IOException ioException) {
			logger.error(ioException.getMessage());
		} finally {

			try {
				out.flush();
				out.close();
			} catch (Exception e) {

			}

		}

	}

	@RequestMapping(value = "/tester/viewXMLs.htm", method = RequestMethod.GET)
	public String viewXMLFiles(ModelMap model, HttpServletRequest request) {

		String realPath = request.getSession().getServletContext()
				.getRealPath("");
		List<String> prjfiles = new ArrayList<String>();
		List<String> projects = xmlParser.getProjects(realPath);

		if (projects.isEmpty()) {
			model.put("msg", "There is no project assigned to you ");
			return "viewxmls";
		}
		String projectName = projects.toArray()[0].toString();
		List<String> modules = xmlParser.getCategories(realPath, projectName);
		// String module = request.getParameter("project_module");
		prjfiles = xmlParser.getProjectFiles(realPath, projectName);

		request.setAttribute("PROJECT_FILES", prjfiles);
		request.setAttribute("PROJECT_MODULES", modules);
		request.getSession().setAttribute("PROJECT_NAME", projectName);
		request.setAttribute("SELECTED_MODULE", "");
		return "viewxmls";
	}

	@RequestMapping(value = "/tester/editXMLs.htm", method = RequestMethod.POST)
	public String editXMLFiles(ModelMap model, HttpServletRequest request) {
		boolean fdisplay = false;
		List<String> modfiles = new ArrayList<String>();
		List<String> prjfiles = new ArrayList<String>();
		String filedata = "";
		String sel_fil = "";
		String filePlace = "";
		String realPath = request.getSession().getServletContext()
				.getRealPath("");
		List<String> projects = xmlParser.getProjects(realPath);
		String projectName = projects.toArray()[0].toString();
		List<String> modules = xmlParser.getCategories(realPath, projectName);
		String module = request.getParameter("project_module");
		if ((module != null) && (module.trim().length() > 1)) {
			modfiles = xmlParser.getModuleFiles(realPath, projectName, module);
		}
		prjfiles = xmlParser.getProjectFiles(realPath, projectName);
		sel_fil = request.getParameter("fname");

		if ((sel_fil != null) && (sel_fil.trim().length() > 1)) {
			filePlace = request.getParameter("filePlace");
			if ((filePlace == null) || filePlace.length() < 1)
				filePlace = "";
			String action = request.getParameter("fileAct");
			if ((action != null) && action.equals("Save")) {
				String data = request.getParameter("fileArea");
				if (data != null) {
					int flag = xmlParser.setFileData(realPath, projectName,
							module, sel_fil, data, filePlace);
					if (flag == 0)
						model.put("msg", " File updated successfully ");
					if (flag == -1)
						model.put("msg", " File updation failed try again ");

				}
			}
			filedata = xmlParser.getFileData(realPath, projectName, module,
					sel_fil, filePlace);
			fdisplay = true;
		}
		request.setAttribute("SEL_FILE", filedata.trim());
		request.setAttribute("FILE_PLACE", filePlace);
		request.setAttribute("FileAreaDisplay", fdisplay);
		request.setAttribute("SelFileName", sel_fil);
		request.setAttribute("SELECTED_MODULE", module);
		request.setAttribute("PROJECT_FILES", prjfiles);
		request.setAttribute("MODULE_FILES", modfiles);
		request.setAttribute("PROJECT_MODULES", modules);
		request.getSession().setAttribute("PROJECT_NAME", projectName);
		return "viewxmls";
	}

	@RequestMapping(value = "/tester/screenshots.htm", method = RequestMethod.GET)
	public String viewErrorScreens(ModelMap model, HttpServletRequest request) {

		String timeStampFolder = request.getParameter("time");
		String testcase = request.getParameter("testcase");

		String realPath = request.getSession().getServletContext()
				.getRealPath("");
		List<String> projects = xmlParser.getProjects(realPath);
		String projectName = projects.toArray()[0].toString();

		String imgFolderPath = realPath + File.separator + "data"
				+ File.separator + "projects" + File.separator + projectName
				+ File.separator + "test-output" + File.separator + "images"
				+ File.separator + timeStampFolder + File.separator + testcase;

		File imageFolder = new File(imgFolderPath);

		String relativePath = request.getContextPath() + "/data" + "/projects/"
				+ projectName + "/test-output/images/" + timeStampFolder + "/"
				+ testcase;

		List<String> imgList = new ArrayList<String>();

		if (imageFolder.exists() && imageFolder.isDirectory()) {
			for (String errorImg : imageFolder.list()) {
				imgList.add(relativePath + "/" + errorImg);
			}
		}

		request.setAttribute("TESTCASE_NAME", testcase);

		if (!imgList.isEmpty()) {
			request.setAttribute("BUG_IMAGE_LIST", imgList);
		} else {
			request.setAttribute("MSG",
					"No screen shots available for this test case");
		}

		return "screen-shots";
	}

	@RequestMapping(value = "/tester/failedTestCases.htm", method = RequestMethod.GET)
	public String viewFailedTestCases(ModelMap model, HttpServletRequest request) {

		try {

			String realPath = request.getSession().getServletContext()
					.getRealPath("");

			List<String> projects = xmlParser.getProjects(realPath);

			String projectName = projects.toArray()[0].toString();
			String mainFolder = realPath + File.separator + "data"
					+ File.separator + "projects" + File.separator
					+ projectName + File.separator + "failed-tests";

			File failedFolder = new File(mainFolder);

			if (failedFolder.exists()) {
				List<FailedCases> failedCaseList = new ArrayList<FailedCases>();
				FailedCases failedCases = null;

				List<String> timeFolders = Arrays.asList(failedFolder.list());
				Collections.sort(timeFolders);
				Collections.reverse(timeFolders);

				for (String failedTime : timeFolders) {
					failedCases = new FailedCases();

					failedCases.setTestCaseTime(AdtfDateUtil
							.formatAsDate(failedTime));

					failedCases.setFailCount(xmlParser.getFailedTestCasesCount(
							realPath, projectName, failedTime));

					failedCases.setTimeStamp(failedTime);

					failedCaseList.add(failedCases);

				}

				request.getSession().setAttribute("FAILED_TESTCASES",
						failedCaseList);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return "failed-testcases";
	}

	@RequestMapping(value = "/tester/createFailedTestSuite.htm", method = RequestMethod.GET)
	public String createFailedTestSuite(ModelMap model,
			HttpServletRequest request) {

		String realPath = request.getSession().getServletContext()
				.getRealPath("");

		List<String> projects = xmlParser.getProjects(realPath);

		String projectName = projects.toArray()[0].toString();
		String time = request.getParameter("time");

		List<String> failedCaseNameList = xmlParser.getFailedTestCasesNames(
				realPath, projectName, time);

		Map<String, List<String>> modules = new LinkedHashMap<String, List<String>>();

		String failedFlag = "FAILED_" + AdtfDateUtil.randomString(time);
		modules.put(failedFlag, failedCaseNameList);

		request.getSession().setAttribute("PROJECT_MODULES", modules);
		request.getSession().setAttribute("PROJECT_NAME", projectName);
		request.getSession().setAttribute("PROJECT_LIST", projects);
		request.getSession().setAttribute("FAILED_FLAG", failedFlag);

		return "testsuite-createpage";
	}
}
