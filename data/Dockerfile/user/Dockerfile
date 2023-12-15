FROM openjdk:8

LABEL maintainer="crowdsense"

#定义时区参数
ENV TZ=Asia/Shanghai

#设置时区
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo '$TZ' > /etc/timezone

#设置编码
ENV LANG C.UTF-8

VOLUME /tmp

WORKDIR /root/

ADD crowdsense-module-user-1.0-SNAPSHOT.jar /root/user.jar

RUN bash -c 'touch /root/user.jar'

ENTRYPOINT ["java","-jar","/root/user.jar"]

EXPOSE 3377


# 构建命令
# docker build -t crowdsense/user:2.0 .

# 运行命令
# docker run -d -p 3377:3377 --network fzu_net --restart=always --name user -v /home/crowdsense/logs/crowdsense-user:/root/logs/crowdsense-user -v /home/crowdsense/resource:/root/resource crowdsense/user:2.0


