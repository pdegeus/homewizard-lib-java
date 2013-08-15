package nl.rgonline.homewizardlib.timers;

import java.util.Set;

import lombok.Getter;
import lombok.ToString;
import nl.rgonline.homewizardlib.AbstractHwEntity;
import nl.rgonline.homewizardlib.HWAction;
import nl.rgonline.homewizardlib.HWConnection;

/**
 * Represents a timer in the HomeWizard system.
 * @author pdegeus
 */
@ToString(callSuper = true)
public class HWTimer extends AbstractHwEntity {

    @Getter
    private TimerTrigger trigger;

    @Getter
    private HWAction action;

    @Getter
    private TimerSubject subject;

    @Getter
    private int subjectId;

    @Getter
    private boolean active;

    @Getter
    private String timeOrOffset;

    @Getter
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

}
