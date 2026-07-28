import java.util.*;

// ============================================================
// 1. ENUMS
// ============================================================

enum Severity {
    HIGH,
    MEDIUM,
    LOW
}


// ============================================================
// 2. DOMAIN MODELS
// ============================================================

class Client {

    private final String clientId;
    private final String name;

    public Client(String clientId, String name) {
        this.clientId = clientId;
        this.name = name;
    }

    public String getClientId() {
        return clientId;
    }

    public String getName() {
        return name;
    }
}


class Subscriber {

    private final String subscriberId;
    private final String name;

    public Subscriber(String subscriberId, String name) {
        this.subscriberId = subscriberId;
        this.name = name;
    }

    public String getSubscriberId() {
        return subscriberId;
    }

    public String getName() {
        return name;
    }
}


class Notification {

    private final Client client;
    private final Severity severity;
    private final String message;

    public Notification(
            Client client,
            Severity severity,
            String message) {

        this.client = client;
        this.severity = severity;
        this.message = message;
    }

    public Client getClient() {
        return client;
    }

    public Severity getSeverity() {
        return severity;
    }

    public String getMessage() {
        return message;
    }
}


// ============================================================
// 3. NOTIFICATION CHANNELS
// ============================================================

interface NotificationChannel {

    void send(
            Subscriber subscriber,
            Notification notification);
}


class EmailChannel implements NotificationChannel {

    @Override
    public void send(
            Subscriber subscriber,
            Notification notification) {

        System.out.println(
                "EMAIL -> " +
                subscriber.getName() +
                ": " +
                notification.getMessage());
    }
}


class SmsChannel implements NotificationChannel {

    @Override
    public void send(
            Subscriber subscriber,
            Notification notification) {

        System.out.println(
                "SMS -> " +
                subscriber.getName() +
                ": " +
                notification.getMessage());
    }
}


class PhoneChannel implements NotificationChannel {

    @Override
    public void send(
            Subscriber subscriber,
            Notification notification) {

        System.out.println(
                "PHONE CALL -> " +
                subscriber.getName() +
                ": " +
                notification.getMessage());
    }
}


// ============================================================
// 4. DELIVERY STRATEGY
// ============================================================

interface DeliveryStrategy {

    void execute(
            Subscriber subscriber,
            Notification notification);
}


class SequentialDeliveryStrategy
        implements DeliveryStrategy {

    private final List<NotificationChannel> channels;

    public SequentialDeliveryStrategy(
            List<NotificationChannel> channels) {

        this.channels = channels;
    }

    @Override
    public void execute(
            Subscriber subscriber,
            Notification notification) {

        for (NotificationChannel channel : channels) {

            channel.send(
                    subscriber,
                    notification);
        }
    }
}


// ============================================================
// 5. SUBSCRIPTION PREFERENCE
// ============================================================

class SubscriptionPreference {

    private final Client client;
    private final Subscriber subscriber;

    private final Map<Severity, DeliveryStrategy>
            strategies = new HashMap<>();

    public SubscriptionPreference(
            Client client,
            Subscriber subscriber) {

        this.client = client;
        this.subscriber = subscriber;
    }

    public void setStrategy(
            Severity severity,
            DeliveryStrategy strategy) {

        strategies.put(severity, strategy);
    }

    public DeliveryStrategy getStrategy(
            Severity severity) {

        return strategies.get(severity);
    }

    public Client getClient() {
        return client;
    }

    public Subscriber getSubscriber() {
        return subscriber;
    }
}


// ============================================================
// 6. SUBSCRIPTION REGISTRY
// ============================================================

class SubscriptionRegistry {

    // clientId -> subscriptions
    private final Map<String, List<SubscriptionPreference>>
            subscriptions = new HashMap<>();


    public void subscribe(
            SubscriptionPreference preference) {

        String clientId =
                preference.getClient().getClientId();

        subscriptions
                .computeIfAbsent(
                        clientId,
                        key -> new ArrayList<>())
                .add(preference);
    }


    public List<SubscriptionPreference> getSubscriptions(
            String clientId) {

        return subscriptions.getOrDefault(
                clientId,
                Collections.emptyList());
    }
}


// ============================================================
// 7. NOTIFICATION SERVICE
// ============================================================

class NotificationService {

    private final SubscriptionRegistry registry;

    public NotificationService(
            SubscriptionRegistry registry) {

        this.registry = registry;
    }


