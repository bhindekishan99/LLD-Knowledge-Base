# Adapter Design Pattern

> **Category:** Structural Design Pattern

---

# Sources

## 🎥 YouTube

https://www.youtube.com/watch?v=DPohrgR-SD0

## 🌐 Blog

https://codewitharyan.com/tech-blogs/adapter-design-pattern

---

# How to Think Towards the Adapter Pattern

Forget the Adapter Pattern for a moment.

Imagine you're building a **Smart Home Controller**.

Your goal is simple.

From a single mobile application, the user should be able to control different smart devices.

Initially, the system supports

- Air Conditioner
- Smart Light
- Coffee Machine

Everything sounds easy.

Until you discover something.

---

## Every Device Speaks a Different Language

The Air Conditioner communicates using **Bluetooth**.

```java
connectViaBluetooth();
startCooling();
```

The Smart Light communicates using **Wi-Fi**.

```java
connectToWiFi();
switchOn();
```

The Coffee Machine communicates using **Zigbee**.

```java
initializeZigbeeConnection();
startBrewing();
```

Although all three devices perform a similar job,

they expose completely different APIs.

---

## Our First Thought

As developers,

we naturally write something like this.

```java
if (deviceType.equals("AirConditioner")) {

    airConditioner.connectViaBluetooth();
    airConditioner.startCooling();

}
else if (deviceType.equals("SmartLight")) {

    smartLight.connectToWiFi();
    smartLight.switchOn();

}
else if (deviceType.equals("CoffeeMachine")) {

    coffeeMachine.initializeZigbeeConnection();
    coffeeMachine.startBrewing();

}
```

This works perfectly.

Nothing is wrong.

---

## Requirements Grow

Tomorrow a new manufacturer joins.

```text
Security Camera
```

Next month,

another device arrives.

```text
Smart Speaker
```

Then,

the Smart Light manufacturer changes

from Wi-Fi

to

Cloud API.

Now ask yourself.

How many places must we modify?

Almost every place that directly talks to the devices.

---

## Stop and Observe

What is our application actually trying to do?

It is **not** trying to understand

Bluetooth,

Wi-Fi,

or Zigbee.

It simply wants to perform two operations.

```text
Turn ON

Turn OFF
```

That's all.

The communication protocol is not the application's concern.

It is the device's concern.

---

## The Real Problem

Notice something interesting.

Our application expects every device to behave like this.

```java
turnOn();

turnOff();
```

But every manufacturer provides

its own interface.

```text
Air Conditioner

↓

connectViaBluetooth()

↓

startCooling()
```

```text
Smart Light

↓

connectToWiFi()

↓

switchOn()
```

```text
Coffee Machine

↓

initializeZigbeeConnection()

↓

startBrewing()
```

Both sides are correct.

They simply speak different languages.

---

## Think Like Real Life

Imagine you're travelling to Japan.

You only speak English.

The shopkeeper only speaks Japanese.

Neither of you is wrong.

You simply cannot communicate.

Do you ask the shopkeeper to learn English?

No.

Do you learn Japanese overnight?

No.

You bring a translator.

```text
You

↓

Translator

↓

Shopkeeper
```

The translator understands both languages.

Neither side changes.

Communication becomes possible.

---

## First Refactoring

Instead of allowing the controller to directly communicate with every device,

introduce one common language.

```java
turnOn();

turnOff();
```

Every device will somehow understand these two methods.

But how?

The Air Conditioner doesn't have

```java
turnOn()
```

The Smart Light doesn't have

```java
turnOn()
```

Neither does the Coffee Machine.

So someone has to translate.

---

## Final Refactoring

Introduce an **Adapter**.

```text
Smart Home Controller

↓

turnOn()

↓

Adapter

↓

connectViaBluetooth()

↓

Air Conditioner
```

Similarly,

```text
Smart Home Controller

↓

turnOn()

↓

Adapter

↓

connectToWiFi()

↓

Smart Light
```

The controller always speaks one language.

Every adapter translates it into the language understood by the actual device.

Neither the controller nor the device needs to change.

---

## Complete Thinking Process

```text
Need Smart Home Controller

        ↓

Different Devices

        ↓

Different APIs

        ↓

Client Becomes Full Of if-else

        ↓

Need One Common Interface

        ↓

Devices Cannot Change

        ↓

Translate One Interface Into Another

        ↓

Create Adapter

        ↓

Adapter Pattern
```

---

# How to Remember the Adapter Pattern

