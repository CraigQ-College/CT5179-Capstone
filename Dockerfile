FROM tomcat:latest
ADD target/*.war /usr/local/tomcat/webapps/
EXPOSE 9090
CMD ["catalina.sh", "run"]