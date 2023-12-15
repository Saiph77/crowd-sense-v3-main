package com.fzu.crowdsense.algorithms.constraint;


/*
* POI限制约束，暂未使用
 */
public class POIConstraint implements Constraint{
    private final Coordinate location;
    private final double satisfyRadius;

    //筛选条件，10km
    private long WORK_TASK_DISTANCE_RANGE = 10000;

    public POIConstraint(Coordinate location) {
        this(location, 10000);
    }

    public POIConstraint(Coordinate location, double satisfyRadius){
        this.location = location;
        this.satisfyRadius = satisfyRadius;
    }

    public Coordinate getLocation() {
        return location;
    }



    @Override
    public boolean satisfy(Condition condition) {
        if (! ( condition instanceof Coordinate)) return false;
        Coordinate participantLocation = (Coordinate) condition;
        return participantLocation.euclideanDistance(location) <= satisfyRadius * satisfyRadius;
    }


    @Override
    public Class<? extends Condition> getConditionClass() {
        return Coordinate.class;
    }


    @Override
    public String description() {
        return "POI Constraint";
    }
}
