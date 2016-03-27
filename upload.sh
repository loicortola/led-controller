#/bin/bash
baud=$2
if [ -z "$1" ]; then
 echo "This script requires an argument: USB device port"
 exit 1;
fi
if [ -z "$2" ]; then
 echo "Setting baudrate to default: 115200"
 baud=115200 
 #Default nodemcu 1.5+ baudrate
fi

nodemcu-tool upload routes/animate.delete.lua -k --compile --optimize --port $1 --baud $baud
nodemcu-tool upload routes/animate.post.lua -k --compile --optimize --port $1 --baud $baud
nodemcu-tool upload routes/color.get.lua -k --compile --optimize --port $1 --baud $baud
nodemcu-tool upload routes/color.post.lua -k --compile --optimize --port $1 --baud $baud
nodemcu-tool upload routes/health.get.lua -k --compile --optimize --port $1 --baud $baud
nodemcu-tool upload routes/register.post.lua -k --compile --optimize --port $1 --baud $baud
nodemcu-tool upload routes/restart.post.lua -k --compile --optimize --port $1 --baud $baud
nodemcu-tool upload routes/ssids.get.lua -k --compile --optimize --port $1 --baud $baud
nodemcu-tool upload init.lua -k --port $1 --baud $baud
nodemcu-tool upload service/ledcontroller.lua -k --compile --optimize --port $1 --baud $baud
nodemcu-tool upload service/wificontroller.lua -k --compile --optimize --port $1 --baud $baud
nodemcu-tool upload static/favicon.ico -k --port $1 --baud $baud
nodemcu-tool upload static/favicon-96x96.png -k --port $1 --baud $baud
nodemcu-tool upload static/index.html -k --port $1 --baud $baud
nodemcu-tool upload static/script.js -k --port $1 --baud $baud
nodemcu-tool upload static/spinner.gif -k --port $1 --baud $baud
nodemcu-tool upload static/style.css -k --port $1 --baud $baud
nodemcu-tool upload server.lua -k --compile --optimize --port $1 --baud $baud
nodemcu-tool upload server_setup.lua -k --compile --optimize --port $1 --baud $baud
nodemcu-tool upload routes_custom.lua -k --compile --optimize --port $1 --baud $baud
