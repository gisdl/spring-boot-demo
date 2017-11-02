package org.apache.logging.log4j.core.appender;

import common.OSUtil;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.rolling.DefaultRolloverStrategy;
import org.apache.logging.log4j.core.appender.rolling.DirectFileRolloverStrategy;
import org.apache.logging.log4j.core.appender.rolling.DirectWriteRolloverStrategy;
import org.apache.logging.log4j.core.appender.rolling.RollingRandomAccessFileManager;
import org.apache.logging.log4j.core.appender.rolling.RolloverStrategy;
import org.apache.logging.log4j.core.appender.rolling.TriggeringPolicy;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderFactory;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.net.Advertiser;
import org.apache.logging.log4j.core.util.Booleans;
import org.apache.logging.log4j.core.util.Integers;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by dl on 2017/10/24.
 */

@Plugin(
        name = "APIAppender",
        category = "Core",
        elementType = "appender",
        printObject = true
)
public class APIAppender extends AbstractOutputStreamAppender<RollingRandomAccessFileManager> {
    private String fileName;
    private String filePattern;
    private Object advertisement;
    private Advertiser advertiser;

    private APIAppender(String name, Layout<? extends Serializable> layout, Filter filter, RollingRandomAccessFileManager manager, String fileName, String filePattern, boolean ignoreExceptions, boolean immediateFlush, int bufferSize, Advertiser advertiser) {
        super(name, layout, filter, ignoreExceptions, immediateFlush, manager);
        if (advertiser != null) {
            Map<String, String> configuration = new HashMap(layout.getContentFormat());
            configuration.put("contentType", layout.getContentType());
            configuration.put("name", name);
            this.advertisement = advertiser.advertise(configuration);
        } else {
            this.advertisement = null;
        }

        this.fileName = fileName;
        this.filePattern = filePattern;
        this.advertiser = advertiser;
    }

    public boolean stop(long timeout, TimeUnit timeUnit) {
        this.setStopping();
        super.stop(timeout, timeUnit, false);
        if (this.advertiser != null) {
            this.advertiser.unadvertise(this.advertisement);
        }

        this.setStopped();
        return true;
    }

    public void append(LogEvent event) {
        RollingRandomAccessFileManager manager = (RollingRandomAccessFileManager) this.getManager();
        manager.checkRollover(event);
        manager.setEndOfBatch(event.isEndOfBatch());
        super.append(event);
    }

    public String getFileName() {
        return this.fileName;
    }

    public String getFilePattern() {
        return this.filePattern;
    }

    public int getBufferSize() {
        return ((RollingRandomAccessFileManager) this.getManager()).getBufferSize();
    }

    /**
     * @deprecated
     */
    @Deprecated
    public static <B extends APIAppender.Builder<B>> APIAppender createAppender(String fileName, String filePattern, String append, String name, String immediateFlush, String bufferSizeStr, TriggeringPolicy policy, RolloverStrategy strategy, Layout<? extends Serializable> layout, Filter filter, String ignoreExceptions, String advertise, String advertiseURI, Configuration configuration) {
        boolean isAppend = Booleans.parseBoolean(append, true);
        boolean isIgnoreExceptions = Booleans.parseBoolean(ignoreExceptions, true);
        boolean isImmediateFlush = Booleans.parseBoolean(immediateFlush, true);
        boolean isAdvertise = Boolean.parseBoolean(advertise);
        int bufferSize = Integers.parseInt(bufferSizeStr, 262144);
        return ((APIAppender.Builder) ((APIAppender.Builder) ((APIAppender.Builder) ((APIAppender.Builder) ((APIAppender.Builder) ((APIAppender.Builder) ((APIAppender.Builder) newBuilder().withAdvertise(isAdvertise).withAdvertiseURI(advertiseURI).withAppend(isAppend).withBufferSize(bufferSize)).setConfiguration(configuration)).withFileName(fileName).withFilePattern(filePattern).withFilter(filter)).withIgnoreExceptions(isIgnoreExceptions)).withImmediateFlush(isImmediateFlush)).withLayout(layout)).withName(name)).withPolicy(policy).withStrategy(strategy).build();
    }

    @PluginBuilderFactory
    public static <B extends APIAppender.Builder<B>> B newBuilder() {
        return (B) (new Builder()).asBuilder();
    }

