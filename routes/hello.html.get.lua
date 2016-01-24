do
 local get = function(req, res)
  res:addheader("Content-Type", "text/html; charset=utf-8")
  res:sendfile("hello.html")
 end
 return get
end
