# Car Rental Service - Pseudo Code

---

# 1. Enums and Entities

```text
enum VehicleType:
    CAR, TRUCK, SMALL_SIZE_TRUCK

enum VehicleStatus:
    AVAILABLE,
    RESERVED_HOLD,      // Locked temporarily during checkout
    RENTED,
    MAINTENANCE

enum ReservationStatus:
    PENDING,
    CONFIRMED,
    CANCELLED
```

---

## Vehicle

```java
abstract class Vehicle {

    private String licenseNumber;
    private VehicleType type;
    private double basePricePerHour;
    private VehicleStatus status;

    Vehicle(
        String licenseNumber,
        VehicleType type,
        double basePricePerHour
    ) {
        this.licenseNumber = licenseNumber;
        this.type = type;
        this.basePricePerHour = basePricePerHour;
        this.status = VehicleStatus.AVAILABLE;
    }

    VehicleStatus getStatus() {
        return status;
    }

    void setStatus(VehicleStatus status) {
        this.status = status;
    }

    VehicleType getType() {
        return type;
    }

    double getBasePricePerHour() {
        return basePricePerHour;
    }
}
```

---

## Car

```java
class Car extends Vehicle {

    Car(
        String licenseNumber,
        double basePricePerHour
    ) {
        super(
            licenseNumber,
            VehicleType.CAR,
            basePricePerHour
        );
    }

}
```

---

## Truck

```java
class Truck extends Vehicle {

    Truck(
        String licenseNumber,
        double basePricePerHour
    ) {
        super(
            licenseNumber,
            VehicleType.TRUCK,
            basePricePerHour
        );
    }

}
```

---

# 2. Core Domain Models

## Location

```java
class Location {

    String street;
    String area;
    String city;
    String state;
    String country;
    String pincode;

}
```

---

## User

```java
class User {

    String userId;
    String name;
    String emailId;

}
```

---

## Store

```java
class Store {

    private String storeId;

    private Location location;

    private List<Vehicle> vehicles;

    List<Vehicle> getVehicles() {
        return vehicles;
    }

    Location getLocation() {
        return location;
    }

}
```

---

## Reservation

```java
class Reservation {

    private String reservationId;

    private User user;

    private Vehicle vehicle;

    private Store store;

    private DateTime startTime;

    private DateTime endTime;

    private double rentFees;

    private ReservationStatus status;

    Reservation(
        User user,
        Vehicle vehicle,
        Store store,
        DateTime startTime,
        DateTime endTime,
        double rentFees
    ) {

        this.reservationId = UUID.generate();

        this.user = user;

        this.vehicle = vehicle;

        this.store = store;

        this.startTime = startTime;

        this.endTime = endTime;

        this.rentFees = rentFees;

        this.status = ReservationStatus.PENDING;
    }

    void setStatus(ReservationStatus status) {
        this.status = status;
    }

    ReservationStatus getStatus() {
        return status;
    }

    Vehicle getVehicle() {
        return vehicle;
    }

    double getRentFees() {
        return rentFees;
    }

}
```

---

# 3. Inventory & Discovery Service

## StoreManager

```java
class StoreManager {

    private List<Store> stores;

    void addStore(Store store) {
        stores.add(store);
    }

    List<Store> findStoresByCity(String city) {

        List<Store> matchedStores = new ArrayList<>();

        for (Store store : stores) {

            if (store.getLocation().getCity().equals(city)) {
                matchedStores.add(store);
            }

        }

        return matchedStores;
    }

    List<Vehicle> getAvailableVehiclesByType(
        Store store,
        VehicleType type
    ) {

        List<Vehicle> availableVehicles = new ArrayList<>();

        for (Vehicle vehicle : store.getVehicles()) {

            if (vehicle.getType() == type &&
                vehicle.getStatus() == VehicleStatus.AVAILABLE) {

                availableVehicles.add(vehicle);

            }

        }

        return availableVehicles;
    }

}
```

---

# 4. Pricing & Reservation Service

## RentCalculateStrategy

```java
interface RentCalculateStrategy {

    double calculate(
        Vehicle vehicle,
        int durationHours
    );

}
```

---

## BasePricingStrategy

```java
class BasePricingStrategy
        implements RentCalculateStrategy {

    @Override
    public double calculate(
        Vehicle vehicle,
        int durationHours
    ) {

        return vehicle.getBasePricePerHour() * durationHours;

    }

}
```

---

## ReservationService

