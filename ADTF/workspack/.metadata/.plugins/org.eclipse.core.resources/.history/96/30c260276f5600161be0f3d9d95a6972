package com.adaequare.testng.adtf.processor;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.adaequare.testng.adtf.exceptions.NoSuchUserExistsException;
import com.adaequare.testng.adtf.forms.UserForm;
import com.adaequare.testng.adtf.hbm.OrganizationMapper;
import com.adaequare.testng.adtf.hbm.RoleMapper;
import com.adaequare.testng.adtf.service.UserService;

public class ApplicationProcessor implements ApplicationContextAware {

	public Logger logger = Logger.getRootLogger();

	@Override
	public void setApplicationContext(ApplicationContext context)
			throws BeansException {
		// initData(context);
	//	HttpCommandProcessor processor=new HttpCommandProcessor(serverHost, serverPort, browserStartCommand, browserURL);
		
		System.out.println(" ======   =======   =======  =======");
		System.out.println(" ||   ||   ||   ||    ||     ||     ");
		System.out.println(" ======    ||   ||    ||     ||====");
		System.out.println(" ||   ||   ||   ||    ||     ||     ");
		System.out.println(" ||   ||  =======     ||     ||     ");
		System.out.println("Application Ready to Use");

	}

	private void initData(ApplicationContext context) {

		UserService userService = (UserService) context.getBean("userService");
		try {
			userService.getUser("admin");
		} catch (NoSuchUserExistsException noSuchUserExistsException) {

			RoleMapper roleMapper = new RoleMapper();
			roleMapper.setAuthority("ROLE_ADMIN");
			roleMapper.setAuthorityName("ADMIN");
			userService.addRole(roleMapper);

			roleMapper = new RoleMapper();
			roleMapper.setAuthority("ROLE_TESTER");
			roleMapper.setAuthorityName("TESTER");
			userService.addRole(roleMapper);

			OrganizationMapper organizationMapper = new OrganizationMapper();
			organizationMapper.setOrganization("ORG_CORELOGIC");
			organizationMapper.setOrganizationName("CORELOGIC");
			userService.addOrganization(organizationMapper);

			UserForm form = new UserForm();
			form.setUsername("admin");
			form.setFirstname("ThirupathiReddy");
			form.setLastname("Vajjala");
			form.setDesignation("Sr.Software Engineer");
			form.setEmail("trvajjala@adaequare.com");
			form.setOrganizationId("ORG_CORELOGIC");
			form.setRoleId("ROLE_ADMIN");
			form.setEnabled(true);
			try{
			userService.addUser(form);
			} catch (Exception e) {
				// TODO: handle exception
				logger.error("");
			}
			System.out.println("  application initilized and ready to use 	");
		}

	}

}
