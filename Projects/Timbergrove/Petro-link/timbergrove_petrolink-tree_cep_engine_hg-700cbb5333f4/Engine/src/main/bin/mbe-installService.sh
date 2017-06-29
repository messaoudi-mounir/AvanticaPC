MACHINEINFO="$(cat /etc/*release)"
dir=${PWD}
parentdir="$(dirname "$dir")"
echo === Executing install as
whoami

if [[ $MACHINEINFO == *'CentOS'*' 7.'* || $MACHINEINFO == *'Red Hat'*' 7.'* ]]; then
  echo RHEL 7.x / CentOS 7.x
	echo ___ Stopping Server
	systemctl stop mbe-linux-systemd
	echo ___ Updating Server
	cp /opt/mbe/bin/mbe-linux-systemd.service /usr/lib/systemd/system/
	systemctl enable mbe-linux-systemd.service
	echo ___ Reload
	systemctl daemon-reload
	systemctl status mbe-linux-systemd
fi
if [[ $MACHINEINFO == *'CentOS'*' 6.'* || $MACHINEINFO == *'Red Hat'*' 6.'* ]]; then
  echo RHEL 6.x / CentOS 6.x
  echo ___ Stopping Server
	initctl stop mbe-linux-upstart
	echo ___ Updating Server
	cp /opt/mbe/bin/mbe-linux-upstart.conf /etc/init/
	echo ___ Reload
	initctl reload-configuration
	initctl status mbe-linux-upstart
fi
