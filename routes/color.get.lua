local get = function(req, res)
 local loader = loadfile("ledcontroller.lc")
 local ledcontroller = loader()

 local color = ledcontroller.getcolor()
 res:addheader("Content-Type", "application/json; charset=utf-8")
 res:send("{red:" .. color.r .. ", green:" .. color.g .. ", blue:" .. color.b .. "}")
end
return get