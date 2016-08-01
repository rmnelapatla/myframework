package com.adaequare.testng.adtf.hbm;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "roles")
public class RoleMapper {

	@Id
	// @GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "AUTHORITY", length = 50)
	private String authority;

	@Column(name = "AUTHORITY_NAME", nullable = false, length = 50, unique = true)
	private String authorityName;

	public String getAuthority() {
		return authority;
	}

	public void setAuthority(String authority) {
		this.authority = authority;
	}

	public String getAuthorityName() {
		return authorityName;
	}

	public void setAuthorityName(String authorityName) {
		this.authorityName = authorityName;
	}

}
