# CentOS 7-based containter for ColoradoRLA
# Based on https://serverfault.com/a/825027/64546 for resolving systemd issues
FROM centos
MAINTAINER Neal McBurnett <nealmcb@freeandfair.us>
ENV container docker
RUN yum -y update; yum clean all
RUN yum -y install systemd; yum clean all; \
(cd /lib/systemd/system/sysinit.target.wants/; for i in *; do [ $i == systemd-tmpfiles-setup.service ] || rm -f $i; done); \
rm -f /lib/systemd/system/multi-user.target.wants/*;\
rm -f /etc/systemd/system/*.wants/*;\
rm -f /lib/systemd/system/local-fs.target.wants/*; \
rm -f /lib/systemd/system/sockets.target.wants/*udev*; \
rm -f /lib/systemd/system/sockets.target.wants/*initctl*; \
rm -f /lib/systemd/system/basic.target.wants/*;\
rm -f /lib/systemd/system/anaconda.target.wants/*;
RUN yum -y install httpd
RUN yum -y install wget
RUN yum -y install java-1.8.0-openjdk
RUN yum -y install unzip
RUN yum -y install git
RUN yum -y install epel-release
RUN wget https://dl.fedoraproject.org/pub/epel/epel-release-latest-7.noarch.rpm
RUN yum -y install ./epel-release-latest-*.noarch.rpm
RUN yum -y update
RUN yum -y install python-pip

# Several additional steps are necessary to set up the environment for ColoradoRLA,
# currently done by hand as documented in docker/README.md

VOLUME [ “/sys/fs/cgroup” ]
CMD [“/usr/sbin/init”]
