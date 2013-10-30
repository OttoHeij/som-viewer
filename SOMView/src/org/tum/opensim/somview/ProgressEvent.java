/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tum.opensim.somview;

import java.util.EventObject;

public class ProgressEvent extends EventObject
{
    private String statusMessage;
    private boolean show;
    
    /**
     * Constructor
     * @param source object the sent this event
     * @param statusMessage status message the will be delivered by this event
     * @param show true: will show the statusmessage, false: will hide it if shown already
     */
    public ProgressEvent(Object source, String statusMessage, boolean show)
    {
        super(source);
        this.statusMessage = statusMessage;
        this.show = show;
    }

    /**
     * Status message of this progress event
     * @return the status message
     */
    public String getStatusMessage() {
        return statusMessage;
    }

    /**
     * Should this event be shown or hidden
     * false: hide a statusmessage if it is shown already
     * true: show the status message
     * @return indicated whether the statusmessage should be shown or hidden
     */
    public boolean isShow() {
        return show;
    }
}
