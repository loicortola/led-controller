local f = loadfile('router.lua')
-- load router
local router = f()
f = nil
collectgarbage("collect")
router.post("/color", "color.post.lua")
router.get("/color", "color.get.lua")
router.get("/register.html", "register.html.get.lua")
router.get("/hello.html", "hello.html.get.lua")
local h = router.handler
router = nil
return h