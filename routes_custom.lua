local f = loadfile('router.lc')
-- load router
local router = f()
f = nil
collectgarbage("collect")
router.post("/animate", "routes/animate.post.lc")
router.delete("/animate", "routes/animate.delete.lc")
router.get("/color", "routes/color.get.lc")
router.post("/color", "routes/color.post.lc")
router.get("/health", "routes/health.get.lc")
router.get("/switch", "routes/switch.get.lc")
router.post("/switch", "routes/switch.post.lc")
local h = router.handler
router = nil
return h