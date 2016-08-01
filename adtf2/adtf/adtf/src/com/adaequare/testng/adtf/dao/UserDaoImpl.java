package com.adaequare.testng.adtf.dao;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.adaequare.testng.adtf.exceptions.NoSuchUserExistsException;
import com.adaequare.testng.adtf.exceptions.UserDataAlreadyExistsException;
import com.adaequare.testng.adtf.hbm.Organization;
import com.adaequare.testng.adtf.hbm.OrganizationMapper;
import com.adaequare.testng.adtf.hbm.Role;
import com.adaequare.testng.adtf.hbm.RoleMapper;
import com.adaequare.testng.adtf.hbm.UserVO;

@Repository("userDao")
public class UserDaoImpl implements UserDao {


	private SessionFactory sessionFactory;
	Session session;
	Transaction transaction;
	public Logger logger = Logger.getRootLogger();

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public String getRoleNameByUserId(Long userId) {
		String queryString = "select distinct r.authority_name from roles r,user_roles ur where ur.authority=r.authority and ur.user_id=:uid";
		SQLQuery query = sessionFactory.openSession().createSQLQuery(
				queryString);
		query.setParameter("uid", userId);
		// query.executeUpdate();

		return query.list().get(0).toString();
	}

	@Transactional
	public void saveUser(Role role, Organization org) {

		session = sessionFactory.openSession();

		try {
			transaction = session.beginTransaction();
			session.saveOrUpdate(role.getUser());
			session.saveOrUpdate(role);
			session.saveOrUpdate(org);
			transaction.commit();
		} catch (Exception e) {
			e.printStackTrace();
			transaction.rollback();

			if (e instanceof ConstraintViolationException) {
				throw new UserDataAlreadyExistsException(
						"Username or Email-Id Already exist", null,
						"Unique Key Constraint");
			}

		}

	}

	@Transactional
	public void updateUser(Role role, Organization org) {

		session = sessionFactory.openSession();

		try {
			transaction = session.beginTransaction();

			session.update(role.getUser());
			Role pRole = getRole(role.getUser());
			Organization pOrg = getOrganization(role.getUser());

			pRole.setRole(role.getRole());
			pOrg.setOrganization(org.getOrganization());
			session.update(pRole);

			session.update(pOrg);
			transaction.commit();
			
			updateUserRole(role.getUser().getUserid(), role.getRole());

		} catch (Exception e) {
			e.printStackTrace();
			transaction.rollback();

			if (e instanceof ConstraintViolationException) {
				throw new UserDataAlreadyExistsException(
						"Username or Email-Id Already exist", null,
						"Unique Key Constraint");
			}

		}

	}

	private void updateUserRole(Long userId, String authority) {

		String queryString = "update user_roles set authority=:authority where user_id=:userId";
		SQLQuery query = sessionFactory.openSession().createSQLQuery(
				queryString);
		query.setParameter("authority", authority);
		query.setParameter("userId", userId);

		query.executeUpdate();

	}

	public Role getRole(UserVO userVO) {
		session = sessionFactory.openSession();

		Query query = session.createQuery("from Role where user=:user");
		query.setParameter("user", userVO);

		Role role = (Role) query.list().get(0);
		return role;
	}

	public Organization getOrganization(UserVO userVO) {
		session = sessionFactory.openSession();

		Query query = session.createQuery("from Organization where user=:user");
		query.setParameter("user", userVO);

		Organization org = (Organization) query.list().get(0);
		return org;
	}

	@SuppressWarnings("unchecked")
	public List<UserVO> listAllUsers() {
		return (List<UserVO>) sessionFactory.openSession()
				.createCriteria(UserVO.class).list();
	}

	@SuppressWarnings("unchecked")
	public List<Role> listAllRoles() {
		return (List<Role>) sessionFactory.openSession()
				.createCriteria(Role.class).list();
	}

	@Override
	public void addOrganization(OrganizationMapper organizationMapper) {
		session = sessionFactory.openSession();

		try {
			transaction = session.beginTransaction();

			session.saveOrUpdate(organizationMapper);
			transaction.commit();
		} catch (Exception e) {
			e.printStackTrace();
			transaction.rollback();

			if (e instanceof ConstraintViolationException) {
				e.printStackTrace();
				throw new UserDataAlreadyExistsException(
						"Organization Already exist", null,
						"Unique Key Constraint");
			}

		}

	}

	@Override
	public void addRole(RoleMapper roleMapper) {
		session = sessionFactory.openSession();

		try {
			transaction = session.beginTransaction();

			session.saveOrUpdate(roleMapper);
			transaction.commit();
		} catch (Exception e) {
			e.printStackTrace();
			transaction.rollback();

			if (e instanceof ConstraintViolationException) {
				throw new UserDataAlreadyExistsException(
						"RoleName Already Exist", null, "Unique Key Constraint");
			}

		}

	}

	@Override
	@SuppressWarnings("all")
	public Role getUser(String username) throws NoSuchUserExistsException {
		session = sessionFactory.openSession();

		Query query = session
				.createQuery("from UserVO where username=:username");
		query.setParameter("username", username);

		List<UserVO> list = query.list();

		if (list.isEmpty()) {
			throw new NoSuchUserExistsException(
					"there is no such user exist with username : " + username);
		}

		UserVO userVO = (UserVO) list.get(0);
		query = session.createQuery("from Role where user=:user");
		query.setParameter("user", userVO);

		Role role = (Role) query.list().get(0);

		return role;
	}

	@Override
	public Map<String, String> getOrganizationsMap() {

		session = sessionFactory.openSession();
		Query query = session.createQuery("from OrganizationMapper");

		Map<String, String> orgMap = new LinkedHashMap<String, String>();// ?LinkedHashMap

		List<OrganizationMapper> list = query.list();

		for (OrganizationMapper om : list) {
			orgMap.put(om.getOrganization(), om.getOrganizationName());
		}

		return orgMap;
	}

	@Override
	public Map<String, String> getRolesMap() {

		session = sessionFactory.openSession();
		Query query = session.createQuery("from RoleMapper");

		Map<String, String> roleMap = new LinkedHashMap<String, String>();// ?LinkedHashMap

		List<RoleMapper> list = query.list();

		for (RoleMapper om : list) {
			roleMap.put(om.getAuthority(), om.getAuthorityName());
		}

		return roleMap;
	}

}
