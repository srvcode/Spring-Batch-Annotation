package com.srvcode.springbatch.service;

import org.springframework.batch.item.ItemProcessor;

import com.srvcode.springbatch.model.FlightTicket;

public class CustomItemProcessor implements ItemProcessor<FlightTicket, FlightTicket> {

	@Override
	public FlightTicket process(FlightTicket item) throws Exception {

		System.out.printf("Processing %s...%n", item);
		item.setRoute(item.getRoute().toUpperCase());
		
		return item;
	}

}
