package cpuscheduling;

import java.util.*;

import javax.swing.*;
import java.io.FileWriter;
import java.io.File;
import java.io.IOException;

public class CPU_Scheduling {
	
	List<Job> _jobs; 
	Algorithm _algorithm;
	
	List<Job> _joblist;
	PriorityQueue _queue;
	List<Double> _TT;
	List<Double> _WT;
	double _TTAve;
	double _WTAve;
	
	String _result_tt;
	String _result_wt;
	
	public CPU_Scheduling(List<Job> _joblist, Algorithm _algorithm)	{
		this._jobs = copyList(_joblist);
		this._algorithm = _algorithm;
		this._joblist = copyList(_joblist);
		this._queue = new PriorityQueue(_algorithm);
		
		this._TT = new ArrayList<>();
		this._WT = new ArrayList<>();
		this._TTAve = 0.0;
		this._WTAve = 0.0;
		
		this._result_tt = new String();
		this._result_wt = new String();
	}
	
	List<Double> getTT()	{
		return _TT;
	}
	List<Double> getWT()	{
		return _WT;
	}
	
	double getTTAve()	{
		return _TTAve;
	}
	
	double getWTAve()	{
		return _WTAve;
	}
	
	
	String getResultTT()	{
		return _result_tt;
	}
	
	String getResultWT()	{
		return _result_wt;
	}
	
	boolean solve()	{
		sortArrival();
		if( _algorithm == Algorithm.FCFS ||
			_algorithm == Algorithm.SJF ||
			_algorithm == Algorithm.Prio )	{
			nonPreemptiveAlgo();
		}
		else if( _algorithm == Algorithm.PPrio ||
				 _algorithm == Algorithm.SRTF )	{
			preemptiveAlgo();
		}

		else if(_algorithm == Algorithm.RR)	{
			double q = 0.0;
			try	{
				String input = JOptionPane.showInputDialog(null, "Quantum: ", 
					"Quantum", JOptionPane.QUESTION_MESSAGE);
				q = Double.parseDouble(input);
			}
			catch(Exception exc)	{
				JOptionPane.showMessageDialog(null, "Quantum must be a double!", 
						"Input Error", JOptionPane.ERROR_MESSAGE);
				return false;
			}	
			RR(q);
		}
		computeTTWT();
		return true;
	}
	
	void nonPreemptiveAlgo()	{
		Job tempJob = new Job();
		double time = 0.0;
		while( !_joblist.isEmpty() )	{
			for(int i = 0; i < _joblist.size(); i++)	{		
				if( _joblist.get(i).getArrivalTime() <= time ) 	
					_queue.enqueue(_joblist.get(i));
				else	break;
			}
			if( !_queue.isEmpty() )	{
				tempJob = _queue.dequeue();
				time += tempJob.getBurstTime();
				tempJob.setJobFinish(time);
				_jobs.get( tempJob.getJobNumber()-1 ).setJobFinish(time);
				delete(tempJob);
			}
			else	{
				tempJob = dequeue();
				enqueue(tempJob);
			}
		}
	}
	
	void preemptiveAlgo()	{
		Job tempJob = new Job();
		Job nextJob = new Job();
		double time = 0.0;
		double temp = 0.0;
		while( _joblist.isEmpty() != true )	{
			for(int i = 0; i < _joblist.size(); i++)	{		
				if( _joblist.get(i).getArrivalTime() <= time ) 		
					_queue.enqueue(_joblist.get(i));
				else	break;
			}
			if( _queue.isEmpty() != true )	{
				tempJob = _queue.dequeue();
				for(int i = 0; i < _joblist.size(); i++)	{
					if( _joblist.get(i).getArrivalTime() > time)	{
						nextJob = _joblist.get(i);
						break;
					}
				}
				if( ( (time+tempJob.getBurstTime()) > nextJob.getArrivalTime() ) 
						&& time < _joblist.get(_joblist.size()-1).getArrivalTime())	{
					temp = nextJob.getArrivalTime() - time;
					time = nextJob.getArrivalTime();
					tempJob.setBurstTime( tempJob.getBurstTime()-temp );
				}
				else	{
					time += tempJob.getBurstTime();
					tempJob.setJobFinish(time);
					_jobs.get( tempJob.getJobNumber()-1 ).setJobFinish(time);
					delete(tempJob);
				}
			}
			else	{
				tempJob = dequeue();
				enqueue(tempJob);
			}
		}
	}
	
