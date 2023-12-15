package com.fzu.crowdsense.algorithms.resource;

import com.fzu.crowdsense.algorithms.constraint.Coordinate;
import com.fzu.crowdsense.model.entity.Task;

/**
 * @author Lenovo
 * @version 1.0
 * @description: TODO
 * @date 2023/5/25 13:07
 */
public abstract class AbstractTask implements SenseTask {

    private Task task;

    private long taskCounter = 0;
    protected TaskStatus status;
    protected Coordinate coordinate;


    protected AbstractTask(Task task) {

        this.task =task;

        this.coordinate = new Coordinate(task.getLatitude(), task.getLongitude());

        // 设置在线状态
        switch (task.getOnlineStatus()) {
            // TODO: 补全状态转换
            case 1 :
                this.status = TaskStatus.ONLINE;
                break;
        }

        taskCounter++;
    }


    @Override
    public Long getTaskId() {
        return this.task.getId();
    }

    @Override
    public Coordinate getCoordinate() {
        return this.coordinate;
    }


    @Override
    public TaskStatus getTaskStatus() {
        return status;
    }

    @Override
    public void setTaskStatus(TaskStatus status) {
        this.status = status;
    }

    @Override
    public int getMaxAllocationNum() {
        return Math.toIntExact(this.task.getMaxPassed());
    }

    /*
     * @description: 该参与者是否能够被分配
    */
    @Override
    public boolean canAssignTo(Participant participant) {
        // TODO 加入限制条件

        return true;
    }

    @Override
    public boolean finished() {
        return status == TaskStatus.FINISHED;
    }
}
