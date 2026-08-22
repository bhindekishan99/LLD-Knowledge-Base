package strategies.requestselectionstrategy;

import models.Elevator;

public interface RequestSelectionStrategy {

    public int selectNextRequest(Elevator elevator);
    
}
