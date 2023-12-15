package com.fzu.crowdsense.algorithms.resource;


import com.fzu.crowdsense.algorithms.constraint.Condition;
import com.fzu.crowdsense.algorithms.constraint.Coordinate;
import lombok.ToString;

@ToString
public abstract class AbstractParticipant implements Participant{

    protected Long id;

    protected Coordinate location;

    //暂时设置为都空闲
    protected ParticipantStatus status;


    protected AbstractParticipant(Long id, Coordinate location, ParticipantStatus status) {
        this.id = id;
        this.location = location;
        this.status = status;
    }

    @Override
    public ParticipantStatus getStatus() {
        return status;
    }

    @Override
    public void setStatus(ParticipantStatus status) {

        this.status = status;
    }

    @Override
    public Coordinate getLocation() {
        return this.location;
    }

    @Override
    public Long getId() {
        return this.id;
    }


    public abstract boolean hasAbility(Class<? extends Condition> conditionClass);

    public abstract Condition getAbility(Class<? extends Condition> conditionClass);
    @Override
    public boolean available() {
        return status == ParticipantStatus.AVAILABLE;
    }
}
