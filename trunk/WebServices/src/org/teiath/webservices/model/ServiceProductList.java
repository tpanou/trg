package org.teiath.webservices.model;

import org.codehaus.jackson.map.annotate.JsonRootName;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "products")
@JsonRootName(value = "products")
public class ServiceProductList {

	private List<ServiceProduct> products;

	public ServiceProductList() {

	}

	public ServiceProductList(List<ServiceProduct> products) {
		this.products = products;
	}

	@XmlElement(name = "product")
	public List<ServiceProduct> getServiceProducts() {
		return this.products;
	}

	public void setServiceProducts(List<ServiceProduct> products) {
		this.products = products;
	}
}
