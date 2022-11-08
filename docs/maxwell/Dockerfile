FROM openjdk:11
MAINTAINER chao9038@gmail.com
ADD maxwell-1.33.0.tar.gz /usr/local
ADD raft.xml /usr/local/maxwell-1.33.0/bin
WORKDIR /usr/local/maxwell-1.33.0
#ENV PARAMS=NOTNULL
CMD ["param"]
ENTRYPOINT ["/usr/local/maxwell-1.33.0/bin/maxwell"]
