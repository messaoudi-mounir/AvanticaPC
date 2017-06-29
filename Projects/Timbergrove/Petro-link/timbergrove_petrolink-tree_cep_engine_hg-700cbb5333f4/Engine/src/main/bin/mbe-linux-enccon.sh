dir=${PWD}
parentdir="$(dirname "$dir")"
mainClass=com.smartnow.engine.settings.EncryptConfigFile
mainJar=petrolink-mbe-engine.jar

whoami
echo ${parentdir}
( cd ${parentdir} && /usr/bin/java -Duser.dir=${parentdir} -cp ${mainJar}:.  ${mainClass} connections.xml )
