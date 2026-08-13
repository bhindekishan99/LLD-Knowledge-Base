
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class BookMyShow {

    // =========================================================
    // ENUMS
    // =========================================================

    enum SeatType {
        REGULAR,
        RECLINER
    }

    enum PaymentType {
        UPI,
        CARD
    }

    enum BookingStatus {
        CREATED,
        CONFIRMED,
        FAILED,
        CANCELLED
    }

    // =========================================================
    // MODELS
    // =========================================================

    abstract static class Seat {

        private final String id;
        private final double price;

        Seat(String id, double price) {
            this.id = id;
            this.price = price;
        }

        String getId() {
            return id;
        }

        double getPrice() {
            return price;
        }

        abstract SeatType getType();
    }

    static class RegularSeat extends Seat {

        RegularSeat(String id, double price) {
            super(id, price);
        }

        @Override
        SeatType getType() {
            return SeatType.REGULAR;
        }
    }

    static class ReclinerSeat extends Seat {

        ReclinerSeat(String id, double price) {
            super(id, price);
        }

        @Override
        SeatType getType() {
            return SeatType.RECLINER;
        }
    }

    static class Screen {

        private final String id;
        private final Map<String, Seat> seats =
                new HashMap<>();

        Screen(String id) {
            this.id = id;
        }

        void addSeat(Seat seat) {
            seats.put(seat.getId(), seat);
        }

        Seat getSeat(String seatId) {
            return seats.get(seatId);
        }

        Collection<Seat> getSeats() {
            return seats.values();
        }
    }

    static class Theater {

        private final String id;
        private final String name;

        private final Map<String, Screen> screens =
                new HashMap<>();

        Theater(String id, String name) {
            this.id = id;
            this.name = name;
        }

        void addScreen(Screen screen) {
            screens.put(screen.id, screen);
        }

        Screen getScreen(String screenId) {
            return screens.get(screenId);
        }
    }

    static class Movie {

        private final String id;
        private final String title;
        private final int durationMinutes;

        Movie(
                String id,
                String title,
                int durationMinutes) {

            this.id = id;
            this.title = title;
            this.durationMinutes = durationMinutes;
        }
    }

    static class Show {

        private final String id;
        private final Movie movie;
        private final Theater theater;
        private final Screen screen;
        private final String startTime;
        private final String endTime;

        Show(
                String id,
                Movie movie,
                Theater theater,
                Screen screen,
                String startTime,
                String endTime) {

            this.id = id;
            this.movie = movie;
            this.theater = theater;
            this.screen = screen;
            this.startTime = startTime;
            this.endTime = endTime;
        }

        Seat getSeat(String seatId) {
            return screen.getSeat(seatId);
        }

        Collection<Seat> getSeats() {
            return screen.getSeats();
        }
    }

    static class Booking {

        private final String bookingId;
        private final String userId;
        private final String showId;
        private final List<String> seatIds;
        private final double amount;
        private final PaymentType paymentType;

        private BookingStatus status;

        Booking(
                String bookingId,
                String userId,
                String showId,
                List<String> seatIds,
                double amount,
                PaymentType paymentType) {

            this.bookingId = bookingId;
            this.userId = userId;
            this.showId = showId;
            this.seatIds = new ArrayList<>(seatIds);
            this.amount = amount;
            this.paymentType = paymentType;
            this.status = BookingStatus.CREATED;
        }
    }

    // =========================================================
    // REPOSITORIES
    // =========================================================

    static class TheaterRepository {

        private final Map<String, Theater> theaters =
                new ConcurrentHashMap<>();

        void save(Theater theater) {
            theaters.put(theater.id, theater);
        }

        Theater get(String theaterId) {
            return theaters.get(theaterId);
        }
    }

    static class MovieRepository {

        private final Map<String, Movie> movies =
                new ConcurrentHashMap<>();

        void save(Movie movie) {
            movies.put(movie.id, movie);
        }

        Movie get(String movieId) {
            return movies.get(movieId);
        }
    }

    static class ShowRepository {

        private final Map<String, Show> shows =
                new ConcurrentHashMap<>();

        void save(Show show) {
            shows.put(show.id, show);
        }

        Show get(String showId) {
            return shows.get(showId);
        }

        List<Show> getShowsByMovie(String movieId) {

            List<Show> result = new ArrayList<>();

            for (Show show : shows.values()) {

                if (show.movie.id.equals(movieId)) {
                    result.add(show);
                }
            }

            return result;
        }
    }

    static class BookingRepository {

        // BookingId -> Booking
        private final Map<String, Booking> bookings =
                new ConcurrentHashMap<>();

        /*
         * Confirmed seats:
         *
         * SHOW1 -> {S1, S2, S5}
         * SHOW2 -> {S1, S3}
         *
         * A concurrent Set is used because multiple booking
         * requests may confirm seats concurrently.
         */
        private final Map<String, Set<String>> confirmedSeats =
                new ConcurrentHashMap<>();

        void save(Booking booking) {
            bookings.put(booking.bookingId, booking);
        }

        Booking get(String bookingId) {
            return bookings.get(bookingId);
        }

        Set<String> getConfirmedSeatIds(String showId) {

            return confirmedSeats.getOrDefault(
                    showId,
                    Collections.emptySet());
        }

        void markSeatsConfirmed(
                String showId,
                List<String> seatIds) {

            Set<String> seats =
                    confirmedSeats.computeIfAbsent(
                            showId,
                            id -> ConcurrentHashMap.newKeySet());

            seats.addAll(seatIds);
        }
    }

    // =========================================================
    // SERVICES
    // =========================================================

    static class TheaterService {

        private final TheaterRepository repository;

        TheaterService(TheaterRepository repository) {
            this.repository = repository;
        }

        void createTheater(Theater theater) {
            repository.save(theater);
        }

        void addScreen(
                String theaterId,
                Screen screen) {

            Theater theater =
                    repository.get(theaterId);

            theater.addScreen(screen);
        }
    }

    static class MovieService {

        private final MovieRepository repository;

        MovieService(MovieRepository repository) {
            this.repository = repository;
        }

        void createMovie(Movie movie) {
            repository.save(movie);
        }

        Movie getMovie(String movieId) {
            return repository.get(movieId);
        }
    }

    static class ShowService {

        private final ShowRepository repository;

        ShowService(ShowRepository repository) {
            this.repository = repository;
        }

        void createShow(Show show) {
            repository.save(show);
        }

        Show getShow(String showId) {
            return repository.get(showId);
        }

        List<Show> getShowsByMovie(String movieId) {
            return repository.getShowsByMovie(movieId);
        }
    }

    // =========================================================
    // LOCK PROVIDER
    // =========================================================

    static class LockExpiry {

        final long deadline;
        final String ownerUserId;

        LockExpiry(
                long deadline,
                String ownerUserId) {

            this.deadline = deadline;
            this.ownerUserId = ownerUserId;
        }
    }

    interface LockProvider {

        /*
         * NON-BLOCKING.
         *
         * If another user owns the lock,
         * return false immediately.
         */
        boolean tryLock(
                String key,
                long ttlMillis,
                String userId);

        void unlock(
                String key,
                String userId);

        boolean isLockedBy(
                String key,
                String userId);

        /*
         * Used by SeatAvailabilityService to find
         * temporarily locked seats for a show.
         */
        Set<String> getLockedSeatIds(
                String showId);
    }

    static class InMemoryLockProvider
            implements LockProvider {

        private final Map<String, LockExpiry> locks =
                new ConcurrentHashMap<>();

        @Override
        public boolean tryLock(
                String key,
                long ttlMillis,
                String userId) {

            long now =
                    System.currentTimeMillis();

            /*
             * Atomic check + acquire for this key.
             *
             * If there is no lock, or the old lock expired,
             * this user gets the lock.
             *
             * If another user owns it, the existing lock
             * is returned and this method returns false.
             */
            LockExpiry result =
                    locks.compute(
                            key,
                            (k, existingLock) -> {

                                if (existingLock == null ||
                                        existingLock.deadline <= now) {

                                    return new LockExpiry(
                                            now + ttlMillis,
                                            userId);
                                }

                                return existingLock;
                            });

            return result.ownerUserId.equals(userId);
        }

        @Override
        public void unlock(
                String key,
                String userId) {

            /*
             * Never allow User2 to unlock User1's seat.
             */
            locks.computeIfPresent(
                    key,
                    (k, existingLock) -> {

                        if (existingLock.ownerUserId
                                .equals(userId)) {

                            return null;
                        }

                        return existingLock;
                    });
        }

        @Override
        public boolean isLockedBy(
                String key,
                String userId) {

            LockExpiry lock =
                    locks.get(key);

            return lock != null &&
                    lock.deadline >
                            System.currentTimeMillis() &&
                    lock.ownerUserId.equals(userId);
        }

        @Override
        public Set<String> getLockedSeatIds(
                String showId) {

            Set<String> result =
                    new HashSet<>();

            String prefix =
                    showId + ":";

            long now =
                    System.currentTimeMillis();

            for (Map.Entry<String, LockExpiry> entry :
                    locks.entrySet()) {

                LockExpiry lock =
                        entry.getValue();

                if (lock.deadline <= now) {
                    continue;
                }

                if (entry.getKey().startsWith(prefix)) {

                    String seatId =
                            entry.getKey()
                                    .substring(prefix.length());

                    result.add(seatId);
                }
            }

            return result;
        }
    }

    // =========================================================
    // SEAT AVAILABILITY SERVICE
    // =========================================================
    // This is to display avaiable seat to user, let say User1 has locked S1 and he is on payment page
    // so to other user we shoud not display this seat to be booked
    static class SeatAvailabilityService {

        private final BookingRepository bookingRepository;
        private final LockProvider lockProvider;

        SeatAvailabilityService(
                BookingRepository bookingRepository,
                LockProvider lockProvider) {

            this.bookingRepository =
                    bookingRepository;

            this.lockProvider =
                    lockProvider;
        }

        /*
         * Available seats =
         *
         *     All seats
         *       -
         *     Confirmed seats
         *       -
         *     Temporarily locked seats
         */
        List<Seat> getAvailableSeats(Show show) {

            Set<String> unavailableSeats =
                    new HashSet<>(
                            bookingRepository
                                    .getConfirmedSeatIds(show.id));

            unavailableSeats.addAll(
                    lockProvider
                            .getLockedSeatIds(show.id));

            List<Seat> result =
                    new ArrayList<>();

            for (Seat seat : show.getSeats()) {

                if (!unavailableSeats
                        .contains(seat.getId())) {

                    result.add(seat);
                }
            }

            return result;
        }
    }

    // =========================================================
    // PAYMENT - STRATEGY + FACTORY
    // =========================================================

    interface PaymentStrategy {

        boolean pay(Booking booking);
    }

    static class UpiPaymentStrategy
            implements PaymentStrategy {

        @Override
        public boolean pay(Booking booking) {

            System.out.println(
                    "UPI payment: "
                            + booking.amount);

            return true;
        }
    }

    static class CardPaymentStrategy
            implements PaymentStrategy {

        @Override
        public boolean pay(Booking booking) {

            System.out.println(
                    "Card payment: "
                            + booking.amount);

            return true;
        }
    }

    static class PaymentStrategyFactory {

        PaymentStrategy getStrategy(
                PaymentType type) {

            switch (type) {

                case UPI:
                    return new UpiPaymentStrategy();

                case CARD:
                    return new CardPaymentStrategy();

                default:
                    throw new IllegalArgumentException(
                            "Unsupported payment type");
            }
        }
    }

    // =========================================================
    // BOOKING SERVICE
    // =========================================================

    static class BookingService {

        private static final long LOCK_TTL =
                2 * 60 * 1000;

        private final LockProvider lockProvider;
        private final BookingRepository bookingRepository;
        private final PaymentStrategyFactory paymentFactory;

        private final AtomicInteger bookingCounter =
                new AtomicInteger(1);

        BookingService(
                LockProvider lockProvider,
                BookingRepository bookingRepository,
                PaymentStrategyFactory paymentFactory) {

            this.lockProvider =
                    lockProvider;

            this.bookingRepository =
                    bookingRepository;

            this.paymentFactory =
                    paymentFactory;
        }

        // -----------------------------------------------------
        // CREATE BOOKING
        // -----------------------------------------------------

        Booking createBooking(
                String userId,
                Show show,
                List<String> seatIds,
                PaymentType paymentType) {

            if (seatIds == null ||
                    seatIds.isEmpty()) {

                return null;
            }

            /*
             * Same seat should not appear twice
             * in one booking request.
             */
            if (new HashSet<>(seatIds).size()
                    != seatIds.size()) {

                return null;
            }

            /*
             * First check permanent booking state.
             *
             * This is an optimization / early rejection.
             *
             * tryLock() is still necessary because another
             * user may be concurrently trying to book the seat.
             */
            Set<String> confirmedSeats =
                    bookingRepository
                            .getConfirmedSeatIds(show.id);

            for (String seatId : seatIds) {

                if (confirmedSeats
                        .contains(seatId)) {

                    System.out.println(
                            "Seat already booked: "
                                    + seatId);

                    return null;
                }
            }

            List<String> lockedSeats =
                    new ArrayList<>();

            double totalAmount = 0;

            try {

                /*
                 * ALL-OR-NOTHING:
                 *
                 * S1 -> lock succeeds
                 * S2 -> lock fails
                 *
                 * Then release S1 and fail.
                 */
                for (String seatId : seatIds) {

                    Seat seat =
                            show.getSeat(seatId);

                    if (seat == null) {

                        releaseLocks(
                                show.id,
                                lockedSeats,
                                userId);

                        return null;
                    }

                    String lockKey =
                            createLockKey(
                                    show.id,
                                    seatId);

                    /*
                     * NON-BLOCKING.
                     *
                     * If another user owns this seat,
                     * return false immediately.
                     */
                    boolean locked =
                            lockProvider.tryLock(
                                    lockKey,
                                    LOCK_TTL,
                                    userId);

                    if (!locked) {

                        System.out.println(
                                "Seat unavailable: "
                                        + seatId);

                        /*
                         * Rollback all seats acquired
                         * by this booking request.
                         */
                        releaseLocks(
                                show.id,
                                lockedSeats,
                                userId);

                        return null;
                    }

                    lockedSeats.add(seatId);

                    totalAmount +=
                            seat.getPrice();
                }

                // All requested seats are now locked.

                String bookingId =
                        "B"
                                + bookingCounter
                                .getAndIncrement();

                Booking booking =
                        new Booking(
                                bookingId,
                                userId,
                                show.id,
                                seatIds,
                                totalAmount,
                                paymentType);

                bookingRepository.save(booking);

                System.out.println(
                        "Booking created: "
                                + bookingId);

                return booking;

            } catch (RuntimeException e) {

                /*
                 * Defensive rollback if something
                 * unexpected happens.
                 */
                releaseLocks(
                        show.id,
                        lockedSeats,
                        userId);

                throw e;
            }
        }

        // -----------------------------------------------------
        // CONFIRM BOOKING
        // -----------------------------------------------------

        boolean confirmBooking(
                String bookingId) {

            Booking booking =
                    bookingRepository
                            .get(bookingId);

            if (booking == null) {
                return false;
            }

            /*
             * Idempotent retry:
             *
             * If the same confirmation request reaches
             * us again after success, do not charge again.
             */
            if (booking.status ==
                    BookingStatus.CONFIRMED) {

                return true;
            }

            if (booking.status !=
                    BookingStatus.CREATED) {

                return false;
            }

            /*
             * Before payment, verify that the user
             * still owns EVERY temporary lock.
             */
            for (String seatId :
                    booking.seatIds) {

                String lockKey =
                        createLockKey(
                                booking.showId,
                                seatId);

                if (!lockProvider.isLockedBy(
                        lockKey,
                        booking.userId)) {

                    booking.status =
                            BookingStatus.FAILED;

                    releaseLocks(
                            booking.showId,
                            booking.seatIds,
                            booking.userId);

                    System.out.println(
                            "Booking expired / lock lost");

                    return false;
                }
            }

            /*
             * Payment Strategy is selected through
             * PaymentStrategyFactory.
             */
            PaymentStrategy strategy =
                    paymentFactory.getStrategy(
                            booking.paymentType);

            boolean paymentSuccessful =
                    strategy.pay(booking);

            if (!paymentSuccessful) {

                booking.status =
                        BookingStatus.FAILED;

                /*
                 * Payment failed -> release temporary locks.
                 */
                releaseLocks(
                        booking.showId,
                        booking.seatIds,
                        booking.userId);

                return false;
            }

            /*
             * Convert temporary seat locks into
             * confirmed seat ownership BEFORE
             * releasing the temporary locks.
             */
            bookingRepository.markSeatsConfirmed(
                    booking.showId,
                    booking.seatIds);

            booking.status =
                    BookingStatus.CONFIRMED;

            /*
             * Temporary locks are no longer needed.
             */
            releaseLocks(
                    booking.showId,
                    booking.seatIds,
                    booking.userId);

            System.out.println(
                    "Booking confirmed: "
                            + booking.bookingId);

            return true;
        }

        private void releaseLocks(
                String showId,
                List<String> seatIds,
                String userId) {

            for (String seatId : seatIds) {

                lockProvider.unlock(
                        createLockKey(
                                showId,
                                seatId),
                        userId);
            }
        }

        private String createLockKey(
                String showId,
                String seatId) {

            return showId + ":" + seatId;
        }
    }

    // =========================================================
    // CLIENT
    // =========================================================

    public static void main(String[] args) {

        // -----------------------------------------------------
        // Theater
        // -----------------------------------------------------

        TheaterRepository theaterRepository =
                new TheaterRepository();

        TheaterService theaterService =
                new TheaterService(
                        theaterRepository);

        Theater theater =
                new Theater(
                        "T1",
                        "PVR");

        Screen screen =
                new Screen("SC1");

        screen.addSeat(
                new RegularSeat(
                        "S1",
                        200));

        screen.addSeat(
                new RegularSeat(
                        "S2",
                        200));

        screen.addSeat(
                new ReclinerSeat(
                        "S3",
                        300));

        theaterService.createTheater(
                theater);

        theaterService.addScreen(
                "T1",
                screen);

        // -----------------------------------------------------
        // Movie
        // -----------------------------------------------------

        MovieRepository movieRepository =
                new MovieRepository();

        MovieService movieService =
                new MovieService(
                        movieRepository);

        Movie movie =
                new Movie(
                        "M1",
                        "Interstellar",
                        180);

        movieService.createMovie(movie);

        // -----------------------------------------------------
        // Show
        // -----------------------------------------------------

        ShowRepository showRepository =
                new ShowRepository();

        ShowService showService =
                new ShowService(
                        showRepository);

        Show show =
                new Show(
                        "SHOW1",
                        movie,
                        theater,
                        screen,
                        "18:00",
                        "21:00");

        showService.createShow(show);

        // -----------------------------------------------------
        // Booking infrastructure
        // -----------------------------------------------------

        BookingRepository bookingRepository =
                new BookingRepository();

        LockProvider lockProvider =
                new InMemoryLockProvider();

        SeatAvailabilityService
                availabilityService =
                new SeatAvailabilityService(
                        bookingRepository,
                        lockProvider);

        PaymentStrategyFactory
                paymentFactory =
                new PaymentStrategyFactory();

        BookingService bookingService =
                new BookingService(
                        lockProvider,
                        bookingRepository,
                        paymentFactory);

        // -----------------------------------------------------
        // Initially available seats
        // -----------------------------------------------------

        System.out.println(
                "Available seats:");

        for (Seat seat :
                availabilityService
                        .getAvailableSeats(show)) {

            System.out.println(
                    seat.getId()
                            + " - "
                            + seat.getType()
                            + " - "
                            + seat.getPrice());
        }

        // =====================================================
        // USER 1: S1 + S2
        // =====================================================

        Booking user1Booking =
                bookingService.createBooking(
                        "USER1",
                        show,
                        List.of("S1", "S2"),
                        PaymentType.UPI);

        /*
         * S1 -> temporarily locked by USER1
         * S2 -> temporarily locked by USER1
         */

        // =====================================================
        // USER 2: S2 + S3
        // =====================================================

        Booking user2Booking =
                bookingService.createBooking(
                        "USER2",
                        show,
                        List.of("S2", "S3"),
                        PaymentType.CARD);

        /*
         * S2 is already locked by USER1.
         *
         * USER2:
         *
         * tryLock(S2)
         *       ↓
         * false immediately
         *       ↓
         * release any seats USER2 acquired
         *       ↓
         * booking fails
         *
         * S3 is therefore not left locked by USER2.
         */

        // =====================================================
        // USER 1 PAYS
        // =====================================================

        if (user1Booking != null) {

            bookingService.confirmBooking(
                    user1Booking.bookingId);
        }

        // =====================================================
        // AFTER CONFIRMATION
        // =====================================================

        System.out.println(
                "\nAvailable seats after USER1 confirmation:");

        for (Seat seat :
                availabilityService
                        .getAvailableSeats(show)) {

            System.out.println(
                    seat.getId());
        }
    }
}
