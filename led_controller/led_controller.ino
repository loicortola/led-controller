
#include <ESP8266WebServer.h>
#include <ESP8266mDNS.h>
#include <FS.h>
#include "DAO.h"
#include "LedController.h"
#include "WifiController.h"
#include "Color.h"
#include "Animation.h"
#include "AnimationRequestHandler.h"
#include "StatusRequestHandler.h"
#include "ColorRequestHandler.h"
#include "PowerRequestHandler.h"
#include "WifiRequestHandler.h"
#include "SSDPDevice.h"

ESP8266WebServer server(80); // on instancie un serveur ecoutant sur le port 80
DAO dao;
LedController lc(&dao);
WifiController wc(&dao, &lc);
AnimationRequestHandler animationHandler(server, lc);
StatusRequestHandler statusHandler(server, lc);
ColorRequestHandler colorHandler(server, lc);
PowerRequestHandler powerHandler(server, lc);
WifiRequestHandler wifiHandler(server, lc, wc);

int c = 0;

void setup(void){
  
  Serial.begin(115200);
  Serial.println("");
  
  // First thing to do is load the leds
  lc.begin();
  // Then load wifi controller
  wc.begin();
  if (wc.isConfigured()) {
    start();
  } else {
    configure();
  }
  // Get heap status, analog input value and all GPIO statuses in one json call
  server.on("/health", HTTP_GET, [](){
    String json = "{";
    json += "\"heap\":"+String(ESP.getFreeHeap());
    json += "}";
    server.send(200, "application/json charset=utf-8;", json);
  });
  server.begin();
}

void start() {
  lc.loadDefaults();
  wc.connect();
  
  // Status route
  server.on("/api/status", HTTP_GET, [statusHandler](){statusHandler.getHandler();});
  
  // Switch route
  server.on("/api/power", HTTP_POST, [powerHandler](){powerHandler.postHandler();});
  
  // Animate route
  server.on("/api/animate", HTTP_POST, [animationHandler](){animationHandler.postHandler();});

  // Color route
  server.on("/api/color", HTTP_POST, [colorHandler](){colorHandler.postHandler();});

  // FIXME remove in prod
  server.on("/api/clear", [](){
    dao.clear();
    ESP.restart();
  });
  
  // SSDP route
  server.on("/description.xml", HTTP_GET, []() {
    SSDPDevice.schema(server.client());
  });
  String name = "Led Controller " + String(ESP.getChipId());
  SSDPDevice.setName(name);
  SSDPDevice.setDeviceType("urn:schemas-upnp-org:device:DimmableRGBLight:2");
  SSDPDevice.setSchemaURL("description.xml");
  SSDPDevice.setSerialNumber(ESP.getChipId());
  SSDPDevice.setURL("/description.xml");
  SSDPDevice.setModelName(name);
  SSDPDevice.setModelNumber(String(ESP.getChipId()));
  SSDPDevice.setManufacturer("Resourcepool.io");
}

void configure() {
  // Blink green <=> configuration mode
  lc.blink(new Color(0, 255, 0));
  
  // Get SSIDs route
  server.on("/api/ssids", [wifiHandler](){wifiHandler.getHandler();});
  server.on("/api/register", [wifiHandler](){wifiHandler.postHandler();});
  server.on("/api/restart", [](){
    ESP.restart();
  });
  server.on("/api/clear", [](){
    dao.clear();
    ESP.restart();
  });
  // Serve static files
  SPIFFS.begin();
  Serial.println("Mounted filesystem");
  server.serveStatic("/", SPIFFS, "/index.html");
  server.serveStatic("/static/", SPIFFS, "/");
}

void loop(void){
  server.handleClient();
  SSDPDevice.handleClient();
  yield();
}
