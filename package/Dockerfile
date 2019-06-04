FROM ubuntu:xenial

#RUN apt-get update && apt-get install -y \
#  openjdk-8-jdk \
#  maven

RUN mkdir -p ballovertime/DEBIAN ballovertime/etc/systemd/system/

COPY build.sh /build
COPY control ballovertime/DEBIAN
COPY preinst.sh ballovertime/DEBIAN/preinst
COPY postinst.sh ballovertime/DEBIAN/postinst
COPY ballovertime.service ballovertime/etc/systemd/system/

RUN chmod +x build && chmod +x ballovertime/DEBIAN/postinst && chmod +x ballovertime/DEBIAN/preinst

CMD /build
