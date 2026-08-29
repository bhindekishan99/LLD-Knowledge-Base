# Car Rental Service — Thread-Safe Single File

```java
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

// ============================================================
// ENUMS
// ============================================================

enum VehicleType {
    CAR,
    TRUCK,
    SMALL_SIZE_TRUCK
}

enum VehicleStatus {
    AVAILABLE,
    RESERVED_HOLD,
    RENTED,
    MAINTENANCE
}

enum ReservationStatus {
    PENDING,
    CONFIRMED,
    CANCELLED
}

// ============================================================
// VEHICLE
// ============================================================

abstract class Vehicle {

    private final String licenseNumber;
    private final VehicleType type;
    private final double basePricePerHour;

    /*
     * Access to status must be protected by the
     * vehicle's monitor: synchronized(vehicle).
     */
    private VehicleStatus status;

    public Vehicle(
            String licenseNumber,
            VehicleType type,
            double basePricePerHour) {

        this.licenseNumber = licenseNumber;
        this.type = type;
        this.basePricePerHour = basePricePerHour;
        this.status = VehicleStatus.AVAILABLE;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public VehicleType getType() {
        return type;
    }

    public double getBasePricePerHour() {
        return basePricePerHour;
    }

    /*
     * These methods are synchronized on the vehicle itself.
     */
    public synchronized VehicleStatus getStatus() {
        return status;
    }

    public synchronized void setStatus(
            VehicleStatus status) {

        this.status = status;
    }
}

// ============================================================
// CONCRETE VEHICLES
// ============================================================

class Car extends Vehicle {

    public Car(
            String licenseNumber,
            double basePricePerHour) {

        super(
                licenseNumber,
                VehicleType.CAR,
                basePricePerHour
        );
    }
}

class Truck extends Vehicle {

    public Truck(
            String licenseNumber,
            double basePricePerHour) {

        super(
                licenseNumber,
                VehicleType.TRUCK,
                basePricePerHour
        );
    }
}

class SmallSizeTruck extends Vehicle {

    public SmallSizeTruck(
            String licenseNumber,
            double basePricePerHour) {

        super(
                licenseNumber,
                VehicleType.SMALL_SIZE_TRUCK,
                basePricePerHour
        );
    }
}

// ============================================================
// LOCATION
// ============================================================

class Location {

    private final String city;

    public Location(String city) {
        this.city = city;
    }

    public String getCity() {
        return city;
    }
}

// ============================================================
// USER
// ============================================================

class User {

    private final String userId;
    private final String name;
    private final String emailId;

    public User(
            String userId,
            String name,
            String emailId) {

        this.userId = userId;
        this.name = name;
        this.emailId = emailId;
    }

    public String getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getEmailId() {
        return emailId;
    }
}

// ============================================================
// STORE
// ============================================================

class Store {

    private final String storeId;
    private final Location location;

    /*
     * Vehicle inventory may be read frequently and
     * occasionally modified.
     *
     * CopyOnWriteArrayList gives safe iteration while
     * vehicles are added/removed.
     */
    private final List<Vehicle> vehicles;

    public Store(
            String storeId,
            Location location) {

        this.storeId = storeId;
        this.location = location;
        this.vehicles =
                new CopyOnWriteArrayList<>();
    }

    public String getStoreId() {
        return storeId;
    }

    public Location getLocation() {
        return location;
    }

    public void addVehicle(Vehicle vehicle) {
        vehicles.add(vehicle);
    }

    public void removeVehicle(Vehicle vehicle) {
        vehicles.remove(vehicle);
    }

    /*
     * Return a snapshot.
     */
    public List<Vehicle> getVehicles() {
        return new ArrayList<>(vehicles);
    }
}

// ============================================================
// RESERVATION
// ============================================================

class Reservation {

    private final String reservationId;

    private final User user;
    private final Vehicle vehicle;
    private final Store store;

    private final Date startTime;
    private final Date endTime;

    private final double rentFees;

    private ReservationStatus status;

    public Reservation(
            User user,
            Vehicle vehicle,
            Store store,
            Date startTime,
            Date endTime,
            double rentFees) {

        this.reservationId =
                UUID.randomUUID().toString();

        this.user = user;
        this.vehicle = vehicle;
        this.store = store;
        this.startTime = startTime;
        this.endTime = endTime;
        this.rentFees = rentFees;

        this.status =
                ReservationStatus.PENDING;
    }

    public String getReservationId() {
        return reservationId;
    }

    public User getUser() {
        return user;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public Store getStore() {
        return store;
    }

    public double getRentFees() {
        return rentFees;
    }

    public synchronized ReservationStatus getStatus() {
        return status;
    }

    public synchronized void setStatus(
            ReservationStatus status) {

        this.status = status;
    }
}

// ============================================================
// STORE MANAGER
// ============================================================

class StoreManager {

    /*
     * Stores are read frequently and can potentially
     * be added/removed at runtime.
     */
    private final List<Store> stores;

    public StoreManager() {
        this.stores =
                new CopyOnWriteArrayList<>();
    }

    public void addStore(Store store) {
        stores.add(store);
    }

    public void removeStore(Store store) {
        stores.remove(store);
    }

    public List<Store> findStoresByCity(
            String city) {

        List<Store> result =
                new ArrayList<>();

        for (Store store : stores) {

            if (store.getLocation()
                    .getCity()
                    .equals(city)) {

                result.add(store);
            }
        }

        return result;
    }

    public List<Vehicle> getVehiclesByType(
            Store store,
            VehicleType type) {

        List<Vehicle> result =
                new ArrayList<>();

        /*
         * getVehicles() already returns a snapshot.
         */
        for (Vehicle vehicle :
                store.getVehicles()) {

            if (vehicle.getType() == type) {

                /*
                 * This is only candidate selection.
                 * The actual availability check happens
                 * again inside synchronized(vehicle).
                 */
                if (vehicle.getStatus()
                        == VehicleStatus.AVAILABLE) {

                    result.add(vehicle);
                }
            }
        }

        return result;
    }
}

// ============================================================
// RESERVATION REPOSITORY
// ============================================================

class ReservationRepository {

    private final Map<String, Reservation>
            reservations;

    public ReservationRepository() {

        reservations =
                new ConcurrentHashMap<>();
    }

    public void add(
            Reservation reservation) {

        reservations.put(
                reservation.getReservationId(),
                reservation
        );
    }

    public Reservation get(
            String reservationId) {

        return reservations.get(
                reservationId
        );
    }

    public void remove(
            String reservationId) {

        reservations.remove(
                reservationId
        );
    }

    public Collection<Reservation> getAll() {

        return reservations.values();
    }
}

// ============================================================
// PRICING STRATEGY
// ============================================================

interface RentCalculateStrategy {

    double calculate(
            Vehicle vehicle,
            int durationHours);
}

// ============================================================
// BASE PRICING STRATEGY
// ============================================================

class BasePricingStrategy
        implements RentCalculateStrategy {

    @Override
    public double calculate(
            Vehicle vehicle,
            int durationHours) {

        return vehicle.getBasePricePerHour()
                * durationHours;
    }
}

// ============================================================
// RESERVATION SERVICE STRATEGY
// ============================================================

interface ReservationServiceStrategy {

    Reservation reserveVehicle(
            User user,
            VehicleType type,
            Location location,
            Date startTime,
            Date endTime);
}

// ============================================================
// DEFAULT RESERVATION SERVICE STRATEGY
// ============================================================

class DefaultReservationServiceStrategy
        implements ReservationServiceStrategy {

    private final StoreManager storeManager;

    private final ReservationRepository
            reservationRepository;

    private final RentCalculateStrategy
            pricingStrategy;

    public DefaultReservationServiceStrategy(
            StoreManager storeManager,
            ReservationRepository reservationRepository,
            RentCalculateStrategy pricingStrategy) {

        this.storeManager = storeManager;

        this.reservationRepository =
                reservationRepository;

        this.pricingStrategy =
                pricingStrategy;
    }

    @Override
    public Reservation reserveVehicle(
            User user,
            VehicleType type,
            Location location,
            Date startTime,
            Date endTime) {

        /*
         * Find candidate stores.
         */
        List<Store> stores =
                storeManager.findStoresByCity(
                        location.getCity()
                );

        /*
         * Try each store.
         */
        for (Store store : stores) {

            /*
             * Candidate vehicles only.
             */
            List<Vehicle> vehicles =
                    storeManager.getVehiclesByType(
                            store,
                            type
                    );

            /*
             * Try each candidate and we do not just check avaialbe vehicle, we also lock it if it's available to make check-then-act atomic
             * like same way we did in parking lot
             */
            for (Vehicle vehicle : vehicles) {

                /*
                 * IMPORTANT:
                 * 1. here we are usign synchronized keyword on vechicle not on this(DefaultReservationServiceStrategy) so same time multiple thread can reseve the diff vehicles
                 * Check + reserve must happen
                 * atomically.
                 *
                 */
                synchronized (vehicle) {

                    /*
                     * Re-check availability.
                     *
                     * Another thread could have
                     * reserved it after candidate
                     * selection.
                     */
                    if (vehicle.getStatus()
                            != VehicleStatus.AVAILABLE) {

                        continue;
                    }

                    /*
                     * ACT:
                     *
                     * AVAILABLE
                     *      ↓
                     * RESERVED_HOLD
                     */
                    vehicle.setStatus(
                            VehicleStatus.RESERVED_HOLD
                    );

                    /*
                     * From this point this vehicle
                     * is reserved for this request.
                     */

                    int durationHours =
                            calculateDurationHours(
                                    startTime,
                                    endTime
                            );

                    double rentFees =
                            pricingStrategy.calculate(
                                    vehicle,
                                    durationHours
                            );

                    Reservation reservation =
                            new Reservation(
                                    user,
                                    vehicle,
                                    store,
                                    startTime,
                                    endTime,
                                    rentFees
                            );

                    /*
                     * Store reservation.
                     */
                    reservationRepository.add(
                            reservation
                    );

                    return reservation;
                }
            }
        }

        throw new RuntimeException(
                "No vehicle available"
        );
    }

    private int calculateDurationHours(
            Date start,
            Date end) {

        long duration =
                end.getTime()
                        - start.getTime();

        return (int) Math.ceil(
                duration / (1000.0 * 60 * 60)
        );
    }
}

// ============================================================
// PAYMENT STRATEGY
// ============================================================

interface PaymentStrategy {

    boolean executePayment(
            double amount);
}

// ============================================================
// CREDIT CARD PAYMENT
// ============================================================

class CreditCardPayment
        implements PaymentStrategy {

    private final String cardNumber;

    public CreditCardPayment(
            String cardNumber) {

        this.cardNumber = cardNumber;
    }

    @Override
    public boolean executePayment(
            double amount) {

        System.out.println(
                "Processing card payment: $"
                        + amount
        );

        return true;
    }
}

// ============================================================
// NOTIFICATION SERVICE
// ============================================================

interface NotificationService {

    void send(Reservation reservation);
}

// ============================================================
// WHATSAPP NOTIFICATION
// ============================================================

class WhatsAppNotificationService
        implements NotificationService {

    @Override
    public void send(
            Reservation reservation) {

        System.out.println(
                "WhatsApp notification sent for "
                        + reservation
                                .getReservationId()
        );
    }
}

// ============================================================
// RENTAL BOOKING ORCHESTRATOR
// ============================================================

class RentalBookingOrchestrator {

    private final ReservationServiceStrategy
            reservationServiceStrategy;

    private final PaymentStrategy paymentStrategy;

    private final NotificationService
            notificationService;

    public RentalBookingOrchestrator(
            ReservationServiceStrategy reservationServiceStrategy,
            PaymentStrategy paymentStrategy,
            NotificationService notificationService) {

        this.reservationServiceStrategy =
                reservationServiceStrategy;

        this.paymentStrategy =
                paymentStrategy;

        this.notificationService =
                notificationService;
    }

    public Reservation checkoutVehicle(
            User user,
            VehicleType type,
            Location location,
            Date startTime,
            Date endTime) {

        /*
         * This method is responsible only for
         * orchestrating the workflow.
         *
         * reserve
         *    ↓
         * payment
         *    ↓
         * finalize
         *    ↓
         * notification
         */

        Reservation reservation =
                reservationServiceStrategy
                        .reserveVehicle(
                                user,
                                type,
                                location,
                                startTime,
                                endTime
                        );

        /*
         * Vehicle is currently:
         *
         * RESERVED_HOLD
         */

        boolean paymentSuccessful =
                paymentStrategy.executePayment(
                        reservation.getRentFees()
                );

        /*
         * Final vehicle state transition.
         *
         * We synchronize on the same vehicle
         * object used during reservation.
         */
        synchronized (reservation.getVehicle()) {

            /*
             * Confirm that this reservation still
             * owns the vehicle.
             */
            if (reservation.getVehicle()
                    .getStatus()
                    != VehicleStatus.RESERVED_HOLD) {

                throw new IllegalStateException(
                        "Vehicle reservation state changed"
                );
            }

            if (paymentSuccessful) {

                reservation.setStatus(
                        ReservationStatus.CONFIRMED
                );

                reservation.getVehicle()
                        .setStatus(
                                VehicleStatus.RENTED
                        );

            } else {

                reservation.setStatus(
                        ReservationStatus.CANCELLED
                );

                /*
                 * Release vehicle.
                 */
                reservation.getVehicle()
                        .setStatus(
                                VehicleStatus.AVAILABLE
                        );
            }
        }

        notificationService.send(
                reservation
        );

        return reservation;
    }

    public void returnVehicle(String reservationId) {

    Reservation reservation =
            reservationRepository.get(reservationId);

    if (reservation == null) {
        throw new IllegalArgumentException(
                "Reservation not found"
        );
    }

    Vehicle vehicle =
            reservation.getVehicle();

    synchronized (vehicle) {

        // Validate current state
        if (vehicle.getStatus()
                != VehicleStatus.RENTED) {

            throw new IllegalStateException(
                    "Vehicle is not currently rented"
            );
        }

        // Complete reservation
        reservation.setStatus(
                ReservationStatus.COMPLETED
        );

        // Make vehicle available again
        vehicle.setStatus(
                VehicleStatus.AVAILABLE
        );

        // Vehicle is returned to its store
        reservation.getStore()
                .addVehicle(vehicle);
    }
 }
}

// ============================================================
// MAIN
// ============================================================

public class Main {

    public static void main(String[] args) {

        // ----------------------------------------------------
        // Store
        // ----------------------------------------------------

        Store store =
                new Store(
                        "S1",
                        new Location("Bangalore")
                );

        // ----------------------------------------------------
        // Vehicles
        // ----------------------------------------------------

        Vehicle car1 =
                new Car(
                        "KA01AB1234",
                        50
                );

        Vehicle car2 =
                new Car(
                        "KA01AB5678",
                        40
                );

        Vehicle truck =
                new Truck(
                        "KA01TR1234",
                        100
                );

        store.addVehicle(car1);
        store.addVehicle(car2);
        store.addVehicle(truck);

        // ----------------------------------------------------
        // Store Manager
        // ----------------------------------------------------

        StoreManager storeManager =
                new StoreManager();

        storeManager.addStore(store);

        // ----------------------------------------------------
        // Repository
        // ----------------------------------------------------

        ReservationRepository
                reservationRepository =
                new ReservationRepository();

        // ----------------------------------------------------
        // Pricing
        // ----------------------------------------------------

        RentCalculateStrategy
                pricingStrategy =
                new BasePricingStrategy();

        // ----------------------------------------------------
        // Reservation Strategy
        // ----------------------------------------------------

        ReservationServiceStrategy
                reservationStrategy =
                new DefaultReservationServiceStrategy(
                        storeManager,
                        reservationRepository,
                        pricingStrategy
                );

        // ----------------------------------------------------
        // Payment
        // ----------------------------------------------------

        PaymentStrategy paymentStrategy =
                new CreditCardPayment(
                        "XXXX-1234"
                );

        // ----------------------------------------------------
        // Notification
        // ----------------------------------------------------

        NotificationService notificationService =
                new WhatsAppNotificationService();

        // ----------------------------------------------------
        // Orchestrator
        // ----------------------------------------------------

        RentalBookingOrchestrator orchestrator =
                new RentalBookingOrchestrator(
                        reservationStrategy,
                        paymentStrategy,
                        notificationService
                );

        // ----------------------------------------------------
        // User
        // ----------------------------------------------------

        User user =
                new User(
                        "U1",
                        "Kishan",
                        "kishan@example.com"
                );

        // ----------------------------------------------------
        // Booking
        // ----------------------------------------------------

        Date start = new Date();

        Date end =
                new Date(
                        start.getTime()
                                + 2
                                * 60
                                * 60
                                * 1000
                );

        Reservation reservation =
                orchestrator.checkoutVehicle(
                        user,
                        VehicleType.CAR,
                        new Location("Bangalore"),
                        start,
                        end
                );

        System.out.println(
                "Reservation ID: "
                        + reservation
                                .getReservationId()
        );

        System.out.println(
                "Status: "
                        + reservation.getStatus()
        );

        System.out.println(
                "Vehicle status: "
                        + reservation
                                .getVehicle()
                                .getStatus()
        );
    }
}
