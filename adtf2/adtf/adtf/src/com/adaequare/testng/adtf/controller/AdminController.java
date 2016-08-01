package com.adaequare.testng.adtf.controller;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.adaequare.testng.adtf.exceptions.UserDataAlreadyExistsException;
import com.adaequare.testng.adtf.forms.UserForm;
import com.adaequare.testng.adtf.hbm.UserVO;
import com.adaequare.testng.adtf.parser.XMLParser;
import com.adaequare.testng.adtf.service.UserService;
import com.adaequare.testng.adtf.testng.beans.Reports;

@Controller
public class AdminController {

	public Logger logger = Logger.getRootLogger();

	@Autowired
	UserService userService;

	@Autowired
	XMLParser xmlParser;

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	@RequestMapping(value = "/admin/createModules.htm", method = RequestMethod.GET)
	public String createModules(ModelMap model, HttpServletRequest request,
			HttpServletResponse response) {

		String realPath = request.getSession().getServletContext()
				.getRealPath("");
		List<String> projects = xmlParser.getProjects(realPath);

		if (projects.isEmpty()) {
			model.put("msg", "Please create project before module creation  ");
		}
		request.getSession().setAttribute("PROJECTS_LIST", projects);
		return "create-modules";
	}

	@RequestMapping(value = "/admin/createProject.htm", method = RequestMethod.GET)
	public String createProject(ModelMap model, HttpServletRequest request,
			HttpServletResponse response) {
		String realPath = request.getSession().getServletContext()
				.getRealPath("");

		List<String> projects = xmlParser.getProjects(realPath);

		if (!projects.isEmpty()) {
			model.put("msg",
					"Please remove existing project before creating new project.");
		}

		request.getSession().setAttribute("PROJECTS_LIST", projects);
		return "create-projects";
	}

	@RequestMapping(value = "/admin/addProject.htm", method = RequestMethod.POST)
	public String addProject(ModelMap model, HttpServletRequest request) {

		String projectName = request.getParameter("pname");

		if ((projectName == null) || (projectName.trim().length() < 3)) {
			model.put("msg", " Enter project name  ");
			return "create-projects";
		}

		Pattern pattern = Pattern.compile("^[a-zA-Z0-9_]*$");
		Matcher matcher = pattern.matcher(projectName);

		if (!matcher.matches()) {
			model.put("msg", "Only alphanumeric, space and _ allowed.");
			return "create-projects";
		}

		projectName = projectName.toUpperCase().replaceAll("\\s+", "_");

		String realPath = request.getSession().getServletContext()
				.getRealPath("");

		List<String> projects = xmlParser.getProjects(realPath);

		if (projects.isEmpty()) {

			int flag = xmlParser.createProjects(realPath, projectName);
			if (flag < 0) {
				model.put("msg", "Project is not created, try again later ");
			} else if (flag == 0) {
				model.put("msg", "Project created successfully ");
			} else if (flag == 1) {
				model.put("msg", "Project already exists, please verify ");
			}

		} else {
			model.put("msg",
					"Please remove existing project before creating new project.");
		}

		return "create-projects";
	}

	@RequestMapping(value = "/admin/addCategory.htm", method = RequestMethod.POST)
	public String addModule(ModelMap model, HttpServletRequest request) {

		String projectName = request.getParameter("pname");
		String module = request.getParameter("pmodule");

		if ((projectName == null) || (projectName.trim().length() < 1)) {
			model.put("msg", " Please select project ");
			return "create-modules";
		}

		if ((module == null) || (module.trim().length() <= 3)) {
			model.put("msg", "Please enter project module name.");
			return "create-modules";
		}

		Pattern pattern = Pattern.compile("^[a-zA-Z0-9_]*$");
		Matcher matcher = pattern.matcher(module);

		if (!matcher.matches()) {
			model.put("msg", "Only alphanumeric, space and _ allowed");
			return "create-modules";
		}
		module = module.toUpperCase().replaceAll("\\s+", "_");

		String realPath = request.getSession().getServletContext()
				.getRealPath("");

		System.out.println("" + realPath);

		int flag = xmlParser.createModule(realPath, projectName, module);
		if (flag < 0) {
			model.put("msg", "Module not created, try again later ");
		} else if (flag == 0) {
			model.put("msg", "Module created successfully. ");
		} else if (flag == 1) {
			model.put("msg", "Module already exists, please verify ");
		}
		return "create-modules";
	}

	@RequestMapping(value = "/admin/showAllUsers.htm", method = RequestMethod.GET)
	public String displayUsers(ModelMap model, HttpServletRequest request,
			HttpServletResponse response) {

		logger.info("show user form method");

		List<UserVO> users = userService.listAllUsers();

		model.put("userlist", users);
		request.getSession().setAttribute("organizationMap",
				userService.getOrganizationsMap());
		request.getSession().setAttribute("roleMap", userService.getRolesMap());

		return "user-mgmt";
	}

