package it.polito.escape.verify.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import it.polito.escape.verify.resources.GraphCustomDeserializer;
import it.polito.escape.verify.resources.CustomMapSerializer;

@ApiModel(value = "Graph")
@XmlRootElement
@JsonDeserialize(using = GraphCustomDeserializer.class)
public class Graph {
	@ApiModelProperty(required = false, hidden = true)
	@XmlTransient
	private long id;
	
	@ApiModelProperty(name ="nodes", notes = "Nodes", dataType = "List[it.polito.escape.verify.model.Node]")
	private Map<Long, Node> nodes = new HashMap<Long,Node>();
	
	@ApiModelProperty(required = false, hidden = true)
	@XmlTransient
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private Set<Link> links = new HashSet<Link>();
	
	public Graph(){
		
	}
	
	public Graph(long id){
		this.id = id;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@JsonSerialize(using = CustomMapSerializer.class)
	public Map<Long, Node> getNodes() {
		return nodes;
	}
	
	public void setNodes(Map<Long, Node> nodes) {
		this.nodes = nodes;
	}
	
	@XmlTransient
	public Set<Link> getLinks() {
		return links;
	}

	public void setLinks(Set<Link> links) {
		this.links = links;
	}
	
	public void addLink(String url, String rel) {
		Link link = new Link();
		link.setLink(url);
		link.setRel(rel);
		links.add(link);
	}
	
	public Node searchNodeByName(String name){
		for (Node node : this.nodes.values()){
			if (node.getName().equals(name))
				return node;
		}
		return null;
	}
	
}