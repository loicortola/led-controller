------------------------------------------------------------------------------
-- HTTP server module
-- LICENCE: http://opensource.org/licenses/MIT
-- Author: Loic Ortola https://github.com/loicortola
------------------------------------------------------------------------------
-- HTTP status codes as defined in RFC 2616 + common ones along with their message
do
 ------------------------------------------------------------------------------
 -- HTTP parser
 ------------------------------------------------------------------------------
 local httphandler = function(handler_callback)
  return function(conn)
   collectgarbage("collect")
   print("Begin request: " .. node.heap())
   -- Keep reference to callback
   local self = handler_callback
   local req, res, ondisconnect, onheader, ondata, onreceive
   local buf = ""
   local parsedlines = 0
   local bodylength = 0
   
   ondisconnect = function(conn)
    -- Manually set everything to nil to allow gc
    req = nil
    res = nil
    ondisconnect = nil
    onheader = nil
    ondata = nil
    onreceive = nil
    buf = nil
    parsedlines = nil
    bodylength = nil
    collectgarbage("collect")
    print("Garbage Collector is sweeping " .. node.heap())
   end
   
   -- Header parser
   onheader = function(k, v)
    print("Adding header " .. k)
    if k == "content-length" then
     bodylength = tonumber(v)
    end
    -- Delegate to request object
    if req then
     req:addheader(k, v)
    end
   end

   -- Body parser
   ondata = function(conn, chunk)
    -- Prevent MCU from resetting
    tmr.wdclr()
    collectgarbage("collect")
    if chunk then
     req.body = req.body .. chunk
     if #req.body >= bodylength then
      conn:on("receive", onreceive)
      dofile(self.handler)(req, res)
     end
    end
   end

   -- Metadata parser
   onreceive = function(conn, chunk)
    collectgarbage("collect")
    -- concat chunks in buffer
    if buf then
     buf = buf .. chunk
    else
     buf = chunk
    end
    -- Read line from chunk
    while #buf > 0 do
     local e = buf:find("\r\n", 1, true)
     -- Leave if line not done
     if not e then break end
     local line = buf:sub(1, e - 1)
     buf = buf:sub(e + 2)
     if parsedlines == 0 then
      -- FIRST LINE
      req = dofile('http-request.lua')(conn, line)
      res = dofile('http-response.lua')(conn)
     elseif #line > 0 then
      -- HEADER LINES
      -- Parse header
      local _, _, k, v = line:find("^([%w-]+):%s*(.+)")
      if k then
      -- Valid header
       k = k:lower()
       onheader(k, v)
      end
     else
      -- BODY
      tmr.wdclr()
      -- Buffer no longer needed
      buf = nil
      if bodylength == 0 then
       collectgarbage("collect")
       -- Handle request if no body present
       dofile(self.handler)(req, res)
      else
       -- Change receive hook to body parser if body present
       conn:on("receive", ondata)
      end
      break
     end
     parsedlines = parsedlines + 1
    end
   end
   conn:on("receive", onreceive)
   conn:on("disconnection", ondisconnect)
  end
 end

 ------------------------------------------------------------------------------
 -- HTTP server
 ------------------------------------------------------------------------------
 local srv
 local createserver = function(port)
  if srv then srv:close()
  end
  srv = net.createServer(net.TCP, 5)
  local externalhandler = {}
  externalhandler.use = function(self, routes)
   self.handler = routes;
  end
  -- Listen
  srv:listen(port, httphandler(externalhandler))
  print(node.heap())
  print("Server listening on port " .. tostring(port))
  return externalhandler
 end
 
 return {
  createserver = createserver
 }
end
