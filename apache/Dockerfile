# Use official httpd image
FROM httpd:2.4

# Install tools (and keep image small)
RUN apt-get update && \
    apt-get install -y --no-install-recommends apache2-utils ca-certificates && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# Enable required Apache modules (proxy, proxy_http, ssl, headers, rewrite, socache_shmcb)
RUN sed -i '/^#LoadModule proxy_module/s/^#//' /usr/local/apache2/conf/httpd.conf && \
    sed -i '/^#LoadModule proxy_http_module/s/^#//' /usr/local/apache2/conf/httpd.conf && \
    sed -i '/^#LoadModule ssl_module/s/^#//' /usr/local/apache2/conf/httpd.conf && \
    sed -i '/^#LoadModule socache_shmcb_module/s/^#//' /usr/local/apache2/conf/httpd.conf && \
    sed -i '/^#LoadModule rewrite_module/s/^#//' /usr/local/apache2/conf/httpd.conf && \
    sed -i '/^#LoadModule headers_module/s/^#//' /usr/local/apache2/conf/httpd.conf && \
    sed -i '/^#LoadModule proxy_wstunnel_module/s/^#//' /usr/local/apache2/conf/httpd.conf || true

# Make sure Apache listens on 443
RUN grep -q "^Listen 443" /usr/local/apache2/conf/httpd.conf || echo "Listen 443" >> /usr/local/apache2/conf/httpd.conf

# Copy custom HTTP/HTTPS config (we will mount the conf dir in docker-compose)
COPY ./conf/recnaile.conf /usr/local/apache2/conf/extra/recnaile.conf

# Include the custom config
RUN echo "Include /usr/local/apache2/conf/extra/recnaile.conf" >> /usr/local/apache2/conf/httpd.conf

# Expose both HTTP and HTTPS
EXPOSE 80 443

# Start Apache in foreground
CMD ["httpd-foreground"]
