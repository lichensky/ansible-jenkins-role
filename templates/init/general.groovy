import java.util.logging.Logger
import jenkins.model.Jenkins
import jenkins.model.JenkinsLocationConfiguration
import org.jenkinsci.plugins.workflow.flow.GlobalDefaultFlowDurabilityLevel
import org.jenkinsci.plugins.workflow.flow.FlowDurabilityHint

def logger = Logger.getLogger("InitGeneral")
logger.info("Setting Jenkins URL.")

def jenkinsDomain = "{{ jenkins_domain }}"
def locationUrl = JenkinsLocationConfiguration.get()
locationUrl.setUrl("http://${jenkinsDomain}")
locationUrl.save()

// Set master executors number
def jenkins = Jenkins.getInstance()
jenkins.setNumExecutors({{ jenkins_executors_number }})

// Set up performance optimized pipelines
GlobalDefaultFlowDurabilityLevel.DescriptorImpl level =
    jenkins.getExtensionList(GlobalDefaultFlowDurabilityLevel.DescriptorImpl.class).get(0)
level.setDurabilityHint(FlowDurabilityHint.PERFORMANCE_OPTIMIZED)

jenkins.save()