    public static class Builder<B extends APIAppender.Builder<B>> extends AbstractOutputStreamAppender.Builder<B> implements org.apache.logging.log4j.core.util.Builder<APIAppender> {
        @PluginBuilderAttribute("fileName")
        private String fileName;
        @PluginBuilderAttribute("filePattern")
        private String filePattern;
        @PluginBuilderAttribute("append")
        private boolean append = true;
        @PluginElement("Policy")
        private TriggeringPolicy policy;
        @PluginElement("Strategy")
        private RolloverStrategy strategy;
        @PluginBuilderAttribute("advertise")
        private boolean advertise;
        @PluginBuilderAttribute("advertiseURI")
        private String advertiseURI;
        @PluginBuilderAttribute
        private String filePermissions;
        @PluginBuilderAttribute
        private String fileOwner;
        @PluginBuilderAttribute
        private String fileGroup;

        public Builder() {
            this.withBufferSize(262144);
            this.withIgnoreExceptions(true);
            this.withImmediateFlush(true);
        }

        public APIAppender build() {
            String name = this.getName();
            if (name == null) {
                APIAppender.LOGGER.error("No name provided for FileAppender");
                return null;
            } else {
                if (this.strategy == null) {
                    if (this.fileName != null) {

                        this.strategy = DefaultRolloverStrategy.newBuilder().withCompressionLevelStr(String.valueOf(-1)).withConfig(this.getConfiguration()).build();
                    } else {
                        this.strategy = DirectWriteRolloverStrategy.newBuilder().withCompressionLevelStr(String.valueOf(-1)).withConfig(this.getConfiguration()).build();
                    }
                } else if (this.fileName == null && !(this.strategy instanceof DirectFileRolloverStrategy)) {
                    APIAppender.LOGGER.error("RollingFileAppender '{}': When no file name is provided a DirectFilenameRolloverStrategy must be configured");
                    return null;
                }

                if (this.filePattern == null) {
                    APIAppender.LOGGER.error("No filename pattern provided for FileAppender with name " + name);
                    return null;
                } else if (this.policy == null) {
                    APIAppender.LOGGER.error("A TriggeringPolicy must be provided");
                    return null;
                } else {
                    /*ediat start --duliang*/
                    //所有日志类文件命名为hostname打头
                    this.fileName = addHostName(this.fileName);
                    this.filePattern = addHostName(this.filePattern);
                    /*ediat end*/
                    Layout<? extends Serializable> layout = this.getOrCreateLayout();
                    boolean immediateFlush = this.isImmediateFlush();
                    int bufferSize = this.getBufferSize();
                    RollingRandomAccessFileManager manager = RollingRandomAccessFileManager.getRollingRandomAccessFileManager(this.fileName, this.filePattern, this.append, immediateFlush, bufferSize, this.policy, this.strategy, this.advertiseURI, layout, this.filePermissions, this.fileOwner, this.fileGroup, this.getConfiguration());
                    if (manager == null) {
                        return null;
                    } else {
                        manager.initialize();
                        return new APIAppender(name, layout, this.getFilter(), manager, this.fileName, this.filePattern, this.isIgnoreExceptions(), immediateFlush, bufferSize, this.advertise ? this.getConfiguration().getAdvertiser() : null);
                    }
                }
            }
        }

        private String addHostName(String str) {
            //今后考虑多线程就不使用StringBuilder
            StringBuffer sb = new StringBuffer(str);//分割后
            if (sb.toString().contains("/")) {
                sb.insert(sb.toString().lastIndexOf("/") + 1, OSUtil.getHostNameForLiunx() + "-");
            } else {
                sb.insert(sb.toString().lastIndexOf(File.separator) + 1, OSUtil.getHostNameForLiunx() + "-");
            }
            return sb.toString();
        }
        /*ediat start ，一下代码均删除强转,直接返回this.asBuilder();--duliang 20171026*/
        public B withFileName(String fileName) {
            this.fileName = fileName;
            return this.asBuilder();
        }

        public B withFilePattern(String filePattern) {
            this.filePattern = filePattern;
            return this.asBuilder();
        }

        public B withAppend(boolean append) {
            this.append = append;
            return this.asBuilder();
        }

        public B withPolicy(TriggeringPolicy policy) {
            this.policy = policy;
            return this.asBuilder();
        }

        public B withStrategy(RolloverStrategy strategy) {
            this.strategy = strategy;
            return this.asBuilder();
        }

        public B withAdvertise(boolean advertise) {
            this.advertise = advertise;
            return this.asBuilder();
        }

        public B withAdvertiseURI(String advertiseURI) {
            this.advertiseURI = advertiseURI;
            return this.asBuilder();
        }

        public B withFilePermissions(String filePermissions) {
            this.filePermissions = filePermissions;
            return this.asBuilder();
        }

        public B withFileOwner(String fileOwner) {
            this.fileOwner = fileOwner;
            return this.asBuilder();
        }

        public B withFileGroup(String fileGroup) {
            this.fileGroup = fileGroup;
            return this.asBuilder();
        }
        /*ediat end*/
    }
}