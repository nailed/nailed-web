package jk_5.nailed.web.webserver.socketio.packet;

/**
 * No description given
 *
 * @author jk-5
 */
public enum PacketType {
    DISCONNECT(0),
    CONNECT(1),
    HEARTBEAT(2),
    MESSAGE(3),
    JSON(4),
    EVENT(5),
    ACK(6),
    ERROR(7),
    NOOP(8);

    //This cache is needed to avoid cloning
    public static final PacketType[] VALUES = values();
    private final int value;

    PacketType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static PacketType valueOf(int value) {
        return VALUES[value];
    }
}
