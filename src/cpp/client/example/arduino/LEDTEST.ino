#include <UMS3.h>
#include "sessionless.h"
#include <ArduinoBLE.h>
#include <cstdio>
#include <string>
#include <random>
#include <bitset>
#include <iostream>
#include <sstream>
#include <iomanip>

#if !defined(CONFIG_BT_ENABLED) || !defined(CONFIG_BLUEDROID_ENABLED)
#error Bluetooth is not enabled! Please run `make menuconfig` to and enable it
#endif

UMS3 ums3;
//BLEDevice central = BLE.central();

std::string toHexString(const unsigned char *buffer, const size_t length)
{
    std::stringstream ss;
    ss << std::hex;

    for (auto i = 0; i < length; ++i)
    {
        // Format for zero padded hex string
        ss << std::setw(2)
           << std::setfill('0')
           << (int)buffer[i];
    }
    return ss.str();
}

void setup() {
  Serial.begin(115200);
  Serial.setDebugOutput(true);
  //Serial.println("ooga booga");
  // Initialize all board peripherals, call this first
  ums3.begin();

  // Brightness is 0-255. We set it to 1/3 brightness here
  ums3.setPixelBrightness(255 / 3);
  if(!BLE.begin())
  {
    Serial.println("starting Bluetooth® Low Energy module failed!");

    while (1);
  }

  //BLE.scanForUuid("5995ab90-709a-4735-aaf2-df2c8b061bb4");
  //BLE.scan();
  BLE.scanForName("TwoWay");
}

int color = 100;
int success = 115;
int failure = 50;
Keys keys;

void loop() {
  const char *msg = "Here is a message";
  //const unsigned char msg[18] = "Here is a message";
  /*unsigned char privateKey[] = {std::byte{0xd6} std::byte{0xbf} std::byte{0xeb} std::byte{0xea} std::byte{0xfa} 
    std::byte{0x60} std::byte{0xe2} std::byte{0x71} std::byte{0x14} std::byte{0xa4} std::byte{0x00}
    std::byte{0x59} std::byte{0xa4} std::byte{0xfe} std::byte{0x82} std::byte{0xb3} std::byte{0xe7}
    std::byte{0xa1} std::byte{0xdd} std::byte{0xb3} std::byte{0x80} std::byte{0x6c} std::byte{0xd5}
    std::byte{0x10} std::byte{0x26} std::byte{0x91} std::byte{0xc3} std::byte{0x98} std::byte{0x5d}
    std::byte{0x7f} std::byte{0xa5} std::byte{0x91}};*/
  unsigned char privateKey[] = {0xd6, 0xbf, 0xeb, 0xea, 0xfa, 
    0x60, 0xe2, 0x71, 0x14, 0xa4, 0x00,
    0x59, 0xa4, 0xfe, 0x82, 0xb3, 0xe7,
    0xa1, 0xdd, 0xb3, 0x80, 0x6c, 0xd5,
    0x10, 0x26, 0x91, 0xc3, 0x98, 0x5d,
    0x7f, 0xa5, 0x91};
  unsigned char *privKey = privateKey;
  unsigned char signature[64];
  unsigned char msgHash[32];

  //ums3.setPixelColor(UMS3::colorWheel(color));
  //int ret = 1;
  bool ret = sessionless::sign((unsigned char *)msg, sizeof(msg), privKey, signature);
  //if (!sessionless::sign((unsigned char *)msg, sizeof(msg), privKey, signature))
  if(ret)
  {
    ums3.setPixelColor(UMS3::colorWheel(success));
  } else 
  {
    ums3.setPixelColor(UMS3::colorWheel(failure));
  }

  BLEDevice peripheral = BLE.available();

  if (peripheral) {
    ums3.setPixelColor(UMS3::colorWheel(125));
    if(!peripheral.connect()) {
      ums3.setPixelColor(UMS3::colorWheel(1));
    }
    if(!peripheral.discoverAttributes()) {
      ums3.setPixelColor(UMS3::colorWheel(35));
    }

    BLECharacteristic writeCharacteristic = peripheral.characteristic("4D8D84E5-5889-4310-80BF-0D44DCB49762");
    /*uint8_t buffer[128];
    for (int i = 0; i < 128; i++)
    {
      buffer[i] = uint8_t(signature[i]);
    }*/

    /*char* stringCharValue = new char[128];
    for (int i = 0; i < 128; i++)
    {
      stringCharValue[i] = char(signature[i]);
    }
    if(!writeCharacteristic.writeValue(stringCharValue)) {*/
    //char *foobar = "foo bar";
    /*std::stringstream sigstream;
    for(int i = 0; i < sizeof(signature); ++i)
      sigstream << std::hex << (int)signature[i];*/
    std::string str = toHexString(signature, sizeof(signature));
    if(!writeCharacteristic.writeValue(str.c_str())) {
      //if(!writeCharacteristic.writeValue(sigstream.str().c_str())) {

    //unsigned char *sig = &signature;
    //if(!writeCharacteristic.writeValue(signature, sizeof(signature))) {
      
    

    //if(!writeCharacteristic.writeValue(buffer, sizeof(buffer))) {
      ums3.setPixelColor(UMS3::colorWheel(35));
    }

    ums3.setPixelColor(UMS3::colorWheel(135));
  } else {
    ums3.setPixelColor(UMS3::colorWheel(10));
  }

  /*// colorWheel cycles red, orange, ..., back to red at 256
  Serial.println("starting");
  //ums3.setPixelColor(UMS3::colorWheel(color));
  color++;

  int res = sessionless::generateKeys(keys);
  if(res > -1) {
 // if(!sessionless::generateKeys(&keys)) {
    Serial.println("no keys");
    ums3.setPixelColor(UMS3::colorWheel(res * 50));
  }

  // On the feathers3, toggle the LED twice per cycle
#ifdef ARDUINO_FEATHERS3
  if (color % 128 == 0) {
    ums3.toggleBlueLED();
  }
#endif*/

  delay(1500);
}