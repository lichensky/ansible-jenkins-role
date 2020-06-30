import jenkins.model.*
import jenkins.security.s2m.AdminWhitelistRule
import hudson.model.*
import hudson.security.*
import hudson.security.csrf.*
import org.jenkinsci.plugins.BitbucketSecurityRealm
import java.util.logging.Logger

def instance = Jenkins.getInstance()
def logger = Logger.getLogger("security")

logger.info("Setting default CSRF Crumb issuer.")
instance.setCrumbIssuer(new DefaultCrumbIssuer(true))

logger.info("Enable Master Access Control mechanism.")
instance.getInjector().getInstance(AdminWhitelistRule.class).setMasterKillSwitch(false)

logger.info("Disable deprecated agent protocols.")
def protocols = new HashSet<String>(["JNLP4-connect", "Ping"])
instance.setAgentProtocols(protocols)

instance.save()
