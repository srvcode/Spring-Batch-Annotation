package com.srvcode.springbatch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.srvcode.springbatch.config.SpringBatchConfig;
import com.srvcode.springbatch.config.SpringConfig;

public class BatchProcessingApplication {

	public static void main(String[] args) {

		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
		context.register(SpringConfig.class);
		context.register(SpringBatchConfig.class);
		context.refresh();

		JobLauncher jobLauncher = (JobLauncher) context.getBean("jobLauncher");
		Job job = (Job) context.getBean("CSVtoXML");

		System.out.println("Starting the batch job...");

		try {
			JobExecution execution = jobLauncher.run(job, new JobParameters());
			
			System.out.println("\n***********************************\n");
			System.out.printf("Job Status: %s%n", execution.getStatus());
			System.out.println("Job Completed");
			
		} catch (JobExecutionAlreadyRunningException | JobRestartException |
				JobInstanceAlreadyCompleteException | JobParametersInvalidException e) {
			
			e.printStackTrace();
			
			System.out.println("Job failed");
		}

	}

}
