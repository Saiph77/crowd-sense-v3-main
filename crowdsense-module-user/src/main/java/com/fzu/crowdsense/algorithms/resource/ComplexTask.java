package com.fzu.crowdsense.algorithms.resource;

import com.fzu.crowdsense.model.entity.Task;
import com.fzu.crowdsense.model.vo.TaskVO;
import com.fzu.crowdsense.utils.BeanCopyUtils;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Lenovo
 * @version 1.0
 * @description: 大任务
 * @date 2023/6/2 9:54
 */
@ToString
public class ComplexTask extends AbstractTask {

    // 大任务下的子任务
    private List<SimpleTask> childrenList = new ArrayList<>();


    public ComplexTask(TaskVO taskVO) {
        super(BeanCopyUtils.copyBean(taskVO, Task.class));

        List<TaskVO> childTasks = taskVO.getChildTask();

        List<Task> tasks = BeanCopyUtils.copyBeanList(childTasks, Task.class);

        for (Task task : tasks) {
            childrenList.add(new SimpleTask(task));
        }

    }

    public List<SimpleTask> getChildren() {
        return childrenList;
    }
}
