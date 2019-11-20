package com.infoway.banking.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "permission")
public class Permission implements Serializable {
    
	private static final long serialVersionUID = -1221933913941657686L;
	
	private Integer id;
    private String name;
    
	public Permission() {}
	
	public Permission(String name) {
		this.name = name;
	}

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
    
}
