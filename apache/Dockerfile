FROM httpd:2.4

# Enable modules for proxy and SSL
RUN apt-get update && \
    apt-get install -y apache2-utils vim && \
    sed -i 's/#LoadModule proxy_module/LoadModule proxy_module/' /usr/local/apache2/conf/httpd.conf && \
    sed -i 's/#LoadModule proxy_http_module/LoadModule proxy_http_module/' /usr/local/apache2/conf/httpd.conf && \
    sed -i 's/#LoadModule ssl_module/LoadModule ssl_module/' /usr/local/apache2/conf/httpd.conf && \
    sed -i 's/#LoadModule headers_module/LoadModule headers_module/' /usr/local/apache2/conf/httpd.conf && \
    sed -i 's/#Include conf\/extra\/httpd-ssl.conf/Include conf\/extra\/httpd-ssl.conf/' /usr/local/apache2/conf/httpd.conf

COPY conf/recnaile.conf /usr/local/apache2/conf/extra/recnaile.conf
RUN echo "Include conf/extra/recnaile.conf" >> /usr/local/apache2/conf/httpd.conf
