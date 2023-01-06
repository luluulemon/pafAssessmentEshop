package vttp2022.paf.assessment.eshop.respositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


import vttp2022.paf.assessment.eshop.models.Customer;
import vttp2022.paf.assessment.eshop.models.Order;
import vttp2022.paf.assessment.eshop.models.OrderStatus;

import static vttp2022.paf.assessment.eshop.respositories.Queries.*;

import java.sql.Time;
import java.util.List;

@Repository
public class OrderRepository {
	
	@Autowired
	JdbcTemplate jdbcTemplate;

	@Autowired
	CustomerRepository customerRepo;

	@Transactional
	public int saveOrder(Order order){

		Customer customer = customerRepo.findCustomerByName(order.getName()).get();
		int result = jdbcTemplate.update(SQL_INSERT_ORDER, order.getOrderId(), order.getName(), 
						customer.getAddress(), customer.getEmail());

		List<Object[]> itemsParams = order.getLineItems().stream()
									.map(item -> new Object[]{ item.getItem(), item.getQuantity(), order.getOrderId()})
									.toList();

		int[] result2 = jdbcTemplate.batchUpdate(SQL_INSERT_ORDER_ITEMS,itemsParams);

		int sum = 0;
		for(int i:result2){	sum+= i;}

		// if not all updated
		if(result != 1 || sum != order.getLineItems().size()){	return 0;	}
		

		return 1;
	}




	public void saveOrderStatusSuccess(OrderStatus status){
		jdbcTemplate.update(SQL_SAVE_ORDER_STATUS_SUCCESS, status.getOrderId(), status.getDeliveryId(), "dispatched", System.currentTimeMillis()); 
	}

	public void saveOrderStatusPending(OrderStatus status){
		jdbcTemplate.update(SQL_SAVE_ORDER_STATUS_SUCCESS, status.getOrderId(), "pending", System.currentTimeMillis()); 
	}


	public int[] orderStatusNumbers(String name){

		int dispatched = 0;
		int pending = 0;

		SqlRowSet rs = jdbcTemplate.queryForRowSet(SQL_GET_ORDER_STATUS, "dispatched", name);
		if(rs.next())	
		{	dispatched = rs.getInt("count");	}

		SqlRowSet rs2 = jdbcTemplate.queryForRowSet(SQL_GET_ORDER_STATUS, "pending", name);
		if(rs.next()){	pending = rs2.getInt("count");	}

		int[] counts = {dispatched, pending};

		return counts;
	}
	
}
