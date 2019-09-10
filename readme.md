

Setting up project on local machine.
------------------------------------

1. Checkout project from github
2. Refresh Gradle project - uses gradle
3. Start mysql instance and load dbschema.sql into it
3. Start main class ServerVerticle with program arguments "apiserver/config/base.json,apiserver/config/dev.services.json,apiserver/config/resturls.json"
4. The local server should start at http://localhost/7000
5. All configuration is stored in json files under resources/apiserver/config folder

Couple of API calls to test
1. http://localhost:7000/api/userprofile?userid=one@onemilestone.com
2. http://localhost:7000/api/menu

To deploy on server
------------------------------
1. Run shadowJar target from Gradle, this creates plianced-server-1.0-all.jar in build/libs folder, it has all dependencies in it
2. Copy this jar to the target machine /mnt/software/plianced folder, backup the existing jar file into bkp folder with a different name.
3. Kill the running java server and execute api_start.sh to start server.
4. If having trouble running the above script, use this command from inside /mnt/software/plianced folder
sudo java -Xloggc:/mnt/logs/gcapi.log -verbose:gc -XX:+PrintGCDateStamps -XX:+PrintGCDetails -Xms64m -Xmx512m -server -jar plianced-server-1.0-all.jar apiserver/config/dev.json,apiserver/config/dev.services.json,apiserver/config/resturls.json

5. The above command will start the server and logs are created in /mnt/logs folder.
