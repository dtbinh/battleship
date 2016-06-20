package ca.kanoa.battleship.game;

import ca.kanoa.battleship.network.AIGame;
import ca.kanoa.battleship.network.BaseServer;
import org.newdawn.slick.SlickException;

import java.util.ArrayList;

/**
 * Creates a class to control the AI
 * @author Evan
 */
public class AI {

    //Creates variables for use in the class
    boolean win = false;
    boolean[] shipsSunk = {false,false,false,false,false}; //remaining ships is a array which holds whether or not the ships are sunk 0 is carrier, 1 is battleship, 2 is cruiser, 3 is sub and 4 is pt boat
    boolean searching = false;
    boolean check = false;
    boolean[] miss = {true,true,true,true};
    int x = 0;
    int xtemp = -1;
    long temp = 0;
    int y;
    int ytemp = -1;
    ArrayList filledGrids = new ArrayList();
    private Map myMap;
    private Map theirMap;
    private AIGame game;
    private BaseServer server;

    public AI(AIGame game, BaseServer server) throws SlickException {
        myMap = new Map("mymap", null, false);
        theirMap = new Map("theirmap", null, false);
        this.game = game;
        this.server = server;
    }

    /**
     * Gives the AI the option to place its ships into it's internal map
     */
    public void placeShips() { }

    /**
     * Gets called when the AI has sunk one of the players ships. Used to update the map
     * @param theirShip
     */
    public void sunkenShip(Ship theirShip) {
        // shipsSunk[theirShip.getType().getShipID() - 1] = true;
        miss = new boolean[]{true, true, true, true};
        xtemp = -1;
        ytemp = -1;
    }

    /**
     * Gets called for each time a player attacks the AI
     * @param x The x position of the player's attack
     * @param y The y position of the player's attack
     * @return The ship sunk if a player hit a ship, otherwise null
     */
    public Ship attack(int x, int y) { return null; }

    public void result(int x, int y, boolean newHit) {
        theirMap.place(new Marker(newHit, x, y));
        if (searching && !newHit) {
            if (miss[0]) miss[0] = false;
            else if (miss[1]) miss[1] = false;
            else if (miss[2]) miss[2] = false;
            else if (miss[3]) miss[3] = false;
        }
        if (newHit) {
            searching = true;
        }
    }

