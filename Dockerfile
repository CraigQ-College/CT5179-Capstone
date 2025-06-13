FROM tomcat:latest
ADD target/*.war /usr/local/tomcat/webapps/
ENV HOST=0.0.0.0
EXPOSE 9090
CMD ["catalina.sh", "run"]