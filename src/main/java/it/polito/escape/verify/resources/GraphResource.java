package it.polito.escape.verify.resources;

import java.io.File;
import java.net.URI;
import java.util.List;

import javax.servlet.ServletContext;
import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.glassfish.jersey.servlet.ServletContainer;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import it.polito.escape.verify.model.ErrorMessage;
import it.polito.escape.verify.model.Graph;
import it.polito.escape.verify.model.Node;
import it.polito.escape.verify.model.Verification;
import it.polito.escape.verify.resources.beans.VerificationBean;
import it.polito.escape.verify.service.GraphService;
import it.polito.escape.verify.service.VerificationService;
import it.polito.nffg.neo4j.jaxb.Paths;

@Path("/graphs")
@Api(value = "/graphs", description = "Manage graphs")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class GraphResource {
	GraphService graphService = new GraphService();
	VerificationService verificationService = new VerificationService();

	@GET
	@ApiOperation(httpMethod = "GET", value = "Returns all graphs", notes = "Returns an array of graphs", response = Graph.class, responseContainer = "List")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "All the graphs have been returned in the message body", response=Graph.class, responseContainer = "List") })
	public List<Graph> getGraphs() {
		return graphService.getAllGraphs();
	}

	@POST
	@ApiOperation(httpMethod = "POST", value = "Creates a graph", notes = "Creates a signle graph", response = Response.class)
	@ApiResponses(value = { @ApiResponse(code = 400, message = "Invalid graph supplied", response = ErrorMessage.class),
							@ApiResponse(code = 201, message = "Graph successfully created", response=Graph.class)})
	public Response addGraph(@ApiParam(value = "New graph object", required = true) Graph graph, @Context UriInfo uriInfo) {	
		Graph newGraph = graphService.addGraph(graph);
		String newId = String.valueOf(newGraph.getId());
		URI uri = uriInfo.getAbsolutePathBuilder().path(newId).build();
		return Response.created(uri).entity(newGraph).build();
	}

	@GET
	@Path("/{graphId}")
	@ApiOperation(httpMethod = "GET", value = "Returns a graph", notes = "Returns a signle graph", response = Graph.class)
	@ApiResponses(value = { @ApiResponse(code = 403, message = "Invalid graph id", response = ErrorMessage.class),
							@ApiResponse(code = 404, message = "Graph not found", response = ErrorMessage.class),
							@ApiResponse(code = 200, message = "The requested graph has been returned in the message body", response = Graph.class)})
	public Graph getGraph(@ApiParam(value = "Graph id", required = true) @PathParam("graphId") long graphId,
			@Context UriInfo uriInfo) {
		Graph graph = graphService.getGraph(graphId);
		graph.addLink(getUriForSelf(uriInfo, graph), "self");
		graph.addLink(getUriForNodes(uriInfo, graph), "nodes");
		return graph;
	}

	@PUT
	@Path("/{graphId}")
	@ApiOperation(httpMethod = "PUT", value = "Edits a graph", notes = "Edits a single graph", response = Graph.class)
	@ApiResponses(value = { @ApiResponse(code = 400, message = "Invalid graph object", response = ErrorMessage.class),
							@ApiResponse(code = 403, message = "Invalid graph id", response = ErrorMessage.class),
							@ApiResponse(code = 404, message = "Graph not found", response = ErrorMessage.class),
							@ApiResponse(code = 200, message = "Graph edited successfully", response = Graph.class)})
	public Graph updateGraph(@ApiParam(value = "Graph id", required = true) @PathParam("graphId") long id,
			@ApiParam(value = "Updated graph object", required = true) Graph graph) {
		graph.setId(id);
		return graphService.updateGraph(graph);
	}

	@DELETE
	@Path("/{graphId}")
	@ApiOperation(httpMethod = "DELETE", value = "Deletes a graph", notes = "Deletes a signle graph")
	@ApiResponses(value = { @ApiResponse(code = 403, message = "Invalid graph id", response = ErrorMessage.class),
							@ApiResponse(code = 204, message = "Graph successfully deleted")})
	public void deleteGraph(@ApiParam(value = "Graph id", required = true) @PathParam("graphId") long id) {
		graphService.removeGraph(id);
	}
	
	@GET
	@Path("/{graphId}/policy")
	@ApiOperation(
				httpMethod = "GET",
				value = "Verifies a given policy in a graph",
				notes = "In order to verify a given policy (e.g. 'reachability') all nodes of the desired graph must have a valid configuration.")
	@ApiResponses(value = { @ApiResponse(code = 403, message = "Invalid graph id or invalid configuration for source and/or destination node", response = ErrorMessage.class),
							@ApiResponse(code = 404, message = "Graph not found or source node not found or destination node not found or configuration for source and/or destination node not available", response = ErrorMessage.class),
							})
	public Verification verifyGraph(@Context ServletContext context, @ApiParam(value = "Graph id", required = true) @PathParam("graphId") long id, @ApiParam(value = "'source' and 'destination' must refer to names of existing nodes in the same graph, 'type' refers to the required verification between the two (e.g. 'reachability')", required = true) @BeanParam VerificationBean verificationBean) {
		Graph graph = graphService.getGraph(id);
		Paths paths = verificationService.getPaths(context.getRealPath(File.separator),graph, verificationBean);
		String result = verificationService.runTests(graph, paths, verificationBean.getSource(), verificationBean.getDestination());
		return new Verification(result);
	}

	private String getUriForSelf(UriInfo uriInfo, Graph graph) {
		String uri = uriInfo.getBaseUriBuilder()
				.path(GraphResource.class)
				.path(Long.toString(graph.getId()))
				.build()
				.toString();
		return uri;
	}

	private String getUriForNodes(UriInfo uriInfo, Graph graph) {
		String uri = uriInfo.getBaseUriBuilder()
				.path(GraphResource.class)
				.path(GraphResource.class, "getNodeResource")
				//.path(NodeResource.class)
				.resolveTemplate("graphId", graph.getId())
				.build().
				toString();
		return uri;
	}

	@Path("/{graphId}/nodes")
	public NodeResource getNodeResource() {
		return new NodeResource();
	}
}