package models;

import java.util.LinkedList;
import java.util.Queue;

import enums.*;

public class Elevator {
    String elevatorId;
    int currentFloor;
    Direction currentDir;
    State currState;
    Display display;
    Queue<Integer> requestQueue;

    public Elevator(String id, Display display){
        this.elevatorId = id;
        this.currentFloor = 0;
        this.currentDir = null;
        this.currState = State.HALT;
        this.display = display;
        this.requestQueue = new LinkedList<>();
    }

    public void moveTo(int floorNum){
        
        /*
        Logic:
            if currentFloor = floorNum
                currentState = Halt
                display.notify
                return
            
            if current state = Halt
                if( floorNum > currentFloor )
                    currentDir = UP
                    currentState = moving
                    while(currentFloor != FloorNum){
                        currentFloor++;
                        display.notify
                    }
                    currentState = Halt
                    return
                else // (floorNum < currentFloor)
                    currentDir = DOWN
                    currentState = moving
                    while(currentFloor != FloorNum){
                        currentFloor--;
                        display.notify
                    }
                    currentState = Halt
                    return
            
            if currentDir == UP
                if( floorNum > currentFloor )
                    while(currentFloor != FloorNum){
                            currentFloor++;
                            display.notify
                        }
                    currentState = Halt
                    return
                else // (floorNum < currentFloor)
                    currentDir = DOWN
                    while(currentFloor != FloorNum){
                        currentFloor--;
                        display.notify
                    }
                    currentState = Halt
                    return


            else// currentDir == DOWN
                if( floorNum > currentFloor )
                    currentDir = UP
                    while(currentFloor != FloorNum){
                            currentFloor++;
                            display.notify
                        }
                    currentState = Halt
                    return
                else // (floorNum < currentFloor)
                    while(currentFloor != FloorNum){
                        currentFloor--;
                        display.notify
                    }
                    currentState = Halt
                    return
        */
        display.notify(this.elevatorId,this.currentFloor,this.currentDir,this.currState);
    }

    public void addRequest(int floor){
        requestQueue.offer(floor);
    }

    public String getId(){
        return this.elevatorId;
    }
}
