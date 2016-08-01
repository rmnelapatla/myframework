package com.adaequare.testng.adtf.dao;

import java.util.List;
import java.util.Map;

import com.adaequare.testng.adtf.exceptions.NoSuchUserExistsException;
import com.adaequare.testng.adtf.hbm.Organization;
import com.adaequare.testng.adtf.hbm.OrganizationMapper;
import com.adaequare.testng.adtf.hbm.Role;
import com.adaequare.testng.adtf.hbm.RoleMapper;
import com.adaequare.testng.adtf.hbm.UserVO;

public interface UserDao {

	public void addRole(RoleMapper roleMapper);
	public void addOrganization(OrganizationMapper organizationMapper);
	
	public Role getUser(String username) throws NoSuchUserExistsException;
	public void saveUser(Role user,Organization org);
	public void updateUser(Role role, Organization org);
	public List<UserVO> listAllUsers();
	public List<Role> listAllRoles() ;
	public String getRoleNameByUserId(Long userId);

	public Map<String,String> getOrganizationsMap();
	public Map<String,String> getRolesMap();
}
