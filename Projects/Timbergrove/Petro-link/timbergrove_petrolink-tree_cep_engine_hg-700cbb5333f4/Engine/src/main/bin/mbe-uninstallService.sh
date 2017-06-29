MACHINEINFO="$(cat /etc/*release)"
dir=${PWD}
parentdir="$(dirname "$dir")"
echo === Executing uninstall as
whoami

if [[ $MACHINEINFO == *'CentOS'*' 7.'* || $MACHINEINFO == *'Red Hat'*' 7.'* ]]; then
	echo ___ Stopping Server
	systemctl stop mbe-linux-systemd
	echo ___ Updating Server
	systemctl disable mbe-linux-systemd.service
	rm -f /usr/lib/systemd/system/mbe-linux-systemd.service
	echo ___ Reload
	systemctl daemon-reload
	systemctl status mbe-linux-systemd
fi
if [[ $MACHINEINFO == *'CentOS'*' 6.'* || $MACHINEINFO == *'Red Hat'*' 6.'* ]]; then
	echo ___ Stopping Server
	initctl stop mbe-linux-upstart
	echo ___ Updating Server
	rm -f /etc/init/mbe-linux-upstart.conf
	echo ___ Reload
	initctl reload-configuration
	initctl status mbe-linux-upstart
fi
