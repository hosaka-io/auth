FROM registry.i.hosaka.io/bootstrap:8-jre-alpine
COPY ./target/auth.jar /srv/auth.jar
WORKDIR /srv

EXPOSE 8080 8079

ENTRYPOINT /usr/bin/bootstrap /usr/bin/java -Xms128m -Xmx512m -jar /srv/auth.jar