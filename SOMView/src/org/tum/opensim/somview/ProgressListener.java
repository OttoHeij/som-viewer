package org.tum.opensim.somview;

import java.util.EventListener;

/**
 * Listener interface for progress events
 */
interface ProgressListener extends EventListener{
    void progressUpdate(ProgressEvent e);
}
