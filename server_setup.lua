do
 local espress = require 'espress'
 local port = 80
 print("Heap after http : " .. tostring(node.heap()))
 
 local server = espress.createserver()
 server:use("routes_auto.lc")
 server:listen(port)
end