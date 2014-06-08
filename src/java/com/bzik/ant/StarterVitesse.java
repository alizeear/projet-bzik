package com.bzik.ant;

import static com.bzik.ant.Config.ANT_SPORT_FREQ;
import static com.bzik.ant.Config.ANT_SPORT_SPEED_PERIOD;
import static com.bzik.ant.Config.ANT_SPORT_SandC_TYPE;
import static com.bzik.ant.Config.HRM_DEVICE_ID;
import static com.bzik.ant.Config.HRM_PAIRING_FLAG;
import static com.bzik.ant.Config.HRM_TRANSMISSION_TYPE;
import org.cowboycoders.ant.Channel;
import org.cowboycoders.ant.NetworkKey;
import org.cowboycoders.ant.Node;
import org.cowboycoders.ant.messages.ChannelType;
import org.cowboycoders.ant.messages.SlaveChannelType;
import org.cowboycoders.ant.messages.data.BroadcastDataMessage;

/**
 *
 * @author lafosse
 */
public class StarterVitesse extends Thread implements Config {

    private ListenerVitesse listener;
    private Channel channel;
    private Node node;

    public StarterVitesse(Node node) {
        this.node = node;
        System.out.println("Dans le starter au demarrage de la vitesse");
        listener = new ListenerVitesse();
    }

    @Override
    public void start() {
        System.out.println("start()");
        super.start();
        this.run();
    }

    public void run() {
        
        channel = node.getFreeChannel();

        // Arbitrary name : useful for identifying channel
        channel.setName("C:SC");

		// choose slave or master type. Constructors exist to set
        // two-way/one-way and shared/non-shared variants.
        ChannelType channelType = new SlaveChannelType();

        // use ant network key "N:ANT+"
        channel.assign(new NetworkKey(0xB9, 0xA5, 0x21, 0xFB, 0xBD, 0x72, 0xC3, 0x45), channelType);

        // registers an instance of our callback with the channel
        channel.registerRxListener(listener, BroadcastDataMessage.class);

        /**
         * ***** start device specific configuration *****
         */
        channel.setId(HRM_DEVICE_ID, ANT_SPORT_SandC_TYPE,
                HRM_TRANSMISSION_TYPE, HRM_PAIRING_FLAG);

        channel.setFrequency(ANT_SPORT_FREQ);

        channel.setPeriod(ANT_SPORT_SPEED_PERIOD);

        /**
         * ***** end device specific configuration *****
         */
        // timeout before we give up looking for device
        channel.setSearchTimeout(Channel.SEARCH_TIMEOUT_NEVER);

        // start listening
        channel.open();

                // temps de blocage
        // Listen for 120 seconds (120000)
//        try {
//            Thread.sleep(10000);
//            
//        } catch (Exception e2) {
//            System.out.println("Erreur de fermeture des canaux materiel " + e2);
//        }

                // code sur le bouton deconnecter
        
//        if (channel.isFree()) {
//            speed = 12.0;
//        }

		// optional : demo requesting of channel configuration. If device
        // connected
        // this will reflect actual device id, transmission type etc. This info
        // will allow
        // you to only connect to this device in the future.
//        printChannelConfig(channel);
        
    }

    @Override
    public void interrupt() {
        super.interrupt();
        
        // stop listening
        channel.close();
        
        // resets channel configuration
        channel.unassign();

        // return the channel to the pool of available channels
        node.freeChannel(channel);

        // cleans up : gives up control of usb device etc.
        node.stop();
        node = null;
    }
    
    
    
    public ListenerVitesse getListener() {
        return listener;
    }

    public void setListener(ListenerVitesse listener) {
        this.listener = listener;
    }

    public double getSpeed() {
        return listener.getSpeed();
    }
    
    public double getDistance() {
        return listener.getTotalDistance();
    }

}
