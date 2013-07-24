package nl.rgonline.homewizardlibgui;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.*;

import nl.rgonline.homewizardlib.switches.HWSwitch;
import nl.rgonline.homewizardlib.HWSystem;
import nl.rgonline.homewizardlib.exceptions.HWException;

public class HomeWizardGui extends JFrame {

	private HWSystem hwSystem;
	private ArrayList<SwitchPanel> switchpanels;
	private Timer timer;
	
	public HomeWizardGui() throws HWException {
		switchpanels = new ArrayList<>();
		timer = new Timer();
		
		//Init HWSystem
		hwSystem = new HWSystem();

		//Init frame
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		this.setBounds(100, 100, 600, 400);
		this.setTitle("Homewizard GUI, Homewizard version: " + hwSystem.getHwVersion());
		
		JPanel contentPane = new JPanel();
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.PAGE_AXIS));
		
		//Create switchpanels
		for (HWSwitch theSwitch: hwSystem.getSwitchManager().getAll()) {
			SwitchPanel panel = new SwitchPanel(theSwitch);
			switchpanels.add(panel);
			contentPane.add(panel);
		}
		
		this.setContentPane(contentPane);
		
		//Do regular updates
		timer.scheduleAtFixedRate(new UpdateTask(), 1000, 500);
	}
	
	private class UpdateTask extends TimerTask {
		public void run() {
			for (SwitchPanel switchpanel: switchpanels) {
                try {
                    switchpanel.updateSwitchStatus();
                } catch (HWException e) {
                    //FIXME: Report error in UI
                    e.printStackTrace();
                }
            }
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		JFrame frame = null;
		try {
			frame = new HomeWizardGui();
            frame.setVisible(true);
		} catch (HWException e) {
            //FIXME: Report error in UI
			e.printStackTrace();
		}
	}

}
