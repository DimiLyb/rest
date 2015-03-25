package edu.ap.jaxrs;

import java.io.*;
import java.util.*;
import java.util.logging.XMLFormatter;

import javax.enterprise.context.RequestScoped;
import javax.json.JsonObject;
import javax.json.spi.JsonProvider;
import javax.ws.rs.*;
import javax.xml.bind.*;
import javax.json.*;

//import org.json.simple.JSONArray;
//import org.json.simple.JSONObject;
//import org.json.simple.parser.JSONParser;

@RequestScoped
@Path("/products")
public class ProductResource {
	
	public static final String JSON_FILE="/Users/philippepossemiers/Desktop/Products.json";
	
	@GET
	@Produces({"text/html"})
	public String getProductsHTML() {	
		String htmlString = "<html><body>";
		
	       
		 
        try {
        	
        	InputStream fis = new FileInputStream(JSON_FILE);
	        JsonReader jsonReader = Json.createReader(fis);
	        JsonObject jsonObject = jsonReader.readObject();
	        JsonArray jsonArray = jsonObject.getJsonArray("products");
	        
	        int index = 0;
	        for(JsonValue value : jsonArray){
	        	JsonObject strings = jsonArray.getJsonObject(index++);
	        	htmlString += "<b>ShortName : " + strings.getString("name") + "</b><br>";
				htmlString += "Id : " + strings.getString("id") + "<br>";
				htmlString += "Brand : " + strings.getString("brand") + "<br>";
				htmlString += "Description : " + strings.getString("description") + "<br>";
				htmlString += "Price : " + strings.getString("price")+ "<br>";
				htmlString += "<br><br>";
	        }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
		return htmlString;
	}
	
	@GET
	@Produces({"application/json"})
	public String getProductsJSON() {
		String jsonString = "{\"products\" : [";
		try {
			InputStream fis = new FileInputStream(JSON_FILE);
	        JsonReader jsonReader = Json.createReader(fis);
	        JsonObject jsonObject = jsonReader.readObject();
	        JsonArray jsonArray = jsonObject.getJsonArray("products");
	        
	        int index = 0;
	        for(JsonValue value : jsonArray){
	        	JsonObject strings = jsonArray.getJsonObject(index++);
				jsonString += "{\"shortname\" : \"" + strings.getString("name") + "\",";
				jsonString += "\"id\" : " + strings.getString("id") + ",";
				jsonString += "\"brand\" : \"" + strings.getString("brand") + "\",";
				jsonString += "\"description\" : \"" + strings.getString("discription") + "\",";
				jsonString += "\"price\" : " + strings.getString("price") + "},";
			}
			jsonString = jsonString.substring(0, jsonString.length()-1);
			jsonString += "]}";
		} 
        catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jsonString;
	}
	
	@GET
	@Produces({"text/xml"})
	public String getProductsXML() {
		String content = "";
		InputStream fis;
		try {
			fis = new FileInputStream(JSON_FILE);
		
        JsonReader jsonReader = Json.createReader(fis);
			JsonObject json = jsonReader.readObject();;
			//content = XMLFormatter.toString(json);
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
		
			InputStream fis;
			try {
				fis = new FileInputStream(JSON_FILE);
			
	        JsonReader jsonReader = Json.createReader(fis);
	        JsonObject jsonObject = jsonReader.readObject();
	        JsonArray jsonArray = jsonObject.getJsonArray("products");
	        
	        int index = 0;
	        for(JsonValue value : jsonArray){
					JsonObject strings = jsonArray.getJsonObject(index++);
					jsonString += "{\"shortname\" : \"" + strings.getString("name") + "\",";
					jsonString += "\"id\" : " + strings.getString("id") + ",";
					jsonString += "\"brand\" : \"" + strings.getString("brand") + "\",";
					jsonString += "\"description\" : \"" + strings.getString("discription") + "\",";
					jsonString += "\"price\" : " + strings.getString("price") + "},";
				}
			
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
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