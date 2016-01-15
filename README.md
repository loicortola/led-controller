# espress NodeMCU http-server

Lightweight modular http-server for NodeMCU. Emphasizes code-as-a-config.

Features:  
 * ApiKey authentication module
 * Easy service of static pages
 * Easy custom webservices with your own lua code
 * GET, POST PUT, DELETE, OPTIONS, HEAD supported
 * Querystring, headers, method and body parser
 * Completely customizable. Make your own plugin work in a few seconds.
 * Inspired by Node.js Express
 * ESP8266 friendly : Can take between 10 and 20 Kb of memory for typical setups
 * Gatling-tool ready, with a scenario provided to test on your own API
 
## Setup

Transfer the plugin content using the upload.sh or upload.bat scripts

## Recipes

### How to use

 ```lua
  local espress = require 'espress'
  local port = 80
  local server = espress.createserver(port)
  -- Declare desired plugins one by one
  -- syntax is server:use("plugin" [, opts)
  server:use("auth_api_key.lua", {apikey = "1234-abcd", includes = "/api"})
  server:use("routes_auto.lua")
 ```

### Req/Res API
 #### Request
 req = {  
  params,  
  headers,  
  method,  
  body  
 }
 
 ##### req.params
 Holds the querystring parameters.
 Example: for http://host/api/computers?id=1234  
 ```lua
   local id = tonumber(req.params["id"]) -- 1234
 ```
 
 ##### req.headers
 Holds the request headers.  
 N.B.: header name is stored in lower-case
 ```lua
   local contenttype = req.headers["content-type"] -- "application/json"
 ```
 
 ##### req.method
 Contains the request method: "GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD"
 
 ##### req.body
 Contains the body parsed into a string.  
 N.B.: You have the liberty of parsing this string to whatever you feel comfortable with. It is a voluntary choice not to parse it into JSON or other formats.
 Sample parsers for form and json are available in the samples.  
 
 #### Response
 
 Example :
 res = {
  conn,  
  send,  
  sendfile,  
  statuscode,  
  headers,  
  addheader  
 }
 
 ##### res.conn
 The http connection socket. Please refer to NodeMCU's API for more details
  
 ##### res.send
 Send payload in response and close connection after. The payload is sent in one chunk and length should not exceed 1460 bytes.
 If payload is bigger than 1460, please use res.sendfile instead.
 
 Example:  
 ```lua
  local content = "{message: \"Hello world\"}"
  res:send(content)
 ```
 
 ##### res.sendfile
 Send static file in response and close connection after. The payload is sent into multiple chunks of 1460 bytes, and should be used to process all static content. 
 
 Example:
 ```lua
   res:sendfile("static/404-not-found.html")
 ```
 
 ##### res.statuscode
 Sets the response http statuscode.
  
 Example:
 ```lua
   res.statuscode = 102
 ```
 
 ##### res.headers
 Contains the response headers. Use for read-only. To add or edit response headers, rather use res.addheader
 
 ##### res.addheader
 Add or edit response header.
 
 Example:  
 ```lua
   res.addheader("Content-Type", "application/json")
 ```
 
 

### Plugins
Available plugins are:  
 * auth_api_key : implementation for an Api-Key header-based authentication
 * routes_auto : automatic routing using your /static and /routes sub-folders (recommended)
 * routes_custom : to perform advanced routing
 
#### Plugin auth-api-key
 This plugin will intercept all requests and look for an Api-Key header.  
 Valid options for this plugin are:  
 * apikey: the desired apikey to secure  
 * includes: a uri prefix to which auth will be enabled (will exclude all others)  
 * excludes: a uri prefix to which auth will be bypassed (will include all others)  
 
 ```lua
  server:use("auth_api_key.lua", {apikey = "1234-abcd", includes = "/api"})
 ```
 
 The following responses can be expected :  
 * **400 BAD-REQUEST** if Api-Key header is not present  
 * **401 UNAUTHORIZED** if Api-Key header value does not match the one in **auth-api-key-conf.lua**    
 * Forward to next handler if everything went well
 
#### Plugin routes-auto
The plugin will automatically parse the request url and lookup for the corresponding files depending on their name.

Two kinds of content are supported :

##### Static files (webcontent)
Your webpages, css, images, and static content should be stored as static/filename.ext onto the NodeMCU filesystem.  

For instance:  
static/index.html  
static/style.css  
static/logo.png  
static/script.js  

##### Dynamic content
Your API scripts should be stored as routes/path.method.lua (example: register.post.lua, computers.get.lua ...) and should hold the following function:  
 ```lua
 return function(req, res)
  -- your code here
  res:addheader("Content-Type", "application/json; charset=utf-8")
  res:send("{red:" .. 255 - pwm.getduty(1) / 4 .. ", green:" .. 255 - pwm.getduty(2) / 4 .. ", blue:" .. 255 - pwm.getduty(3) / 4 .. "}")
  -- end of your code
 end
 ```

The scripts will be available under the following uri: host/api/path  
For instance: 
routes/computers.get.lua <=> [GET] http://host/api/computers  
routes/register.post.lua <=> [POST] http://host/api/register  

Don't forget to take a look at the req and res API.  
Samples are available into the sample/ subfolder

#### Plugin routes-custom
This plugin uses a node.js like route handler.  
Declarations are made this way:
 ```lua
 local router = require 'router'
 router.get("/computers", "routes/computers.get.lua")
 router.post("/user/register", "routes/register.get.lua")
 router.delete("/user/employee", "routes/employee-revoke.delete.lua")
 return router.handler 
 ```

Save your own script with your own routes and load it using server:use()