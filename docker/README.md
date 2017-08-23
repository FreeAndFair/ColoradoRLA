# CentOS 7-based container for ColoradoRLA Tool

These notes are very rough and untested, and basically
here to document a work-in-progress to more fully
automate the procedure for setting up a runnable system
on CentOS 7, which is close to our target environment of RHEL 7.

# Follow Instructions in the INSTALL.md file in the deliverable.

```
systemctl enable httpd
cd /etc/httpd/conf.d/
cat > corla.conf <<!EoF
ProxyPass /api/ http://localhost:8888/
ProxyPassReverse /api/ http://localhost:8888/
!EoF

systemctl start httpd

# RHEL only:
# /usr/sbin/setsebool -P httpd_can_network_connect 1

cd /opt/ColoradoRLA/
unzip -d /var/www/html  corla-client.zip

...clone the git repo...

cd test/smoketest
pip install -r requirements.txt

....more steps - see INSTALL.md...

```

## TODO:
 Explore implications of systemd and docker run --privileged

 Add instructions to run the container and and connect to it.

docker run --privileged -e container=docker -v /sys/fs/cgroup:/sys/fs/cgroup -v ColoradoRLA-dv:/srv/ColoradoRLA-dv --expose=8888 --expose=80 --expose=443 -d centos7-coloradorla /usr/sbin/init