Imagine you're travelling to another country.

You speak English.

The shopkeeper speaks Japanese.

Neither of you understands the other.

A translator stands between both of you.

```text
You

↓

Translator

↓

Shopkeeper
```

The translator converts

English

↓

Japanese

and

Japanese

↓

English.

Neither person changes.

The translator simply allows two incompatible interfaces to communicate.

Exactly the same thing happens in the Adapter Pattern.

The client continues using the interface it already understands.

The adapter translates those requests into the format understood by the existing class.

## Memory Trick

> **The Adapter is simply a translator between two incompatible interfaces.**

---

# Traditional Solution

<details>
<summary><strong>Click to view complete implementation</strong></summary>

## SmartHomeController.java

```java
public class SmartHomeController {

    public void turnOnDevice(String deviceType) {

        if (deviceType.equalsIgnoreCase("AirConditioner")) {

            AirConditioner airConditioner = new AirConditioner();

            airConditioner.connectViaBluetooth();
            airConditioner.startCooling();

        }
        else if (deviceType.equalsIgnoreCase("SmartLight")) {

            SmartLight smartLight = new SmartLight();

            smartLight.connectToWiFi();
            smartLight.switchOn();

        }
        else if (deviceType.equalsIgnoreCase("CoffeeMachine")) {

            CoffeeMachine coffeeMachine = new CoffeeMachine();

            coffeeMachine.initializeZigbeeConnection();
            coffeeMachine.startBrewing();

        }
        else {

            System.out.println("Device not supported.");

        }

    }

    public void turnOffDevice(String deviceType) {

        if (deviceType.equalsIgnoreCase("AirConditioner")) {

            AirConditioner airConditioner = new AirConditioner();

            airConditioner.stopCooling();
            airConditioner.disconnectBluetooth();

        }
        else if (deviceType.equalsIgnoreCase("SmartLight")) {

            SmartLight smartLight = new SmartLight();

            smartLight.switchOff();
            smartLight.disconnectWiFi();

        }
        else if (deviceType.equalsIgnoreCase("CoffeeMachine")) {

            CoffeeMachine coffeeMachine = new CoffeeMachine();

            coffeeMachine.stopBrewing();
            coffeeMachine.terminateZigbeeConnection();

        }
        else {

            System.out.println("Device not supported.");

        }

    }

}
```

---

## AirConditioner.java

```java
public class AirConditioner {

    public void connectViaBluetooth() {
        System.out.println("Air Conditioner connected via Bluetooth.");
    }

    public void startCooling() {
        System.out.println("Air Conditioner is now cooling.");
    }

    public void stopCooling() {
        System.out.println("Air Conditioner stopped cooling.");
    }

    public void disconnectBluetooth() {
        System.out.println("Air Conditioner disconnected from Bluetooth.");
    }

}
```

---

## SmartLight.java

```java
public class SmartLight {

    public void connectToWiFi() {
        System.out.println("Smart Light connected to Wi-Fi.");
    }

    public void switchOn() {
        System.out.println("Smart Light is now ON.");
    }

    public void switchOff() {
        System.out.println("Smart Light is now OFF.");
    }

    public void disconnectWiFi() {
        System.out.println("Smart Light disconnected from Wi-Fi.");
    }

}
```

---

## CoffeeMachine.java

```java
public class CoffeeMachine {

    public void initializeZigbeeConnection() {
        System.out.println("Coffee Machine connected via Zigbee.");
    }

    public void startBrewing() {
        System.out.println("Coffee Machine is now brewing coffee.");
    }

    public void stopBrewing() {
        System.out.println("Coffee Machine stopped brewing coffee.");
    }

    public void terminateZigbeeConnection() {
        System.out.println("Coffee Machine disconnected from Zigbee.");
    }

}
```

---

## Client Code

```java
public class Main {

    public static void main(String[] args) {

        SmartHomeController controller = new SmartHomeController();

        controller.turnOnDevice("AirConditioner");
        controller.turnOnDevice("SmartLight");
        controller.turnOnDevice("CoffeeMachine");

        System.out.println();

        controller.turnOffDevice("AirConditioner");
        controller.turnOffDevice("SmartLight");
        controller.turnOffDevice("CoffeeMachine");

    }

}
```

---

## Output

