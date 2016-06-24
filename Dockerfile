FROM tomcat
MAINTAINER Sagar

RUN ["rm", "-fr", "/usr/local/tomcat/webapps/ROOT"]

RUN apt-get update
RUN apt-get install -y git
RUN apt-get install -y curl

# try with wget just war file

# RUN curl -O https://github.com/teco-kit/ESMAC/blob/master/ESMServer.war
# RUN chmod 777 ESMServer.war
# RUN mv ESMServer.war /usr/local/tomcat/webapps/ROOT.war

RUN git clone https://3843fa454aee904cd6ca3b6986e4a6f5c4287a19:x-oauth-basic@github.com/teco-kit/ESMAC.git
RUN mv ESMAC/ESMServer.war /usr/local/tomcat/webapps/ROOT.war
RUN rm -r ESMAC


CMD ["catalina.sh", "run"]