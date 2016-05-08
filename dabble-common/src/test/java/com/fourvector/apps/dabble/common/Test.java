package com.fourvector.apps.dabble.common;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Test {
	
	public static void main(String args[]){
		
		JobModel jm1 = new JobModel();
		jm1.setIsEvent("1");
		jm1.setStartDate(new Date());
		
		JobModel jm2 = new JobModel();
		jm2.setIsEvent("1");
		jm2.setStartDate(new Date());
		
		JobModel jm3 = new JobModel();
		jm3.setIsEvent("0");
		jm3.setStartDate(new Date());
		
		JobModel jm4 = new JobModel();
		jm4.setIsEvent("2");
		jm4.setStartDate(new Date());
		
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DATE, 17);
		Date d = cal.getTime();
		
		JobModel jm5 = new JobModel();
		jm5.setIsEvent("1");
		jm5.setStartDate(d);
		
		JobModel jm6 = new JobModel();
		jm6.setIsEvent("0");
		jm6.setStartDate(d);
		
		JobModel jm7 = new JobModel();
		jm7.setIsEvent("2");
		jm7.setStartDate(d);
		
		JobModel jm8 = new JobModel();
		jm8.setIsEvent("1");
		jm8.setStartDate(d);
		
		List<JobModel> jms = new ArrayList<JobModel>();
		jms.add(jm1);
		jms.add(jm2);
		jms.add(jm3);
		jms.add(jm4);
		jms.add(jm5);
		jms.add(jm6);
		jms.add(jm7);
		jms.add(jm8);
		
		System.out.println(jms);
		
		Map<Integer, List<JobModel>> subs = new HashMap<Integer, List<JobModel>>();

		for(JobModel o : jms){
			Calendar cale = Calendar.getInstance();
			cale.setTime(o.getStartDate());
			
		    List<JobModel> temp = subs.get(cale.get(Calendar.DATE));
		    if(temp == null){
		        temp = new ArrayList<JobModel>();
		        subs.put(cale.get(Calendar.DATE), temp);
		    }
		    temp.add(o);
		}
		
		Iterator iter = subs.keySet().iterator();
		List<JobModel> finalList = new ArrayList<JobModel>();
		while(iter.hasNext()){
			Integer dateKey = (Integer)iter.next();
			List<JobModel> alist = subs.get(dateKey);
			Collections.sort(alist, JobModel.COMPARE_BY_EVENT);
			finalList.addAll(alist);
		}
		
		
		System.out.println(finalList);
			
		}
		
		
		
	

}
