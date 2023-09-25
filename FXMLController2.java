

import java.io.IOException;
import java.util.ArrayList;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.Node;
import javafx.stage.Stage;
import javafx.util.converter.NumberStringConverter;

/**
 * This controller controls the addcreatureview FXML file. It handles the adding od creatures to the combatant arraylist.
 */

public class FXMLController2 {

    private Stage stage;
    private Scene scene;
    private Parent root;

    ArrayList<Creature> combatantList = new ArrayList<Creature>();
    
    @FXML
    private Button btn_add_creature;

    @FXML
    private Button btn_start_encounter;

    @FXML
    private Label lbl_num;

    @FXML
    private TextArea textarea_creature_log;

    @FXML
    private TextField txtfield_name;

    @FXML
    private TextField txtfield_max_hp;

    @FXML
    private TextField txtfield_initiative;

    public void initialize() {
        NumberStringConverter converter = new NumberStringConverter();
        TextFormatter<Number> textFormatter = new TextFormatter<>(converter);
        TextFormatter<Number> textFormatter2 = new TextFormatter<>(converter);

        txtfield_max_hp.setTextFormatter(textFormatter);
        txtfield_initiative.setTextFormatter(textFormatter2);
    }

    @FXML
    public void switchToCombatView(ActionEvent event) throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("combatview.fxml"));	
		root = loader.load();

        FXMLController FXMLController = loader.getController();
		FXMLController.getList(combatantList);

        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void handleAddButtonClick(ActionEvent event) {
        

        if(txtfield_name.getText().isEmpty() || txtfield_max_hp.getText().isEmpty() || txtfield_initiative.getText().isEmpty()){
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Notification");
            alert.setHeaderText(null);
            alert.setContentText("All fields must have a value!");

            alert.showAndWait();
        }
        else{
            if(textarea_creature_log.getText().contains("CLEARED")) textarea_creature_log.clear();
            String name = txtfield_name.getText();
            String hpString = txtfield_max_hp.getText();
            int maxHp = Integer.parseInt(hpString); //Add check to see if the entered values are numbers
            String initiativeString = txtfield_initiative.getText();
            int initiative = Integer.parseInt(initiativeString); //Add check to see if the entered values are numbers



            Creature creature = new Creature(name, maxHp, initiative);
            combatantList.add(creature);
            int searchIndex = combatantList.size() - 1;
            

            String appendString = "Name: " + combatantList.get(searchIndex).getName() 
            + ", HP: " + combatantList.get(searchIndex).getCurrentHp() 
            + ", Initiative: " + combatantList.get(searchIndex).getInitiative() + "\n";
        
            textarea_creature_log.appendText(appendString);

            lbl_num.setText(String.valueOf(combatantList.size()));
            txtfield_name.setText("");
            txtfield_max_hp.setText("");
            txtfield_initiative.setText("");
            
        }
   }

   @FXML
    private void handleClearButtonClick(ActionEvent event) {
        

        if(combatantList.isEmpty()){
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Notification");
            alert.setHeaderText(null);
            alert.setContentText("Combatant list is already empty!");

            alert.showAndWait();
        }
        else{
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Clear combatant list");
            alert.setHeaderText("Are you sure you want to clear the list?");

            alert.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK){
                        combatantList.clear();
                        textarea_creature_log.clear();
                        textarea_creature_log.appendText("CLEARED" + "\n");
                        lbl_num.setText(String.valueOf(combatantList.size()));

                    }
            });

            
            
        }
   }


   //TEST
   //Get the list containing the creatures and sort them. Better option would be to make the whole process work through obsevable lists but this'll do for now.
    public void getList(ArrayList<Creature> list){
        combatantList = list;

        for(int i = 0; i < combatantList.size(); i++){
            String appendString = "Name: " + combatantList.get(i).getName() 
            + ", HP: " + combatantList.get(i).getCurrentHp() 
            + ", Initiative: " + combatantList.get(i).getInitiative() + "\n";
        
            textarea_creature_log.appendText(appendString);
            
        }
    }

  

}
