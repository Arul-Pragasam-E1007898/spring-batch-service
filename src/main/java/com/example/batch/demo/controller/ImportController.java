package com.example.batch.demo.controller;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

@RestController
@RequestMapping("/rest/v2")
public class ImportController {
  private JobLauncher jobLauncher;
  private ApplicationContext applicationContext;

  @Autowired
  public void setJobLauncher(JobLauncher jobLauncher) {
    this.jobLauncher = jobLauncher;
  }

  @Autowired
  public void setApplicationContext(ApplicationContext applicationContext) {
    this.applicationContext = applicationContext;
  }

  @PostMapping("/import")
  public ResponseEntity<?> handleFileUpload(@RequestParam("file") MultipartFile file ) throws IOException, JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
    File csv = store(file);
    Job importJob = applicationContext.getBean("importJob", Job.class);
    HashMap<String, JobParameter> hm = new HashMap<>();
    hm.put("file", new JobParameter(csv.getPath()));
    jobLauncher.run(importJob, new JobParameters(hm));
    return ResponseEntity.ok("File imported successfully.");
  }

  private static File store(MultipartFile file) throws IOException {
    File tmp = File.createTempFile("csv", ".tmp");
    file.transferTo(tmp);
    return tmp;
  }


}
