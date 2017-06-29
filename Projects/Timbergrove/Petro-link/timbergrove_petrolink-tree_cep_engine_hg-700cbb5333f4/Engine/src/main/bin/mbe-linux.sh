dir=${PWD}
parentdir="$(dirname "$dir")"
mainClass=com.petrolink.mbe.engine.ComplexEventProcessor
mainJar=petrolink-mbe-engine.jar

whoami
echo ${parentdir}
( cd ${parentdir} && /usr/bin/java -Duser.dir=${parentdir} -DlogPath=log -Dmq.data=amq -DIGNITE_PERFORMANCE_SUGGESTIONS_DISABLED=true -Xmx4G -XX:+HeapDumpOnOutOfMemoryError -cp ${mainJar}:.  ${mainClass})
