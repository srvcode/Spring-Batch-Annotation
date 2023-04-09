package com.srvcode.springbatch.model;

import javax.xml.bind.annotation.XmlRootElement;

import lombok.Data;

@Data
@XmlRootElement(name = "ticket")
public class FlightTicket {

	private String name;
	private int ticketNumber;
	private String route;
	private double ticketPrice;
}
