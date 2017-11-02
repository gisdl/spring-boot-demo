package cn.iot.api;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = Log4j2demoApplication.class)
public class Log4j2demoApplicationTests {

    /**
     * 配置日志级别为info，输出位置为控制台
     */
//    private static Logger ASYNC_LOGGER = LogManager.getLogger("ASYNC_LOGGER");
    private static final Logger ASYNC_LOGGER = LoggerFactory.getLogger("ASYNC_LOGGER");
    private static final Logger CDR_LOGGER = LoggerFactory.getLogger("cdrLogger");
    private static final Logger CLASSLOG = LoggerFactory.getLogger(Log4j2demoApplicationTests.class);

    @Test
    public void testLogger() {
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < 500; i++) {
//            System.out.println(ASYNC_LOGGER.isDebugEnabled());
            ASYNC_LOGGER.info(i + "=======12345678978456123123");
//            ASYNC_LOGGER.error("{i},msg[{}]",i,"llllllkjaksjdl");
            ASYNC_LOGGER.trace(i + "trace");
            ASYNC_LOGGER.debug(i + "debug");
            ASYNC_LOGGER.info(i + "info");
            ASYNC_LOGGER.warn(i + "warn");
            ASYNC_LOGGER.error(i + "error");
//            ASYNC_LOGGER.fatal(i + "fatal");

            CDR_LOGGER.trace(i + "trace-1");
            CDR_LOGGER.debug(i + "debug-1");
            CDR_LOGGER.info(i + "info-1");
            CDR_LOGGER.warn(i + "warn-1");
            CDR_LOGGER.error(i + "error-1");
//            CDR_LOGGER.fatal(i + "fatal-1");
            CDR_LOGGER.trace(i + "trace-2");
            CDR_LOGGER.debug(i + "debug-2");
            CDR_LOGGER.info(i + "info-2");
            CDR_LOGGER.warn(i + "warn-2");
            CDR_LOGGER.error(i + "error-2");
//            CDR_LOGGER.fatal(i + "fatal-2");
            CDR_LOGGER.trace(i + "trace-3");
            CDR_LOGGER.debug(i + "debug-3");
            CDR_LOGGER.info(i + "info-3");
            CDR_LOGGER.warn(i + "warn-3");
            CDR_LOGGER.error(i + "error-3");
//            CDR_LOGGER.fatal(i + "fatal-3");
            CLASSLOG.trace("error test{}", i + "-> trace");
            CLASSLOG.debug("debug test{}", i + "-> debug");
            CLASSLOG.error("error test{}", i + "-> error");
            CLASSLOG.info("debug test{}", i + "-> info");
            CLASSLOG.warn("error test{}", i + "-> warn");
//            CLASSLOG.fatal("debug test{}", i + "-> fatal");
        }
        long endTime = System.currentTimeMillis();
        System.out.println(endTime - startTime);
        CLASSLOG.debug("endTime-startTime[{}]", (endTime - startTime));
    }
}
