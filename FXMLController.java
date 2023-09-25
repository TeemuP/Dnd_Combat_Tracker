

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;

import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;

/*
 * This controller controls the combatview FXML file.
 * It is also in charge of keeping track of turns and rounds.
 */

public class FXMLController {

    private Stage stage;
    private Scene scene;
    private Parent root;

    ArrayList<Creature> combatantList = new ArrayList<Creature>();
    private ObservableList<Creature> creatures = FXCollections.observableArrayList(); // For the tableview <-- NEED A CHANGE LISTENER

    int roundNumber = 0;
    int turnCounter = 0;
    Boolean combatStarted = false;
    Creature selection;
    
    @FXML
    private Button btn_add_creature;

    @FXML
    private Button btn_start_combat;

    @FXML
    private TableView<Creature> tblview_tracker;

    @FXML
    private TableColumn<Creature, String> tbl_col_name;

    @FXML
    private TableColumn<Creature, Integer> tbl_col_initiative;

    @FXML
    private TableColumn<Creature, Integer> tbl_col_hp;

    @FXML
    private TableColumn<Creature, Integer> tbl_col_maxhp;

    @FXML
    private Label lbl_selected_name;

    @FXML
    private TextArea ta_combat_log;

    @FXML
    private Button btn_attack;

    @FXML
    private Button btn_conditions;

    @FXML
    private Button btn_heal;

    @FXML
    private Button btn_ranged_attack;

    @FXML
    private Button btn_effects;

