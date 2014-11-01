package com.fmt.password;

/**
 * Contact Bean.
 **/
//@Bean
public class Contact {
	//member variables
	private String name;
	private String role;
	private String password;
	private String site;

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the role
	 */
	public String getRole() {
		return role;
	}
	/**
	 * @param role the role to set
	 */
	public void setRole(String role) {
		this.role = role;
	}
	
	/**
	 * @return the site
	 */
	public String getSite() {
		return site;
	}
	/**
	 * @param site the site to set
	 */
	public void setSite(String site) {
		this.site = site;
	}
	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}
	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}
	/** Constructor.
	 * @param name username
	 * @param role role of user
	 * @param password password of user
	 * @param site site username is associated with
	 **/
	public Contact(String name, String role, String password, String site) {
		super();
		this.name = name;
		this.role = role;
		this.password = password;
		this.site = site;
	}
	
	/** Constructor. **/
	public Contact() {
		this("", "", "", "");
	}
			
	@Override
	public String toString() {
		return "Contact [name=" + name + ", role=" + role + "]";
	}
}
