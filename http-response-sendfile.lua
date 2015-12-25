do
 ------------------------------------------------------------------------------
 -- send header
 ------------------------------------------------------------------------------
 local sendheader = function(res, k, v)
  res.conn:send(k .. ": " .. v .. "\r\n")
 end

 local sendfile = function(res, filename, status)
  print("Before send : " .. node.heap())
  local buffersize = 512
  local offset = 0
  local buf

  if not file.open(filename, "r") then
   dofile("http-not-found.lua")(res)
  else
   res.conn:send("HTTP/1.1 " .. tostring(status or res.statuscode) .. " " .. dofile('http-' .. tostring(status or res.statuscode)) .. "\r\n")
   --   Write response headers
   res:addheader("Server", "NodeMCU")
   res:addheader("Transfer-Encoding", "chunked")
   for key, value in pairs(res.headers) do
    sendheader(res, key, value)
   end
   res.conn:send("\r\n")

   -- Send file body
   local function sendnextchunk()
    collectgarbage("collect")
    file.seek("set", offset)
    buf = file.read(buffersize)
    res.conn:send(("%X\r\n"):format(#buf) .. buf .. "\r\n")
   end

   res.conn:on("sent", function(conn)
    if (#buf == buffersize) then
     offset = offset + buffersize
     sendnextchunk()
    else
     -- Manually free resources for gc
     buf = nil
     buffersize = nil
     offset = nil
     sendnextchunk = nil
     -- Close connection
     conn:send("0\r\n\r\n")
     file.close()
     conn:close()
    end
   end)
   
   sendnextchunk()
  end
 end
 return sendfile
end