    //Allows for switching back to the add creatureview 
    //NOTE: Round number resets if done in the middle of combat.
    @FXML
    public void switchToAddCreatureView(ActionEvent event) throws IOException {

         Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Return to Add Creature View");
            alert.setHeaderText("Are you sure you want to leave this view?" + "\n" + "This will reset the round counter.");

            alert.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK){
                        
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("addcreatureview.fxml"));	
                        try {
                            root = loader.load();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                        FXMLController2 FXMLController2 = loader.getController();
                        FXMLController2.getList(combatantList);

                        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
                        scene = new Scene(root);
                        stage.setScene(scene);
                        stage.show();

                    }
            });

        
    }


    //Initialize the tableview
    @FXML
    public void initialize(){
        tbl_col_name.setCellValueFactory(new PropertyValueFactory<Creature, String>("name"));
        tbl_col_initiative.setCellValueFactory(new PropertyValueFactory<Creature, Integer>("initiative"));
        tbl_col_initiative.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        tbl_col_hp.setCellValueFactory(new PropertyValueFactory<Creature, Integer>("currentHp"));
        tbl_col_hp.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        tbl_col_maxhp.setCellValueFactory(new PropertyValueFactory<Creature, Integer>("maxHp"));
        tblview_tracker.setItems(creatures);
        tblview_tracker.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        tblview_tracker.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                    selection = newSelection;
                    lbl_selected_name.setText(selection.getName() + ", " + selection.getCurrentHp() + "/" + selection.getMaxHp() + "HP");
                }
            });
    }

    @FXML
    public void setOnHpEditCommit(CellEditEvent<Creature, Integer> event){
        Creature creature = event.getRowValue();
        if(creature.getMaxHp() < event.getNewValue()) creature.setCurrentHp(creature.getMaxHp());
        else creature.setCurrentHp(event.getNewValue());
        lbl_selected_name.setText(selection.getName() + ", " + selection.getCurrentHp() + "/" + selection.getMaxHp() + "HP");
    }

    @FXML
    public void setOnInitiativeEditCommit(CellEditEvent<Creature, Integer> event){
        Creature creature = event.getRowValue();
        creature.setInitiative(event.getNewValue());      
    }

   

    @FXML
    private void handleStartCombatButtonClick(ActionEvent event) {
        
        //If combat has been started already we don't do anything with this button. <-- SET DISABLED WHEN COMBAT ACTIVE
        if(combatStarted == true){
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Combat has already been initiated!");
            alert.setHeaderText(null);
            alert.setContentText("Combat has already been initiated!");

            alert.showAndWait();
        }
        else if(creatures.isEmpty()){
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("No combatants!");
            alert.setHeaderText(null);
            alert.setContentText("Please add creatures to the encounter before trying again!");

            alert.showAndWait();
        }
        //Else we initiate combat.
        else{
            combatStarted = true;
            roundNumber++;
            ta_combat_log.appendText("ROUND " + roundNumber + "\n");
            ta_combat_log.appendText("[" + creatures.get(turnCounter).getName() + "'s turn]" + "\n");
            String cond = creatures.get(turnCounter).conditionsToString();
                if( cond == "");
                else{
                    ta_combat_log.appendText("** " + creatures.get(turnCounter).getName()+ " is still " + cond + "\n");
                }
        }
    }

    @FXML
    private void handleEndCombatButtonClick(ActionEvent event) {
        
        //If combat has not been started we don't do anything with this button. 
        if(combatStarted == false){
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Combat has not been initiated!");
            alert.setHeaderText(null);
            alert.setContentText("Combat has not been initiated!");

            alert.showAndWait();
        }
        //Else we end combat.
        else{
            combatStarted = false;
            roundNumber = 0;
            turnCounter = 0;
            ta_combat_log.appendText("\n" + "COMBAT HAS ENDED " + "\n");
           
        }
    }

    @FXML
    private void handleEndTurnButtonClick(ActionEvent event) {
        
        //If combat has not been started already we don't do anything with this button. <-- SET ACTIVE WHEN COMBAT ACTIVE
        if(combatStarted == false){
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Combat has not been initiated!");
            alert.setHeaderText(null);
            alert.setContentText("Please start combat before trying again.");

            alert.showAndWait();
        }
        //Else we initiate combat.
        else{
            //Make sure that our turn counter does not exceed the size of the list.
            if(turnCounter + 1 >= creatures.size()){
                turnCounter = 0;
                roundNumber++;
                ta_combat_log.appendText("\n" + "ROUND " + roundNumber + "\n");
                ta_combat_log.appendText("[" +creatures.get(turnCounter).getName() + "'s turn]" + "\n");
                String cond = creatures.get(turnCounter).conditionsToString();
                if( cond == "");
                else{
                    ta_combat_log.appendText("** " + creatures.get(turnCounter).getName()+ " is still " + cond + "\n");
                }
                String eff = creatures.get(turnCounter).getEffects();
                if( eff == "");
                else{
                    ta_combat_log.appendText("** " + creatures.get(turnCounter).getName()+ " is still " + eff + "\n");
                }
            }
            else{
                turnCounter++;
                ta_combat_log.appendText("[" +creatures.get(turnCounter).getName() + "'s turn]" + "\n");
                String cond = creatures.get(turnCounter).conditionsToString();
                if( cond == "");
                else{
                    ta_combat_log.appendText("** " + creatures.get(turnCounter).getName()+ " is still " + cond + "\n");
                }
                String eff = creatures.get(turnCounter).getEffects();
                if( eff == "");
                else{
                    ta_combat_log.appendText("** " + creatures.get(turnCounter).getName()+ " is still " + eff + "\n");
                }
            }
            
        }
    }

    //TODO
    //Error handling and clean up
    //Test if concentration checks work - OK
    // Get both melee and ranged attacks under one button

    //Attack button logic 
    @FXML
    public void handleAttackMButtonClick(ActionEvent event){
        //If combat has not been started already we don't do anything with this button. <-- SET ACTIVE WHEN COMBAT ACTIVE
        if(combatStarted == false){
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Combat has not been initiated!");
            alert.setHeaderText(null);
            alert.setContentText("Please start combat before trying again.");

            alert.showAndWait();
        }
        else if(selection == null){
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("No Target Selected");
            alert.setHeaderText(null);
            alert.setContentText("Please select a target for the attack!");

            alert.showAndWait();
        }
        else{
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Attack Confirmation Alert");
            alert.setHeaderText("Enter damage OR 0 if it missed");

            int atk = creatures.get(turnCounter).checkVantages(true, false);
            int def = selection.checkVantages(false, false);
            String atkModifier = "";

            if(atk + def >= 1) atkModifier = "with advantage";
            else if (atk + def <= -1) atkModifier = "with disadvantage";
            else atkModifier = "";


            GridPane gridPane = new GridPane();
            Label lbl_attack_info = new Label(creatures.get(turnCounter).getName() + " is attacking " + selection.getName() + " " + atkModifier);
            NumericTextField DamageField = new NumericTextField();
            Label lbl_dmg = new Label("Damage");
            gridPane.add(lbl_attack_info, 0, 0);
            gridPane.add(lbl_dmg, 0, 1);
            gridPane.add(DamageField, 1, 1);
            alert.getDialogPane().setContent(gridPane);

            alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                String damage = DamageField.getText();
                if(damage == "") damage = "0";
                int dmg = Integer.parseInt(damage);
                if(dmg <= 0){
                    ta_combat_log.appendText("- " + creatures.get(turnCounter).getName() + "'s attack missed " + selection.getName() + "\n");
                }
                else{
                    ta_combat_log.appendText("- " + creatures.get(turnCounter).getName() + "'s attack hit " + selection.getName() + " for " + dmg + " dmg" + "\n");
                    
                    //Change the hp of the creature that was attacked and check if it was concentrating. 
                    if(selection.isConcentrating()){
                        int dc = Math.floorDiv(dmg, 2);
                        if(dc < 10) dc = 10;
                        ta_combat_log.appendText("** " + selection.getName() + " needs to make a concentration check DC: " + dc + "\n");
                    }
                    //if HP goes below 0 set it to 0
                    int newHpValue = selection.getCurrentHp() - dmg;
                    if(newHpValue < 0) newHpValue = 0;
                    if(newHpValue == 0) {
                        ta_combat_log.appendText("!! " + selection.getName() + " is now Uncouncious" + "\n");
                        selection.setUnconscious(true);

                        //If the creature was concentrating on a spell it drops.
                        if(selection.isConcentrating()){
                            selection.setConcentrating(false);
                            ta_combat_log.appendText("** " + selection.getName() + " is no longer Concentrating" + "\n");
                        }
                    }
                 
                    selection.setCurrentHp(newHpValue); 
                    lbl_selected_name.setText(selection.getName() + ", " + selection.getCurrentHp() + "/" + selection.getMaxHp() + "HP");
                    tblview_tracker.refresh(); //<-- WORKS BUT IT IS NOT IDEAL.
                }
                
            }
        });
        }
    }

     //Ranged attack button logic 
    @FXML
    public void handleAttackRButtonClick(ActionEvent event){
        //If combat has not been started already we don't do anything with this button. <-- SET ACTIVE WHEN COMBAT ACTIVE
        if(combatStarted == false){
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Combat has not been initiated!");
            alert.setHeaderText(null);
            alert.setContentText("Please start combat before trying again.");

            alert.showAndWait();
        }
        else if(selection == null){
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("No Target Selected");
            alert.setHeaderText(null);
            alert.setContentText("Please select a target for the attack!");

            alert.showAndWait();
        }
        else{
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Ranged Attack Confirmation Alert");
            alert.setHeaderText("Enter damage OR 0 if it missed");

            int atk = creatures.get(turnCounter).checkVantages(true, true);
            int def = selection.checkVantages(false, true);
            String atkModifier = "";

            if(atk + def >= 1) atkModifier = "with advantage";
            else if (atk + def <= -1) atkModifier = "with disadvantage";
            else atkModifier = "";


            GridPane gridPane = new GridPane();
            Label lbl_attack_info = new Label(creatures.get(turnCounter).getName() + " is attacking " + selection.getName() + " " + atkModifier);
            NumericTextField DamageField = new NumericTextField();
            Label lbl_dmg = new Label("Damage");
            gridPane.add(lbl_attack_info, 0, 0);
            gridPane.add(lbl_dmg, 0, 1);
            gridPane.add(DamageField, 1, 1);
            alert.getDialogPane().setContent(gridPane);

            alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                String damage = DamageField.getText();
                int dmg = Integer.parseInt(damage);
                if(dmg <= 0){
                    ta_combat_log.appendText("- " + creatures.get(turnCounter).getName() + "'s attack missed " + selection.getName() + "\n");
                }
                else{
                    ta_combat_log.appendText("- " + creatures.get(turnCounter).getName() + "'s attack hit " + selection.getName() + " for " + dmg + " dmg" + "\n");
                    
                    //Change the hp of the creature that was attacked and check if it was concentrating. 
                    if(selection.isConcentrating()){
                        int dc = Math.floorDiv(dmg, 2);
                        if(dc < 10) dc = 10;
                        ta_combat_log.appendText("** " + selection.getName() + " needs to make a concentration check DC: " + dc + "\n");
                    }
                    //if HP goes below 0 set it to 0
                    int newHpValue = selection.getCurrentHp() - dmg;
                    if(newHpValue < 0) newHpValue = 0;
                    if(newHpValue == 0) {
                        ta_combat_log.appendText("!! " + selection.getName() + " is now Uncouncious" + "\n");
                        selection.setUnconscious(true);

                        //If the creature was concentrating on a spell it drops.
                        if(selection.isConcentrating()){
                            selection.setConcentrating(false);
                            ta_combat_log.appendText("** " + selection.getName() + " is no longer Concentrating" + "\n");
                        }
                    }
                 
                    selection.setCurrentHp(newHpValue); 
                    lbl_selected_name.setText(selection.getName() + ", " + selection.getCurrentHp() + "/" + selection.getMaxHp() + "HP");
                    tblview_tracker.refresh(); //<-- WORKS BUT IT IS NOT IDEAL.
                }
                
            }
        });
        }
    }

    //Heal button logic 
    @FXML
    public void handleHealButtonClick(ActionEvent event){
        //If combat has not been started already we don't do anything with this button. <-- SET ACTIVE WHEN COMBAT ACTIVE
        if(combatStarted == false){
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Combat has not been initiated!");
            alert.setHeaderText(null);
            alert.setContentText("Please start combat before trying again.");

            alert.showAndWait();
        }
        else if(selection == null){
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("No Target Selected");
            alert.setHeaderText(null);
            alert.setContentText("Please select a target for the heal!");

            alert.showAndWait();
        }
        else{
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Heal Confirmation Alert");
            alert.setHeaderText("Enter healed amount");

            GridPane gridPane = new GridPane();
            Label lbl_heal_info = new Label(creatures.get(turnCounter).getName() + " is healing " + selection.getName());
            NumericTextField HealField = new NumericTextField();
            Label lbl_healing = new Label("Heal Amount:");
            gridPane.add(lbl_heal_info, 0, 0);
            gridPane.add(lbl_healing, 0, 1);
            gridPane.add(HealField, 1, 1);
            alert.getDialogPane().setContent(gridPane);

            alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                String heal = HealField.getText();
                int healing = Integer.parseInt(heal);

                    ta_combat_log.appendText("- " + creatures.get(turnCounter).getName() + " healed " + selection.getName() + " for " + healing + " HP" + "\n");
                    
                    
                    //if HP goes above max set it to max
                    int newHpValue = selection.getCurrentHp() + healing;
                    if(newHpValue > selection.getMaxHp()) newHpValue = selection.getMaxHp();
                    if(selection.isUnconscious()) {
                        ta_combat_log.appendText("!! " + selection.getName() + " is no longer Uncouncious");
                        selection.setUnconscious(false);                       
                    }
                 
                    selection.setCurrentHp(newHpValue); 
                    lbl_selected_name.setText(selection.getName() + ", " + selection.getCurrentHp() + "/" + selection.getMaxHp() + "HP");
                    tblview_tracker.refresh(); //<-- WORKS BUT IT IS NOT IDEAL.
                
                
            }
        });
        }
    }

    //Special creature effects button logic 
    @FXML
    public void handleEffectsButtonClick(ActionEvent event){
        //If combat has not been started already we don't do anything with this button. <-- SET ACTIVE WHEN COMBAT ACTIVE
        if(selection == null){
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("No Creature Selected");
            alert.setHeaderText(null);
            alert.setContentText("Please choose a creature from the initiative list first.");

            alert.showAndWait();
        }
        else{
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("New Effect Confirmation Alert");
            alert.setHeaderText("Enter the new effect name OR clear to remove all.");

            String activeEffects = "";
            if(selection.getEffects().equals("")) activeEffects = "under no effects.";
            else activeEffects = selection.getEffects();

            GridPane gridPane = new GridPane();
            Label lbl_effect_info = new Label(selection.getName() + " is currently " + activeEffects);
            TextField EffectField = new TextField();
            Label lbl_new_effect = new Label("New effect:");
            gridPane.add(lbl_effect_info, 0, 0);
            gridPane.add(lbl_new_effect, 0, 1);
            gridPane.add(EffectField, 1, 1);
            alert.getDialogPane().setContent(gridPane);

            alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                String newEffects = "";
                if(EffectField.getText().equals("clear")) {
                    newEffects = "";
                    ta_combat_log.appendText("- " + selection.getName() + " is no longer " + selection.getEffects() + "\n");  
                }
                else {
                    newEffects = selection.getEffects() + " " + EffectField.getText();
                    ta_combat_log.appendText("- " + selection.getName() + " is now " + newEffects + "\n");  
                }
      
                
                selection.setEffects(newEffects); 
                
                
            }
        });
        }
    }



    //Alert in which you can select conditions. FIND IF THERE IS A BETTER WAY OF DONG THIS
    @FXML
    public void handleConditionButtonClick(ActionEvent event){

        if(selection == null){
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("No Creature Selected");
            alert.setHeaderText(null);
            alert.setContentText("Please choose a creature from the initiative list first.");

            alert.showAndWait();
        }
        else
        {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Creature Conditions");
            alert.setHeaderText( selection.getName() + "'s Conditions");

            // Create CheckBoxes for D&D 5e conditions
            CheckBox blindedCheckBox = new CheckBox("Blinded");
            CheckBox invisibleCheckBox = new CheckBox("Invisible");
            CheckBox charmedCheckBox = new CheckBox("Charmed");
            CheckBox deafenedCheckBox = new CheckBox("Deafened");
            CheckBox frightenedCheckBox = new CheckBox("Frightened");
            CheckBox grappledCheckBox = new CheckBox("Grappled");
            CheckBox incapacitatedCheckBox = new CheckBox("Incapacitated");
            CheckBox paralyzedCheckBox = new CheckBox("Paralyzed");
            CheckBox petrifiedCheckBox = new CheckBox("Petrified");
            CheckBox poisonedCheckBox = new CheckBox("Poisoned");
            CheckBox proneCheckBox = new CheckBox("Prone");
            CheckBox restrainedCheckBox = new CheckBox("Restrained");
            CheckBox stunnedCheckBox = new CheckBox("Stunned");
            CheckBox unconsciousCheckBox = new CheckBox("Unconscious");
            CheckBox concentratingCheckBox = new CheckBox("Concentrating");

            //Let's set them on/off based on the creature's status.
            /*
            * blinded
            * charmed
            * deafened
            * frightened
            * grappled
            * incapacitated
            * invisible
            * paralyzed --> incapacitated
            * petrified --> incapacitated
            * poisoned
            * prone
            * restrained
            * stunned --> incapacitated
            * uncouncious --> incapacitated
            */

            blindedCheckBox.setSelected(selection.isBlinded());
            invisibleCheckBox.setSelected(selection.isInvisible());
            charmedCheckBox.setSelected(selection.isCharmed());
            deafenedCheckBox.setSelected(selection.isDeafened());
            frightenedCheckBox.setSelected(selection.isFrightened());
            grappledCheckBox.setSelected(selection.isGrappled());
            incapacitatedCheckBox.setSelected(selection.isIncapacitated());
            paralyzedCheckBox.setSelected(selection.isParalyzed());
            petrifiedCheckBox.setSelected(selection.isPetrified());
            poisonedCheckBox.setSelected(selection.isPoisoned());
            proneCheckBox.setSelected(selection.isProne());
            restrainedCheckBox.setSelected(selection.isRestrained());
            stunnedCheckBox.setSelected(selection.isStunned());
            unconsciousCheckBox.setSelected(selection.isUnconscious());
            concentratingCheckBox.setSelected(selection.isConcentrating());

            //Add listeners for all checkboxes

            blindedCheckBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                    selection.setBlinded(newValue);
                    if(newValue){
                        ta_combat_log.appendText("** " + selection.getName() + " is now Blinded" + "\n");
                    }
                    else{
                        ta_combat_log.appendText("** " + selection.getName() + " is no longer Blinded" + "\n");
                    }
                }
            });
            charmedCheckBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                    selection.setCharmed(newValue);
                    if(newValue){
                        ta_combat_log.appendText("** " + selection.getName() + " is now Charmed" + "\n");
                    }
                    else{
                        ta_combat_log.appendText("** " + selection.getName() + " is no longer Charmed" + "\n");
                    }
                }
            });
            deafenedCheckBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                    selection.setDeafened(newValue);
                    if(newValue){
                        ta_combat_log.appendText("** " + selection.getName() + " is now Deafened" + "\n");
                    }
                    else{
                        ta_combat_log.appendText("** " + selection.getName() + " is no longer Deafened" + "\n");
                    }
                }
            });
            frightenedCheckBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                    selection.setFrightened(newValue);
                    if(newValue){
                        ta_combat_log.appendText("** " + selection.getName() + " is now Frightened" + "\n");
                    }
                    else{
                        ta_combat_log.appendText("** " + selection.getName() + " is no longer Frightened" + "\n");
                    }
                }
            });
            grappledCheckBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                    selection.setGrappled(newValue);
                    if(newValue){
                        ta_combat_log.appendText("** " + selection.getName() + " is now Grappled" + "\n");
                    }
                    else{
                        ta_combat_log.appendText("** " + selection.getName() + " is no longer Grappled" + "\n");
                    }
                }
            });
            incapacitatedCheckBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                    selection.setIncapacitated(newValue);
                    if(newValue){
                        ta_combat_log.appendText("** " + selection.getName() + " is now Incapacitated" + "\n");
                    }
                    else{
                        ta_combat_log.appendText("** " + selection.getName() + " is no longer Incapacitated" + "\n");
                    }
                }
            });
            paralyzedCheckBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                    selection.setParalyzed(newValue);
                    if(newValue){
                        ta_combat_log.appendText("** " + selection.getName() + " is now Paralyzed" + "\n");
                    }
                    else{
                        ta_combat_log.appendText("** " + selection.getName() + " is no longer Paralyzed" + "\n");
                    }
                }
            });
            petrifiedCheckBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                    selection.setPetrified(newValue);
                    if(newValue){
                        ta_combat_log.appendText("** " + selection.getName() + " is now Petrified" + "\n");
                    }
                    else{
                        ta_combat_log.appendText("** " + selection.getName() + " is no longer Petrified" + "\n");
                    }
                }
            });
            poisonedCheckBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                    selection.setPoisoned(newValue);
                    if(newValue){
                        ta_combat_log.appendText("** " + selection.getName() + " is now Poisoned" + "\n");
                    }
                    else{
                        ta_combat_log.appendText("** " + selection.getName() + " is no longer Poisoned" + "\n");
                    }
                }
            });
            restrainedCheckBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                    selection.setRestrained(newValue);
                    if(newValue){
                        ta_combat_log.appendText("** " + selection.getName() + " is now Restrained" + "\n");
                    }
                    else{
                        ta_combat_log.appendText("** " + selection.getName() + " is no longer Restrained" + "\n");
                    }
                }
            });
            stunnedCheckBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                    selection.setStunned(newValue);
                    if(newValue){
                        ta_combat_log.appendText("** " + selection.getName() + " is now Stunned" + "\n");
                    }
                    else{
                        ta_combat_log.appendText("** " + selection.getName() + " is no longer Stunned" + "\n");
                    }
                }
            });
            unconsciousCheckBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                    selection.setUnconscious(newValue);
                    if(newValue){
                        ta_combat_log.appendText("!! " + selection.getName() + " is now Uncouncious" + "\n");
                    }
                    else{
                        ta_combat_log.appendText("!! " + selection.getName() + " is no longer Uncouncious" + "\n");
                    }
                }
            });
            concentratingCheckBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                    selection.setConcentrating(newValue);
                    if(newValue){
                        ta_combat_log.appendText("** " + selection.getName() + " is now Concentrating" + "\n");
                    }
                    else{
                        ta_combat_log.appendText("** " + selection.getName() + " is no longer Concentrating" + "\n");
                    }
                }
            });
            proneCheckBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                    selection.setProne(newValue);
                    if(newValue){
                        ta_combat_log.appendText("** " + selection.getName() + " is now Prone" + "\n");
                    }
                    else{
                        ta_combat_log.appendText("** " + selection.getName() + " is no longer Prone" + "\n");
                    }
                }
            });
            invisibleCheckBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                    selection.setInvisible(newValue);
                    if(newValue){
                        ta_combat_log.appendText("** " + selection.getName() + " is now Invisible" + "\n");
                    }
                    else{
                        ta_combat_log.appendText("** " + selection.getName() + " is no longer Invisible" + "\n");
                    }
                }
            });



            VBox vbox = new VBox(10,
                    blindedCheckBox, invisibleCheckBox, charmedCheckBox, deafenedCheckBox, frightenedCheckBox,
                    grappledCheckBox, incapacitatedCheckBox, paralyzedCheckBox, petrifiedCheckBox,
                    poisonedCheckBox, proneCheckBox, restrainedCheckBox, stunnedCheckBox,
                    unconsciousCheckBox, concentratingCheckBox
            );

            // Set the content of the Alert to the VBox
            alert.getDialogPane().setContent(vbox);

            alert.showAndWait();
        }
    }




    //Get the list containing the creatures and sort them. Better option would be to make the whole process work through obsevable lists but this'll do for now.
    public void getList(ArrayList<Creature> list){
        combatantList = list;

        Collections.sort(combatantList);

        for(int i = 0; i < combatantList.size(); i++){
            creatures.add(combatantList.get(i));
            
        }
    }





}
