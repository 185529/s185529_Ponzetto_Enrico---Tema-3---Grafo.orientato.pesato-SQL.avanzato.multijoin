package it.polito.tdp.formulaone;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import it.polito.tdp.formulaone.model.Driver;
import it.polito.tdp.formulaone.model.Model;
import it.polito.tdp.formulaone.model.Season;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class FormulaOneController {
	
	Model model;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private ComboBox<Season> boxAnno;

    @FXML
    private TextField textInputK;

    @FXML
    private TextArea txtResult;

    @FXML
    void doCreaGrafo(ActionEvent event) {
    	
    	Season s = boxAnno.getValue();
    	
    	if(s==null){
    		txtResult.appendText("ERRORE: Selezionare una stagione.\n");
    	}
    	
    	model.creaGrafo(s);
    	
    	Driver best = model.getBestDriver();
    	
    	txtResult.appendText(String.format("Il pilota migliore della stagione %d è stato %s\n", s.getYear().getValue(), best.toString()));

    }

    @FXML
    void doTrovaDreamTeam(ActionEvent event) {
    	
    	String input = textInputK.getText();
    	
    	int K;
    	
    	try {
    		
			K = Integer.parseInt(input);
			
		} catch (NumberFormatException e) {
			
			e.printStackTrace();
			txtResult.setText("ERRORE: Inserire un numero.\n");
			return;
			
		}
    	
    	Long start = System.nanoTime();
    	
    	List<Driver> dreamTeam = model.getDreamTeam(K);
    	
    	Long stop = System.nanoTime();
    	
    	txtResult.appendText("\nDREAM TEAM (N. "+K+" PILOTI IN "+(stop-start)/(1e9*60)+" MINUTI):\n");
    	
    	for(Driver d : dreamTeam){
    		txtResult.appendText(d.toString()+"\n");
    	}

    }

    @FXML
    void initialize() {
        assert boxAnno != null : "fx:id=\"boxAnno\" was not injected: check your FXML file 'FormulaOne.fxml'.";
        assert textInputK != null : "fx:id=\"textInputK\" was not injected: check your FXML file 'FormulaOne.fxml'.";
        assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'FormulaOne.fxml'.";

    }
    
    public void setModel(Model model){
    	
    	this.model = model;
    	
    	boxAnno.getItems().addAll(model.getSeasons());
    	
    }
}
