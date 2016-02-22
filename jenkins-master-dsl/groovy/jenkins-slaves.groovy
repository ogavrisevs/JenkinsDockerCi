import com.amazonaws.services.ec2.model.InstanceType
import hudson.model.*;
import jenkins.model.*;
import hudson.plugins.ec2.*;
import groovy.json.JsonSlurper;
import java.text.SimpleDateFormat

Thread.start {

    def jsonSlurper = new JsonSlurper()
    def jenkins = Jenkins.getInstance()

    def mac = "http://169.254.169.254/latest/meta-data/network/interfaces/macs/".toURL().text.replace("/", "")
    def subnet_id_url = "http://169.254.169.254/latest/meta-data/network/interfaces/macs/${mac}/subnet-id/"
    def subnet_id = subnet_id_url.toURL().text

    def keyFileName = new SimpleDateFormat("yyyyMMddhhmm").format(new Date());
    def newKey = "aws --region eu-west-1 ec2 create-key-pair --key-name=${keyFileName} --output json".execute().text
    def newPrivSshKey = jsonSlurper.parseText(newKey).KeyMaterial
    println "New Ssh key : "+ jsonSlurper.parseText(newKey)

    def sec_groups = "aws --region eu-west-1 ec2 describe-security-groups --output json".execute().text
    def securityGroup = jsonSlurper.parseText(sec_groups).SecurityGroups.GroupName.find { it.contains('JenkinsSlaveSecurityGroup') }

    def profiles = "aws --region eu-west-1 iam list-instance-profiles --output json".execute().text
    def instanceProfile = jsonSlurper.parseText(profiles).InstanceProfiles.Arn.find { it.contains('EcsInstanceProfile') }

    List<SlaveTemplate> slaveTemplates = new ArrayList<SlaveTemplate>();

    DockerSlaveTemplate dockerSlave = new DockerSlaveTemplate()
    dockerSlave.setAmi("ami-13f84d60");
    dockerSlave.setUserData(new File("/usr/share/jenkins/ref/init.groovy.d/cloud-init/docker-19.yaml").text);
    dockerSlave.setLabel("docker-191 docker")
    dockerSlave.setDescription("Slave with Docker 1.9.1")

    List<EC2Tag> tags = new ArrayList<EC2Tag>();
    tags.add(new EC2Tag("Name", "jenkinsSlave-docker1.9.1"));
    dockerSlave.setTags(tags)

    dockerSlave.setUseEphemeralDevices(false)
    dockerSlave.setInstanceCapStr("10")
    dockerSlave.setSecurityGroups(securityGroup)
    dockerSlave.setIamInstanceProfile(instanceProfile)
    dockerSlave.setSubnetId(subnet_id)
    slaveTemplates.add(dockerSlave.getSlaveTemplate())

    String cloudName = "jenkinsSlaveCloud"
    String accessId = ""
    String secreyKey = ""
    String region = "eu-west-1"
    String instanceCapStr = "10"
    Boolean useInstanceProfileForCredentials = true

    AmazonEC2Cloud cloud = new AmazonEC2Cloud(cloudName, useInstanceProfileForCredentials, accessId, secreyKey, region,
            newPrivSshKey, instanceCapStr, slaveTemplates);

    jenkins.clouds.add(cloud);
    jenkins.reload()
}


public class DockerSlaveTemplate {
    String remoteFS = "/home/ec2-user"
    String remoteAdmin = "ec2-user"
    UnixData unixData = new UnixData("", "22");
    String numExecutors = "1";
    String jvmopts = "-Xmx1g"
    Boolean stopOnTerminate = false;
    Boolean usePrivateDnsName = false;
    String instanceCapStr = "5";
    String iamInstanceProfile = "";
    Boolean useEphemeralDevices = true;
    Boolean useDedicatedTenancy = false;
    String launchTimeoutStr = "";
    Boolean associatePublicIp = false;
    String customDeviceMapping = "";
    Boolean connectBySSHProcess = false;
    Boolean ebsOptimized = false;
    SpotConfiguration spotConfiguration = null;
    String securityGroups = "EcsSecurityGroup";
    InstanceType type = InstanceType.T2Micro;
    String zone = "";
    Node.Mode mode = Node.Mode.EXCLUSIVE
    String description = ""
    String initScript = "whoami"
    String tmpDir = "/tmp"
    String userData = ""
    AMITypeData amiType = new UnixData("", "22");
    String subnetId = ""
    List<EC2Tag> tags
    String idleTerminationMinutes = "-5"
    String ami = "";
    String label = "";
    SlaveTemplate slaveTemplate

    SlaveTemplate getSlaveTemplate() {
        return new SlaveTemplate(ami, zone, spotConfiguration, securityGroups, remoteFS, type, ebsOptimized,
                label, mode, description, initScript, tmpDir, userData,
                numExecutors, remoteAdmin, amiType, jvmopts, stopOnTerminate, subnetId,
                tags, idleTerminationMinutes, usePrivateDnsName, instanceCapStr,
                iamInstanceProfile, useEphemeralDevices, useDedicatedTenancy, launchTimeoutStr,
                associatePublicIp, customDeviceMapping, connectBySSHProcess);

    }
}
