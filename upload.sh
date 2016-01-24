#/bin/bash
#nodemcu-uploader.py --port /dev/cu.SLAB_USBtoUART upload init.lua server.lua routes_custom.lua
#nodemcu-uploader.py --port /dev/cu.SLAB_USBtoUART upload routes/animate.delete.lua routes/animate.post.lua routes/color.post.lua routes/color.get.lua

nodemcu-tool upload routes/animate.delete.lua
nodemcu-tool upload routes/animate.post.lua
nodemcu-tool upload routes/color.get.lua
nodemcu-tool upload routes/color.post.lua
nodemcu-tool upload init.lua
nodemcu-tool upload service/ledcontroller.lua
nodemcu-tool upload server.lua
nodemcu-tool upload routes_custom.lua
