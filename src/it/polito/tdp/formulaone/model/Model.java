package it.polito.tdp.formulaone.model;

import java.util.List;

import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import it.polito.tdp.formulaone.db.FormulaOneDAO;

public class Model {
	
	private List<Season> seasons;
	private SimpleDirectedWeightedGraph<Driver, DefaultWeightedEdge> graph;
	
	public List<Season> getSeasons(){
		
		if(seasons==null){
			FormulaOneDAO dao = new FormulaOneDAO();
			this.seasons = dao.getAllSeasons();
		}
		
		return this.seasons;
		
	}

	public void creaGrafo(Season s){
		
		this.graph = new SimpleDirectedWeightedGraph<Driver, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		
		FormulaOneDAO dao = new FormulaOneDAO();
		
		List<Driver> drivers = dao.getDriversForSeason(s);
		
		// creazione vertici --- tutti i piloti arrivati in quella stagione
		
		Graphs.addAllVertices(this.graph, drivers);
		
		// creazione archi --- un pilota in una gara è arrivato prima di un altro arrivato nella stessa gara
		
		for(Driver d1 : this.graph.vertexSet()){
			for(Driver d2 : this.graph.vertexSet()){
				if(!d1.equals(d2)){
					Integer vittorie = dao.contaVittorie(d1, d2, s);
					if(vittorie!=0){
						Graphs.addEdgeWithVertices(this.graph, d1, d2, vittorie);
					}
				}
			}
		}
		
	}
	
	public Driver getBestDriver(){
		
		Driver best = null;
		Integer max = Integer.MIN_VALUE;
		
		for(Driver d : this.graph.vertexSet()){
			
			int peso = 0;
			
			for(DefaultWeightedEdge e : graph.outgoingEdgesOf(d)){
				peso += graph.getEdgeWeight(e);
			}
			
			for(DefaultWeightedEdge e : graph.incomingEdgesOf(d)){
				peso -= graph.getEdgeWeight(e);
			}
			
			if(peso>max){
				max = peso;
				best = d;
			}
			
		}
		
		return best;
		
	}

}
