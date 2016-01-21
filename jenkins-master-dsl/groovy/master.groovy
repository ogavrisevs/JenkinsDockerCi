import hudson.model.*;
import jenkins.model.*;
import hudson.plugins.ec2.*;

Thread.start {
    sleep 10000

    def jenkins = Jenkins.getInstance()

    jenkins.setLabelString("master")
    jenkins.setSlaveAgentPort(50000)
    jenkins.setNumExecutors(1)
}
