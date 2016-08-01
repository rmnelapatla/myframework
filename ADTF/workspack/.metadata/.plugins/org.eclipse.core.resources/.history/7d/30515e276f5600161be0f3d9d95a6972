package com.adaequare.testng.adtf.service;

import java.util.List;
import java.util.Map;

import com.adaequare.testng.adtf.exceptions.NoSuchUserExistsException;
import com.adaequare.testng.adtf.forms.UserForm;
import com.adaequare.testng.adtf.hbm.OrganizationMapper;
import com.adaequare.testng.adtf.hbm.Role;
import com.adaequare.testng.adtf.hbm.RoleMapper;
import com.adaequare.testng.adtf.hbm.UserVO;

public interface UserService {

	public void addUser(UserForm userForm);
	public void updateUser(UserForm userForm);
	
	public void addRole(RoleMapper roleMapper);
	public void addOrganization(OrganizationMapper organizationMapper);
	
	
	public UserForm getUser(String username) throws NoSuchUserExistsException;
	
	public List<UserVO> listAllUsers();
	public List<Role> listAllRoles() ;

	public String getRoleNameByUserId(Long userId);
	public Map<String,String> getOrganizationsMap();
	public Map<String,String> getRolesMap();
}
