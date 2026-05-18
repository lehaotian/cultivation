FROM public.ecr.aws/amazoncorretto/amazoncorretto:21.0.6

EXPOSE 9367
WORKDIR /app
COPY ./target/logic-0.0.1-SNAPSHOT.jar /app
COPY ../.data/meta /app/meta
#时区
ENV TZ=Asia/Shanghai
ENTRYPOINT ["java", "-jar", "logic-0.0.1-SNAPSHOT.jar"]