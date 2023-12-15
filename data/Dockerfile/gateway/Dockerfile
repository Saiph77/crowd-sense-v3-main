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

ADD crowdsense-module-gateway-1.0-SNAPSHOT.jar /root/gateway.jar

RUN bash -c 'touch /root/gateway.jar'

ENTRYPOINT ["java","-jar","/root/gateway.jar"]

EXPOSE 6702


# 构建命令
# docker build -t crowdsense/gateway:2.0 .

# 运行命令
# docker run -d -p 6702:6702 --network fzu_net --restart=always --name gateway -v /home/crowdsense/logs/crowdsense-gateway:/root/logs/crowdsense-gateway   crowdsense/gateway:2.0


