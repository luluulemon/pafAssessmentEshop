package vttp2022.paf.assessment.eshop.controllers;


import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonReader;
import jakarta.json.JsonValue;
import vttp2022.paf.assessment.eshop.models.Customer;
import vttp2022.paf.assessment.eshop.models.LineItem;
import vttp2022.paf.assessment.eshop.models.Order;
import vttp2022.paf.assessment.eshop.models.OrderStatus;
import vttp2022.paf.assessment.eshop.respositories.CustomerRepository;
import vttp2022.paf.assessment.eshop.respositories.OrderRepository;
import vttp2022.paf.assessment.eshop.services.SaveOrderException;
import vttp2022.paf.assessment.eshop.services.WarehouseService;



@RestController
public class OrderController {

	@Autowired
	CustomerRepository customerRepo;

	@Autowired
	OrderRepository orderRepo;

	@Autowired
	WarehouseService warehseSvc;

	@PostMapping(path="/api/order")
	public ResponseEntity<String> saveOrder(@RequestBody String body){

		JsonReader reader = Json.createReader(new StringReader(body));
		JsonObject results = reader.readObject(); 

		// Check if customer is valid
		String name = results.getString("name");
		Optional<Customer> customer = customerRepo.findCustomerByName(name);

		if(customer.isEmpty()){
			JsonObject errorBody = Json.createObjectBuilder().add("error", "Customer %s not found".formatted(name)).build();
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorBody.toString());
		}
		
		// Populate the model -> Create new Order Object
		Order order = new Order();
		order.setOrderId( UUID.randomUUID().toString().substring(0, 8) );
		order.setName(name);
		order.setAddress(customer.get().getAddress());
		order.setEmail(customer.get().getEmail());
		List<LineItem> items = new ArrayList<>();
		for(JsonValue v: results.getJsonArray("lineItems")){
			JsonObject obj = (JsonObject)v;
			LineItem item = new LineItem();
			item.setItem(obj.getString("item") );
			item.setQuantity( obj.getInt("quantity") );
			items.add(item);
		}
		order.setLineItems(items);

		// save the order to database
		try
		{	int saveResult = orderRepo.saveOrder(order);	}
		catch(SaveOrderException e)
		{	JsonObject errorBody = Json.createObjectBuilder().add("Error", "Order not saved").build();	
			return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(errorBody.toString());
		}

		// if(saveResult != 1)
		// {	JsonObject errorBody = Json.createObjectBuilder().add("Error", "Order not saved").build();	
		// 	return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(errorBody.toString());
		// }

		// call dispatch() and send the response
		OrderStatus status = warehseSvc.dispatch(order);
		JsonObjectBuilder statusBuilder = Json.createObjectBuilder()
												.add("orderId", status.getOrderId());
		
		if(status.getStatus().equals("Pending"))
		{	JsonObject statusJson = statusBuilder.add("status", "Pending").build();	
			return ResponseEntity.ok().body(statusJson.toString());
		}
		else
		{	JsonObject statusJson = statusBuilder.add("deliveryId", status.getDeliveryId())
												.add("status", status.getStatus())
												.build();
			return ResponseEntity.ok().body(statusJson.toString());
		}
		
	}
	

	@GetMapping(path = "/api/order/{name}/status", produces = "application/json")
	public ResponseEntity<String> getOrderStatus(@PathVariable String name){

		int[] counts = orderRepo.orderStatusNumbers(name);

		JsonObject countStatus = Json.createObjectBuilder().add("name", name)
									.add("dispatched", counts[0])
									.add("pending", counts[1])
									.build();


		return ResponseEntity.ok().body(countStatus.toString());		
	}

}
