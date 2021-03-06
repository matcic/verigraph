package it.polito.escape.verify.resources;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

import it.polito.escape.verify.exception.BadRequestException;
import it.polito.escape.verify.exception.InternalServerErrorException;
import it.polito.escape.verify.model.Graph;
import it.polito.escape.verify.model.Node;


/**
 * The Class GraphCustomDeserializer is a custom deserializer for a Graph object
 */
public class GraphCustomDeserializer extends JsonDeserializer<Graph>{

	/* (non-Javadoc)
	 * @see com.fasterxml.jackson.databind.JsonDeserializer#deserialize(com.fasterxml.jackson.core.JsonParser, com.fasterxml.jackson.databind.DeserializationContext)
	 */
	@Override
	public Graph deserialize(JsonParser jp, DeserializationContext context){
		JsonNode root = null;
		try {
			root = jp.getCodec().readTree(jp);
		}
		catch (JsonProcessingException e) {
			throw new InternalServerErrorException("Error parsing a graph: " + e.getMessage());
		}
		catch (IOException e) {
			throw new InternalServerErrorException("I/O error parsing a graph: " + e.getMessage());
		} 
		
		JsonNode nodesJson = root.get("nodes");
		
		List<Node> nodeList = null;
		try {
			nodeList = new ObjectMapper().readValue(nodesJson.toString(), TypeFactory.defaultInstance().constructCollectionType(List.class, Node.class));
		}
		catch (JsonParseException e) {
			throw new BadRequestException("Invalid content for a graph: " + e.getMessage());
		}
		catch (JsonMappingException e) {
			throw new BadRequestException("Invalid input json structure for a graph: " + e.getMessage());
		}
		catch (IOException e) {
			throw new InternalServerErrorException("I/O error parsing a graph: " + e.getMessage());
		}
		for (Node node : nodeList){
			System.out.println("Node name: " + node.getName());
		}
		Graph graph = new Graph();
		Map<Long, Node> nodes = graph.getNodes();
		
		long numberOfNodes = 0;
		for (Node node : nodeList){
			nodes.put(++numberOfNodes, node);
		}
		return graph;

	}

}
