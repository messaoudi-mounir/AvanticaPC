user=mbeuser
group=mbeusers

chown $user:$group /opt/mbe
chown -R $user:$group /opt/mbe/log
chown -R $user:$group /tmp/ignite
chown -R $user:$group /opt/mbe/flows
chown -R $user:$group /opt/mbe/h2