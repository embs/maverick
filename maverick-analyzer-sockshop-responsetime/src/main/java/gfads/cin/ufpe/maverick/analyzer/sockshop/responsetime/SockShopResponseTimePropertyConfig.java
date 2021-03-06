package gfads.cin.ufpe.maverick.analyzer.sockshop.responsetime;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import gfads.cin.ufpe.maverick.analyzer.worker.Property;

@Configuration
// To get properties from dependencies
@ComponentScan(basePackages={"gfads.cin.ufpe.maverick.analyzer.config"}) 
public class SockShopResponseTimePropertyConfig {
	
	@Bean
	/**
	 * Instantiate property's name. This name come from user paramenter
	 * --maverick.property.name=<property name>
	 * @param name
	 * @return
	 */
	public String name(@Value("${maverick.property.name}") String name) {
		return name;
	}
	
	@Bean
	public Float offset(@Value("${maverick.property.offset}") Float offset) {
		return offset;
	}
	
	@Bean
	/**
	 * Instantiate a property with the name already instantiated
	 * @param name
	 * @return
	 */
	public Property property(String name, Float offset) {
		return new SockShopResponseTimeProperty(name, offset);
	}
}
