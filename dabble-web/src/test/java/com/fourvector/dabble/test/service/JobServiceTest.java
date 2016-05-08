/**
 * 
 */
package com.fourvector.dabble.test.service;

import java.util.Calendar;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.fourvector.apps.dabble.common.dto.JobDTO;
import com.fourvector.apps.dabble.service.IJobService;
import com.fourvector.apps.dabble.web.rest.BaseRestController;
import com.google.gson.Gson;

/**
 * @author asharma
 */
public class JobServiceTest {

	private static ApplicationContext	applicationContext;
	private IJobService					jobService;
	private static final Logger			LOG		= LoggerFactory.getLogger(JobServiceTest.class);
	protected final static Gson			gson	= BaseRestController.gson;

	/**
	 * 
	 */
	public JobServiceTest() {
		super();
		//initComponents();
	}

	private void initComponents() {
		LOG.debug("Method [initComponents]: Called");
		applicationContext = new ClassPathXmlApplicationContext("classpath:spring/*.xml");
		jobService = applicationContext.getBean(IJobService.class);
		LOG.debug("Method [initComponents]: Returning.");
	}

	public static void main(String[] args) {
		System.out.println("starting..");
		
		//JobServiceTest jst = new JobServiceTest();
		//jst.testSorting(122);
		
		
		/*JobServiceTest jst = new JobServiceTest();
		try {
			JobDTO dto = new JobDTO();
			dto.getAudios().add(UUID.randomUUID().toString() + ".mp3");
			dto.getAudios().add(UUID.randomUUID().toString() + ".mp3");
			dto.getAudios().add(UUID.randomUUID().toString() + ".mp3");
			dto.getAudios().add(UUID.randomUUID().toString() + ".mp3");
			dto.getAudios().add(UUID.randomUUID().toString() + ".mp3");
			dto.getVideos().add(UUID.randomUUID().toString() + ".mp4");
			dto.getVideos().add(UUID.randomUUID().toString() + ".mp4");
			dto.getVideos().add(UUID.randomUUID().toString() + ".mp4");
			dto.getVideos().add(UUID.randomUUID().toString() + ".mp4");
			dto.getVideos().add(UUID.randomUUID().toString() + ".mp4");
			dto.getImages().add(UUID.randomUUID().toString() + ".png");
			dto.getImages().add(UUID.randomUUID().toString() + ".png");
			dto.getImages().add(UUID.randomUUID().toString() + ".png");
			dto.getImages().add(UUID.randomUUID().toString() + ".png");
			dto.getImages().add(UUID.randomUUID().toString() + ".png");
			dto.getImages().add(UUID.randomUUID().toString() + ".png");

// System.out.println(gson.toJson(dto));
//			jst.testCreateJob();
		} catch (Exception e) {
			LOG.error("Found exception Exception in method main", e);
		} finally {
			System.out.println("Finished.");
		}*/

	}

	private void testSorting(int userId) {
		List<JobDTO> jobList = jobService.getMyJobs(userId);
		System.out.println("After Sorting");
		for(JobDTO job : jobList){	
			System.out.println(job.getJobTitle()+":::"+job.getStartDate().getTime());
		}
		
	}

	public void testCreateJob() throws Exception {
		String payload = "{  'description': 'My Home, My Work, My Money.',  'categoryId': 3,  'posterId': 33,  'lat': 18.7743,  'lng': 12.2296,  'durationOfWorkInDays': 3,  'workHours': '0900-1800',  'amount': 34.0,  'currency': 'USD',  'startDate': '02-Aug-2015',  'isMondayWorking': true,  'isTuesdayWorking': false,  'isWednesdayWorking': true,  'isThursdayWorking': false,  'isFridayWorking': true,  'isSaturdayWorking': false,  'isSundayWorking': false}";
		JobDTO jobDto = gson.fromJson(payload, JobDTO.class);
//		System.out.println(jobService.createJob(jobDto));
	}

}
