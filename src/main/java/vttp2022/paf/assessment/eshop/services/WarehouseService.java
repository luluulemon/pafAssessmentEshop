package vttp2022.paf.assessment.eshop.services;

import java.io.StringReader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonReader;
import vttp2022.paf.assessment.eshop.models.LineItem;
import vttp2022.paf.assessment.eshop.models.Order;
import vttp2022.paf.assessment.eshop.models.OrderStatus;
import vttp2022.paf.assessment.eshop.respositories.OrderRepository;

@Service
public class WarehouseService {

	@Autowired
	OrderRepository orderRepo;

	// You cannot change the method's signature
	// You may add one or more checked exceptions

	public OrderStatus dispatch(Order order) {

		String url = "http://paf.chuklee.com/dispatch/" + order.getOrderId();

		// create JsonArray for LineItems
		JsonArrayBuilder lineItemsBuilder = Json.createArrayBuilder();
		for(LineItem item: order.getLineItems()){
			JsonObject itemJson = Json.createObjectBuilder().add("item", item.getItem())
										.add("quantity", item.getQuantity()).build();
			lineItemsBuilder.add(itemJson);				
		}
		
		JsonObjectBuilder orderBuilder = Json.createObjectBuilder();
		
		orderBuilder.add("orderId", order.getOrderId())
					.add("name", order.getName())
					.add("address", order.getAddress())
					.add("email", order.getEmail())
					.add("lineItems", lineItemsBuilder.build())
					.add("createdBy", "Chen Luwei");


		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> request = new HttpEntity<>(orderBuilder.build().toString(), headers);

		RestTemplate template = new RestTemplate();
		ResponseEntity<String> response = template.postForEntity(url, request, String.class);
					
		JsonReader reader = Json.createReader(new StringReader(response.getBody()));
		JsonObject results = reader.readObject(); 

		// create OrderStatus object to return
		OrderStatus orderStatus = new OrderStatus();
		orderStatus.setDeliveryId(results.getString("deliveryId", "NA"));
		orderStatus.setOrderId(results.getString("orderId", "NA"));
		if(orderStatus.getOrderId().equals("NA"))
		{	orderStatus.setStatus("Pending");	
			// save the status
			orderRepo.saveOrderStatusPending(orderStatus);
		}
		else
		{	orderStatus.setStatus("dispatched");	
			orderRepo.saveOrderStatusSuccess(orderStatus);
		}


		return orderStatus;

	}
}
