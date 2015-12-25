do
 print("Before router " .. node.heap())
 local router = require 'router'
 print("After router " .. node.heap())
 router.post("/color", "$post-color.lua")
 router.get("/color", "$get-color.lua")
 router.get("/register.html", "$get-register.html.lua")
 router.get("/hello.html", "$get-hello.html.lua")
 print("After handler " .. node.heap())
 return router.handler
end