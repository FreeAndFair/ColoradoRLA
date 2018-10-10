#!/bin/sh

#### Guest provisioner for use with Vagrant

## This script will install the prerequisites for running the Colorado
## RLA system on Red Hat Enterprise Linux 7 (RHEL 7) or CentOS.

set -e

### Basics

## Required for SELinux policies on /vagrant that allow files within it to be
## served by Apache httpd.
yum -y install policycoreutils-python

### Java

yum -y install java-1.8.0-openjdk

### Apache httpd

yum -y install httpd

## Required for acting as a reverse proxy. Without this, httpd cannot initiate
## connections to the Java backend.
setsebool -P httpd_can_network_connect true

## Allow Apache httpd to read all the client-side files.
semanage fcontext -a -t httpd_sys_content_t "/vagrant/client(/.*)?"
## Ensure any existing files in /vagrant/client (recursively) comply with the
## policy.
restorecon -r /vagrant/client

(
cat << 'EOF'
<VirtualHost _default_:*>
  ServerName localhost
  DirectoryIndex index.html
  DocumentRoot /vagrant/client/dist

  <Directory "/vagrant/client/dist">
    AllowOverride None
    Require all granted

    RewriteEngine on

    RewriteCond %{REQUEST_FILENAME} -s [OR]
    RewriteCond %{REQUEST_FILENAME} -l [OR]
    RewriteCond %{REQUEST_FILENAME} -d
    RewriteRule ^.*$ - [NC,L]
    RewriteRule ^(.*) /index.html [NC,L]
  </Directory>

  <Location "/api/">
    AllowOverride None
    Require all granted
  </Location>

  ProxyPass /api/ http://localhost:8888/
  ProxyPassReverse /api/ http://localhost:8888/
</VirtualHost>
EOF
) > /etc/httpd/conf.d/50_corla.conf

systemctl enable httpd.service
systemctl start httpd.service

### PostgreSQL

yum -y install postgresql-server postgresql-contrib

postgresql-setup initdb

systemctl enable postgresql.service
systemctl start postgresql.service

sudo -u postgres psql -c "CREATE USER corla WITH PASSWORD 'corla';"
sudo -u postgres createdb -O corla corla

(
cat << 'EOF'
# TYPE       DATABASE       USER       ADDRESS       METHOD
host         all            all        ::1/128       md5
host         all            all        127.0.0.1/32  md5
EOF
) > /var/lib/pgsql/data/pg_hba.conf

chown postgres /var/lib/pgsql/data/pg_hba.conf
chmod 0600 /var/lib/pgsql/data/pg_hba.conf

(
cat << 'EOF'
localhost:5432:corla:corla:corla
EOF
) > /home/vagrant/.pgpass

chown vagrant /home/vagrant/.pgpass
chmod 0600 /home/vagrant/.pgpass

systemctl reload postgresql.service
