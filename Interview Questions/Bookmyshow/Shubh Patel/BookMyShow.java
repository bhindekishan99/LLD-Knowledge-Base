import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class BookMyShow {

        // =========================================================
        // ENUMS
        // =========================================================

        enum SeatType {
                REGULAR, RECLINER
        }

        enum PaymentType {
                UPI, CARD
        }

        enum BookingStatus {
                CREATED, CONFIRMED, FAILED, CANCELLED
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

                public String getId() {
                        return id;
                }

                public double getPrice() {
                        return price;
                }

                public abstract SeatType getType();
        }

        static class RegularSeat extends Seat {

                RegularSeat(String id, double price) {
                        super(id, price);
                }

                @Override
                public SeatType getType() {
                        return SeatType.REGULAR;
                }
        }

        static class ReclinerSeat extends Seat {

                ReclinerSeat(String id, double price) {
                        super(id, price);
                }

                @Override
                public SeatType getType() {
                        return SeatType.RECLINER;
                }
        }

        

        static class Screen {
                private final String id;
                private final Map<String, Seat> seats = new HashMap<>();

                Screen(String id) {
                        this.id = id;
                }

                void addSeat(Seat seat) {
                        seats.put(seat.getId(), seat);
                }

                Seat getSeat(String seatId) {
                        return seats.get(seatId);
                }
        }

        static class Theater {
                private final String id;
                private final String name;
                private final Map<String, Screen> screens = new HashMap<>();

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

                Movie(String id, String title, int durationMinutes) {
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
                private final Map<String, Theater> data = new ConcurrentHashMap<>();

                void save(Theater theater) {
                        data.put(theater.id, theater);
                }

                Theater get(String id) {
                        return data.get(id);
                }
        }

        static class MovieRepository {
                private final Map<String, Movie> data = new ConcurrentHashMap<>();

                void save(Movie movie) {
                        data.put(movie.id, movie);
                }

                Movie get(String id) {
                        return data.get(id);
                }
        }

        static class ShowRepository {
                private final Map<String, Show> data = new ConcurrentHashMap<>();

                void save(Show show) {
                        data.put(show.id, show);
                }

                Show get(String id) {
                        return data.get(id);
                }

                List<Show> getShowsByMovie(String movieId) {
                        List<Show> result = new ArrayList<>();

                        for (Show show : data.values()) {
                                if (show.movie.id.equals(movieId)) {
                                        result.add(show);
                                }
                        }

                        return result;
                }
        }

        static class BookingRepository {
                private final Map<String, Booking> data = new ConcurrentHashMap<>();

                void save(Booking booking) {
                        data.put(booking.bookingId, booking);
                }

                Booking get(String id) {
                        return data.get(id);
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

                void addScreen(String theaterId, Screen screen) {
                        Theater theater = repository.get(theaterId);
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
        // PAYMENT - STRATEGY + FACTORY
        // =========================================================

        interface PaymentStrategy {
                boolean pay(Booking booking);
        }

        static class UpiPaymentStrategy implements PaymentStrategy {
                public boolean pay(Booking booking) {
                        System.out.println("UPI payment: " + booking.amount);
                        return true;
                }
        }

        static class CardPaymentStrategy implements PaymentStrategy {
                public boolean pay(Booking booking) {
                        System.out.println("Card payment: " + booking.amount);
                        return true;
                }
        }

        static class PaymentStrategyFactory {

                PaymentStrategy getStrategy(PaymentType type) {
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
        // LOCK PROVIDER
        // =========================================================

        static class LockExpiry {
                final long deadline;
                final String ownerUserId;

                LockExpiry(long deadline, String ownerUserId) {
                        this.deadline = deadline;
                        this.ownerUserId = ownerUserId;
                }
        }

        interface LockProvider {

                // Non-blocking: returns false immediately if another
                // user currently owns the lock.
                boolean tryLock(
                                String key,
                                long ttlMillis,
                                String userId);

                void unlock(String key, String userId);

                boolean isLockExpired(String key);

                boolean isLockedBy(String key, String userId);
        }

        static class InMemoryLockProvider
                        implements LockProvider {

                private final Map<String, LockExpiry> locks = new ConcurrentHashMap<>();

                @Override
                public boolean tryLock(
                                String key,
                                long ttlMillis,
                                String userId) {

                        long now = System.currentTimeMillis();

                        /*
                         * compute() makes "check + acquire" atomic
                         * for this particular seat lock key.
                         */
                        LockExpiry result = locks.compute(
                                        key,
                                        (k, existing) -> {

                                                if (existing == null ||
                                                                existing.deadline <= now) {

                                                        return new LockExpiry(
                                                                        now + ttlMillis,
                                                                        userId);
                                                }

                                                // Someone else owns the lock.
                                                return existing;
                                        });

                        return result.ownerUserId.equals(userId);
                }

                @Override
                public void unlock(String key, String userId) {

                        locks.computeIfPresent(
                                        key,
                                        (k, existing) -> {

                                                // Never unlock another user's lock.
                                                if (existing.ownerUserId.equals(userId)) {
                                                        return null;
                                                }

                                                return existing;
                                        });
                }

                @Override
                public boolean isLockExpired(String key) {

                        LockExpiry lock = locks.get(key);

                        return lock == null ||
                                        lock.deadline <= System.currentTimeMillis();
                }

                @Override
                public boolean isLockedBy(
                                String key,
                                String userId) {

                        LockExpiry lock = locks.get(key);

                        return lock != null &&
                                        lock.deadline > System.currentTimeMillis() &&
                                        lock.ownerUserId.equals(userId);
                }
        }

        /*
         * In a real distributed deployment, this interface can be
         * implemented using Redis. BookingService does not change.
         *
         * class RedisLockProvider implements LockProvider { ... }
         */

        // =========================================================
        // BOOKING SERVICE
        // =========================================================

        static class BookingService {

                private static final long LOCK_TTL = (long) 2 * 60 * 1000; // 5 seconds.

                private final LockProvider lockProvider;
                private final BookingRepository bookingRepository;
                private final PaymentStrategyFactory paymentFactory;

                private final AtomicInteger bookingId = new AtomicInteger(1);

                BookingService(
                                LockProvider lockProvider,
                                BookingRepository bookingRepository,
                                PaymentStrategyFactory paymentFactory) {

                        this.lockProvider = lockProvider;
                        this.bookingRepository = bookingRepository;
                        this.paymentFactory = paymentFactory;
                }

                /*
                 * ALL-OR-NOTHING locking:
                 *
                 * User wants S1 + S2.
                 *
                 * S1 -> success
                 * S2 -> failure
                 *
                 * => release S1
                 * => booking fails
                 */
                Booking createBooking(
                                String userId,
                                Show show,
                                List<String> seatIds,
                                PaymentType paymentType) {

                        List<String> lockedSeats = new ArrayList<>();
                        double totalAmount = 0;

                        try {

                                for (String seatId : seatIds) {

                                        Seat seat = show.getSeat(seatId);

                                        if (seat == null) {
                                                releaseLocks(
                                                                show.id,
                                                                lockedSeats,
                                                                userId);

                                                return null;
                                        }

                                        String lockKey = createLockKey(show.id, seatId);

                                        boolean locked = lockProvider.tryLock(
                                                        lockKey,
                                                        LOCK_TTL,
                                                        userId);

                                        /*
                                         * NON-BLOCKING:
                                         *
                                         * If another user owns the seat,
                                         * tryLock() immediately returns false.
                                         */
                                        if (!locked) {

                                                System.out.println(
                                                                "Seat unavailable: " + seatId);

                                                // Rollback all locks acquired by this request.
                                                releaseLocks(
                                                                show.id,
                                                                lockedSeats,
                                                                userId);

                                                return null;
                                        }

                                        lockedSeats.add(seatId);
                                        totalAmount += seat.getPrice();
                                }

                                // Every requested seat is locked.

                                String id = "B" + bookingId.getAndIncrement();

                                Booking booking = new Booking(
                                                id,
                                                userId,
                                                show.id,
                                                seatIds,
                                                totalAmount,
                                                paymentType);

                                bookingRepository.save(booking);

                                System.out.println(
                                                "Booking created: " + id);

                                return booking;

                        } catch (RuntimeException e) {

                                // Defensive rollback.
                                releaseLocks(
                                                show.id,
                                                lockedSeats,
                                                userId);

                                throw e;
                        }
                }

                boolean confirmBooking(String bookingId) {

                        Booking booking = bookingRepository.get(bookingId);

                        if (booking == null) {
                                return false;
                        }

                        /*
                         * Idempotent retry: If the same request is sent multiple times, the result should be the same as if it was sent only once, so to avoid double payment
                         */
                        if (booking.status == BookingStatus.CONFIRMED) {

                                return true;
                        }

                        if (booking.status != BookingStatus.CREATED) {

                                return false;
                        }

                        /*
                         * Before payment, verify that the user
                         * still owns EVERY requested seat.
                         */
                        for (String seatId : booking.seatIds) {

                                String key = createLockKey(
                                                booking.showId,
                                                seatId);

                                if (!lockProvider.isLockedBy(
                                                key,
                                                booking.userId)) {

                                        booking.status = BookingStatus.FAILED;

                                        releaseLocks(
                                                        booking.showId,
                                                        booking.seatIds,
                                                        booking.userId);

                                        System.out.println(
                                                        "Booking expired / lock lost");

                                        return false;
                                }
                        }

                        // Payment is executed only after
                        // all seat locks are validated.

                        PaymentStrategy strategy = paymentFactory.getStrategy(
                                        booking.paymentType);

                        boolean paymentSuccessful = strategy.pay(booking);

                        if (!paymentSuccessful) {

                                booking.status = BookingStatus.FAILED;

                                // Payment failure -> release seats.
                                releaseLocks(
                                                booking.showId,
                                                booking.seatIds,
                                                booking.userId);

                                return false;
                        }

                        booking.status = BookingStatus.CONFIRMED;

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
                                                createLockKey(showId, seatId),
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

                // Theater
                TheaterRepository theaterRepository = new TheaterRepository();

                TheaterService theaterService = new TheaterService(theaterRepository);

                Theater theater = new Theater("T1", "PVR");

                Screen screen = new Screen("SC1");

                screen.addSeat(new RegularSeat("S1", 200));
                screen.addSeat(new RegularSeat("S2", 200));
                screen.addSeat(new ReclinerSeat("S3", 200));
                

                theaterService.createTheater(theater);
                theaterService.addScreen("T1", screen);

                // Movie
                MovieRepository movieRepository = new MovieRepository();

                MovieService movieService = new MovieService(movieRepository);

                Movie movie = new Movie(
                                "M1",
                                "Interstellar",
                                180);

                movieService.createMovie(movie);

                // Show
                ShowRepository showRepository = new ShowRepository();

                ShowService showService = new ShowService(showRepository);

                Show show = new Show(
                                "SHOW1",
                                movie,
                                theater,
                                screen,
                                "18:00",
                                "21:00");

                showService.createShow(show);

                // Booking infrastructure
                BookingRepository bookingRepository = new BookingRepository();

                LockProvider lockProvider = new InMemoryLockProvider();

                PaymentStrategyFactory paymentFactory = new PaymentStrategyFactory();

                BookingService bookingService = new BookingService(
                                lockProvider,
                                bookingRepository,
                                paymentFactory);

                // =====================================================
                // USER 1: S1 + S2
                // =====================================================

                Booking user1Booking = bookingService.createBooking(
                                "USER1",
                                show,
                                List.of("S1", "S2"),
                                PaymentType.UPI);

                /*
                 * S1 -> locked by USER1
                 * S2 -> locked by USER1
                 */

                // =====================================================
                // USER 2: S2 + S3
                // =====================================================

                Booking user2Booking = bookingService.createBooking(
                                "USER2",
                                show,
                                List.of("S2", "S3"),
                                PaymentType.CARD);

                /*
                 * S2 is already locked by USER1.
                 *
                 * USER2:
                 * tryLock(S2) -> false immediately
                 * rollback any locks acquired by USER2
                 * booking fails
                 *
                 * S3 therefore does NOT remain locked by USER2.
                 */

                // =====================================================
                // USER 1 PAYS
                // =====================================================

                if (user1Booking != null) {
                        bookingService.confirmBooking(
                                        user1Booking.bookingId);
                }

                // USER2 booking is null, so nothing to confirm.
        }
}
