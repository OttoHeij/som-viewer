package org.tum.opensim.somexport;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;
import org.opensim.modeling.CoordinateSet;
import org.opensim.modeling.Model;
import org.opensim.modeling.OpenSimContext;
import org.opensim.utils.DialogUtils;
import org.opensim.view.motions.MotionsDB;
import org.opensim.view.pub.OpenSimDB;
import org.opensim.view.pub.ViewDB;

public final class SOMExport extends CallableSystemAction{

    /**
     * Will open the export dialog window
     */
    public void performAction() {

        //Check whether there is a model loaded
        Model currentModel = ViewDB.getCurrentModel();
        if (currentModel == null)
        {
            JOptionPane.showMessageDialog(null, "You have to load a model first.");
            return;
        }
        CoordinateSet cs = currentModel.getCoordinateSet();
        OpenSimContext oc = OpenSimDB.getInstance().getContext(currentModel);

        //Check if there is any motion available
        if(MotionsDB.getInstance().getNumCurrentMotions() > 0)
        {
            //If there is, show the export dialog
            SOMExportPanel export = new SOMExportPanel();
            JFrame f = DialogUtils.createFrameForPanel(export, "Export motiondata to SOMtoolbox format");
            f.setVisible(true);
        }else{
            //If there isn't, show a  message
            JOptionPane.showMessageDialog(null, "You have to load a motion before you can export motion data");
        }
    }

    public String getName() {
        return NbBundle.getMessage(SOMExport.class, "CTL_SOMExport");
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
