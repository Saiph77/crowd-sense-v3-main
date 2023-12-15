package com.fzu.crowdsense.algorithms.resource;


import com.fzu.crowdsense.algorithms.constraint.Condition;
import com.fzu.crowdsense.algorithms.constraint.Coordinate;


public class SimpleParticipant extends AbstractParticipant{


    protected SimpleParticipant(Long id, Coordinate location, ParticipantStatus participantStatus) {
        super(id, location, participantStatus);
    }

    @Override
    public boolean hasAbility(Class<? extends Condition> conditionClass) {
        return true;
    }

    @Override
    public Condition getAbility(Class<? extends Condition> conditionClass) {
        return null;
    }
}
