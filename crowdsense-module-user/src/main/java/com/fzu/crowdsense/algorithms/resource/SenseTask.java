package com.fzu.crowdsense.algorithms.resource;

import com.fzu.crowdsense.algorithms.constraint.Coordinate;

/**
 * @author Lenovo
 * @version 1.0
 * @description: TODO
 * @date 2023/5/25 13:04
 */
public interface SenseTask{



    // Defining a new type called `TaskStatus` with three possible values: `READY`, `IN_PROGRESS`, and `FINISHED`.
    enum TaskStatus {
        AUDIT,
        ONLINE,
        IN_PROGRESS,
        FINISHED,
        VALID,
    }


    Long getTaskId();

    Coordinate getCoordinate();

    /**
     * Returns the status of the task.
     *
     * @return The task status is being returned.
     */
    TaskStatus getTaskStatus();
    /**
     * Sets the status of the task to the given status.
     *
     * @param status The status of the task.
     */
    void setTaskStatus(TaskStatus status);

    int getMaxAllocationNum();


    /**
     * Returns true if the given participant can be assigned to this role.
     *
     * @param participant The participant to assign the task to.
     * @return A boolean value.
     */
    boolean canAssignTo(Participant participant);


    /**
     * Returns true if the game is over, false otherwise.
     *
     * @return A boolean value.
     */
    boolean finished();

}

