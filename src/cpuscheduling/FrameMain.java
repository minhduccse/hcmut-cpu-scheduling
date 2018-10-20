package cpuscheduling;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import java.util.Scanner;
import javax.swing.*;

public class FrameMain 
	extends JFrame
	implements ActionListener, ItemListener	{
	
	private static final long serialVersionUID = 1L;
	
	List<Job> _jobs;
	Algorithm _algorithm;
	
	JScrollPane _scrollpane;
	Font _font_job;
		
	JLabel _lbl_import;
	JTextField _txt_import;
	
	JLabel _lbl_TT;
	JLabel _lbl_WT;
	
		
	CheckboxGroup _cbg_algo;
	Checkbox _cb_FCFS;
	Checkbox _cb_SJF;
	Checkbox _cb_PRIO;
	Checkbox _cb_PPRIO;
	Checkbox _cb_SRTF;
	Checkbox _cb_RR;
	
	JButton _btn_save;
	JButton _btn_run;
	
	TextArea _txt_result_tt;
	TextArea _txt_result_wt;
	
	public FrameMain()	{
		_jobs = new ArrayList<Job>();
		_algorithm = Algorithm.FCFS;
		initUI();
	}
	
	void initUI()	{
		
		_font_job = new Font("Times New Roman", Font.PLAIN, 16);
								
		
		_cbg_algo = new CheckboxGroup();
		
		_cb_FCFS = new Checkbox("First Come First Serve (FCFS)", true, _cbg_algo);
		_cb_FCFS.setBounds(325, 20, 200, 20);
		_cb_FCFS.addItemListener(this);
		
		_cb_SJF = new Checkbox("Shortest Job First (SJF)", false, _cbg_algo);
		_cb_SJF.setBounds(325, 50, 200, 20);
		_cb_SJF.addItemListener(this);
		
		_cb_PRIO = new Checkbox("Priority (Prio)", false, _cbg_algo);
		_cb_PRIO.setBounds(325, 80, 200, 20);
		_cb_PRIO.addItemListener(this);
		
		_cb_PPRIO = new Checkbox("Preemptive Priority (P-Prio)", false, _cbg_algo);
		_cb_PPRIO.setBounds(325, 110, 200, 20);
		_cb_PPRIO.addItemListener(this);
		
		_cb_SRTF = new Checkbox("Shortest Remaining Time First (SRTF)", false, _cbg_algo);
		_cb_SRTF.setBounds(325, 140, 250, 20);
		_cb_SRTF.addItemListener(this);
		
		_cb_RR = new Checkbox("Round Robin (RR)", false, _cbg_algo);
		_cb_RR.setBounds(325, 170, 200, 20);
		_cb_RR.addItemListener(this);
				
		_lbl_import = new JLabel("Import file : ");
		_lbl_import.setBounds(50, 20, 100, 20);
		_txt_import = new JTextField();
		_txt_import.setBounds(130, 20, 100, 25);
		
		
		_btn_save = new JButton("Save changes");
		_btn_save.setBounds(110, 80, 140, 25);
		_btn_save.addActionListener(this);
				
		_btn_run = new JButton("Export");
		_btn_run.setBounds(130, 140, 100, 25);
		_btn_run.addActionListener(this);
		
		
		_lbl_TT = new JLabel("Turnaround Time");
		_lbl_TT.setBounds(100, 210, 100, 25);
		_txt_result_tt = new TextArea("", 540, 120, TextArea.SCROLLBARS_VERTICAL_ONLY);
		_txt_result_tt.setBounds(20, 240, 260, 180);
		_txt_result_tt.setEditable(false);
		
		_lbl_WT = new JLabel("Waiting Time");
		_lbl_WT.setBounds(400, 210, 100, 25);
		_txt_result_wt = new TextArea("", 540, 120, TextArea.SCROLLBARS_VERTICAL_ONLY);
		_txt_result_wt.setBounds(300, 240, 260, 180);
		_txt_result_wt.setEditable(false);
		
		this.setLayout(null);
		
				
		this.add(_lbl_import);
		this.add(_txt_import);
		
		
		this.add(_cb_FCFS);
		this.add(_cb_SJF);
		this.add(_cb_PRIO);
		this.add(_cb_PPRIO);
		this.add(_cb_SRTF);
		this.add(_cb_RR);
		
		this.add(_btn_save);
		
		this.add(_btn_run);
		
		this.add(_lbl_TT);
		this.add(_lbl_WT);
		
		this.add(_txt_result_tt);
		this.add(_txt_result_wt);
		
		this.setSize(600, 500);
		this.setTitle("CPU Scheduling Algorithms");
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setVisible(true);
	}
	
	void refreshList()	{
		while(_jobs.size()!=0)
		_jobs.remove(0);
	}
	
	
	
	public void actionPerformed(ActionEvent e)	{
		int idx=0;
		//double	quantum=0.0;
		if(e.getSource() == _btn_save)	{
			refreshList();
				//quantum= Double.parseDouble(_txt_quantum.getText());
				String	fname= _txt_import.getText();
				File temp = new File(fname);
				System.out.print("number of jobs : ");
		        Scanner sc = null;
				try {
					sc = new Scanner(temp);
				} catch (FileNotFoundException e1) {
					JOptionPane.showMessageDialog(null, "Oops!The system cannot find the file specified", 
							"Save changes", JOptionPane.INFORMATION_MESSAGE);
					e1.printStackTrace();
				}
				idx=0;
		        while(sc.hasNext()){
		        	_jobs.add( 
							new Job(_jobs.size()+1, 0, 0, 
									_jobs.size()+1, Double.POSITIVE_INFINITY)
						);
		        	String line = sc.nextLine();
		        	String elements[] = line.split(" ");
		        	
		        	_jobs.get(idx).setArrivalTime(
							Double.parseDouble(elements[1])
						);
		        	_jobs.get(idx).setBurstTime(
							Double.parseDouble(elements[2])
						);
		        	_jobs.get(idx).setPriority(
							Double.parseDouble(elements[3])
						);
		        	idx++;
		        }
		        System.out.println(_jobs.size());
		        JOptionPane.showMessageDialog(null, (idx)+ " Job(s)" +" Saved", 
						"Save changes", JOptionPane.INFORMATION_MESSAGE);
		}
		if(e.getSource() == _btn_run)	{
			if(_jobs.isEmpty())	{
				_txt_result_tt.setText("");
				_txt_result_wt.setText("");
				return;
			}
			for(int i = 1; i <= idx; i++)
				_jobs.get(i-1).setJobNumber(i);
			CPU_Scheduling _solver = new CPU_Scheduling(_jobs, _algorithm);
			if( _solver.solve() )	{
				_txt_result_tt.setText(_solver.getResultTT());
				_txt_result_wt.setText(_solver.getResultWT());
				//drawGanttChart(_solver.getGanttChart());
			}
	        JOptionPane.showMessageDialog(null, "Succesfully exported results to output.txt", 
					"Export", JOptionPane.INFORMATION_MESSAGE);
		}
	}
	
	public void itemStateChanged(ItemEvent e)	{
		if(_cb_FCFS.getState())	{
			_algorithm = Algorithm.FCFS;
		}
		if(_cb_SJF.getState())	{
			_algorithm = Algorithm.SJF;
		}
		if(_cb_PRIO.getState())	{
			_algorithm = Algorithm.Prio;
		}
		if(_cb_PPRIO.getState())	{
			_algorithm = Algorithm.PPrio;
		}
		if(_cb_SRTF.getState())	{
			_algorithm = Algorithm.SRTF;
		}
		if(_cb_RR.getState())	{
			_algorithm = Algorithm.RR;
		}
	}

	public static void main(String[] args) {
		System.out.println("start");
		new FrameMain();
		JOptionPane.showMessageDialog(null, "+ Type your file name to import\n+ Choose algorithm\n+ Click Save changes\n+ Click Export ", 
				null, JOptionPane.INFORMATION_MESSAGE);
	}

}
