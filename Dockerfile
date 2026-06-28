#FROM 037548085579.dkr.ecr.ap-southeast-1.amazonaws.com/dtac-one-checkin-java-template:jdk-alpine-base
FROM public.ecr.aws/docker/library/eclipse-temurin:17-jre-alpine

RUN apk add --no-cache tzdata \
    && rm -rf /tmp/* /var/tmp/*

ENV JAVA_OPTS '-server -XshowSettings:vm'
ENV TZ Asia/Bangkok

# Add src to working directory
ADD *.jar commonbe-reward.jar

# Start app
ENTRYPOINT java $JAVA_OPTS $RUN_ARGS -jar commonbe-reward.jar
EXPOSE 8080