	void RR(double quantum)	{
		Job tempJob = new Job();
		double time = 0.0;
		while( _joblist.isEmpty() != true )	{
			if( _joblist.get(0).getArrivalTime() <= time )	{
				tempJob = dequeue();
				if(tempJob.getBurstTime() != 0)	{
					if(tempJob.getBurstTime() > quantum)	{
						time += quantum;
						tempJob.setBurstTime( tempJob.getBurstTime()- quantum );
						tempJob.setArrivalTime(time);
						enqueue(tempJob);
					}
					else	{
						time += tempJob.getBurstTime();
						tempJob.setBurstTime(0.0);
						tempJob.setJobFinish(time);
						_jobs.get( tempJob.getJobNumber()-1 ).setJobFinish(time);
					}
				}
			}
			else	{
				tempJob = dequeue();
				enqueue(tempJob);
			}
		}
	}
	
	void computeTTWT()	{
		String r = new String();
		
		r = "\tTurnaround Time (tt)\n";
		r += "\tfinish time - arrival time\n\n";
		
		for(int i = 0; i < _jobs.size(); i++)	{
			_TT.add( _jobs.get(i).getJobFinish()- _jobs.get(i).getArrivalTime() );
			r += "tt" + _jobs.get(i).getJobNumber() + "   " + 
					_jobs.get(i).getJobFinish() + " - " + _jobs.get(i).getArrivalTime() +
					" = " + _TT.get(i) + "\n";
			_TTAve += _TT.get(i);
		}
		_TTAve /= _jobs.size();
		r += "Turnaround time Average: " + _TTAve + "\n\n";
		
		_result_tt = r;
		File file = new File("output.txt");
		FileWriter fileWriter = null;
		try {
			fileWriter = new FileWriter(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			fileWriter.write(_result_tt);
		} catch (IOException e) {
			e.printStackTrace();
		}

		r = "\tWaiting Time (wt)\n";
		r += "\tturnaround time - burst time\n\n";
		for(int i = 0; i < _jobs.size(); i++)	{
			_WT.add( _TT.get(i) - _jobs.get(i).getBurstTime() );
			r += "wt" + _jobs.get(i).getJobNumber() + "   " + 
					_TT.get(i) + " - " + _jobs.get(i).getBurstTime() +
					" = " + _WT.get(i) + "\n";
			_WTAve += _WT.get(i);
		}
		_WTAve /= _jobs.size();
		r += "Waiting time Average: " + _WTAve + "\n\n";
		
		_result_wt = r;
		try {
			fileWriter.write(_result_wt);
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			fileWriter.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			fileWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	void enqueue(Job job)	{
		_joblist.add(job);
		sortArrival();
	}
	
	Job dequeue()	{
		Job temp = new Job();
		temp = _joblist.get(0);
		_joblist.remove(0);
		return temp;
	}
	
	void delete(Job job)	{
		for(int i = 0; i < _joblist.size(); i++)	
			if( job == _joblist.get(i) )	{
				_joblist.remove(i);
				break;
			}
	}
	
	boolean isEmpty()	{
		return _joblist.isEmpty(); 
	}
	
	void sortArrival()	{
		int i =0 , j = 0; 
		Job temp = new Job();
		for(i = 1; i < _joblist.size(); i++)	{
			temp = _joblist.get(i);
			j = i;
			while( (j>0) && (_joblist.get(j-1).getArrivalTime() > temp.getArrivalTime()) )	{
				_joblist.remove(j);
				_joblist.add(j, _joblist.get(j-1));
				j -= 1;
			}
			_joblist.remove(j);
			_joblist.add(j, temp);
		}
	}
	
	ArrayList<Job> copyList(List<Job> _jobs)	{
		ArrayList<Job> temp = new ArrayList<Job>();
		for(Job j : _jobs)	{
			temp.add(
					new Job(j.getJobNumber(), j.getArrivalTime(),
							j.getBurstTime(), j.getJobFinish(),
							j.getPriority())
				);
		}
		return temp;
	}
	
}
