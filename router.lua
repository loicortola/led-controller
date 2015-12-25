do
 local routes = {}
 
 local addHandler = function(url, method, callback)
  if not (routes[url]) then
   routes[url] = {}
  end
  routes[url][method] = callback
 end

 local makeHandler = function(method)
  return function(url, callback)
   addHandler(url, method, callback)
  end
 end
 
 local handler = function(req, res)
  print("Request received: ", req.method, req.url)
  if routes[req.url] then
   local h = dofile(routes[req.url][req.method])
   h(req, res)
  else
   res.statuscode = 404
   res:send()
  end
 end
 return { handler = handler, get = makeHandler('GET'), post = makeHandler('POST'), put = makeHandler('PUT'), delete = makeHandler('DELETE'), options = makeHandler('OPTIONS'), head = makeHandler('HEAD') }
end
