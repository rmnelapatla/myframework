package com.adaequare.testng.adtf.hbm;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "organizations")
public class OrganizationMapper {

	@Id
	// @GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "organizationId", length = 50)
	private String organization;

	public String getOrganization() {
		return organization;
	}

	public void setOrganization(String organization) {
		this.organization = organization;
	}

	@Column(name = "ORG_NAME", nullable = false, length = 50, unique = true)
	private String organizationName;

	public String getOrganizationName() {
		return organizationName;
	}

	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}

	
}
