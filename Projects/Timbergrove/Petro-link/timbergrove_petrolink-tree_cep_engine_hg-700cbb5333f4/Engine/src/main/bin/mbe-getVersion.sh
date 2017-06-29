dir=${PWD}
parentdir="$(dirname "$dir")"
mainClass=com.petrolink.mbe.util.PrintVersionString
mainJar=petrolink-mbe-engine.jar

whoami
echo ${parentdir}
( cd ${parentdir} && /usr/bin/java -Duser.dir=${parentdir} -cp ${mainJar}:.  ${mainClass})
