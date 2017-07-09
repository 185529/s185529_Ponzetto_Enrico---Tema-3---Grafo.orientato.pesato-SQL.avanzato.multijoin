package it.polito.tdp.formulaone.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import it.polito.tdp.formulaone.db.FormulaOneDAO;

public class Model {
	
	private List<Season> seasons;
	private SimpleDirectedWeightedGraph<Driver, DefaultWeightedEdge> graph;
	
	// variabili di stato della ricorsione
	
	private int tassoMin;
	private List<Driver> teamMin;
	
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
	
	public List<Driver> getDreamTeam(int K){
		
		Set<Driver> team = new HashSet<Driver>();
		
		this.tassoMin = Integer.MAX_VALUE;
		this.teamMin = null;
		
		recursive(0, team, K);
		
		return this.teamMin;
		
	}
	
	/**
	 * 
	 * Ricevo in ingresso il team parziale composto da "passo (che parte da zero)" elementi
	 * Termino quando passo=K
	 * Calcolo tasso di sconfitta, altrimenti procedo ricorsivamente e aggiungo un nuovo vertice (non ancora nel team) al passo+1
	 * 
	 * @param passo
	 * @param team
	 * @param K
	 * 
	 */
	private void recursive(int passo, Set<Driver> team, int K){
		
		// caso terminale
		
		if(passo==K){
			
			// calcolo tasso di sconfitta del team e aggiorno il minimo
			
			int tasso = this.tassoSconfitta(team);
			
			// eventuale aggiornamento minimo
			
			if(tasso<tassoMin){
				
				tassoMin = tasso;
				teamMin = new ArrayList<Driver>(team);
				
			}
			
		} else {
			
			// caso normale --- scelgo il prossimo vertice
			
			Set<Driver> candidati = new HashSet<Driver>(this.graph.vertexSet());
			candidati.removeAll(team);
			
			for(Driver d : candidati){
				team.add(d);
				recursive(passo+1, team, K);
				team.remove(d);
			}
			
		}
		
	}

	private int tassoSconfitta(Set<Driver> team) {
		
		int tasso = 0;
		
		for(DefaultWeightedEdge e : this.graph.edgeSet()){
			if(!team.contains(this.graph.getEdgeSource(e)) && team.contains(this.graph.getEdgeTarget(e))){
				tasso += graph.getEdgeWeight(e);
			}
		}
		
		return tasso;
		
	}

}
