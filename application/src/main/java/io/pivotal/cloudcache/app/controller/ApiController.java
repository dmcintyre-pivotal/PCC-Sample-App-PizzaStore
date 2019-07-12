/*
 * Copyright (C) 2018-Present Pivotal Software, Inc. All rights reserved.
 * This program and the accompanying materials are made available under
 * the terms of the under the Apache License, Version 2.0 (the "License”);
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.pivotal.cloudcache.app.controller;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import org.apache.geode.LogWriter;
import org.apache.geode.cache.GemFireCache;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Lists;

import io.pivotal.cloudcache.app.model.Menu;
import io.pivotal.cloudcache.app.model.Oven;
import io.pivotal.cloudcache.app.model.Pizza;
import io.pivotal.cloudcache.app.model.Sauce;
import io.pivotal.cloudcache.app.model.Topping;
import io.pivotal.cloudcache.app.repository.NameRepository;
import io.pivotal.cloudcache.app.repository.PizzaRepository;

/**
 * Implementation of all the REST APIs exposed by pizza store app
 */
@RestController
@SuppressWarnings("unused")
public class ApiController {

    private final GemFireCache gemfireCache;

    private final NameRepository nameRepository;

    private final PizzaRepository pizzaRepository;

    public ApiController(GemFireCache gemfireCache, NameRepository nameRepository, PizzaRepository pizzaRepository) {

        this.gemfireCache = gemfireCache;
        this.nameRepository = nameRepository;
        this.pizzaRepository = pizzaRepository;
    }
    
    @RequestMapping("/menu")
    public @ResponseBody Menu getMenu() {
    	return Menu.lunchtimeMenu();
    }

    /**
     * Clears data from all regions.
     */
    @GetMapping("/cleanSlate")
    public String cleanSlate() {

        this.nameRepository.deleteAll();
        this.pizzaRepository.deleteAll();

        return "<h1>OVEN EMPTY!</h1>";
    }

    /**
     * Health check
     */
    @GetMapping("/ping")
    public String ping() {
        return "<h1>PONG!</h1>";
    }

    /**
     * Creates some predefined pizzas.
     *
     */
    @RequestMapping("/preheatOven")
    public ResponseEntity<Object> preheatOven() {

        LogWriter logger = gemfireCache.getLogger();

        Pizza plainPizza = makePlainPizza();
        Pizza fancyPizza = makeFancyPizza();
        Pizza superFancyPizza = makeSuperFancyPizza("test");

        this.pizzaRepository.save(plainPizza);
        this.pizzaRepository.save(fancyPizza);
        this.pizzaRepository.save(superFancyPizza);

        logger.info("Finished baking pizzas");

        Optional<Pizza> pizza = this.pizzaRepository.findById("plain");

        if (!pizza.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        if (!pizza.filter(it -> it.uses(Sauce.TOMATO)).isPresent()) {

            logger.info(String.format("I ordered tomato sauce; Pizza was [%s]",
                pizza.map(Pizza::toString).orElse(null)));

            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (!pizza.filter(it -> it.has(Topping.CHEESE)).isPresent()) {

            logger.info(String.format("Where's my cheese? Pizza was [%s]",
                pizza.map(Pizza::toString).orElse(null)));

            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>("<h1>OVEN HEATED!</h1>", HttpStatus.OK);
    }

    /**
     * Returns the list of pizzas currently in the oven
     */
    @GetMapping("/oven")
    public Oven getPizzas() {

        Iterable<Pizza> pizzas = this.pizzaRepository.findAll();

        return new Oven(true, Lists.newArrayList(pizzas));
    }

    /**
     * Returns details of a given pizza.
     * @param pizzaName
     */
    @GetMapping("/pizzas/{name}")
    public Object getNamedPizza(@PathVariable("name") String pizzaName) {

        Pizza namedPizza = this.pizzaRepository.findById(pizzaName).orElse(null);

        return namedPizza != null ? namedPizza : String.format("<h1>Pizza [%s] Not Found</h1>", pizzaName);
    }

    /**
     * Creates a new pizza with the given name, toppings and sauce.
     * @param name
     * @param pizzaSauce
     * @param toppings
     */
    @GetMapping("/pizzas/order/{name}")
    public String order(@PathVariable("name") String name,
            @RequestParam(name = "sauce", defaultValue = "TOMATO") Sauce pizzaSauce,
            @RequestParam(name = "toppings", defaultValue = "CHEESE") Topping[] toppings) {

        Pizza namedPizza = new Pizza(name).having(pizzaSauce);

        Arrays.stream(toppings).forEach(namedPizza::with);

        this.pizzaRepository.save(namedPizza);

        return String.format("<h1>Pizza [%s] Ordered</h1>", namedPizza);
    }

    /**
     * Orders a Pesto Pizza
     * @param name
     */
    // Technically, this should be a POST, but...
    @GetMapping("/pizzas/pestoOrder/{name}")
    public String pestoOrder(@PathVariable("name") String name) {

        this.pizzaRepository.save(makeSuperFancyPizza(name));

        return String.format("<h1>Pesto Pizza [%s] Ordered</h1>", name);
    }

    private Pizza makeFancyPizza() {

        return new Pizza("fancy")
            .having(Sauce.ALFREDO)
            .with(Topping.ARUGULA)
            .with(Topping.CHICKEN);
    }

    private Pizza makePlainPizza() {
        return new Pizza("plain").with(Topping.CHEESE);
    }

    private Pizza makeSuperFancyPizza(String name) {

        return new Pizza(name)
            .having(Sauce.PESTO)
            .with(Topping.CHICKEN)
            .with(Topping.PARMESAN)
            .with(Topping.CHERRY_TOMATOES);
    }

    private <T> Iterable<T> nullSafeIterable(Iterable<T> iterable) {
        return iterable != null ? iterable : Collections::emptyIterator;
    }
}
