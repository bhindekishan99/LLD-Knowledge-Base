package elevator.models;

import elevator.enums.RequestType;

import java.util.Objects;

//Request is immutable, so it is naturally safe to share between threads.
public final class Request {

    private final int floor;
    private final RequestType type;

    public Request(int floor, RequestType type) {
        this.floor = floor;
        this.type = type;
    }

    public int getFloor() {
        return floor;
    }

    public RequestType getType() {
        return type;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Request)) {
            return false;
        }

        Request other = (Request) obj;

        return floor == other.floor
                && type == other.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(floor, type);
    }

    @Override
    public String toString() {
        return "Request{" +
                "floor=" + floor +
                ", type=" + type +
                '}';
    }
}