```text
Air Conditioner connected via Bluetooth.
Air Conditioner is now cooling.

Smart Light connected to Wi-Fi.
Smart Light is now ON.

Coffee Machine connected via Zigbee.
Coffee Machine is now brewing coffee.

Air Conditioner stopped cooling.
Air Conditioner disconnected from Bluetooth.

Smart Light is now OFF.
Smart Light disconnected from Wi-Fi.

Coffee Machine stopped brewing coffee.
Coffee Machine disconnected from Zigbee.
```

---

## Problems With This Approach

Although this implementation works today, it becomes difficult to maintain as the number of devices grows.

---

### 1. The Controller Is Tightly Coupled to Every Device

The controller directly knows about every concrete device.

```text
SmartHomeController

↓

AirConditioner

↓

SmartLight

↓

CoffeeMachine
```

Whenever a new device is introduced, the controller must be modified.

The controller should only know **how to control a device**, not **how every manufacturer has implemented it**.

---

### 2. Different APIs Everywhere

Every manufacturer exposes a different API.

```text
Air Conditioner

connectViaBluetooth()

startCooling()
```

```text
Smart Light

connectToWiFi()

switchOn()
```

```text
Coffee Machine

initializeZigbeeConnection()

startBrewing()
```

The controller now needs to remember every device's API.

This makes the code harder to read and maintain.

---

### 3. Violates the Open/Closed Principle

Tomorrow a new device arrives.

```text
Security Camera
```

Now we must modify

```java
SmartHomeController
```

and add another

```java
else if (...)
```

block.

Every new device requires changing existing code.

Instead of extending the system,

we keep modifying it.

---

### 4. Protocol Changes Affect the Controller

Suppose the Smart Light manufacturer changes its implementation.

Old protocol

```text
Wi-Fi
```

New protocol

```text
Cloud API
```

Now the controller must also change.

Even though the controller's responsibility has nothing to do with communication protocols.

---

### 5. Controller Has Too Many Responsibilities

The controller should simply perform

```text
Turn ON

Turn OFF
```

Instead, it also understands

- Bluetooth
- Wi-Fi
- Zigbee
- Device-specific APIs
- Manufacturer implementations

These responsibilities don't belong in the controller.

---

### 6. Difficult to Scale

Imagine supporting

- 50 Smart Devices
- 10 Manufacturers

The controller would become hundreds of lines long.

Every new manufacturer increases its complexity.

Eventually, the controller becomes the hardest class to maintain.

---

## The Real Problem

Notice something interesting.

The controller doesn't actually care

**how** a device turns on.

It only wants to execute

```java
turnOn();

turnOff();
```

The problem is that every device exposes a completely different interface.

We need something that can **translate** the controller's requests into the language understood by each device.

That "translator" is exactly what the **Adapter Pattern** provides.

</details>

---

# Applying the Adapter Pattern

<details>
<summary><strong>Click to view complete implementation</strong></summary>

## SmartDevice.java

```java
// Target Interface
// Defines the common interface expected by the Smart Home Controller.
public interface SmartDevice {

    void turnOn();

    void turnOff();

}
```

<details>
<summary>💡 Why do we need a common interface?</summary>

A common question is:

> **Why can't the SmartHomeController directly communicate with every device?**

Initially, it can.

```java
AirConditioner airConditioner = new AirConditioner();

airConditioner.connectViaBluetooth();
airConditioner.startCooling();
```

Similarly,

```java
SmartLight smartLight = new SmartLight();

smartLight.connectToWiFi();
smartLight.switchOn();
```

Every device has its own API.

As more manufacturers are integrated, the controller keeps learning new APIs.

Instead, we define **one language** that every device should understand.

```java
turnOn();

turnOff();
```

Now the controller doesn't care whether the device internally uses

- Bluetooth
- Wi-Fi
- Zigbee
- Cloud API

It simply calls

```java
device.turnOn();
```

This keeps the controller independent of manufacturer-specific implementations.

</details>

---

## Existing Device Classes (Adaptees)

These are third-party classes.

We cannot modify them because they belong to different manufacturers.

### AirConditioner.java

```java
public class AirConditioner {

    public void connectViaBluetooth() {
        System.out.println("Connecting Air Conditioner via Bluetooth...");
    }

    public void startCooling() {
        System.out.println("Air Conditioner started cooling.");
    }

    public void stopCooling() {
        System.out.println("Air Conditioner stopped cooling.");
    }

    public void disconnectBluetooth() {
        System.out.println("Disconnecting Bluetooth.");
    }

}
```

---

### SmartLight.java

