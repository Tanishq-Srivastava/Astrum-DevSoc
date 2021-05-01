#include <ETH.h>
#include <WiFi.h>
#include <FirebaseESP32.h>
#define FIREBASE_HOST "https://astrum-54dea-default-rtdb.firebaseio.com/"
#define FIREBASE_AUTH "6j0UzfCNLKDaOLMGKtAUjvKktxu3h5vVXzLpJ9yn"
#define WIFI_SSID "Inspiron"
#define WIFI_PASSWORD "inspiron"
FirebaseData firebaseData;

#define silent_mode_pin 34
//#define noisy_mode_pin 13
//char noisy_char='N';
//char silent_char='S';
float num=1.0;

void setup() {
  // put your setup code here, to run once:
    Serial.begin(115200);
    delay(1000);
    WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
    Serial.print("Connecting to Wi-Fi");
    while (WiFi.status() != WL_CONNECTED)
    {
      Serial.print(".");
      delay(300);
    }
    Firebase.begin(FIREBASE_HOST, FIREBASE_AUTH);
    Firebase.reconnectWiFi(true);
    Serial.println(WiFi.localIP());

    pinMode(silent_mode_pin, OUTPUT);
    //pinMode(noisy_mode_pin, OUTPUT);
}

void loop() {
  // put your main code here, to run repeatedly:
    /*int Noisy=digitalRead(noisy_mode_pin);
    if (Noisy==HIGH){
       if(Firebase.set(firebaseData, "/Noisy_Mode",(1)))
     {
        Serial.println("Success_Noisy");
     }
        else
     {
        Serial.println("error_Noisy");
        Serial.println(firebaseData.errorReason());
     }
        delay(1500);
    }*/

    int Silent=digitalRead(silent_mode_pin);
    if (Silent==HIGH){
       if(Firebase.set(firebaseData, "/Silent_Mode",(num)))
     {
        Serial.println("Success_Silent");
     }
        else
     {
        Serial.println("error_Silent");
        Serial.println(firebaseData.errorReason());
     }
        delay(1500);
    }
    
}
