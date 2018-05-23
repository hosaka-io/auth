FROM registry.i.hosaka.io/bootstrap
COPY ./target/uberjar/auth.jar /srv/auth.jar
WORKDIR /srv

EXPOSE 8080

ENTRYPOINT /usr/bin/bootstrap /usr/bin/java -jar /srv/auth.jar