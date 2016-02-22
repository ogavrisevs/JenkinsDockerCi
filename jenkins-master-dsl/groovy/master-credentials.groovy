import hudson.security.*;
import com.cloudbees.jenkins.plugins.sshcredentials.impl.*;
import com.cloudbees.plugins.credentials.*;
import com.cloudbees.plugins.credentials.domains.*;
import hudson.plugins.sshslaves.*;
import jenkins.model.*;
import hudson.model.*;

Thread.start {
    sleep 10000

    //Restrct access to jenkins
    hudson.security.HudsonPrivateSecurityRealm realm = new hudson.security.HudsonPrivateSecurityRealm(false)
    Jenkins.instance.setSecurityRealm(realm);
    Jenkins.instance.setAuthorizationStrategy(new FullControlOnceLoggedInAuthorizationStrategy());
    User user1 = realm.createAccount("admin", "y8D3hvCTnCv6bjAhPxAzHyqL8qpx");
}
