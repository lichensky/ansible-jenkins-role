import groovy.json.JsonSlurper
import jenkins.model.*
import hudson.model.*
import hudson.slaves.*
import hudson.plugins.sshslaves.*
import hudson.plugins.sshslaves.verifiers.NonVerifyingKeyVerificationStrategy

instance = Jenkins.instance

def slurper = new JsonSlurper()
def slaves = slurper.parseText('{{ jenkins_slaves | to_json }}')

for (def slave : slaves) {
    def hostVerificationStrategy = new NonVerifyingKeyVerificationStrategy()
    // https://javadoc.jenkins.io/hudson/slaves/DumbSlave.html
    Slave s = new DumbSlave(
        slave.name,
        slave.description,
        slave.remote_fs,
        slave.executors,
        Node.Mode.NORMAL,
        slave.label,
        // https://github.com/jenkinsci/ssh-slaves-plugin/blob/master/src/main/java/hudson/plugins/sshslaves/SSHLauncher.java
        new SSHLauncher(slave.host, 22, slave.credentials, null, null, null,
                        null, 60, 3, 15, hostVerificationStrategy),
        new RetentionStrategy.Always(),
        new LinkedList())

    instance.addNode(s)
}

instance.save()
