------------------------------------------------------------------------------
-- add header (should be called before send)
------------------------------------------------------------------------------
local addheader = function(res, name, value)
 local h = res.headers
 h[name] = value
end

------------------------------------------------------------------------------
-- send response
------------------------------------------------------------------------------
local send = function(res, data, status)
 dofile("http-response-send.lua")(res, data, status)
end

local sendfile = function(res, filename, status)
 dofile("http-response-sendfile.lua")(res, filename, status)
end

local createresponse = function(conn)
 return {
  conn = conn,
  headers = {},
  addheader = addheader,
  send = send,
  sendfile = sendfile,
  statuscode = 200
 }
end

return createresponse
