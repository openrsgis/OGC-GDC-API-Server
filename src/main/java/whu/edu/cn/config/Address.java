package whu.edu.cn.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Some constant addresses.
 */
@Component
@ConfigurationProperties(prefix = "address")
public class Address {
    private String localDataRoot;
    private String processApiUrl;
    private String coverageApiUrl;
    private String dapaApiUrl;
    private String gdcApiUrl;

    public String getGdcApiUrl() {
        return gdcApiUrl;
    }


    public void setLocalDataRoot(String localDataRoot) {
        this.localDataRoot = localDataRoot;
    }

    public String getProcessApiUrl() {
        return processApiUrl;
    }

    public void setProcessApiUrl(String processApiUrl) {
        this.processApiUrl = processApiUrl;
    }

    public String getCoverageApiUrl() {
        return coverageApiUrl;
    }

    public void setCoverageApiUrl(String coverageApiUrl) {
        this.coverageApiUrl = coverageApiUrl;
    }

    public String getDapaApiUrl() {
        return dapaApiUrl;
    }

    public void setDapaApiUrl(String dapaApiUrl) {
        this.dapaApiUrl = dapaApiUrl;
    }

    public void setGdcApiUrl(String gdcApiUrl) { this.gdcApiUrl = gdcApiUrl; }

    public String getLocalDataRoot() { return localDataRoot; }

}
