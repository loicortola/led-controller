local get = function(req, res)
 local loader = loadfile("service/ledcontroller.lc")
 local ledcontroller = loader()
 print("coucou")
 local color = ledcontroller.getcolor()
 res:addheader("Content-Type", "application/json; charset=utf-8")
 res:send("{red:" .. color.r .. ", green:" .. color.g .. ", blue:" .. color.b .. "}")
end
return get