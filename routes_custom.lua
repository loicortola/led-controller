local f = loadfile('router.lc')
-- load router
local router = f()
f = nil
collectgarbage("collect")
router.post("/animate", "animate.post.lc")
router.delete("/animate", "animate.delete.lc")
router.get("/color", "color.get.lc")
router.post("/color", "color.post.lc")
local h = router.handler
router = nil
return h