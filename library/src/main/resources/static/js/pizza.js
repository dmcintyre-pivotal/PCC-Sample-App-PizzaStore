/**
* A collection of javascript to update the web UI using the JSON API
**/

function updateMenu() {
	
	$.getJSON("/menu")
		.done(	
			function(data) {
				
				$("#menu-toppings").html('');
				for(i=0; i<data.toppings.length; i++) {
					$('<div><input type="checkbox" id="TOPPING_' + data.toppings[i] + '">' + data.toppings[i] + '</div>').appendTo("#menu-toppings");
				}
				
				$("#menu-sauces").html('');
				for(i=0; i<data.sauces.length; i++) {
					$('<div><input type="radio" id="SAUCE_' + data.sauces[i] + '">' + data.sauces[i] + '</div>').appendTo("#menu-sauces");
				}
				
				$("#menu-bases").html('');
				for(i=0; i<data.bases.length; i++) {
					$('<div><input type="radio" id="BASE_' + data.bases[i] + '">' + data.bases[i] + '</div>').appendTo("#menu-bases");
				}
			}
			);
	
}


function updateOven() {
	
	$.getJSON("/oven")
		.done(
				function(data) {
					
					if(data.pizzas.length == 0) {
						$("#oven-status").text('The oven is empty');
					} else {
						
						$("#oven-status").text('The oven is hot!');
						$("#pizza-toppings").html('');
						$("#pizza-sauce").html('');
						$("#pizza-base").html('');
						
						for(i=0; i<data.pizzas.length; i++) {
							
							for(j=0; j<data.pizzas[i].toppings.length; j++) {
								$('<div>' + data.pizzas[i].toppings[j] + '</div>').appendTo("#pizza-toppings");
							}
							
							$('<div>' + data.pizzas[i].sauce + '</div>').appendTo("#pizza-sauce");
							
							
							$('<div>' + data.pizzas[i].base + '</div>').appendTo("#pizza-base");
														
						}
					}
					
				}
			);
	setTimeout(updateOven, 1000);
	
}

function orderPizza() {
	alert('Ordering a pizza');
	return false;
}

function makeShopPizzas() {
	$.getJSON("/preheatOven")	;
}