    /**
     * Causes the AI to determine where its next attack will be
     * @return An array of length two with the x and y coordinate
     */
    public int[] getAttack()  {

        //creates variables for use in the method
        String j;
        int multiplier = 0;
        int numberOfTiles = 0;
        long tiles;


        //Makes sure the AI has not hit a ship yet
        if (searching == false){
            server.console(game, "searching for ship...");


            //Checks to see if the Carrier and the Battleship are still in play
            if (!game.isPlayerShipSunk(ShipType.CARRIER) || !game.isPlayerShipSunk(ShipType.BATTLESHIP)){

                //Partitions the shooting to target the Carrier and the Battleship
                multiplier = 4;
                numberOfTiles = 20;

            }else if (!game.isPlayerShipSunk(ShipType.CRUISER) || !game.isPlayerShipSunk(ShipType.SUBMARINE)){ //Checks to see if the Cruiser and the Sub are still in play

                //Partitions the shooting to target the Cruiser and the Sub
                multiplier = 3;
                numberOfTiles = 33;

            }else if (!game.isPlayerShipSunk(ShipType.DESTROYER)){ //Checks to see if the destroyer is still in play

                //Partitions the shooting to target the Destroyer
                multiplier = 2;
                numberOfTiles = 50;

            }

            //Generates a tile
            tiles = Math.round(Math.random() * numberOfTiles) * multiplier;

            y=0;

            //Splits the number into rows and collums
            for (long i = tiles; i > 9 ; i = i-10){

                y++;
                temp = i;

            }

            if (tiles < 10) {
                temp = tiles;
            }

            if (temp > 9){
                temp = temp - 10;
                y++;

            }

            //Makes sure to hit odd numbered tiles for evenly partitioned numbers
            if (multiplier == 2 && y%2 == 1 || multiplier == 4 && y%2 == 1){
                if (temp==0){
                    temp = 9;
                }else {
                    temp = temp - 1;
                }
            }

            //converts long to string
            j = Long.toString(temp);
            x = Integer.parseInt(j);

            //Creates co-ordinants
            j = x + "," + y;

            //Makes sure that the co-ordinant has not already been generated
            if (filledGrids.indexOf(j) == -1) {
                filledGrids.add(j);

            }else {//Generates new co-ordinant
                return getAttack();
            }

            return new int [] {x,y};
        } else if (searching == true){
            server.console(game, "ship found... destroying...");
            //makes sure the point isn't against a wall
            if (miss[0] == false || miss[2] == false || xtemp == -1) {
                xtemp = x;
            }
            if (miss[1] == false || miss[3] == false || ytemp == -1) {
                ytemp = y;
            }
            if (xtemp > 0 && xtemp < 9 && ytemp > 0 && ytemp< 9){
                //Searches for the ship and seeks out after it
                if (miss[0] == true && miss [1] == true && miss [2] == true){
                    xtemp--;
                }else if (miss[0] == false && miss [1] == true && miss [2] == true){
                    ytemp--;
                }else if (miss[0] == false && miss [1] == false && miss [2] == true){
                    xtemp++;
                }else if (miss[0] == false && miss [1] == false && miss [2] == false){
                    ytemp++;
                }

            }else if (xtemp == 0 && xtemp < 9 && ytemp > 0 && ytemp < 9){//Searches for ship if ship is against a wall
                miss[0] = false;
                if (miss[0] == false && miss [1] == true && miss [2] == true){
                    ytemp--;

                }else if (miss[0] == false && miss [1] == false && miss [2] == true){
                    xtemp++;

                }else if (miss[0] == false && miss [1] == false && miss [2] == false){
                    ytemp++;

                }

            }else if (xtemp > 0 && xtemp == 9 && ytemp > 0 && ytemp < 9){ //Searches for ship if ship is against a wall
                if (miss[0] == true && miss [1] == true && miss [2] == true){
                    xtemp--;

                }else if (miss[0] == false && miss [1] == true && miss [2] == true){
                    ytemp--;

                    miss[2] = false;
                }else if (miss[0] == false && miss [1] == false && miss [2] == false){
                    ytemp++;
                }

            }else if (xtemp > 0 && xtemp < 9 && ytemp == 0 && ytemp < 9){ //Searches for ship if ship is against a wall
                if (miss[0] == true && miss [1] == true && miss [2] == true){
                    xtemp--;

                    miss[1] = false;
                }else if (miss[0] == false && miss [1] == false && miss [2] == true){
                    xtemp++;

                }else if (miss[0] == false && miss [1] == false && miss [2] == false){
                    ytemp++;
                }
            }else if (xtemp > 0 && xtemp < 9 && ytemp > 0 && ytemp == 9){ //Searches for ship if ship is against a wall
                if (miss[0] == true && miss [1] == true && miss [2] == true){
                    xtemp--;

                }else if (miss[0] == false && miss [1] == true && miss [2] == true){
                    ytemp--;

                }else if (miss[0] == false && miss [1] == false && miss [2] == true){
                    xtemp++;

                }

            }else if (xtemp == 0 && xtemp < 9 && ytemp == 0 && ytemp < 9){ //Searches for ship if ship is against two walls
                miss[0] = false;
                miss[1] = false;
                if (miss[0] == false && miss [1] == false && miss [2] == true){
                    xtemp++;

                }else if (miss[0] == false && miss [1] == false && miss [2] == false){
                    ytemp++;

                }

            }else if (xtemp == 0 && xtemp < 9 && ytemp > 0 && ytemp == 9){ //Searches for ship if ship is against two walls
                miss[0] = false;
                if (miss[0] == false && miss [1] == true && miss [2] == true){
                    ytemp--;
                }else if (miss[0] == false && miss [1] == false && miss [2] == true){
                    xtemp++;
                }

            }else if (xtemp > 0 && xtemp == 9 && ytemp == 0 && ytemp < 9){ //Searches for ship if ship is against two walls
                if (miss[0] == true && miss [1] == true && miss [2] == true){
                    xtemp--;

                    miss[1] = false;
                    miss[2] = false;
                }else if (miss[0] == false && miss [1] == false && miss [2] == false){
                    ytemp++;
                }

            }else if (xtemp > 0 && xtemp == 9 && ytemp > 0 && ytemp == 9){ //Searches for ship if ship is against two walls
                if (miss[0] == true && miss [1] == true && miss [2] == true){
                    xtemp--;

                }else if (miss[0] == false && miss [1] == true && miss [2] == true){
                    ytemp--;
                }

            }

            //Checks to make sure spot has not been guessed yet and commits
            if (miss[0] == true && miss [1] == true && miss [2] == true){

                check = myMap.checkSunkenShip(xtemp, y) != null;

                j = xtemp + "," + y;

                if (filledGrids.indexOf(j) == -1) {
                    filledGrids.add(j);

                    return new int [] {xtemp,y};

                }else {
                    getAttack();
                }

            }else if (miss[0] == false && miss [1] == true && miss [2] == true){

                j = x + "," + ytemp;

                if (filledGrids.indexOf(j) == -1) {
                    filledGrids.add(j);

                    return new int [] {x,ytemp};

                }else {
                    getAttack();
                }

            }else if (miss[0] == false && miss [1] == false && miss [2] == true){

                j = xtemp + "," + y;

                if (filledGrids.indexOf(j) == -1) {
                    filledGrids.add(j);

                    return new int [] {xtemp,y};

                }else {
                    getAttack();
                }

            }else if (miss[0] == false && miss [1] == false && miss [2] == false){

                j = x + "," + ytemp;

                if (filledGrids.indexOf(j) == -1) {
                    filledGrids.add(j);

                    return new int [] {x,ytemp};

                }else {
                    getAttack();
                }

            }

            if (shipsSunk[0] == false && game.isPlayerShipSunk(ShipType.CARRIER)){

                shipsSunk[0] = true;

                searching = false;

            }else if (shipsSunk[1] == false && game.isPlayerShipSunk(ShipType.BATTLESHIP)){
                shipsSunk[1] = true;

                searching = false;
            }else if (shipsSunk[2] == false && game.isPlayerShipSunk(ShipType.SUBMARINE)){
                shipsSunk[2] = true;

                searching = false;
            }else if (shipsSunk[3] == false && game.isPlayerShipSunk(ShipType.CRUISER)){
                shipsSunk[3] = true;

                searching = false;
            }else if (shipsSunk[4] == false && game.isPlayerShipSunk(ShipType.DESTROYER)){
                shipsSunk[4] = true;

                searching = false;
            }

        }


        return new int [] {x,y};

    }

}
