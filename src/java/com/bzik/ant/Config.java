package com.bzik.ant;

import java.util.logging.Level;

/**
 *
 * @author lafosse
 */
public interface Config {
    /*
     * See ANT+ data sheet for explanation
     */

    public static final int ANT_SPORT_SPEED_PERIOD = 8086;

    /*
     * See ANT+ data sheet for explanation
     */
    public static final int ANT_SPORT_FREQ = 57; // 0x39

    /*
     * This should match the device you are connecting with. Some devices are
     * put into pairing mode (which sets this bit).
     * 
     * Note: Many ANT+ sport devices do not set this bit (eg. HRM strap).
     * 
     * See ANT+ docs.
     */
    public static final boolean HRM_PAIRING_FLAG = false;

    /*
     * Should match device transmission id (0-255). Special rules apply for
     * shared channels. See ANT+ protocol.
     * 
     * 0: wildcard, matches any value (slave only)
     */
    public static final int HRM_TRANSMISSION_TYPE = 0;

    /*
     * device type for ANT+ heart rate monitor
     */
    public static final int ANT_SPORT_SandC_TYPE = 121; // 0x78 0x0bu

    /*
     * You should make a note of the device id and use it in preference to the
     * wild card to pair to a specific device.
     * 
     * 0: wild card, matches all device ids any other number: match specific
     * device id
     */
    public static final int HRM_DEVICE_ID = 0;

    public static final Level LOG_LEVEL = Level.SEVERE;

    /*
     * See ANT+ data sheet for explanation
     */
    public static final int HRM_CHANNEL_PERIOD = 8070;

    /*
     * See ANT+ data sheet for explanation
     */
    public static final int HRM_CHANNEL_FREQ = 57;

    /*
     * device type for ANT+ heart rate monitor 
     */
    public static final int HRM_DEVICE_TYPE = 120;

}
