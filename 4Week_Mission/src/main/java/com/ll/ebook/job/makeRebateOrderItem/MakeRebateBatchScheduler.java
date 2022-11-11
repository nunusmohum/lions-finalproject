package com.ll.ebook.job.makeRebateOrderItem;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class MakeRebateBatchScheduler {

    private final JobLauncher jobLauncher;
    private final Job makeRebateOrderItemJob;

    @Scheduled(cron = "0 0 4 15 * ?") // 매달 15일 새벽 4시
//    @Scheduled(cron = "0 0/1 * * * ?") // 1분 마다 실행(테스트용)
    public void runJob() throws Exception {
        Map<String, JobParameter> confMap = new HashMap<>();
        confMap.put("time", new JobParameter(System.currentTimeMillis()));
        JobParameters jobParameters = new JobParameters(confMap);

        jobLauncher.run(makeRebateOrderItemJob, jobParameters);
    }

}
