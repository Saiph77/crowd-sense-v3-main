package com.fzu.crowdsense.algorithms.adapter;


import com.fzu.crowdsense.algorithms.resource.Participant;
import com.fzu.crowdsense.algorithms.resource.ParticipantPool;
import com.fzu.crowdsense.algorithms.resource.SimpleTask;
import com.fzu.crowdsense.algorithms.service.TaskAssignmentAlgo;

import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class AlgoFactoryAdapter implements AlgoFactory{

    @Resource
    private ParticipantPool participantPool;

//    @Override
//    public ParticipantSelectionAlgo getParticipantSelectionAlgo() {
//
//        return new ParticipantSelectionAlgo() {
//
//            @Override
//            public List<Participant> getCandidates(SenseTask task) {
//                List<Participant> candidate = new LinkedList<>();
//                for (Participant participant : participantPool.getParticipants()) {
//                    if (participant.available() && task.canAssignTo(participant)){
//                        candidate.add(participant);
//                    }
//                }
//                return candidate;
//            }
//        };
//    }


    @Override
    public TaskAssignmentAlgo getTaskAssignmentAlgo() {
        return new TaskAssignmentAlgo() {

            @Override
            public Optional<List<Participant>> getAssignmentScheme(SimpleTask task) {
                List<Participant> candidate = new LinkedList<>();
                for (Participant participant : participantPool.getParticipants()) {
                    if (participant.available() && task.canAssignTo(participant)){
                        candidate.add(participant);
                    }
                }
                return Optional.of(candidate);
            }
        };
    }
}
