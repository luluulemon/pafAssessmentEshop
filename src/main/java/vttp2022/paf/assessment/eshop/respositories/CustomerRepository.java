package vttp2022.paf.assessment.eshop.respositories;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import static vttp2022.paf.assessment.eshop.respositories.Queries.*;



import vttp2022.paf.assessment.eshop.models.Customer;

@Repository
public class CustomerRepository {

	@Autowired
	JdbcTemplate jdbcTemplate;
	// You cannot change the method's signature
	public Optional<Customer> findCustomerByName(String name) {

		SqlRowSet rs = jdbcTemplate.queryForRowSet(SQL_FIND_USER_BY_NAME, name);
		
		if(rs.next())
			return Optional.of(Customer.create(rs));

		else
			return Optional.empty();
	}
}
