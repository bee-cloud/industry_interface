package com.fxiaoke.dataplatform.crm.cleantool.service;

import com.github.autoconf.ConfigFactory;
import com.github.autoconf.api.IChangeableConfig;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.spark.launcher.SparkAppHandle;
import org.apache.spark.launcher.SparkLauncher;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * submit spark job to yarn
 * Created by wzk on 16/7/6.
 */
@Service
@Slf4j
public class SparkSubmitService {

    // bridge java.util.logging to slf4j
    static {
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
    }

    public void submit(String jar, String clazz, List<String> args) throws IOException {
        Preconditions.checkNotNull(jar, "spark jar should not be empty.");
        Preconditions.checkNotNull(clazz, "main class should not be empty.");

        IChangeableConfig config = ConfigFactory.getInstance().getConfig("spark-submit-config");
        Map<String, String> sparkSubmitConfigs = config.getAll();

        SparkLauncher sparkLauncher = new SparkLauncher()
                .setAppResource(jar)
                .setMainClass(clazz)
                .setMaster("yarn-cluster")
                .setAppName("spark-launcher");

        if (sparkSubmitConfigs != null && !sparkSubmitConfigs.isEmpty()) {
            for (Map.Entry<String, String> entry : sparkSubmitConfigs.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                if (StringUtils.equals(key, "spark.home")) {
                    sparkLauncher.setSparkHome(value);
                } else if (StringUtils.equals(key, "jars")) {
                    List<String> jars = Splitter.on(',').omitEmptyStrings().trimResults().splitToList(value);
                    jars.forEach(sparkLauncher::addJar);
                } else if (StringUtils.equals(key, "files")) {
                    List<String> files = Splitter.on(',').omitEmptyStrings().trimResults().splitToList(value);
                    files.forEach(sparkLauncher::addFile);
                } else {
                    sparkLauncher.setConf(key, value);
                }
            }
        }

        if (args != null && !args.isEmpty()) {
            args.forEach(sparkLauncher::addAppArgs);
        }

        SparkAppHandle sparkAppHandle = sparkLauncher.startApplication();

        sparkAppHandle.addListener(new SparkAppHandle.Listener() {
            @Override
            public void stateChanged(SparkAppHandle sparkAppHandle) {
                log.info("spark job state was changed to {}", sparkAppHandle.getState());
            }

            @Override
            public void infoChanged(SparkAppHandle sparkAppHandle) {
                log.info("spark job info was changed.");
            }
        });

        log.info("Submit spark job, job application id is {}", sparkAppHandle.getAppId());
    }

    public void submitWithJars(String jar, String clazz, List<String> args, List<String> addJars) throws IOException {
        Preconditions.checkNotNull(jar, "spark jar should not be empty.");
        Preconditions.checkNotNull(clazz, "main class should not be empty.");

        SparkLauncher sparkLauncher = new SparkLauncher()
                .setAppResource(jar)
                .setMainClass(clazz)
                .setMaster("yarn-cluster")
                .setAppName("spark-launcher-with-jars");

        addJars.forEach(sparkLauncher::addJar);
        //.addJar(SparkSubmitService.class.getResource("/").toString() + "elasticsearch-hadoop-2.3.0.jar");

        IChangeableConfig config = ConfigFactory.getInstance().getConfig("spark-submit-config");
        Map<String, String> sparkSubmitConfigs = config.getAll();
        if (null != sparkSubmitConfigs && !sparkSubmitConfigs.isEmpty()) {
            for (Map.Entry<String, String> entry : sparkSubmitConfigs.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                log.info("****** Spark config: key={}, value={}", key, value);
                switch(key){
                    case "spark.home":
                        sparkLauncher.setSparkHome(value);
                        break;
                    case "jars":
                        List<String> jars = Splitter.on(',').omitEmptyStrings().trimResults().splitToList(value);
                        jars.forEach(sparkLauncher::addJar);
                        break;
                    case "files":
                        List<String> files = Splitter.on(',').omitEmptyStrings().trimResults().splitToList(value);
                        files.forEach(sparkLauncher::addFile);
                        break;
                    default:
                        sparkLauncher.setConf(key, value);
                }
            }
        }

        if (args != null && !args.isEmpty()) {
            args.forEach(sparkLauncher::addAppArgs);
        }

        SparkAppHandle sparkAppHandle = sparkLauncher.startApplication();

        sparkAppHandle.addListener(new SparkAppHandle.Listener() {
            @Override
            public void stateChanged(SparkAppHandle sparkAppHandle) {
                log.info("spark job state was changed to {}", sparkAppHandle.getState());
            }

            @Override
            public void infoChanged(SparkAppHandle sparkAppHandle) {
                log.info("spark job info was changed.");
            }
        });

        log.info("Submit spark job, job application id is {}", sparkAppHandle.getAppId());
    }

}
