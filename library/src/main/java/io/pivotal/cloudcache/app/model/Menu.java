package io.pivotal.cloudcache.app.model;

import java.util.HashSet;
import java.util.Set;

public class Menu {
	
	private Set<Sauce> sauces = new HashSet<Sauce>();
	private Set<Topping> toppings = new HashSet<Topping>();
	private Set<Base> bases = new HashSet<Base>();
	
	public static Menu lunchtimeMenu() {
		Menu m = new Menu();
		
		for(Sauce s : Sauce.values()) {
			m.sauces.add(s);
		}
		
		for(Topping t : Topping.values()) {
			m.toppings.add(t);
		}
		
		for(Base b : Base.values()) {
			m.bases.add(b);
		}
		
		return m;
	}

	public Set<Sauce> getSauces() {
		return sauces;
	}

	public Set<Topping> getToppings() {
		return toppings;
	}
	
	public Set<Base> getBases() {
		return bases;
	}
}