	@RequestMapping(value = "/admin/createUser.htm", method = RequestMethod.GET)
	public String showUserForm(ModelMap model, HttpServletRequest request) {

		logger.info("show user form method");

		model.put("userForm", new UserForm());
		request.getSession().setAttribute("organizationMap",
				userService.getOrganizationsMap());
		request.getSession().setAttribute("roleMap", userService.getRolesMap());

		return "create-user";
	}

	@RequestMapping(value = "/admin/saveUser.htm", method = RequestMethod.POST)
	@SuppressWarnings("all")
	public String processUserForm(@Valid UserForm userForm,
			BindingResult result, Map model) {

		userForm = (UserForm) model.get("userForm");// not required

		if (result.hasErrors()) {
			return "create-user";
		}

		try {
			userService.addUser(userForm);
		} catch (UserDataAlreadyExistsException alreadyExistsException) {
			logger.info(alreadyExistsException.getMessage());

			result.addError(new ObjectError("", alreadyExistsException
					.getMessage()));
			return "create-user";
		}
		model.put("msg", "User created successfully");

		return "create-user";
	}

	@RequestMapping(value = "/admin/viewProjects.htm", method = RequestMethod.GET)
	public String viewProjects(ModelMap model, HttpServletRequest request,
			HttpServletResponse response) {
		String realPath = request.getSession().getServletContext()
				.getRealPath("");
		List<String> projectList = xmlParser.getProjects(realPath);

		request.getSession().setAttribute("PROJECTS_LIST", projectList);
		return "view-projects";
	}

	@RequestMapping(value = "/admin/editProjects.htm", method = RequestMethod.POST)
	public String editProjects(ModelMap model, HttpServletRequest request,
			HttpServletResponse response) {
		String realPath = request.getSession().getServletContext()
				.getRealPath("");
		String action = request.getParameter("projectAction");
		if (action != null && action.equals("Delete")) {
			String project = request.getParameter("prjname");
			if (project != null && project.length() > 1) {
				int flag = xmlParser.removeProject(realPath, project);
				if (flag == 0)
					model.put("msg", " Project removed successfully. ");
				else
					model.put("msg",
							" Project is not removed, try again Later.");
			}
		}

		List<String> projectList = xmlParser.getProjects(realPath);
		request.getSession().setAttribute("PROJECTS_LIST", projectList);
		return "view-projects";
	}

	@RequestMapping(value = "/admin/updateUserInfo.htm", method = RequestMethod.GET)
	public String updateUserInfo(
			@RequestParam(value = "uid", required = true) String uid,
			ModelMap model, HttpServletRequest request) {

		logger.error("username : " + uid);

		UserForm userInfoForm = userService.getUser(uid);

		logger.info("update user form method ");
		model.put("userInfoForm", userInfoForm);
		model.put("isUserEnabled", userInfoForm.getEnabled());
		request.getSession().setAttribute("UPDATE_USER_ROLE",
				userInfoForm.getRoleName());
		request.getSession().setAttribute("organizationMap",
				userService.getOrganizationsMap());
		Map<String, String> userRoleMap = userService.getRolesMap();
		userRoleMap.remove("ROLE_ADMIN");// hide admin role
		request.getSession().setAttribute("roleMap", userRoleMap);

		return "update_userdata";
	}

	@RequestMapping(value = "/admin/processUserData.htm", method = RequestMethod.POST)
	@SuppressWarnings("all")
	public String processUserInformation(@Valid UserForm userInfoForm,
			BindingResult result, Map model) {

		// userInfoForm = (UserForm) model.get("userInfoForm");// not required

		if (result.hasErrors()) {
			return "create-user";
		}

		try {
			userService.updateUser(userInfoForm);

		} catch (UserDataAlreadyExistsException alreadyExistsException) {
			logger.info(alreadyExistsException.getMessage());

			result.addError(new ObjectError("", alreadyExistsException
					.getMessage()));
			return "create-user";
		}

		return "redirect:/admin/showAllUsers.htm";
	}

	@RequestMapping(value = "/admin/viewModules.htm", method = RequestMethod.GET)
	public String viewModules(ModelMap model, HttpServletRequest request,
			HttpServletResponse response) {

		String realPath = request.getSession().getServletContext()
				.getRealPath("");
		List<String> projects = xmlParser.getProjects(realPath);

		if (projects.isEmpty()) {
			model.put("msg", "Please create project before module creation  ");
			return "view-modules";
		}

		String projectName = projects.toArray()[0].toString();
		request.setAttribute("SEL_PROJECT", projectName);

		List<String> modules = xmlParser.getCategories(realPath, projectName);
		request.setAttribute("MODULES_LIST", modules);

		request.getSession().setAttribute("PROJECTS_LIST", projects);
		return "view-modules";
	}

