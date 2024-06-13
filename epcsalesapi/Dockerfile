#FROM ccpksharbor11.hksmartone.com/common/jdk:11.0.16-jre-slim
#FROM ccpksharbor11.hksmartone.com/common/jdk:17.0.2-slim
FROM ccpksharbor11.hksmartone.com/common/jdk:21-slim

#ADD cacerts /usr/local/openjdk-17/lib/security/cacerts
ADD cacerts /usr/local/openjdk-21/lib/security/cacerts
ENV TZ=Asia/Hong_Kong

RUN mkdir /app
RUN adduser --no-create-home --disabled-password --disabled-login --gecos GECOS epcuser
COPY target/epcsalesapi-0.0.1-SNAPSHOT.jar /app/epcsalesapi-0.0.1-SNAPSHOT.jar
WORKDIR /app
RUN chown -R epcuser:epcuser /app
USER epcuser

#ENTRYPOINT ["java","-jar","epcsalesapi-0.0.1-SNAPSHOT.jar"]
#ENTRYPOINT ["java -Xms1G -Xmx8G -XX:+UseParallelGC -XX:MaxRAMPercentage=75 -jar epcsalesapi-0.0.1-SNAPSHOT.jar"]
ENTRYPOINT ["java","-Xms1G","-Xmx8G","-XX:+UseParallelGC","-XX:MaxRAMPercentage=75","-jar","epcsalesapi-0.0.1-SNAPSHOT.jar"]
