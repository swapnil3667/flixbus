FROM frolvlad/alpine-oraclejre8:slim

MAINTAINER swapnil.kumar

ENV SCALA_VERSION=2.12.8 \
SCALA_HOME=/usr/share/scala

RUN apk add --no-cache --virtual=.build-dependencies wget ca-certificates && \
apk add --no-cache bash && \
cd "/tmp" && \
wget "https://downloads.typesafe.com/scala/${SCALA_VERSION}/scala-${SCALA_VERSION}.tgz" && \
tar xzf "scala-${SCALA_VERSION}.tgz" && \
mkdir "${SCALA_HOME}" && \
rm "/tmp/scala-${SCALA_VERSION}/bin/"*.bat && \
mv "/tmp/scala-${SCALA_VERSION}/bin" "/tmp/scala-${SCALA_VERSION}/lib" "${SCALA_HOME}" && \
ln -s "${SCALA_HOME}/bin/"* "/usr/bin/" && \
apk del .build-dependencies && \
rm -rf "/tmp/"*

COPY target/scala-2.12/flixbus-assembly-0.1.jar flixbus-assembly-0.1.jar
COPY input.json input.json