	@RequestMapping(value = "/admin/modulesList.htm", method = RequestMethod.POST)
	public String editModules(ModelMap model, HttpServletRequest request,
			HttpServletResponse response) {

		String realPath = request.getSession().getServletContext()
				.getRealPath("");
		List<String> projects = xmlParser.getProjects(realPath);
		request.getSession().setAttribute("PROJECTS_LIST", projects);
		String projectName = request.getParameter("pname");

		if ((projectName == null) || (projectName.trim().length() < 1)) {
			model.put("msg", " Please select project ");
			return "view-modules";
		}
		String action = request.getParameter("moduleAction");
		if (action != null && action.equals("Delete")) {
			String modName = request.getParameter("modulename");
			if (modName != null && modName.length() > 1) {
				int flag = xmlParser.removeModule(realPath, projectName,
						modName);
				if (flag == 0)
					model.put("msg", " Module removed successfully. ");
				else
					model.put("msg", " Module is not removed, try again Later.");
			}
		}

		request.setAttribute("SEL_PROJECT", projectName);

		List<String> modules = xmlParser.getCategories(realPath, projectName);
		request.setAttribute("MODULES_LIST", modules);
		request.setAttribute("LISTSIZE", modules.size());

		request.getSession().setAttribute("PROJECTS_LIST", projects);
		return "view-modules";
	}

	@RequestMapping(value = "/admin/viewAllReports.htm", method = RequestMethod.GET)
	public String viewAllReports(ModelMap model, HttpServletRequest request,
			HttpServletResponse response) {

		String realPath = request.getSession().getServletContext()
				.getRealPath("");
		List<String> projects = xmlParser.getProjects(realPath);
		if (projects.isEmpty()) {
			model.put("msg", "Please create project");
			return "view-allReports";
		}

		String projectName = projects.toArray()[0].toString();
		List<String> userList = xmlParser.getReportUsers(realPath, projectName);

		request.setAttribute("USERS_LIST", userList);
		return "view-allReports";
	}

	@RequestMapping(value = "/admin/detailReports.htm", method = RequestMethod.POST)
	public String reportDetails(ModelMap model, HttpServletRequest request,
			HttpServletResponse response) {
		try {

			String realPath = request.getSession().getServletContext()
					.getRealPath("");

			List<String> projects = xmlParser.getProjects(realPath);

			if (projects.isEmpty()) {
				model.put("msg",
						"Please create project before configuration creation  ");
				return "view-detailReports";
			}
			String userName = request.getParameter("username");

			String projectName = projects.toArray()[0].toString();

			Map<String, List<Reports>> reportsMap = xmlParser.getUserReports(
					request.getContextPath(),realPath, projectName, userName);

			request.setAttribute("TESTNGREPORTS", reportsMap);
			request.setAttribute("USERNAME", userName);

		} catch (Exception e) {
			logger.error(e.getMessage());
		}

		return "view-detailReports";
	}

	@RequestMapping(value = "/admin/manageReports.htm", method = RequestMethod.GET)
	public String manageReports(ModelMap model, HttpServletRequest request,
			HttpServletResponse response) {
		String realPath = request.getSession().getServletContext()
				.getRealPath("");
		List<String> projects = xmlParser.getProjects(realPath);

		if (projects.isEmpty()) {
			model.put("msg", "Please create project");
		}
		request.getSession().setAttribute("PROJECTS_LIST", projects);

		return "manage-Reports";
	}

	@RequestMapping(value = "/admin/manageReportsByDate.htm", method = RequestMethod.POST)
	public String manageReportsByDate(ModelMap model,
			HttpServletRequest request, HttpServletResponse response) {
		String realPath = request.getSession().getServletContext()
				.getRealPath("");

		List<String> projects = xmlParser.getProjects(realPath);
		request.getSession().setAttribute("PROJECTS_LIST", projects);

		String projectName = request.getParameter("pname");

		if ((projectName == null) || (projectName.trim().length() < 1)) {
			model.put("msg", " Please select project ");
			return "manage-Reports";
		}

		String str = request.getParameter("deleteUpto");
		if (str != null && str.equals("Now")) {
			if (xmlParser.clearAllReports(realPath, projectName)) {
				model.put("msg", " All reports removed ");
			} else {
				model.put("msg", " Reports are not removed , Try again later");
			}
		}
		return "manage-Reports";
	}

}
