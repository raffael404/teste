package com.infoway.banking.entities;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.JoinColumn;

@Entity
@Table(name = "role")
public class Role implements Serializable {
    
	private static final long serialVersionUID = 6767946938282953683L;
	
	private Integer id;
    private String name;
    private List<Permission> permissions;

	public Role() {}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name = "name")
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "permission_role",
				joinColumns = {@JoinColumn(name = "role_id", referencedColumnName = "id")},
				inverseJoinColumns = {@JoinColumn(name = "permission_id", referencedColumnName = "id")})
	public List<Permission> getPermissions() {
		return permissions;
	}
	public void setPermissions(List<Permission> permissions) {
		this.permissions = permissions;
	}
    
}
