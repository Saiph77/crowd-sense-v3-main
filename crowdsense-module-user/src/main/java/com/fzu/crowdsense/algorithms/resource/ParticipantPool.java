package com.fzu.crowdsense.algorithms.resource;


import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.fzu.crowdsense.algorithms.constraint.Coordinate;
import com.fzu.crowdsense.mapper.UserMapper;
import com.fzu.crowdsense.model.entity.User;
import com.fzu.crowdsense.utils.RecommUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.fzu.crowdsense.constant.RedisConstants.USER_INFO;

/**
 * @author Lenovo
 * @version 1.0
 * @description: 参与者池，用于存放所有用户资源，并实现参与者筛选管理
 * @date 2023/5/25 10:06
 */
@Component
@Slf4j
public class ParticipantPool {


    private final List<SimpleParticipant> participants = new ArrayList<>();

    private int participantCounter;


    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private UserMapper userMapper;

    /*
     * 实例化
     */
    @PostConstruct
    public void init() {
        //加载用户数据
        setParticipants();
    }


    //添加参与者
    public void addParticipant(SimpleParticipant participant) {
        participants.add(participant);
        this.participantCounter++;
    }

    public void removeParticipant(SimpleParticipant participant) {
        participants.remove(participant);
        this.participantCounter--;
    }

    public int getParticipantCounter() {
        return this.participantCounter;
    }

    public List<SimpleParticipant> getParticipants() {
        return participants;
    }


    public void setParticipants() {

        //获取全表用户数据
        //首先尝试从缓存中读取用户数据
        String pattern = USER_INFO + "*";
        List<User> users = stringRedisTemplate.keys(pattern).stream()
                .map(key -> stringRedisTemplate.opsForValue().get(key))
                .filter(StrUtil::isNotEmpty)
                .map(json -> JSONUtil.toBean(json, User.class))
                .collect(Collectors.toList());

        if (!users.isEmpty()) {
            users.forEach(user -> {
                Coordinate coordinate = new Coordinate(user.getLatitude(), user.getLongitude());
                SimpleParticipant simpleParticipant = new SimpleParticipant(user.getId(), coordinate, Participant.ParticipantStatus.AVAILABLE);
                addParticipant(simpleParticipant);
            });
            return;
        }


        //缓存未命中，从数据库中读取数据所有用户数据
        users = userMapper.findAll();

        participants.addAll(users.stream()
                .filter(Objects::nonNull)
                .map(user -> new SimpleParticipant(
                        user.getId(),
                        new Coordinate(Optional.ofNullable(user.getLatitude()).orElse(0.0),
                                Optional.ofNullable(user.getLongitude()).orElse(0.0)),
                        Participant.ParticipantStatus.AVAILABLE))
                .collect(Collectors.toList()));

    }

    public List<SimpleParticipant> getParticipantsWithinRange(Coordinate coordinate, double radius) {

        return participants.stream()
                .filter(participant -> {
                    double distance = RecommUtil.getDistance(coordinate.getLatitude(), coordinate.getLongitude(), participant.location.getLatitude(), participant.location.getLongitude());
                    return distance <= radius && participant.getStatus() == Participant.ParticipantStatus.AVAILABLE;
                })
                .collect(Collectors.toList());
    }



}
