------------------------------------------------------------------------------
-- HTTP server module
--
-- LICENCE: http://opensource.org/licenses/MIT
-- Vladimir Dronnikov <dronnikov@gmail.com>
-- Contributors: Loic Ortola https://github.com/loicortola
------------------------------------------------------------------------------
local collectgarbage, tonumber, tostring = collectgarbage, tonumber, tostring

-- HTTP status codes as defined in RFC 2616 + common ones along with their message
local statusCodes = {
    [100] = 'Continue',
    [101] = 'Switching Protocols',
    [102] = 'Processing',         -- RFC 4918
    [200] = 'OK',
    [201] = 'Created',
    [202] = 'Accepted',
    [203] = 'Non-Authoritative Information',
    [204] = 'No Content',
    [205] = 'Reset Content',
    [206] = 'Partial Content',
    [207] = 'Multi-Status',           -- RFC 4918
    [300] = 'Multiple Choices',
    [301] = 'Moved Permanently',
    [302] = 'Moved Temporarily',
    [303] = 'See Other',
    [304] = 'Not Modified',
    [305] = 'Use Proxy',
    [307] = 'Temporary Redirect',
    [400] = 'Bad Request',
    [401] = 'Unauthorized',
    [402] = 'Payment Required',
    [403] = 'Forbidden',
    [404] = 'Not Found',
    [405] = 'Method Not Allowed',
    [406] = 'Not Acceptable',
    [407] = 'Proxy Authentication Required',
    [408] = 'Request Time-out',
    [409] = 'Conflict',
    [410] = 'Gone',
    [411] = 'Length Required',
    [412] = 'Precondition Failed',
    [413] = 'Request Entity Too Large',
    [414] = 'Request-URI Too Large',
    [415] = 'Unsupported Media Type',
    [416] = 'Requested Range Not Satisfiable',
    [417] = 'Expectation Failed',
    [418] = 'I\'m a teapot',              -- RFC 2324
    [422] = 'Unprocessable Entity',       -- RFC 4918
    [423] = 'Locked',                     -- RFC 4918
    [424] = 'Failed Dependency',          -- RFC 4918
    [425] = 'Unordered Collection',       -- RFC 4918
    [426] = 'Upgrade Required',           -- RFC 2817
    [500] = 'Internal Server Error',
    [501] = 'Not Implemented',
    [502] = 'Bad Gateway',
    [503] = 'Service Unavailable',
    [504] = 'Gateway Time-out',
    [505] = 'HTTP Version not supported',
    [506] = 'Variant Also Negotiates',    -- RFC 2295
    [507] = 'Insufficient Storage',       -- RFC 4918
    [509] = 'Bandwidth Limit Exceeded',
    [510] = 'Not Extended'                -- RFC 2774
}
    
local http
do
  ------------------------------------------------------------------------------
  -- request methods
  ------------------------------------------------------------------------------
  local make_req = function(conn, method, url, params)
    local req = {
      conn = conn,
      method = method,
      url = url,
      params = params
    }
    -- return setmetatable(req, {
    -- })
    return req
  end

  ------------------------------------------------------------------------------
  -- response methods
  ------------------------------------------------------------------------------
  local send = function(self, data, status)
    local c = self.conn
    c:send("HTTP/1.1 ")
    c:send(tostring(status or self.statusCode))
    c:send(" " + tostring(statusCodes[status or self.statusCode]) + "\r\n")
    self:addHeader("Date",os.date("!%a, %d %b %Y %H:%M:%S GMT"))
    self:addHeader("Server", "NodeMCU")
    if data then
        self:addHeader("Content-Length", string.len(data))
    end
    --   write response headers
    for key,value in pairs(self.headers) do
        c:send(key)
        c:send(": ")
        c:send(value)
        c:send("\r\n")
    end
    if data then
      c:send(data)
    end
    -- close connection
    c:close()
  end
  local addHeader = function(self, name, value)
    local h = self.headers
    h[name] = value
  end
  local make_res = function(conn)
    local res = {
      conn = conn,
      headers = {},
      statusCode = 200
    }
    res.addHeader = addHeader
    res.send = send
    return res
  end

  ------------------------------------------------------------------------------
  -- HTTP parser
  ------------------------------------------------------------------------------
  local http_handler = function(handler)
    return function(conn)
      local req, res
      local buf = ""
      local method, url
      local ondisconnect = function(conn)
        collectgarbage("collect")
      end
      -- header parser
      local cnt_len = 0
      local onheader = function(conn, k, v)
        -- TODO: look for Content-Type: header
        -- to help parse body
        -- parse content length to know body length
        if k == "content-length" then
          cnt_len = tonumber(v)
        end
        if k == "expect" and v == "100-continue" then
          conn:send("HTTP/1.1 100 Continue\r\n")
        end
        -- delegate to request object
        if req and req.onheader then
          req:onheader(k, v)
        end
      end
      -- body data handler
      local body_len = 0
      local ondata = function(conn, chunk)
        -- NB: do not reset node in case of lengthy requests
        tmr.wdclr()
        -- feed request data to request handler
        if not req or not req.ondata then return end
        req:ondata(chunk)
        -- NB: once length of seen chunks equals Content-Length:
        --   onend(conn) is called
        body_len = body_len + #chunk
        -- print("-B", #chunk, body_len, cnt_len, node.heap())
        if body_len >= cnt_len then
          req:ondata()
        end
      end
      local onreceive = function(conn, chunk)
        -- merge chunks in buffer
        if buf then
          buf = buf .. chunk
        else
          buf = chunk
        end
        -- consume buffer line by line
        while #buf > 0 do
          -- extract line
          local e = buf:find("\r\n", 1, true)
          if not e then break end
          local line = buf:sub(1, e - 1)
          buf = buf:sub(e + 2)
          -- method, url?
          if not method then
            local i
            -- NB: just version 1.1 assumed
            _, i, method, url = line:find("^([A-Z]+) (.-) HTTP/1.1$")
            _, _, url, queryString = string.find(url, "([^%s]+)%?([^%s]+)")
            params = {}
            if queryString then
                for name, value in string.gfind(queryString, "([^&=]+)=([^&=]+)") do
                    params[name] = value
                end
            end
            if method then
              -- make request and response objects
              req = make_req(conn, method, url, params)
              res = make_res(conn)
            end
          -- header line?
          elseif #line > 0 then
            -- parse header
            local _, _, k, v = line:find("^([%w-]+):%s*(.+)")
            -- header seems ok?
            if k then
              k = k:lower()
              onheader(conn, k, v)
            end
          -- headers end
          else
            -- spawn request handler
            -- NB: do not reset in case of lengthy requests
            tmr.wdclr()
            handler(req, res)
            tmr.wdclr()
            -- NB: we feed the rest of the buffer as starting chunk of body
            ondata(conn, buf)
            -- buffer no longer needed
            buf = nil
            -- NB: we explicitly reassign receive handler so that
            --   next received chunks go directly to body handler
            conn:on("receive", ondata)
            -- parser done
            break
          end
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
  local createServer = function(port, handler)
    if srv then srv:close() end
    srv = net.createServer(net.TCP, 15)
    -- listen
    srv:listen(port, http_handler(handler))
    return srv
  end
  ------------------------------------------------------------------------------
  -- HTTP server methods
  ------------------------------------------------------------------------------
  http = {
    createServer = createServer
  }
end
return http
