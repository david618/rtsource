# Deploy instructions

The tcp-kafka.json file is an example Marathon configuration for this application.

## id
The id must be lowercase

## cmd
The $MESOS_SANDBOX is where the app can access contents it retrieves from "uris" below.

Runs a Java command to execute the class com.esri.rtsource.TcpKafka

Parameters: 5565 172.16.0.4:9092 simFile $PORT0 true
- 172.16.0.4:9092: is the ip:port of Kafka; this parameter could also be the name of the DCOS Kafka app in Marathon (e.g. hub2)
- simFile: topic name
- $PORT0: Use Marathon assigned port for health check; the app will also make results accessible on this port
- true: Optional parameter for testing latency.  Latency assumes CSV inputs and the last field is epoch time in milliseconds.

## cpus, mem, and disk
You may need to increase these. I originally started with low values; however, during testing the application failed. In Marathon I found an error about insufficent memory. 

## constraints
I have configured hostname:UNIQUE. This is required because we are using a fixed port 5565.

## healthChecks
Looks for response on $PORT0. No resonse and Marathon will restart the application.

## uris
You'll need to put the JRE, libs, and rtsource jar on a web server that is accessible from the Marathon agent nodes.

Created a tgz for lib folder in target. Ran this command "tar cvzf rtlib.gz lib" in the target folder and uploaded the resulting rtlib.gz to the web server. Using a separate file makes it very easy to patch the app. The rtsource.jar is very small (e.g. 40K) and can uploaded quickly and the app restarted.  You could use rtsource-jar-with-dependencies.jar; however, the file includes all of the libs (e.g. 90MB) and take longer to upload. If you add a new lib(s) you'll need to update the rtlib.gz file.

"uris": [
    "http://172.16.0.5/apps/jre-8u91-linux-x64.tar.gz",
    "http://172.16.0.5/apps/rtlib.tgz",
    "http://172.16.0.5/apps/rtsource.jar"
  ]
