package io.pivotal.cloudcache.app.model;

import java.util.ArrayList;
import java.util.List;

public class Oven {
	
	private final boolean on;
	
	private final List<Pizza> pizzas;
	
	public Oven(boolean on, List<Pizza> pizzas) {
		this.on = on;
		this.pizzas = pizzas;
	}

	public boolean isOn() {
		return on;
	}

	public List<Pizza> getPizzas() {
		return pizzas;
	}
}
