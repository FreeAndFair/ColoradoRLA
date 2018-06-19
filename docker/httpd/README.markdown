# Apache httpd Docker image

## Building

**The Docker image assumes the context is the project root**.

In other words, you should invoke it like this:

```sh
cd ${PROJECT_ROOT}
docker build -f docker/httpd/Dockerfile .
```

This is required so that the image build process has access to the RLA frontend
code.

## Creation notes

Since this image tracks the upstream `httpd` project, you may need to make
changes to the `httpd.conf` that ships with that container. Here are the changes
that were made:

1. Copy `/usr/local/apache2/conf/httpd.conf` out of a running official `httpd`
   container. This is your starting point.
2. Modify `httpd.conf` in the following ways:
   1. Change ServerAdmin to `dev@democracy.works`.
   2. Uncomment `mod_rewrite`.
   2. Uncomment `mod_proxy`.
   3. Uncomment `mod_proxy_http`.
   4. Load in `conf/extras/corla.conf` at the end of the configuration file.
3. This becomes your new `httpd.conf`.
