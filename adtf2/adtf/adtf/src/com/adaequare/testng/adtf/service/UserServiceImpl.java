package com.adaequare.testng.adtf.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.adaequare.testng.adtf.dao.UserDao;
import com.adaequare.testng.adtf.exceptions.NoSuchUserExistsException;
import com.adaequare.testng.adtf.forms.UserForm;
import com.adaequare.testng.adtf.hbm.Organization;
import com.adaequare.testng.adtf.hbm.OrganizationMapper;
import com.adaequare.testng.adtf.hbm.Role;
import com.adaequare.testng.adtf.hbm.RoleMapper;
import com.adaequare.testng.adtf.hbm.UserVO;

@Service("userService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class UserServiceImpl implements UserService {

	public Logger logger = Logger.getRootLogger();
	@Autowired
	private UserDao userDao;

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

	public void addUser(UserForm userForm) {

		logger.error(" userForm " + userForm.getEnabled());

		int enabled = (userForm.getEnabled()) ? 1 : 0;
		UserVO user = new UserVO();
		Role role = new Role();

		role.setRole(userForm.getRoleId());
		Organization org = new Organization();
		org.setOrganization(userForm.getOrganizationId());
		user.setUserid(userForm.getUserid());
		user.setUsername(userForm.getUsername());
		user.setFirstname(userForm.getFirstname());
		user.setLastname(userForm.getLastname());
		user.setEmail(userForm.getEmail());
		user.setPhone(userForm.getPhone());
		user.setDesignation(userForm.getDesignation());
		user.setPassword((userForm.getPassword() == null) ? "1234" : ((userForm
				.getPassword().trim().length() < 1) ? "1234" : userForm
				.getPassword()));
		user.setEnabled(enabled);

		role.setUser(user);
		org.setUser(user);

		userDao.saveUser(role, org);
	}

	public List<UserVO> listAllUsers() {

		List<UserVO> users = userDao.listAllUsers();

		List<UserVO> userList = new ArrayList<UserVO>();

		for (UserVO u : users) {

			String roleName = getRoleNameByUserId(u.getUserid());
			if (!"ADMIN".equalsIgnoreCase(roleName)) {
				u.setRoleName(roleName);
				userList.add(u);
			}
		}

		return userList;
	}

	@Override
	public List<Role> listAllRoles() {
		return userDao.listAllRoles();
	}

	@Override
	public void addOrganization(OrganizationMapper organizationMapper) {
		userDao.addOrganization(organizationMapper);
	}

	@Override
	public void addRole(RoleMapper roleMapper) {
		userDao.addRole(roleMapper);
	}

	@Override
	public String getRoleNameByUserId(Long userId) {

		return userDao.getRoleNameByUserId(userId);
	}

	@Override
	public UserForm getUser(String username) throws NoSuchUserExistsException {

		Role role = userDao.getUser(username);
		UserVO userVO = role.getUser();
		UserForm form = new UserForm();
		form.setRoleName(role.getRole());
		form.setRoleId(role.getRid().toString());
		form.setUserid(userVO.getUserid());
		form.setUsername(userVO.getUsername());
		form.setFirstname(userVO.getFirstname());
		form.setLastname(userVO.getLastname());
		form.setEmail(userVO.getEmail());
		form.setPhone(userVO.getPhone());
		form.setDesignation(userVO.getDesignation());
		form.setPassword(userVO.getPassword());
		form.setEnabled((userVO.getEnabled() == 1) ? true : false);
		return form;
	}

	@Override
	public Map<String, String> getOrganizationsMap() {

		return userDao.getOrganizationsMap();
	}

	@Override
	public Map<String, String> getRolesMap() {

		return userDao.getRolesMap();
	}

	@Override
	public void updateUser(UserForm userForm) {


		logger.error(" userForm " + userForm.getEnabled());

		int enabled = (userForm.getEnabled()) ? 1 : 0;
		UserVO user = new UserVO();
		Role role = new Role();

		role.setRole(userForm.getRoleName());
		
		Organization org = new Organization();
		org.setOrganization(userForm.getOrganizationId());
		user.setUserid(userForm.getUserid());
		user.setUsername(userForm.getUsername());
		user.setFirstname(userForm.getFirstname());
		user.setLastname(userForm.getLastname());
		user.setEmail(userForm.getEmail());
		user.setPhone(userForm.getPhone());
		user.setDesignation(userForm.getDesignation());
		user.setPassword((userForm.getPassword() == null) ? "1234" : ((userForm
				.getPassword().trim().length() < 1) ? "1234" : userForm
				.getPassword()));
		user.setEnabled(enabled);

		role.setUser(user);
		org.setUser(user);

		userDao.updateUser(role, org);
	
		
	}

}
