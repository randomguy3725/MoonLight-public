package wtf.moonlight.gui.altmanager.repository.hypixel;

import lombok.NonNull;
import org.jetbrains.annotations.Nullable;
import wtf.moonlight.gui.altmanager.utils.Checks;

import java.time.Instant;
import java.util.Date;
import java.util.Objects;

public class HypixelBan {

    @NonNull
    private final String reason;
    @Nullable
    private final Date unbanDate;

    private HypixelBan(@NonNull String reason, @Nullable Date unbanDate) {
        this.reason = Checks.notBlank(reason, "reason");
        this.unbanDate = unbanDate;
    }

    @NonNull
    static HypixelBan of(@NonNull String reason, long unbanDate) {
        Checks.notNegative(unbanDate, "unban date");
        return new HypixelBan(reason, new Date(unbanDate));
    }

    @NonNull
    static HypixelBan of(@NonNull String reason, @NonNull String s) {
        return new HypixelBan(reason, parse(s));
    }

    @NonNull
    private static Date parse(@NonNull String s) {
        Checks.notBlank(s, "date");

        final String[] split = s.split(" ");
        long millis = 0;

        for (int i = 0; i < split.length; i++) {
            final String v = split[i];
            final int length = v.length() - 1;

            final int value;
            final char type = v.charAt(length);

            try {
                value = Integer.parseInt(v.substring(0, length));
            } catch (NumberFormatException e) {
                throw new RuntimeException("An error occurred while parsing number: " + v.substring(0, length), e);
            }

            switch (type) {
                case 'd':
                    millis += value * 24 * 60 * 60 * 1_000;
                    break;

                case 'h':
                    millis += value * 60 * 60 * 1_000;
                    break;

                case 'm':
                    millis += value * 60 * 1_000;
                    break;

                case 's':
                    millis += value * 1_000;
                    break;
            }
        }

        return Date.from(Instant.now().plusMillis(millis));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HypixelBan)) return false;
        final HypixelBan that = (HypixelBan) o;
        return this.reason.equals(that.reason) && Objects.equals(this.unbanDate, that.unbanDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.reason, this.unbanDate);
    }

    @Override
    public String toString() {
        return "HypixelBan{" + "reason='" + this.reason + '\'' + ", unbanDate=" + this.unbanDate + '}';
    }


}
