#include <NimBLEDevice.h>

#define SERVICE_UUID           "4fafc201-1fb5-459e-8fcc-c5c9c331914b"
#define CHARACTERISTIC_UUID    "beb5483e-36e1-4688-b7f5-ea07361b26a8"

NimBLECharacteristic *pCharacteristic;
bool deviceConnected = false;

// Server Callback to track connection status
class MyServerCallbacks : public NimBLEServerCallbacks {
    void onConnect(NimBLEServer* pServer) {
        deviceConnected = true;
        Serial.println("Device connected.");
    }

    void onDisconnect(NimBLEServer* pServer) {
        deviceConnected = false;
        Serial.println("Device disconnected.");
    }
};

void setup() {
    Serial.begin(115200);
    NimBLEDevice::init("ESP32_Sensor_Device");

    NimBLEServer *pServer = NimBLEDevice::createServer();
    pServer->setCallbacks(new MyServerCallbacks());

    NimBLEService *pService = pServer->createService(SERVICE_UUID);
    pCharacteristic = pService->createCharacteristic(
                        CHARACTERISTIC_UUID,
                        NIMBLE_PROPERTY::NOTIFY
                    );

    pService->start();

    NimBLEAdvertising *pAdvertising = NimBLEDevice::getAdvertising();
    pAdvertising->addServiceUUID(SERVICE_UUID);
    pAdvertising->start();
    Serial.println("ESP32 is advertising...");
}

void loop() {
    if (deviceConnected) {
        int sensorValue = analogRead(34); // Replace with actual sensor code if needed
        String sensorData = String(sensorValue);
        pCharacteristic->setValue(sensorData.c_str());
        pCharacteristic->notify();
        Serial.print("Sending sensor value: ");
        Serial.println(sensorData);
        delay(2000);  // Send updates every 2 seconds
    }
}
