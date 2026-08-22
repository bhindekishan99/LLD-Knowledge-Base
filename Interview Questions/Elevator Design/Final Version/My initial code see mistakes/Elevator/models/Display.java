package models;

import enums.*;

public class Display {

    public void notify(String id, int currentFloor, Direction currentDir, State currentState){
        System.out.println("Elevator information: "+id+currentFloor+currentDir+currentState);
    }
    
}
