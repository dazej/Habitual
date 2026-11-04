package dazzejpro.habitual.api.entities;

public enum HabitStatus {
    ACTIVE(0),
    PAUSED(1),
    COMPLETED(2);

    private final int code;

    HabitStatus(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static HabitStatus fromCode(int code) {
        for (HabitStatus status : values()) {
            if (status.getCode() == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown habit status code: " + code);
    }
}