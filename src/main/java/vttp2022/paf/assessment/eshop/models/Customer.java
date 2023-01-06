package vttp2022.paf.assessment.eshop.models;

import org.springframework.jdbc.support.rowset.SqlRowSet;

// DO NOT CHANGE THIS CLASS
public class Customer {

	private String name;
	private String address;
	private String email;

	public String getName() { return this.name; }
	public void setName(String name) { this.name = name; }

	public String getAddress() { return this.address; }
	public void setAddress(String address) { this.address = address; }

	public String getEmail() { return this.email; }
	public void setEmail(String email) { this.email = email; }


	public static Customer create(SqlRowSet rs){
		Customer customer = new Customer();
		customer.setAddress(rs.getString("address"));
		customer.setName(rs.getString("name"));
		customer.setEmail(rs.getString("email"));
		return customer;
	}
}
