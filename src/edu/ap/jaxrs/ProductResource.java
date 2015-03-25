package edu.ap.jaxrs;

import java.io.*;
import java.util.*;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.*;
import javax.xml.bind.*;

@RequestScoped
@Path("/products")
public class ProductResource {
	
	@GET
	@Produces({"text/html"})
	public String getProductsHTML() {
		String htmlString = "<html><body>";
		try {
			JAXBContext jaxbContext1 = JAXBContext.newInstance(ProductsXML.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext1.createUnmarshaller();
			File XMLfile = new File("/Users/philippepossemiers/Desktop/Products.xml");
			ProductsXML productsXML = (ProductsXML)jaxbUnmarshaller.unmarshal(XMLfile);
			ArrayList<Product> listOfProducts = productsXML.getProducts();
			
			for(Product product : listOfProducts) {
				htmlString += "<b>ShortName : " + product.getShortname() + "</b><br>";
				htmlString += "Id : " + product.getId() + "<br>";
				htmlString += "SKU : " + product.getSku() + "<br>";
				htmlString += "Brand : " + product.getBrand() + "<br>";
				htmlString += "Description : " + product.getDescription() + "<br>";
				htmlString += "Price : " + product.getPrice() + "<br>";
				htmlString += "<br><br>";
			}
		} 
		catch (JAXBException e) {
		   e.printStackTrace();
		}
		return htmlString;
	}
	
	@GET
	@Produces({"application/json"})
	public String getProductsJSON() {
		String jsonString = "{\"products\" : [";
		try {
			JAXBContext jaxbContext1 = JAXBContext.newInstance(ProductsXML.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext1.createUnmarshaller();
			File XMLfile = new File("/Users/philippepossemiers/Desktop/Products.xml");
			ProductsXML productsXML = (ProductsXML)jaxbUnmarshaller.unmarshal(XMLfile);
			ArrayList<Product> listOfProducts = productsXML.getProducts();
			
			for(Product product : listOfProducts) {
				jsonString += "{\"shortname\" : \"" + product.getShortname() + "\",";
				jsonString += "\"id\" : " + product.getId() + ",";
				jsonString += "\"sku\" : \"" + product.getSku() + "\",";
				jsonString += "\"brand\" : \"" + product.getBrand() + "\",";
				jsonString += "\"description\" : \"" + product.getDescription() + "\",";
				jsonString += "\"price\" : " + product.getPrice() + "},";
			}
			jsonString = jsonString.substring(0, jsonString.length()-1);
			jsonString += "]}";
		} 
		catch (JAXBException e) {
		   e.printStackTrace();
		}
		return jsonString;
	}
	
	@GET
	@Produces({"text/xml"})
	public String getProductsXML() {
		String content = "";
		File XMLfile = new File("/Users/philippepossemiers/Desktop/Products.xml");
		try {
			content = new Scanner(XMLfile).useDelimiter("\\Z").next();
		} 
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return content;
	}

	@GET
	@Path("/{shortname}")
	@Produces({"application/json"})
	public String getProductJSON(@PathParam("shortname") String shortname) {
		String jsonString = "";
		try {
			// get all products
			JAXBContext jaxbContext1 = JAXBContext.newInstance(ProductsXML.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext1.createUnmarshaller();
			File XMLfile = new File("/Users/philippepossemiers/Desktop/Products.xml");
			ProductsXML productsXML = (ProductsXML)jaxbUnmarshaller.unmarshal(XMLfile);
			ArrayList<Product> listOfProducts = productsXML.getProducts();
			
			// look for the product, using the shortname
			for(Product product : listOfProducts) {
				if(shortname.equalsIgnoreCase(product.getShortname())) {
					jsonString += "{\"shortname\" : \"" + product.getShortname() + "\",";
					jsonString += "\"id\" : " + product.getId() + ",";
					jsonString += "\"sku\" : \"" + product.getSku() + "\",";
					jsonString += "\"brand\" : \"" + product.getBrand() + "\",";
					jsonString += "\"description\" : \"" + product.getDescription() + "\",";
					jsonString += "\"price\" : " + product.getPrice() + "}";
				}
			}
		} 
		catch (JAXBException e) {
		   e.printStackTrace();
		}
		return jsonString;
	}
	
	@GET
	@Path("/{shortname}")
	@Produces({"text/xml"})
	public String getProductXML(@PathParam("shortname") String shortname) {
		String xmlString = "";
		try {
			// get all products
			JAXBContext jaxbContext1 = JAXBContext.newInstance(ProductsXML.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext1.createUnmarshaller();
			File XMLfile = new File("/Users/philippepossemiers/Desktop/Products.xml");
			ProductsXML productsXML = (ProductsXML)jaxbUnmarshaller.unmarshal(XMLfile);
			ArrayList<Product> listOfProducts = productsXML.getProducts();
			
			// look for the product, using the shortname
			for(Product product : listOfProducts) {
				if(shortname.equalsIgnoreCase(product.getShortname())) {
					JAXBContext jaxbContext2 = JAXBContext.newInstance(Product.class);
					Marshaller jaxbMarshaller = jaxbContext2.createMarshaller();
					StringWriter sw = new StringWriter();
					jaxbMarshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
					jaxbMarshaller.marshal(product, sw);
					xmlString = sw.toString();
				}
			}
		} 
		catch (JAXBException e) {
		   e.printStackTrace();
		}
		return xmlString;
	}
	
	@POST
	@Consumes({"text/xml"})
	public void processFromXML(String productXML) {
		
		/* newProductXML should look like this :
		 *  
		 <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
		 <product>
        	<brand>BRAND</brand>
        	<description>DESCRIPTION</description>
        	<id>123456</id>
        	<price>20.0</price>
        	<shortname>SHORTNAME</shortname>
        	<sku>SKU</sku>
		 </product>
		 */
		
		try {
			// get all products
			JAXBContext jaxbContext1 = JAXBContext.newInstance(ProductsXML.class);
			Unmarshaller jaxbUnmarshaller1 = jaxbContext1.createUnmarshaller();
			File XMLfile = new File("/Users/philippepossemiers/Desktop/Products.xml");
			ProductsXML productsXML = (ProductsXML)jaxbUnmarshaller1.unmarshal(XMLfile);
			ArrayList<Product> listOfProducts = productsXML.getProducts();
			
			// unmarshal new product
			JAXBContext jaxbContext2 = JAXBContext.newInstance(Product.class);
			Unmarshaller jaxbUnmarshaller2 = jaxbContext2.createUnmarshaller();
			StringReader reader = new StringReader(productXML);
			Product newProduct = (Product)jaxbUnmarshaller2.unmarshal(reader);
			
			// add product to existing product list 
			// and update list of products in  productsXML
			listOfProducts.add(newProduct);
			productsXML.setProducts(listOfProducts);
			
			// marshal the updated productsXML object
			Marshaller jaxbMarshaller = jaxbContext1.createMarshaller();
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			jaxbMarshaller.marshal(productsXML, XMLfile);
		} 
		catch (JAXBException e) {
		   e.printStackTrace();
		}
	}
}