```java
class ReservationService {

    private RentCalculateStrategy pricingStrategy;

    ReservationService(
        RentCalculateStrategy pricingStrategy
    ) {

        this.pricingStrategy = pricingStrategy;

    }

    Reservation createPendingReservation(

        User user,
        Vehicle vehicle,
        Store store,
        DateTime start,
        DateTime end

    ) {

        synchronized (vehicle) {

            if (vehicle.getStatus() != VehicleStatus.AVAILABLE) {

                throw new VehicleUnavailableException(
                    "Vehicle is no longer available."
                );

            }

            vehicle.setStatus(
                VehicleStatus.RESERVED_HOLD
            );

        }

        int durationHours =
            DateTime.differenceInHours(start, end);

        double totalFees =
            pricingStrategy.calculate(
                vehicle,
                durationHours
            );

        return new Reservation(
            user,
            vehicle,
            store,
            start,
            end,
            totalFees
        );

    }

}
```

---

# 5. Strategy Pattern : Payment & Notification

## PaymentStrategy

```java
interface PaymentStrategy {

    boolean executePayment(double amount);

}
```

---

## CreditCardPayment

```java
class CreditCardPayment
        implements PaymentStrategy {

    private String cardNumber;

    private String cvv;

    CreditCardPayment(
        String cardNumber,
        String cvv
    ) {

        this.cardNumber = cardNumber;
        this.cvv = cvv;

    }

    @Override
    public boolean executePayment(double amount) {

        System.out.println(
            "Charging card "
            + cardNumber
            + " for amount : $"
            + amount
        );

        return true;

    }

}
```

---

## NotificationService

```java
interface NotificationService {

    void send(Reservation reservation);

}
```

---

## WhatsAppNotificationService

```java
class WhatsAppNotificationService
        implements NotificationService {

    @Override
    public void send(
        Reservation reservation
    ) {

        System.out.println(
            "WhatsApp notification sent for Reservation ID : "
            + reservation.getReservationId()
        );

    }

}
```

---

# 6. Rental Booking Orchestrator

## RentalBookingOrchestrator

```java
class RentalBookingOrchestrator {

    private StoreManager storeManager;

    private ReservationService reservationService;

    private NotificationService notificationService;

    private List<Reservation> globalReservationLedger;

    RentalBookingOrchestrator(

        StoreManager storeManager,

        ReservationService reservationService,

        NotificationService notificationService

    ) {

        this.storeManager = storeManager;

        this.reservationService = reservationService;

        this.notificationService = notificationService;

        this.globalReservationLedger = new ArrayList<>();

    }

    Reservation checkoutVehicle(

        User user,

        VehicleType type,

        Location location,

        DateTime start,

        DateTime end,

        PaymentStrategy paymentMethod

    ) {

        try {

            // Step 1 : Find stores in requested city
            List<Store> matchedStores =
                storeManager.findStoresByCity(
                    location.getCity()
                );

            List<Vehicle> availableVehicles = null;

            Store selectedStore = null;

            // Step 2 : Find first available vehicle
            for (Store store : matchedStores) {

                availableVehicles =
                    storeManager.getAvailableVehiclesByType(
                        store,
                        type
                    );

                if (!availableVehicles.isEmpty()) {

                    selectedStore = store;
                    break;

                }

            }

            if (availableVehicles == null ||
                availableVehicles.isEmpty()) {

                throw new RuntimeException(
                    "No vehicle available of type : "
                    + type
                );

            }

            Vehicle targetVehicle =
                availableVehicles.get(0);

            // Step 3 : Lock vehicle and create reservation
            Reservation reservation =
                reservationService.createPendingReservation(
                    user,
                    targetVehicle,
                    selectedStore,
                    start,
                    end
                );

            // Step 4 : Execute payment
            boolean paymentSuccessful =
                paymentMethod.executePayment(
                    reservation.getRentFees()
                );

            // Step 5 : Finalize booking
            if (paymentSuccessful) {

                reservation.setStatus(
                    ReservationStatus.CONFIRMED
                );

                targetVehicle.setStatus(
                    VehicleStatus.RENTED
                );

                globalReservationLedger.add(
                    reservation
                );

            } else {

                reservation.setStatus(
                    ReservationStatus.CANCELLED
                );

                targetVehicle.setStatus(
                    VehicleStatus.AVAILABLE
                );

            }

            // Step 6 : Send notification
            notificationService.send(reservation);

            return reservation;

        }

        catch (VehicleUnavailableException ex) {

            System.out.println(
                "Checkout Failed : "
                + ex.getMessage()
            );

            return null;

        }

    }

}
```
