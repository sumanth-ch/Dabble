package com.fourvector.apps.dabble.service.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.fourvector.apps.dabble.dao.config.Status;
import com.fourvector.apps.dabble.model.job.JobModel;

public class ServiceUtil {
	
	//Group the jobs based on event type for a specific date	
		public static List<JobModel> sortJobs(List<JobModel> jobs){
			List<JobModel> jobList = new ArrayList<JobModel>();
			for(JobModel jm : jobs){
				
				Calendar cal = Calendar.getInstance();
				cal.add(Calendar.DATE, -7);
				
				//Truncating the jobs created before 7 days.	
				if(jm.getCreatedOn().after(cal.getTime())){
					jobList.add(jm);
				}
				//If the job is older than 7 days, truncate the jobs that are active but without any bids
				else if(jm.getStatus().equals(Status.ACTIVE) && jm.getBids().size() == 0){
					continue;
				}else{
					jobList.add(jm);
				}
			}
			return jobList;
			
			
			/*List<JobModel> sortedList = new ArrayList<JobModel>();
			List<JobModel> tempList = new ArrayList<JobModel>();
			//group the jobs based on jobs. Preparing the hashmap with date as key and value as the joblist for that date.
			for(int i = 0; i < jobs.size(); i++){
				
				if(i == 0){
					tempList.add(jobs.get(i));
					continue;
				}
					
				JobModel currentJob = jobs.get(i);
				JobModel previousJob = jobs.get(i-1);
				
				Calendar cal1 = Calendar.getInstance();
				cal1.setTime(currentJob.getStartDate());
				
				Calendar cal2 = Calendar.getInstance();
				cal2.setTime(previousJob.getStartDate());
				
				if(cal1.get(Calendar.DATE) != cal2.get(Calendar.DATE)){
					Collections.sort(tempList, JobModel.COMPARE_BY_EVENT);
					sortedList.addAll(tempList);
					tempList.removeAll(tempList);
					tempList.add(jobs.get(i));
				}else{
					tempList.add(jobs.get(i));
				}
			}
			sortedList.addAll(tempList);
			return sortedList;
		*/}

}
