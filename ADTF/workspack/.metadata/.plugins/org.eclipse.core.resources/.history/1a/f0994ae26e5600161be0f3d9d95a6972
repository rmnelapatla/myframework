package com.adaequare.testng.adtf.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.adaequare.testng.adtf.exceptions.UserDataAlreadyExistsException;
import com.adaequare.testng.adtf.forms.UserForm;
import com.adaequare.testng.adtf.service.UserService;

@Controller
@SessionAttributes("adminInfoForm")
public class AdminPersonalInfoController {

	public Logger logger = Logger.getRootLogger();
	@Autowired
	UserService userService;

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	@RequestMapping(value = "/admin/updateInfo.htm", method = RequestMethod.GET)
	public String showAdminInfo(ModelMap model, HttpServletRequest request) {

		User u = (User) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		UserForm adminInfoForm = userService.getUser(u.getUsername());

		logger.info("show user form method");
		model.put("adminInfoForm", adminInfoForm);
		model.put("isUserEnabled", adminInfoForm.getEnabled());
		model.put("organizationMap", userService.getOrganizationsMap());
		request.getSession().setAttribute("UPDATE_USER_ROLE",
				adminInfoForm.getRoleName());

		Map<String, String> adminMap = new HashMap<String, String>();
		adminMap.put("ROLE_ADMIN", "ADMIN");
		model.put("roleMap", adminMap);
		return "update-admin";
	}

	@RequestMapping(value = "/admin/updateAdmin.htm", method = RequestMethod.POST)
	@SuppressWarnings("all")
	public String processAdminInfo(@Valid UserForm adminInfoForm,
			BindingResult result, Map model) {
		logger.info("processAdminInfo");
		// userForm = (UserForm) model.get("adminInfoForm");// not required

		if (result.hasErrors()) {
			return "update-admin";
		}

		try {
			userService.updateUser(adminInfoForm);
		} catch (UserDataAlreadyExistsException alreadyExistsException) {
			logger.info(alreadyExistsException.getMessage());

			result.addError(new ObjectError("", alreadyExistsException
					.getMessage()));
			return "update-admin";
		} catch (Exception e) {
			logger.info(e.getMessage());

			result.addError(new ObjectError("", e.getMessage()));
			return "update-admin";
		}
		// model.put("msg", "Personal Information Updated Successfully");
		return "redirect:/admin/updateInfo.htm";
	}

}