```java
public class SmartLight {

    public void connectToWiFi() {
        System.out.println("Connecting Smart Light to Wi-Fi...");
    }

    public void switchOn() {
        System.out.println("Smart Light switched ON.");
    }

    public void switchOff() {
        System.out.println("Smart Light switched OFF.");
    }

    public void disconnectWiFi() {
        System.out.println("Disconnecting Wi-Fi.");
    }

}
```

---

### CoffeeMachine.java

```java
public class CoffeeMachine {

    public void initializeZigbeeConnection() {
        System.out.println("Connecting Coffee Machine via Zigbee...");
    }

    public void startBrewing() {
        System.out.println("Coffee brewing started.");
    }

    public void stopBrewing() {
        System.out.println("Coffee brewing stopped.");
    }

    public void terminateZigbeeConnection() {
        System.out.println("Disconnecting Zigbee.");
    }

}
```

---

## AirConditionerAdapter.java

```java
// Adapter for Air Conditioner.
public class AirConditionerAdapter implements SmartDevice {

    private final AirConditioner airConditioner;

    public AirConditionerAdapter(AirConditioner airConditioner) {
        this.airConditioner = airConditioner;
    }

    @Override
    public void turnOn() {

        airConditioner.connectViaBluetooth();
        airConditioner.startCooling();

    }

    @Override
    public void turnOff() {

        airConditioner.stopCooling();
        airConditioner.disconnectBluetooth();

    }

}
```

---

## SmartLightAdapter.java

```java
// Adapter for Smart Light.
public class SmartLightAdapter implements SmartDevice {

    private final SmartLight smartLight;

    public SmartLightAdapter(SmartLight smartLight) {
        this.smartLight = smartLight;
    }

    @Override
    public void turnOn() {

        smartLight.connectToWiFi();
        smartLight.switchOn();

    }

    @Override
    public void turnOff() {

        smartLight.switchOff();
        smartLight.disconnectWiFi();

    }

}
```

---

## CoffeeMachineAdapter.java

```java
// Adapter for Coffee Machine.
public class CoffeeMachineAdapter implements SmartDevice {

    private final CoffeeMachine coffeeMachine;

    public CoffeeMachineAdapter(CoffeeMachine coffeeMachine) {
        this.coffeeMachine = coffeeMachine;
    }

    @Override
    public void turnOn() {

        coffeeMachine.initializeZigbeeConnection();
        coffeeMachine.startBrewing();

    }

    @Override
    public void turnOff() {

        coffeeMachine.stopBrewing();
        coffeeMachine.terminateZigbeeConnection();

    }

}
```

---

## SmartHomeController.java

```java
// Client
public class SmartHomeController {

    public void turnOn(SmartDevice device) {

        device.turnOn();

    }

    public void turnOff(SmartDevice device) {

        device.turnOff();

    }

}
```

---

## Client Code

```java
public class Main {

    public static void main(String[] args) {

        SmartHomeController controller = new SmartHomeController();

        SmartDevice airConditioner =
                new AirConditionerAdapter(new AirConditioner());

        SmartDevice smartLight =
                new SmartLightAdapter(new SmartLight());

        SmartDevice coffeeMachine =
                new CoffeeMachineAdapter(new CoffeeMachine());

        controller.turnOn(airConditioner);
        controller.turnOn(smartLight);
        controller.turnOn(coffeeMachine);

        System.out.println();

        controller.turnOff(airConditioner);
        controller.turnOff(smartLight);
        controller.turnOff(coffeeMachine);

    }

}
```

---

## Output

```text
Connecting Air Conditioner via Bluetooth...
Air Conditioner started cooling.

Connecting Smart Light to Wi-Fi...
Smart Light switched ON.

Connecting Coffee Machine via Zigbee...
Coffee brewing started.

Air Conditioner stopped cooling.
Disconnecting Bluetooth.

Smart Light switched OFF.
Disconnecting Wi-Fi.

Coffee brewing stopped.
Disconnecting Zigbee.
```

---

## Execution Flow

```text
Client

    │

Creates SmartDevice Adapter

    │

    ▼

SmartHomeController

    │

device.turnOn()

    │

    ▼

Adapter

    │

Translates Request

    │

    ▼

Third-Party Device

    │

Actual Device API Executes

    ▼

Operation Completed
```

Notice something important.

The **SmartHomeController** never communicates directly with

- AirConditioner
- SmartLight
- CoffeeMachine

It only knows about

```java
SmartDevice
```

The adapter takes the controller's request and translates it into the API understood by the actual device.

This translation is the core idea behind the Adapter Pattern.

</details>