    public void publish(Notification notification) {

        String clientId =
                notification
                        .getClient()
                        .getClientId();

        List<SubscriptionPreference> subscriptions =
                registry.getSubscriptions(clientId);


        for (SubscriptionPreference preference
                : subscriptions) {

            DeliveryStrategy strategy =
                    preference.getStrategy(
                            notification.getSeverity());

            if (strategy == null) {
                continue;
            }

            strategy.execute(
                    preference.getSubscriber(),
                    notification);
        }
    }
}


// ============================================================
// 8. DRIVER
// ============================================================

public class Main {

    public static void main(String[] args) {

        // ----------------------------------------------------
        // Clients
        // ----------------------------------------------------

        Client amazon =
                new Client("C1", "Amazon");

        Client aws =
                new Client("C2", "AWS");


        // ----------------------------------------------------
        // Subscribers
        // ----------------------------------------------------

        Subscriber bob =
                new Subscriber("S1", "Bob");

        Subscriber alice =
                new Subscriber("S2", "Alice");


        // ----------------------------------------------------
        // Notification Channels
        // ----------------------------------------------------

        NotificationChannel email =
                new EmailChannel();

        NotificationChannel sms =
                new SmsChannel();

        NotificationChannel phone =
                new PhoneChannel();


        // ----------------------------------------------------
        // Bob's Amazon Preferences
        // ----------------------------------------------------

        SubscriptionPreference bobAmazon =
                new SubscriptionPreference(
                        amazon,
                        bob);


        bobAmazon.setStrategy(
                Severity.HIGH,
                new SequentialDeliveryStrategy(
                        List.of(
                                phone,
                                sms,
                                email)));


        bobAmazon.setStrategy(
                Severity.MEDIUM,
                new SequentialDeliveryStrategy(
                        List.of(
                                sms,
                                email)));


        bobAmazon.setStrategy(
                Severity.LOW,
                new SequentialDeliveryStrategy(
                        List.of(email)));


        // ----------------------------------------------------
        // Alice's Amazon Preferences
        // ----------------------------------------------------

        SubscriptionPreference aliceAmazon =
                new SubscriptionPreference(
                        amazon,
                        alice);


        aliceAmazon.setStrategy(
                Severity.HIGH,
                new SequentialDeliveryStrategy(
                        List.of(
                                sms,
                                email)));


        aliceAmazon.setStrategy(
                Severity.MEDIUM,
                new SequentialDeliveryStrategy(
                        List.of(email)));


        aliceAmazon.setStrategy(
                Severity.LOW,
                new SequentialDeliveryStrategy(
                        List.of(email)));


        // ----------------------------------------------------
        // Bob's AWS Preferences
        // ----------------------------------------------------

        SubscriptionPreference bobAws =
                new SubscriptionPreference(
                        aws,
                        bob);


        bobAws.setStrategy(
                Severity.HIGH,
                new SequentialDeliveryStrategy(
                        List.of(
                                phone,
                                email)));


        bobAws.setStrategy(
                Severity.MEDIUM,
                new SequentialDeliveryStrategy(
                        List.of(email)));


        bobAws.setStrategy(
                Severity.LOW,
                new SequentialDeliveryStrategy(
                        List.of(sms)));


        // ----------------------------------------------------
        // Subscription Registry
        // ----------------------------------------------------

        SubscriptionRegistry registry =
                new SubscriptionRegistry();

        registry.subscribe(bobAmazon);
        registry.subscribe(aliceAmazon);
        registry.subscribe(bobAws);


        // ----------------------------------------------------
        // Notification Service
        // ----------------------------------------------------

        NotificationService notificationService =
                new NotificationService(registry);


        // ----------------------------------------------------
        // Amazon publishes HIGH notification
        // ----------------------------------------------------

        System.out.println(
                "\n===== AMAZON HIGH NOTIFICATION =====");

        Notification amazonNotification =
                new Notification(
                        amazon,
                        Severity.HIGH,
                        "Payment Failed");

        notificationService.publish(
                amazonNotification);


        // ----------------------------------------------------
        // AWS publishes LOW notification
        // ----------------------------------------------------

        System.out.println(
                "\n===== AWS LOW NOTIFICATION =====");

        Notification awsNotification =
                new Notification(
                        aws,
                        Severity.LOW,
                        "Monthly billing report generated");

        notificationService.publish(
                awsNotification);
    }
}
