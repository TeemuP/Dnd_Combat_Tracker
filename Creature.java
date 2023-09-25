

public class Creature implements Comparable<Creature> {
    
    private String name;
    private int initiative;
    private int currentHp;
    private int maxHp;
    private String effects;

    // Boolean variables representing D&D 5e conditions
    private boolean blinded = false;
    private boolean charmed = false;
    private boolean deafened = false;
    private boolean exhausted = false;
    private boolean frightened = false;
    private boolean grappled = false;
    private boolean incapacitated = false;
    private boolean invisible = false;
    private boolean paralyzed = false;
    private boolean petrified = false;
    private boolean poisoned = false;
    private boolean prone = false;
    private boolean restrained = false;
    private boolean stunned = false;
    private boolean unconscious = false;
    private boolean concentrating = false;



    public Creature(String creatureName, int maxHp){
        this.name = creatureName;
        this.initiative = 0;
        this.currentHp = maxHp;
        this.maxHp = maxHp;

        this.effects = "";
    }

    public Creature(String creatureName, int maxHp, int initiative){
        this.name = creatureName;
        this.initiative = initiative;
        this.currentHp = maxHp;
        this.maxHp = maxHp;
        this.effects = "";
    }

    //Making sure we can sort our creatures based on their initiative count. Descending order
    @Override public int compareTo(Creature creature){
        int compareInitiative = creature.getInitiative();

        return compareInitiative - this.initiative;
    }

    public String getName(){
        return name;
    }

    public int getInitiative(){
        return initiative;
    }

    public int getCurrentHp(){
        return currentHp;
    }

    public int getMaxHp(){
        return maxHp;
    }

    public String getEffects(){
        return effects;
    }

    public void setName(String newName){
        name = newName;
    }

    public void setInitiative(int ini){
        initiative = ini;
    }

    public void setCurrentHp(int newHp){
        currentHp = newHp;
    }

    public void setMaxHp(int newMaxHp){
        currentHp = newMaxHp;
    }

    public void setEffects(String newEffects){
        effects = newEffects;
    }

    // Getters and setters for the conditions

    public boolean isBlinded() {
        return blinded;
    }

    public void setBlinded(boolean blinded) {
        this.blinded = blinded;
    }

    public boolean isCharmed() {
        return charmed;
    }

    public void setCharmed(boolean charmed) {
        this.charmed = charmed;
    }

    public boolean isDeafened() {
        return deafened;
    }

    public void setDeafened(boolean deafened) {
        this.deafened = deafened;
    }

    public boolean isExhausted() {
        return exhausted;
    }

    public void setExhausted(boolean exhausted) {
        this.exhausted = exhausted;
    }

    public boolean isFrightened() {
        return frightened;
    }

    public void setFrightened(boolean frightened) {
        this.frightened = frightened;
    }

    public boolean isGrappled() {
        return grappled;
    }

    public void setGrappled(boolean grappled) {
        this.grappled = grappled;
    }

    public boolean isIncapacitated() {
        return incapacitated;
    }

    public void setIncapacitated(boolean incapacitated) {
        this.incapacitated = incapacitated;
    }

    public boolean isInvisible() {
        return invisible;
    }

    public void setInvisible(boolean invisible) {
        this.invisible = invisible;
    }

    public boolean isParalyzed() {
        return paralyzed;
    }

    public void setParalyzed(boolean paralyzed) {
        this.paralyzed = paralyzed;
    }

    public boolean isPetrified() {
        return petrified;
    }

    public void setPetrified(boolean petrified) {
        this.petrified = petrified;
    }

    public boolean isPoisoned() {
        return poisoned;
    }

    public void setPoisoned(boolean poisoned) {
        this.poisoned = poisoned;
    }

    public boolean isProne() {
        return prone;
    }

    public void setProne(boolean prone) {
        this.prone = prone;
    }

    public boolean isRestrained() {
        return restrained;
    }

    public void setRestrained(boolean restrained) {
        this.restrained = restrained;
    }

    public boolean isStunned() {
        return stunned;
    }

    public void setStunned(boolean stunned) {
        this.stunned = stunned;
    }

    public boolean isUnconscious() {
        return unconscious;
    }

    public void setUnconscious(boolean unconscious) {
        this.unconscious = unconscious;
    }

    public void setConcentrating(boolean concentrating) {
        this.concentrating = concentrating;
    }

    public boolean isConcentrating() {
        return concentrating;
    }


    public String conditionsToString(){
        String result = "";
        if(this.blinded) result = result + "Blinded "; //Atks against have advantage, own atks disadvantage
        if(this.charmed) result = result + "Charmed ";
        if(this.deafened) result = result + "Deafened ";
        if(this.frightened) result = result + "Frightened ";
        if(this.grappled) result = result + "Grappled ";
        if(this.incapacitated) result = result + "Incapacitated "; //can't act or react
        if(this.invisible) result = result + "Invisible "; // atks against have disadvantage, own atks have advantage
        if(this.paralyzed) result = result + "Paralyzed "; // atks against have advantage, crit if within 5ft.
        if(this.petrified) result = result + "Petrified "; // atks against have advantage
        if(this.poisoned) result = result + "Poisoned "; // own atks have disadvantage
        if(this.prone) result = result + "Prone "; // own atks have disadvantage, ranged atks against have disadvantage, atks against within 5ft have advantage
        if(this.restrained) result = result + "Restrained "; // atks against have advantage, own atks have disadvantage
        if(this.stunned) result = result + "Stunned "; // atks against have advantage
        if(this.unconscious) result = result + "Unconscious "; //atks against have advantage, crit if within 5ft

        return result;
    }

    //Add a check conditions method to check if attacker has advantage or disadvantage on the attack
    //Returns 1 when attacker would have advantage and -1 when they would have disadvantage. 0 if it's just a normal roll.
    public int checkVantages(Boolean attacker, Boolean ranged){

        int total = 0;

        if(attacker && !ranged){
            if(this.invisible) total += 1;
            if(this.blinded || this.poisoned || this.prone || this.restrained) total -= 1;  
        }
        else if(!attacker && !ranged){
            //If it is the defender we check for any conditions that would give the attacker advantage
            if(this.blinded || this.prone || this.restrained || this.paralyzed || this.petrified || this.restrained || this.stunned || this.unconscious) total += 1;
            if(this.invisible) total -= 1;
        }
        else if(attacker && ranged){
            if(this.invisible) total += 1;
            if(this.blinded || this.poisoned || this.prone || this.restrained) total -= 1;  
        }
        else{
            //If it is the defender we check for any conditions that would give the attacker advantage
            //If the attack is ranged and the defender is prone means that the attack has disadvantage.
            if(this.blinded || this.restrained || this.paralyzed || this.petrified || this.restrained || this.stunned || this.unconscious) total += 1;
            if(this.invisible || this.prone) total -= 1;
        }
        

        return total;
    }

    //To string method
    @Override
    public String toString(){
        return this.name + ", " + this.initiative + ", " + this.currentHp + "/ " + this.maxHp;
    }
    
}
