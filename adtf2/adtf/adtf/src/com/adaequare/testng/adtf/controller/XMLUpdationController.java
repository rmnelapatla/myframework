package com.adaequare.testng.adtf.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.adaequare.testng.adtf.parser.XMLParser;
import com.adaequare.testng.adtf.testng.beans.TestSteps;

@Controller
public class XMLUpdationController {
	public Logger logger = Logger.getRootLogger();
	@Autowired
	XMLParser xmlParser;

	@RequestMapping(value = "/tester/viewObjectIds.htm", method = RequestMethod.GET)
	public String viewObjectIds(ModelMap model, HttpServletRequest request) {

		String realPath = request.getSession().getServletContext()
				.getRealPath("");
		List<String> projects = xmlParser.getProjects(realPath);
		// this has to be changed after assign project work is done.
		String projectName = projects.toArray()[0].toString();
		List<String> modules = xmlParser.getCategories(realPath, projectName);
		request.getSession().setAttribute("PROJECT_MODULES", modules);
		request.getSession().setAttribute("PROJECT_NAME", projectName);
		request.getSession().setAttribute("moduleName", "");
		return "objectId-viewpage";
	}

	@RequestMapping(value = "/tester/editObjectIds.htm", method = RequestMethod.POST)
	public String editObjectIds(ModelMap model, HttpServletRequest request) {
		// Map<String, String> objectMap = new HashMap<String, String>();

		String realPath = request.getSession().getServletContext()
				.getRealPath("");
		List<String> projects = xmlParser.getProjects(realPath);
		Map<String, String> objIds = new HashMap<String, String>();
		// this has to be changed after assign project work is done.
		String projectName = projects.toArray()[0].toString();
		List<String> modules = xmlParser.getCategories(realPath, projectName);
		request.getSession().setAttribute("PROJECT_MODULES", modules);
		request.getSession().setAttribute("PROJECT_NAME", projectName);

		String module = request.getParameter("project_module");

		if ((module == null) || (module.trim().length() < 1)) {
			model.put("msg", " Please Select Module ");

		} else {

			String action = request.getParameter("setIds");
			if (action != null && action.equals("Update")) {

				try {
					if (xmlParser.deleteObjectReferencesFile(realPath)) {
						logger.info("Object Reference File Deleted Successfully for Project:"
								+ projectName + "; Module:" + module);
					}
					saveObjectIds(model, request);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			objIds = xmlParser.getObjectReferences(realPath, projectName);
		}
		// get the existing objects as array and pass
		request.getSession().setAttribute("vObjectIds", objIds);
		request.getSession().setAttribute("moduleName", module);
		return "objectId-viewpage";
	}

	public void saveObjectIds(ModelMap model, HttpServletRequest request) {

		String realPath = request.getSession().getServletContext()
				.getRealPath("");
		Map<String, String> objectMap = new HashMap<String, String>();

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

		// xmlParser.removeObjectIdNodes(realPath, project_name, objectMap);
		int flag = xmlParser.updateObjectReferecesFile(realPath, project_name,
				objectMap);

		if (flag < 0) {
			model.put("msg", "Object References Not Updated, Try Again Later ");
		} else if (flag == 0) {
			model.put("msg", "Object References Updated Successfully ");
		} else if (flag == 1) {
			model.put("msg",
					"Some Object References Already Exists, Please Verify ");
		}

	}

	@RequestMapping(value = "/tester/viewMessages.htm", method = RequestMethod.GET)
	public String viewMessages(ModelMap model, HttpServletRequest request) {

		String realPath = request.getSession().getServletContext()
				.getRealPath("");
		List<String> projects = xmlParser.getProjects(realPath);
		// this has to be changed after assign project work is done.
		String projectName = projects.toArray()[0].toString();
		List<String> modules = xmlParser.getCategories(realPath, projectName);
		request.getSession().setAttribute("PROJECT_MODULES", modules);
		request.getSession().setAttribute("PROJECT_NAME", projectName);
		request.getSession().setAttribute("moduleName", "");
		return "message-viewpage";
	}

	@RequestMapping(value = "/tester/editMessages.htm", method = RequestMethod.POST)
	public String editMessages(ModelMap model, HttpServletRequest request) {

		String realPath = request.getSession().getServletContext()
				.getRealPath("");
		Map<String, String> objIds = new HashMap<String, String>();
		List<String> projects = xmlParser.getProjects(realPath);
		// this has to be changed after assign project work is done.
		String projectName = projects.toArray()[0].toString();
		List<String> modules = xmlParser.getCategories(realPath, projectName);
		request.getSession().setAttribute("PROJECT_MODULES", modules);
		request.getSession().setAttribute("PROJECT_NAME", projectName);
		String module = request.getParameter("project_module");

		if ((module == null) || (module.trim().length() < 1)) {
			model.put("msg", " Please Select Module ");

		} else {

			String action = request.getParameter("getMsgs");
			if (action != null && action.equals("Update")) {
				String path = realPath + File.separator + "data"
						+ File.separator + "projects" + File.separator
						+ projectName + File.separator + "test-input"
						+ File.separator + module;
				try {
					if (xmlParser.deleteMessagesFile(path)) {
						logger.info("Messages File Deleted Successfully for Project:"
								+ projectName + "; Module:" + module);
					}
					xmlParser.createMessagesFile(path);
					updateMessages(model, request);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			objIds = xmlParser.getMessageInfo(realPath, projectName);
		}
		// get the existing objects as array and pass
		request.getSession().setAttribute("vMessages", objIds);
		request.getSession().setAttribute("moduleName", module);

		return "message-viewpage";
	}

	public void updateMessages(ModelMap model, HttpServletRequest request) {

		String realPath = request.getSession().getServletContext()
				.getRealPath("");
		Map<String, String> messageMap = new HashMap<String, String>();

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
		// xmlParser.removeMessageNodes(realPath, project_name, messageMap);
		int flag = xmlParser.updateMessageReferenceFile(realPath, project_name,
				messageMap);

		if (flag < 0) {
			model.put("msg", "Message References Not Updated, Try Again Later ");
		} else if (flag == 0) {
			model.put("msg", "Message References Updated Successfully ");
		} else if (flag == 1) {
			model.put("msg",
					"Some Message References Already Exists, Please Verify ");
		}
	}

	@RequestMapping(value = "/tester/viewTestSteps.htm", method = RequestMethod.GET)
	public String viewTestSteps(ModelMap model, HttpServletRequest request) {
		String realPath = request.getSession().getServletContext()
				.getRealPath("");
		List<String> projects = xmlParser.getProjects(realPath);
		String projectName = projects.toArray()[0].toString();
		List<String> modules = xmlParser.getCategories(realPath, projectName);
		request.getSession().setAttribute("PROJECT_MODULES", modules);
		request.getSession().setAttribute("PROJECT_NAME", projectName);
		request.getSession().setAttribute("moduleName", "");
		request.getSession().setAttribute("ACTION_SET", null);
		request.getSession().setAttribute("OBJ_NAMESET", null);
		request.getSession().setAttribute("MSG_NAMESET", null);
		request.getSession().setAttribute("TESTSTEPS_LIST", null);
		request.getSession().setAttribute("StepsData", null);
		request.getSession().setAttribute("selected_step", "");
		return "teststeps-viewpage";
	}

	@RequestMapping(value = "/tester/editTestSteps.htm", method = RequestMethod.POST)
	public String editTestSteps(ModelMap model, HttpServletRequest request) {
		String realPath = request.getSession().getServletContext()
				.getRealPath("");
		Map<String, Object> objData = null;
		List<String> projects = xmlParser.getProjects(realPath);
		// this has to be changed after assign project work is done.
		List<String> actionSet = new ArrayList<String>();
		Set<String> objNameSet = new HashSet<String>();
		Set<String> msgNameSet = new HashSet<String>();
		List<String> testSteps = new ArrayList<String>();
		String selected_step = "";
		String projectName = projects.toArray()[0].toString();
		List<String> modules = xmlParser.getCategories(realPath, projectName);
		String module_name = request.getParameter("project_module");

		if ((module_name == null) || (module_name.trim().length() < 1)) {
			model.put("msg", " Please Select Module ");

		} else {

			selected_step = request.getParameter("teststep_name");

			if ((module_name == null) || (module_name.trim().length() < 1)) {
				model.put("msg", " Please Select Module ");

			} else {

				actionSet = xmlParser.getActionList(realPath);
				objNameSet = xmlParser.getObjectReferences(realPath,
						projectName).keySet();
				msgNameSet = xmlParser.getMessageInfo(realPath, projectName)
						.keySet();
				testSteps = xmlParser.getTestSteps(realPath, projectName);

				if ((selected_step == null)
						|| (selected_step.trim().length() < 1)) {
					model.put("msg", " Please Select Step ");
				} else {
					String repeat_count = request.getParameter("repeat_count");
					if ((repeat_count == null)
							|| (repeat_count.trim().length() < 1)) {
						model.put("msg", "Please Enter Repeat Count Value ");

					} else {

						String action = request.getParameter("setSteps");
						if (action != null && action.equals("Update")) {
							xmlParser.removeTestStepNode(realPath, projectName,
									module_name, selected_step);
							String updation = request.getParameter("choice");
							if (updation.equals("none")) {
								updateTestSteps(model, request);
							}
						}
					}
					objData = xmlParser.getTestSteps(realPath, projectName,
							module_name, selected_step);
				}
			}
			// get steps to display of selected_step
		}
		request.getSession().setAttribute("StepsData", objData);
		request.getSession().setAttribute("selected_step", selected_step);
		request.getSession().setAttribute("moduleName", module_name);
		request.getSession().setAttribute("ACTION_SET", actionSet);
		request.getSession().setAttribute("OBJ_NAMESET", objNameSet);
		request.getSession().setAttribute("MSG_NAMESET", msgNameSet);
		request.getSession().setAttribute("TESTSTEPS_LIST", testSteps);
		request.getSession().setAttribute("PROJECT_MODULES", modules);
		request.getSession().setAttribute("PROJECT_NAME", projectName);

		return "teststeps-viewpage";
	}

	public void updateTestSteps(ModelMap model, HttpServletRequest request) {

		String teststep_name = request.getParameter("teststep_name");

		if ((teststep_name == null) || (teststep_name.trim().length() < 1)) {
			model.put("msg", " Please Enter TestStep Name ");
		}

		Pattern pattern = Pattern.compile("^[a-zA-Z0-9_]*$");
		Matcher matcher = pattern.matcher(teststep_name);

		if (!matcher.matches()) {
			model.put("msg",
					"Please Enter Valid Suite Name. only Alphanumerics & _ allowed ");
		}

		teststep_name = teststep_name.toUpperCase();

		String repeat_count = request.getParameter("repeat_count");

		if ((repeat_count == null) || (repeat_count.trim().length() < 1)) {
			model.put("msg", " Please Enter Repeat Count Value ");
		}

		try {
			Integer.parseInt(repeat_count);
		} catch (NumberFormatException e) {
			model.put("msg", " Repeat Count must be an integer");
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

		int flag = xmlParser.updateTestSteps(realPath, project_name, testSteps,
				teststep_name, repeat_count);

		if (flag < 0) {
			model.put("msg", "TestSteps  Not Updated, Try Again Later ");
		} else if (flag == 0) {
			model.put("msg", "TestSteps References Updated Successfully ");
		} else if (flag == 1) {
			model.put("msg",
					"Some TestSteps References Already Exists, Please Verify ");
		}

	}

	@RequestMapping(value = "/tester/viewTestCases.htm", method = RequestMethod.GET)
	public String viewTestCases(ModelMap model, HttpServletRequest request) {

		String realPath = request.getSession().getServletContext()
				.getRealPath("");
		List<String> projects = xmlParser.getProjects(realPath);
		// this has to be changed after assign project module is done.
		String projectName = projects.toArray()[0].toString();
		List<String> modules = xmlParser.getCategories(realPath, projectName);
		request.getSession().setAttribute("PROJECT_MODULES", modules);
		request.getSession().setAttribute("PROJECT_NAME", projectName);
		request.getSession().setAttribute("SELECTED_MODULE", "");
		request.getSession().setAttribute("SelTestCase", "");
		request.getSession().setAttribute("SelCaseSteps", null);
		return "testcase-viewpage";
	}

	@RequestMapping(value = "/tester/editTestCases.htm", method = RequestMethod.POST)
	public String editTestCases(ModelMap model, HttpServletRequest request) {

		String realPath = request.getSession().getServletContext()
				.getRealPath("");
		String project_name = request.getParameter("project_name");
		String module = request.getParameter("project_module");
		List<String> modules = xmlParser.getCategories(realPath, project_name);
		List<String> testStepsList = new ArrayList<String>();

		String testcase_name = request.getParameter("testcase_name");
		List<String> testCases = new ArrayList<String>();
		List<String> selCaseSteps = new ArrayList<String>();
		if ((module == null) || (module.trim().length() < 1)) {
			model.put("msg", " Please Select Module ");

		} else {
			testStepsList = xmlParser.getTestSteps(realPath, project_name);
			if ((testcase_name == null) || (testcase_name.trim().length() < 1)) {
				model.put("msg", " Please Select TestCase Name ");

			} else {

				String action = request.getParameter("setCases");
				if (action != null && action.equals("Update")) {
					if (request.getParameter("scount") != null) {
						int scount = Integer.parseInt(request
								.getParameter("scount"));
						for (int i = 1; i < scount; i++) {
							selCaseSteps
									.add(request.getParameter("tsteps" + i));

						}
						int flag = xmlParser.updateTestCasesFiles(realPath,
								project_name, module, selCaseSteps,
								testcase_name);
						if (flag < 0) {
							model.put("msg",
									"Object References Not Updated, Try Again Later ");
						} else if (flag == 0) {
							model.put("msg",
									"Object References Updated Successfully ");
						} else if (flag == 1) {
							model.put("msg",
									"Some Object References Already Exists, Please Verify ");
						}

					}
					// updateTestCases(model, request);
					// add updated one.
				}

				selCaseSteps = xmlParser.getSelCaseSteps(realPath,
						project_name, module, testcase_name);

			}
			testCases = xmlParser.getTestCases(realPath, project_name, module);
		}

		request.getSession().setAttribute("SelCaseSteps", selCaseSteps);
		request.getSession().setAttribute("SelTestCase", testcase_name);
		request.getSession().setAttribute("TESTCASES", testCases);
		request.getSession().setAttribute("SELECTED_MODULE", module);
		request.getSession().setAttribute("PROJECT_MODULES", modules);
		request.getSession().setAttribute("PROJECT_NAME", project_name);
		request.getSession().setAttribute("TESTSTEPS_LIST", testStepsList);

		return "testcase-viewpage";
	}

	@RequestMapping(value = "/tester/arTestCase.htm", method = RequestMethod.POST)
	public String addRemoveTestCases(ModelMap model, HttpServletRequest request) {

		String realPath = request.getSession().getServletContext()
				.getRealPath("");

		String module = request.getParameter("project_module");

		String testcase_name = request.getParameter("testcase_name");

		String project_name = request.getParameter("project_name");

		List<String> modules = xmlParser.getCategories(realPath, project_name);
		List<String> testCases = new ArrayList<String>();
		List<String> testSteps = new ArrayList<String>();

		if ((module == null) || (module.trim().length() < 1)) {
			model.put("msg", " Please Select Module ");

		}
		if ((testcase_name == null) || (testcase_name.trim().length() < 1)) {
			model.put("msg", " Please Enter TestCase Name ");
		}

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

		// get choice
		String choice = request.getParameter("choice");
		int position = Integer.parseInt(request.getParameter("position"));
		if (position > 0) {
			if (choice.equals("add")) {
				testSteps.add(position - 1, "");
			}
			if (choice.equals("remove")) {
				testSteps.remove(position - 1);
			}
		}

		testCases = xmlParser.getTestCases(realPath, project_name, module);
		List<String> testStepsList = xmlParser.getTestSteps(realPath,
				project_name);
		request.getSession().setAttribute("SelCaseSteps", testSteps);
		request.getSession().setAttribute("SelTestCase", testcase_name);
		request.getSession().setAttribute("TESTCASES", testCases);
		request.getSession().setAttribute("SELECTED_MODULE", module);
		request.getSession().setAttribute("PROJECT_MODULES", modules);
		request.getSession().setAttribute("PROJECT_NAME", project_name);
		request.getSession().setAttribute("TESTSTEPS_LIST", testStepsList);

		return "testcase-viewpage";

	}

	@RequestMapping(value = "/tester/arTestSteps.htm", method = RequestMethod.POST)
	public String addRemoveTestSteps(ModelMap model, HttpServletRequest request) {

		String realPath = request.getSession().getServletContext()
				.getRealPath("");
		Map<String, Object> objData = new HashMap<String, Object>();
		List<String> projects = xmlParser.getProjects(realPath);

		List<String> actionSet = new ArrayList<String>();
		Set<String> objNameSet = new HashSet<String>();
		Set<String> msgNameSet = new HashSet<String>();
		List<String> testSteps = new ArrayList<String>();
		List<TestSteps> testStepsData = new ArrayList<TestSteps>();
		String selected_step = "";
		String projectName = projects.toArray()[0].toString();
		List<String> modules = xmlParser.getCategories(realPath, projectName);

		String module_name = request.getParameter("project_module");

		if ((module_name == null) || (module_name.trim().length() < 1)) {
			model.put("msg", " Please Select Module ");

		} else {

			selected_step = request.getParameter("teststep_name");

			if ((module_name == null) || (module_name.trim().length() < 1)) {
				model.put("msg", " Please Select Module ");

			} else {

				actionSet = xmlParser.getActionList(realPath);
				objNameSet = xmlParser.getObjectReferences(realPath,
						projectName).keySet();
				msgNameSet = xmlParser.getMessageInfo(realPath, projectName)
						.keySet();
				testSteps = xmlParser.getTestSteps(realPath, projectName);

				if ((selected_step == null)
						|| (selected_step.trim().length() < 1)) {
					model.put("msg", " Please Select Step ");
				} else {
					String repeat_count = request.getParameter("repeat_count");
					if ((repeat_count == null)
							|| (repeat_count.trim().length() < 1)) {
						model.put("msg", "Please Enter Repeat Count Value ");

					} else {

						Map<String, String[]> params = request
								.getParameterMap();

						String compName = "";
						String action = "";
						String msg = "";

						TestSteps steps = null;

						for (int i = 1; i < params.size(); i++) {

							compName = request.getParameter("comp" + i);
							action = request.getParameter("action" + i);
							msg = request.getParameter("msg" + i);

							if ((compName != null) && (action != null)
									&& (msg != null)) {

								if (!("".equals(compName))
										&& !("".equals(action))
										&& !("".equals(msg))) {
									steps = new TestSteps();
									steps.setActionName(action);
									steps.setComponentName(compName);
									steps.setMessageName(msg);
									testStepsData.add(steps);
								}
							}
						}

						String choice = request.getParameter("choice");
						int position = Integer.parseInt(request
								.getParameter("position"));
						if (position > 0) {
							if (choice.equals("add")) {
								testStepsData
										.add(position - 1, new TestSteps());
							}
							if (choice.equals("remove")) {
								testStepsData.remove(position - 1);
							}
						}

					}
					objData.put("count", repeat_count);
					objData.put("name", selected_step);
					objData.put("data", testStepsData);

				}
			}
			// get steps to display of selected_step
		}
		request.getSession().setAttribute("StepsData", objData);
		request.getSession().setAttribute("selected_step", selected_step);
		request.getSession().setAttribute("moduleName", module_name);
		request.getSession().setAttribute("ACTION_SET", actionSet);
		request.getSession().setAttribute("OBJ_NAMESET", objNameSet);
		request.getSession().setAttribute("MSG_NAMESET", msgNameSet);
		request.getSession().setAttribute("TESTSTEPS_LIST", testSteps);
		request.getSession().setAttribute("PROJECT_MODULES", modules);
		request.getSession().setAttribute("PROJECT_NAME", projectName);

		return "teststeps-viewpage";

	}
}
