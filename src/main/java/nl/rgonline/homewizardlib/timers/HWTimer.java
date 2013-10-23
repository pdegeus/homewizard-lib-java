package nl.rgonline.homewizardlib.timers;

import java.util.Set;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import nl.rgonline.homewizardlib.AbstractHwEntity;
import nl.rgonline.homewizardlib.HWAction;
import nl.rgonline.homewizardlib.connection.HWConnection;
import nl.rgonline.homewizardlib.connection.Request;
import nl.rgonline.homewizardlib.exceptions.HWException;

import org.apache.commons.lang.BooleanUtils;

/**
 * Represents a timer in the HomeWizard system.
 * @author pdegeus
 */
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class HWTimer extends AbstractHwEntity {

    @Getter @Setter
    private TimerTrigger trigger;

    @Getter @Setter
    private HWAction action;

    @Getter
    private TimerSubject subject;

    @Getter
    private int subjectId;

    @Getter @Setter
    private boolean active;

    @Getter @Setter
    private String timeOrOffset;

    @Getter @Setter
    private Set<Day> days;

    /**
     * Constructor.
     * @param connection Connection to use.
     * @param id Timer ID.
     * @param trigger Timer trigger.
     * @param action Timer action.
     * @param subject Subject type.
     * @param subjectId Subject ID.
     * @param active Active flag.
     * @param timeOfOffset Activation time of time offset.
     * @param days Active days.
     */
    public HWTimer(
        HWConnection connection, int id, TimerTrigger trigger, HWAction action, TimerSubject subject,
        int subjectId, boolean active, String timeOfOffset, Set<Day> days
    ) {
        super(connection, id, null, false);
        this.trigger = trigger;
        this.action = action;
        this.subject = subject;
        this.subjectId = subjectId;
        this.active = active;
        this.timeOrOffset = timeOfOffset;
        this.days = days;
    }

    /**
     * Indicates whether this timer is repeating, or scheduled for execution once.
     * @return True if the timer is repeating.
     */
    public boolean isRepeatingTimer() {
        return !days.isEmpty();
    }

    @Override
    protected void saveInternal() throws HWException {
        String dayStr = (days == null || days.isEmpty()) ? "7" : Day.toApiFormat(days);
        String enabled = BooleanUtils.toStringYesNo(isActive());

        // /et/<id>/<action on|off>/<trigger type>/<offset|time>/<days>/<enabled yes|no>
        Request request = new Request(
            "/et/", getId(), "/", getAction().getApiString(), "/", getTrigger().getApiString(), "/",
            getTimeOrOffset(), "/", dayStr, "/", enabled
        );
        getConnection().request(request);
    }

}
