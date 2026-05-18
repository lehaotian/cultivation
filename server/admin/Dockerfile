FROM public.ecr.aws/amazoncorretto/amazoncorretto:21.0.6

EXPOSE 9366
WORKDIR /app
COPY ./target/admin-0.0.1-SNAPSHOT.jar /app
COPY ../.data/meta /app/meta
#时区
ENV TZ=Asia/Shanghai
ENTRYPOINT ["java", "-jar", "admin-0.0.1-SNAPSHOT.jar"]