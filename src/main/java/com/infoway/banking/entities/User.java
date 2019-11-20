package com.infoway.banking.entities;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.JoinColumn;

@Entity
@Table(name = "user")
@Inheritance(strategy = InheritanceType.JOINED)
public class User implements Serializable {
	
	private static final long serialVersionUID = -6566112973341864257L;

    private Integer id;
    private String username;
    private String password;
    private String email;
    private boolean enabled;
    private boolean accountNonExpired;
    private boolean credentialsNonExpired;
    private boolean accountNonLocked;
    private List<Role> roles;
	
	public User() {}

    public User(User user) {
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.email = user.getEmail();
        this.enabled = user.isEnabled();
        this.accountNonExpired = user.isAccountNonExpired();
        this.credentialsNonExpired = user.isCredentialsNonExpired();
        this.accountNonLocked = user.isAccountNonLocked();
        this.roles = user.getRoles();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name = "username")
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}

	@Column(name = "password")
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}

	@Column(name = "email")
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}

	@Column(name = "enabled")
	public boolean isEnabled() {
		return enabled;
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	@Column(name = "account_non_expired")
	public boolean isAccountNonExpired() {
		return accountNonExpired;
	}
	public void setAccountNonExpired(boolean accountNonExpired) {
		this.accountNonExpired = accountNonExpired;
	}

	@Column(name = "credentials_non_expired")
	public boolean isCredentialsNonExpired() {
		return credentialsNonExpired;
	}
	public void setCredentialsNonExpired(boolean credentialsNonExpired) {
		this.credentialsNonExpired = credentialsNonExpired;
	}

	@Column(name = "account_non_locked")
	public boolean isAccountNonLocked() {
		return accountNonLocked;
	}
	public void setAccountNonLocked(boolean accountNonLocked) {
		this.accountNonLocked = accountNonLocked;
	}

	@ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
	@JoinTable(name = "role_user",
				joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id")},
				inverseJoinColumns = {@JoinColumn(name = "role_id", referencedColumnName = "id")})
	public List<Role> getRoles() {
		return roles;
	}
	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}
    
}
