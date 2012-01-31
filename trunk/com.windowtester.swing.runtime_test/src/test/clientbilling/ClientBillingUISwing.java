/*******************************************************************************
 *  Copyright (c) 2012 Google, Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *  
 *  Contributors:
 *  Google, Inc. - initial API and implementation
 *******************************************************************************/
package test.clientbilling;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.AbstractTableModel;

import com.windowtester.internal.swing.WidgetLocatorService;
import com.windowtester.runtime.WidgetLocator;

public class ClientBillingUISwing extends JFrame {
	
	class Transaction {
		int transactionID;
		int amount;
		Date date;
		String description;
		
	}

	class InfoTableTableModel extends AbstractTableModel {
		private ArrayList transactions = new ArrayList();
		
		public final String[] COLUMN_NAMES = new String[] {
			"ID","Amount","Date","Description"
		};
		public int getRowCount() {
			return transactions.size();
		}
		public int getColumnCount() {
			return COLUMN_NAMES.length;
		}
		public String getColumnName(int columnIndex) {
			return COLUMN_NAMES[columnIndex];
		}
		public Object getValueAt(int rowIndex, int columnIndex) {
			Transaction transaction = (Transaction)transactions.get(rowIndex);
			
			switch(columnIndex){
			case 0 : //ID column
				return new Integer(transaction.transactionID);
			case 1: // Amount column
				return new Integer(transaction.amount);
			case 3: // Date column
				return transaction.date;
			case 4: // Description column
				return transaction.description;
			default:
				return null;
			}
		}
		
		public void addRow(Transaction transaction){
			transactions.add(transaction);
			fireTableRowsInserted(transactions.size()-1,transactions.size());
		}
	}

	private JTable infoTable;
	private JTextArea descText;
	private JTextField dateField;
	private JTextField amountField;
	private JTextField idField;
	private JTextField totalField;
	private JButton cancelTransButton;
	private JButton saveTransButton;
	private JButton editTransButton;
	private JButton deleteTransButton;
	private JButton newTransButton;
	private JTextArea miscText;
	private JTextArea addressText;
	private JTextField emailField;
	private JTextField phoneField;
	private JTextField dofBirthField;
	private JTextField lastNameField;
	private JTextField fNameField;
	private JTextField accIdField;
	private JButton printButton;
	private JButton cancelButton;
	private JButton saveClientButton;
	private JButton editClientButton;
	private JButton deleteClientButton;
	private JButton newClientButton;
	private ButtonGroup buttonGroup = new ButtonGroup();
	private JTextField filterText;
	private JList list;
	
	
	// for debugging widget locators
	WidgetLocatorService service = new WidgetLocatorService();
	ActionListener listener = new ActionListener(){
		public void actionPerformed(ActionEvent e){
			WidgetLocator locator = service.inferIdentifyingInfo((Component)e.getSource());
			System.out.println(locator.toString());
		}
	};
	
