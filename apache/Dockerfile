# Use official httpd image
FROM httpd:2.4

# Install apache utilities if needed
RUN apt-get update && \
    apt-get install -y apache2-utils && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# Enable required Apache modules
RUN sed -i '/^#LoadModule proxy_module/s/^#//' /usr/local/apache2/conf/httpd.conf && \
    sed -i '/^#LoadModule proxy_http_module/s/^#//' /usr/local/apache2/conf/httpd.conf && \
    sed -i '/^#LoadModule rewrite_module/s/^#//' /usr/local/apache2/conf/httpd.conf && \
    sed -i '/^#LoadModule headers_module/s/^#//' /usr/local/apache2/conf/httpd.conf

# Copy custom HTTP-only config
COPY ./conf/recnaile.conf /usr/local/apache2/conf/extra/recnaile.conf

# Include the custom config
RUN echo "Include /usr/local/apache2/conf/extra/recnaile.conf" >> /usr/local/apache2/conf/httpd.conf

# Expose HTTP
EXPOSE 80

# Start Apache in foreground
CMD ["httpd-foreground"]
