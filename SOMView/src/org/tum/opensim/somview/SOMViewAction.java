/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tum.opensim.somview;

import javax.swing.JFrame;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;
import org.opensim.utils.DialogUtils;

/**
* Action element that can be a part of a menu in opensim
* In this case, it will be part of the "windows" menu
**/
public final class SOMViewAction extends CallableSystemAction {

    /**
    * This function is called of the user clicks the menu element
    **/
    public void performAction() {
        //Create a new SOMView and show it
        SOMView panel = new SOMView();
        //Use the DialogUtils provided by OpenSim to create a frame from a panel
        JFrame frame = DialogUtils.createFrameForPanel(panel, "View SOM uMatrix");
        frame.setVisible(true);
    }

    //######
    // Some functions that need to be implemented to make it work in the Netbeans platform
    // See Netbeans Platform documentation for details
    public String getName() {
        return NbBundle.getMessage(SOMViewAction.class, "CTL_SOMViewAction");
    }

    @Override
    protected void initialize() {
        super.initialize();
        // see org.openide.util.actions.SystemAction.iconResource() Javadoc for more details
        putValue("noIconInMenu", Boolean.TRUE);
    }

    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    @Override
    protected boolean asynchronous() {
        return false;
    }
}