	/**
	 * Launch the application
	 * @param args
	 */
	public static void main(String args[]) {
		try {
			ClientBillingUISwing frame = new ClientBillingUISwing();
			//AWTEventListener
 /**           frame.getToolkit().addAWTEventListener(
              new AWTEventListener() {
                public void eventDispatched(AWTEvent e) {
                  System.out.println(e+"\n");
                }
              }, AWTEvent.ACTION_EVENT_MASK | AWTEvent.CONTAINER_EVENT_MASK |
              	 AWTEvent.COMPONENT_EVENT_MASK | AWTEvent.MOUSE_EVENT_MASK |
                   AWTEvent.FOCUS_EVENT_MASK |AWTEvent.WINDOW_EVENT_MASK |
                   AWTEvent.KEY_EVENT_MASK
               );
    */    	
			frame.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	
	}

	/**
	 * Create the frame
	 */
	public ClientBillingUISwing() {
		super();
		setTitle("Client Billing Application");
		getContentPane().setLayout(new GridBagLayout());
		getContentPane().setName("Client Billing Application");
		setBounds(100, 100, 500, 375);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		final JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		panel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), "Clients", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
		final GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.weighty = 1.0;
		gridBagConstraints.weightx = 0.25;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridx = 0;
		getContentPane().add(panel, gridBagConstraints);

		final JScrollPane scrollPane = new JScrollPane();
		final GridBagConstraints gridBagConstraints_2 = new GridBagConstraints();
		gridBagConstraints_2.gridwidth = 2;
		gridBagConstraints_2.fill = GridBagConstraints.BOTH;
		gridBagConstraints_2.weighty = 1.0;
		gridBagConstraints_2.weightx = 1.0;
		gridBagConstraints_2.gridy = 0;
		gridBagConstraints_2.gridx = 0;
		panel.add(scrollPane, gridBagConstraints_2);

		list = new JList();
		list.setName("clientList");
		scrollPane.setViewportView(list);

		final JLabel filterLabel = new JLabel();
		filterLabel.setText("Filter:");
		final GridBagConstraints gridBagConstraints_3 = new GridBagConstraints();
		gridBagConstraints_3.insets = new Insets(0, 5, 0, 5);
		gridBagConstraints_3.gridy = 1;
		gridBagConstraints_3.gridx = 0;
		panel.add(filterLabel, gridBagConstraints_3);

		filterText = new JTextField();
		filterText.addActionListener(listener);
		final GridBagConstraints gridBagConstraints_4 = new GridBagConstraints();
		gridBagConstraints_4.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_4.gridy = 1;
		gridBagConstraints_4.gridx = 1;
		panel.add(filterText, gridBagConstraints_4);
		

		final JRadioButton nameRadio = new JRadioButton();
		buttonGroup.add(nameRadio);
		nameRadio.setSelected(true);
		nameRadio.setText("View by name");
		nameRadio.addActionListener(listener);
		
		final GridBagConstraints gridBagConstraints_5 = new GridBagConstraints();
		gridBagConstraints_5.anchor = GridBagConstraints.WEST;
		gridBagConstraints_5.gridwidth = 2;
		gridBagConstraints_5.gridy = 2;
		gridBagConstraints_5.gridx = 0;
		panel.add(nameRadio, gridBagConstraints_5);

		final JRadioButton numRadio = new JRadioButton();
		buttonGroup.add(numRadio);
		numRadio.setText("View by ID");
		final GridBagConstraints gridBagConstraints_6 = new GridBagConstraints();
		gridBagConstraints_6.anchor = GridBagConstraints.WEST;
		gridBagConstraints_6.gridwidth = 2;
		gridBagConstraints_6.gridy = 3;
		gridBagConstraints_6.gridx = 0;
		panel.add(numRadio, gridBagConstraints_6);

		final JTabbedPane tabbedPane = new JTabbedPane();
		final GridBagConstraints gridBagConstraints_1 = new GridBagConstraints();
		gridBagConstraints_1.fill = GridBagConstraints.BOTH;
		gridBagConstraints_1.weighty = 1.0;
		gridBagConstraints_1.weightx = 0.75;
		gridBagConstraints_1.gridy = 0;
		gridBagConstraints_1.gridx = 1;
		getContentPane().add(tabbedPane, gridBagConstraints_1);

		final JPanel panel_1 = new JPanel();
		panel_1.setLayout(new BorderLayout());
		panel_1.setName("Clients");
		tabbedPane.addTab("Clients", null, panel_1, null);

		final JPanel panel_3 = new JPanel();
		panel_3.setLayout(new GridLayout(1, 0));
		panel_1.add(panel_3, BorderLayout.SOUTH);

		newClientButton = new JButton();
		newClientButton.setMargin(new Insets(2, 4, 2, 4));
		newClientButton.setText("New");
		newClientButton.addActionListener(listener);
		panel_3.add(newClientButton);

		deleteClientButton = new JButton();
		deleteClientButton.setMargin(new Insets(2, 4, 2, 4));
		deleteClientButton.setText("Delete");
		panel_3.add(deleteClientButton);

		editClientButton = new JButton();
		editClientButton.setMargin(new Insets(2, 4, 2, 4));
		editClientButton.setText("Edit");
		panel_3.add(editClientButton);

		final JLabel label = new JLabel();
		panel_3.add(label);

		saveClientButton = new JButton();
		saveClientButton.setEnabled(false);
		saveClientButton.setMargin(new Insets(2, 4, 2, 4));
		saveClientButton.setText("Save");
		panel_3.add(saveClientButton);

		cancelButton = new JButton();
		cancelButton.setMargin(new Insets(2, 4, 2, 4));
		cancelButton.setText("Cancel");
		cancelButton.setEnabled(false);
		panel_3.add(cancelButton);

		final JLabel label_1 = new JLabel();
		panel_3.add(label_1);

		printButton = new JButton();
		printButton.setMargin(new Insets(2, 4, 2, 4));
		printButton.setText("Print");
		panel_3.add(printButton);

		final JPanel panel_4 = new JPanel();
		panel_4.setLayout(new GridBagLayout());
		panel_1.add(panel_4, BorderLayout.CENTER);

		final JLabel accountIdLabel = new JLabel();
		accountIdLabel.setText("Account ID:");
		final GridBagConstraints gridBagConstraints_22 = new GridBagConstraints();
		gridBagConstraints_22.anchor = GridBagConstraints.WEST;
		gridBagConstraints_22.insets = new Insets(5, 5, 5, 5);
		panel_4.add(accountIdLabel, gridBagConstraints_22);

		accIdField = new JTextField();
		accIdField.setEditable(false);
		final GridBagConstraints gridBagConstraints_14 = new GridBagConstraints();
		gridBagConstraints_14.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_14.weightx = 1;
		gridBagConstraints_14.gridy = 0;
		gridBagConstraints_14.gridx = 1;
		panel_4.add(accIdField, gridBagConstraints_14);

		final JLabel firstNameLabel = new JLabel();
		firstNameLabel.setText("First Name:");
		final GridBagConstraints gridBagConstraints_7 = new GridBagConstraints();
		gridBagConstraints_7.insets = new Insets(5, 5, 5, 5);
		gridBagConstraints_7.anchor = GridBagConstraints.WEST;
		gridBagConstraints_7.gridy = 1;
		gridBagConstraints_7.gridx = 0;
		panel_4.add(firstNameLabel, gridBagConstraints_7);

		fNameField = new JTextField();
		final GridBagConstraints gridBagConstraints_15 = new GridBagConstraints();
		gridBagConstraints_15.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_15.weightx = 1;
		gridBagConstraints_15.gridy = 1;
		gridBagConstraints_15.gridx = 1;
		panel_4.add(fNameField, gridBagConstraints_15);

		final JLabel lastNameLabel = new JLabel();
		lastNameLabel.setText("Last Name:");
		final GridBagConstraints gridBagConstraints_8 = new GridBagConstraints();
		gridBagConstraints_8.insets = new Insets(5, 5, 5, 5);
		gridBagConstraints_8.anchor = GridBagConstraints.WEST;
		gridBagConstraints_8.gridy = 2;
		gridBagConstraints_8.gridx = 0;
		panel_4.add(lastNameLabel, gridBagConstraints_8);

		lastNameField = new JTextField();
		final GridBagConstraints gridBagConstraints_16 = new GridBagConstraints();
		gridBagConstraints_16.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_16.weightx = 1;
		gridBagConstraints_16.gridy = 2;
		gridBagConstraints_16.gridx = 1;
		panel_4.add(lastNameField, gridBagConstraints_16);

		final JLabel dateOfBirthLabel = new JLabel();
		dateOfBirthLabel.setText("Date of Birth:");
		final GridBagConstraints gridBagConstraints_9 = new GridBagConstraints();
		gridBagConstraints_9.insets = new Insets(5, 5, 5, 5);
		gridBagConstraints_9.anchor = GridBagConstraints.WEST;
		gridBagConstraints_9.gridy = 3;
		gridBagConstraints_9.gridx = 0;
		panel_4.add(dateOfBirthLabel, gridBagConstraints_9);

		dofBirthField = new JTextField();
		dofBirthField.setEditable(false);
		final GridBagConstraints gridBagConstraints_17 = new GridBagConstraints();
		gridBagConstraints_17.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_17.weightx = 1;
		gridBagConstraints_17.gridy = 3;
		gridBagConstraints_17.gridx = 1;
		panel_4.add(dofBirthField, gridBagConstraints_17);

		final JLabel phoneNoLabel = new JLabel();
		phoneNoLabel.setText("Phone no:");
		final GridBagConstraints gridBagConstraints_10 = new GridBagConstraints();
		gridBagConstraints_10.insets = new Insets(5, 5, 5, 5);
		gridBagConstraints_10.anchor = GridBagConstraints.WEST;
		gridBagConstraints_10.gridy = 4;
		gridBagConstraints_10.gridx = 0;
		panel_4.add(phoneNoLabel, gridBagConstraints_10);

		phoneField = new JTextField();
		phoneField.setEditable(false);
		final GridBagConstraints gridBagConstraints_18 = new GridBagConstraints();
		gridBagConstraints_18.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_18.weightx = 1;
		gridBagConstraints_18.gridy = 4;
		gridBagConstraints_18.gridx = 1;
		panel_4.add(phoneField, gridBagConstraints_18);

		final JLabel emailIdLabel = new JLabel();
		emailIdLabel.setText("Email ID:");
		final GridBagConstraints gridBagConstraints_11 = new GridBagConstraints();
		gridBagConstraints_11.insets = new Insets(5, 5, 5, 5);
		gridBagConstraints_11.anchor = GridBagConstraints.WEST;
		gridBagConstraints_11.gridy = 5;
		gridBagConstraints_11.gridx = 0;
		panel_4.add(emailIdLabel, gridBagConstraints_11);

		emailField = new JTextField();
		emailField.setEditable(false);
		final GridBagConstraints gridBagConstraints_19 = new GridBagConstraints();
		gridBagConstraints_19.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_19.weightx = 1;
		gridBagConstraints_19.gridy = 5;
		gridBagConstraints_19.gridx = 1;
		panel_4.add(emailField, gridBagConstraints_19);

		final JLabel addressLabel = new JLabel();
		addressLabel.setText("Address:");
		final GridBagConstraints gridBagConstraints_12 = new GridBagConstraints();
		gridBagConstraints_12.insets = new Insets(5, 5, 5, 5);
		gridBagConstraints_12.anchor = GridBagConstraints.WEST;
		gridBagConstraints_12.gridy = 6;
		gridBagConstraints_12.gridx = 0;
		panel_4.add(addressLabel, gridBagConstraints_12);

		final JScrollPane scrollPane_1 = new JScrollPane();
		final GridBagConstraints gridBagConstraints_20 = new GridBagConstraints();
		gridBagConstraints_20.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_20.gridy = 6;
		gridBagConstraints_20.gridx = 1;
		panel_4.add(scrollPane_1, gridBagConstraints_20);

		addressText = new JTextArea();
		addressText.setEditable(false);
		scrollPane_1.setViewportView(addressText);

		final JLabel miscellaneousInformationLabel = new JLabel();
		miscellaneousInformationLabel.setText("Miscellaneous Information:");
		final GridBagConstraints gridBagConstraints_13 = new GridBagConstraints();
		gridBagConstraints_13.weighty = 1;
		gridBagConstraints_13.insets = new Insets(5, 5, 5, 5);
		gridBagConstraints_13.anchor = GridBagConstraints.NORTH;
		gridBagConstraints_13.gridy = 7;
		gridBagConstraints_13.gridx = 0;
		panel_4.add(miscellaneousInformationLabel, gridBagConstraints_13);

		final JScrollPane scrollPane_2 = new JScrollPane();
		final GridBagConstraints gridBagConstraints_21 = new GridBagConstraints();
		gridBagConstraints_21.anchor = GridBagConstraints.NORTH;
		gridBagConstraints_21.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_21.gridy = 7;
		gridBagConstraints_21.gridx = 1;
		panel_4.add(scrollPane_2, gridBagConstraints_21);

		miscText = new JTextArea();
		miscText.setEditable(false);
		scrollPane_2.setViewportView(miscText);

		final JPanel panel_2 = new JPanel();
		panel_2.setLayout(new BorderLayout());
		panel_2.setName("Transactions");
		tabbedPane.addTab("Transactions", null, panel_2, null);

		final JPanel panel_5 = new JPanel();
		panel_5.setLayout(new GridBagLayout());
		panel_2.add(panel_5, BorderLayout.CENTER);

		final JScrollPane scrollPane_4 = new JScrollPane();
		final GridBagConstraints gridBagConstraints_32 = new GridBagConstraints();
		gridBagConstraints_32.fill = GridBagConstraints.BOTH;
		gridBagConstraints_32.weighty = 1;
		gridBagConstraints_32.weightx = 1;
		gridBagConstraints_32.gridwidth = 4;
		gridBagConstraints_32.gridx = 0;
		gridBagConstraints_32.gridy = 0;
		panel_5.add(scrollPane_4, gridBagConstraints_32);

		infoTable = new JTable();
		infoTable.setModel(new InfoTableTableModel());
		infoTable.getTableHeader().setReorderingAllowed(false);
		scrollPane_4.setViewportView(infoTable);

		final JLabel totalLabel = new JLabel();
		totalLabel.setText("Total:");
		final GridBagConstraints gridBagConstraints_33 = new GridBagConstraints();
		gridBagConstraints_33.insets = new Insets(5, 5, 5, 5);
		gridBagConstraints_33.anchor = GridBagConstraints.WEST;
		gridBagConstraints_33.gridy = 1;
		panel_5.add(totalLabel, gridBagConstraints_33);

		totalField = new JTextField();
		totalField.setEditable(false);
		final GridBagConstraints gridBagConstraints_26 = new GridBagConstraints();
		gridBagConstraints_26.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_26.weightx = 0.25;
		gridBagConstraints_26.gridy = 1;
		gridBagConstraints_26.gridx = 1;
		panel_5.add(totalField, gridBagConstraints_26);

		final JLabel idLabel = new JLabel();
		idLabel.setText("ID:");
		final GridBagConstraints gridBagConstraints_23 = new GridBagConstraints();
		gridBagConstraints_23.insets = new Insets(5, 5, 5, 5);
		gridBagConstraints_23.anchor = GridBagConstraints.WEST;
		gridBagConstraints_23.gridy = 2;
		gridBagConstraints_23.gridx = 0;
		panel_5.add(idLabel, gridBagConstraints_23);

		idField = new JTextField();
		final GridBagConstraints gridBagConstraints_27 = new GridBagConstraints();
		gridBagConstraints_27.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_27.gridy = 2;
		gridBagConstraints_27.gridx = 1;
		panel_5.add(idField, gridBagConstraints_27);
		idField.setEditable(false);

		final JLabel amountLabel = new JLabel();
		amountLabel.setText("Amount:");
		final GridBagConstraints gridBagConstraints_24 = new GridBagConstraints();
		gridBagConstraints_24.insets = new Insets(5, 5, 5, 5);
		gridBagConstraints_24.anchor = GridBagConstraints.WEST;
		gridBagConstraints_24.gridy = 3;
		gridBagConstraints_24.gridx = 0;
		panel_5.add(amountLabel, gridBagConstraints_24);

		amountField = new JTextField();
		final GridBagConstraints gridBagConstraints_28 = new GridBagConstraints();
		gridBagConstraints_28.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_28.gridy = 3;
		gridBagConstraints_28.gridx = 1;
		panel_5.add(amountField, gridBagConstraints_28);
		amountField.setEditable(false);

		final JLabel descriptionLabel = new JLabel();
		descriptionLabel.setText("Description:");
		final GridBagConstraints gridBagConstraints_30 = new GridBagConstraints();
		gridBagConstraints_30.insets = new Insets(5, 5, 5, 5);
		gridBagConstraints_30.anchor = GridBagConstraints.WEST;
		gridBagConstraints_30.gridy = 3;
		gridBagConstraints_30.gridx = 2;
		panel_5.add(descriptionLabel, gridBagConstraints_30);

		final JScrollPane scrollPane_3 = new JScrollPane();
		final GridBagConstraints gridBagConstraints_31 = new GridBagConstraints();
		gridBagConstraints_31.fill = GridBagConstraints.BOTH;
		gridBagConstraints_31.weightx = 0.75;
		gridBagConstraints_31.gridheight = 3;
		gridBagConstraints_31.gridy = 3;
		gridBagConstraints_31.gridx = 3;
		panel_5.add(scrollPane_3, gridBagConstraints_31);

		descText = new JTextArea();
		descText.setWrapStyleWord(true);
		descText.setEditable(false);
		descText.setLineWrap(true);
		scrollPane_3.setViewportView(descText);

		final JLabel dateLabel = new JLabel();
		dateLabel.setText("Date:");
		final GridBagConstraints gridBagConstraints_25 = new GridBagConstraints();
		gridBagConstraints_25.insets = new Insets(5, 5, 5, 5);
		gridBagConstraints_25.anchor = GridBagConstraints.WEST;
		gridBagConstraints_25.gridy = 4;
		gridBagConstraints_25.gridx = 0;
		panel_5.add(dateLabel, gridBagConstraints_25);

		dateField = new JTextField();
		dateField.setEditable(false);
		final GridBagConstraints gridBagConstraints_29 = new GridBagConstraints();
		gridBagConstraints_29.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_29.weightx = 0.25;
		gridBagConstraints_29.gridy = 4;
		gridBagConstraints_29.gridx = 1;
		panel_5.add(dateField, gridBagConstraints_29);

		final JPanel panel_6 = new JPanel();
		panel_6.setLayout(new GridLayout(1, 0));
		panel_2.add(panel_6, BorderLayout.SOUTH);

		newTransButton = new JButton();
		newTransButton.setMargin(new Insets(2, 4, 2, 4));
		newTransButton.setText("New");
		newTransButton.addActionListener(listener);
		panel_6.add(newTransButton);

		deleteTransButton = new JButton();
		deleteTransButton.setMargin(new Insets(2, 4, 2, 4));
		deleteTransButton.setText("Delete");
		panel_6.add(deleteTransButton);

		editTransButton = new JButton();
		editTransButton.setMargin(new Insets(2, 4, 2, 4));
		editTransButton.setText("Edit");
		panel_6.add(editTransButton);

		final JLabel label_2 = new JLabel();
		panel_6.add(label_2);

		saveTransButton = new JButton();
		saveTransButton.setMargin(new Insets(2, 4, 2, 4));
		saveTransButton.setText("Save");
		saveTransButton.setEnabled(false);
		panel_6.add(saveTransButton);

		cancelTransButton = new JButton();
		cancelTransButton.setMargin(new Insets(2, 4, 2, 4));
		cancelTransButton.setText("Cancel");
		cancelTransButton.setEnabled(false);
		panel_6.add(cancelTransButton);

		final JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		final JMenu fileMenu = new JMenu();
		fileMenu.setText("File");
		menuBar.add(fileMenu);

		final JMenuItem quitMenuItem = new JMenuItem();
		quitMenuItem.setText("Quit");
		fileMenu.add(quitMenuItem);
		
		//
	}

}
