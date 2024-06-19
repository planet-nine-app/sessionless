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

  // Initialize all board peripherals, call this first
  ums3.begin();

  // Brightness is 0-255. We set it to 1/3 brightness here
  ums3.setPixelBrightness(255 / 3);
  if(!BLE.begin())
  {
    Serial.println("starting Bluetooth® Low Energy module failed!");

    while (1);
  }

  // This works with the TwoWay Peripheral protocol used by MAGIC
  BLE.scanForName("TwoWay");
}

int color = 100;
int success = 115;
int failure = 50;
Keys keys;

void loop() {
  std::string msg = "Here is a message";
  
  // This hardcoded key should NOT be used in production. It will be I'm sure, 
  // but it shouldn't
  unsigned char privateKey[] = {0xd6, 0xbf, 0xeb, 0xea, 0xfa, 
    0x60, 0xe2, 0x71, 0x14, 0xa4, 0x00,
    0x59, 0xa4, 0xfe, 0x82, 0xb3, 0xe7,
    0xa1, 0xdd, 0xb3, 0x80, 0x6c, 0xd5,
    0x10, 0x26, 0x91, 0xc3, 0x98, 0x5d,
    0x7f, 0xa5, 0x91};
  unsigned char *privKey = privateKey;
  unsigned char signature[64];
  unsigned char msgHash[32];

  bool ret = sessionless::sign(msg, privKey, signature);
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

    // This is the write characteristic for TwoWay peripherals
    BLECharacteristic writeCharacteristic = peripheral.characteristic("4D8D84E5-5889-4310-80BF-0D44DCB49762");
    
    std::string str = toHexString(signature, sizeof(signature));
    if(!writeCharacteristic.writeValue(str.c_str())) {
      ums3.setPixelColor(UMS3::colorWheel(35));
    } else {
      ums3.setPixelColor(UMS3::colorWheel(135));
    }
  } else {
    ums3.setPixelColor(UMS3::colorWheel(10));
  }

  delay(1500);
}