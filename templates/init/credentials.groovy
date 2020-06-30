import groovy.json.JsonSlurper
import jenkins.model.Jenkins
import com.cloudbees.plugins.credentials.impl.*;
import com.cloudbees.plugins.credentials.*;
import com.cloudbees.plugins.credentials.domains.*;
import com.cloudbees.jenkins.plugins.sshcredentials.impl.*;

// Definitions
def credentialsExt = "com.cloudbees.plugins.credentials.SystemCredentialsProvider"

def setCredentials(credentials) {
    def Credentials c
    switch(credentials.type) {
        case "userpass":
            c = newUserPasswordCredentials(credentials)
        case "ssh":
            c = newSSHCredentials(credentials)
    }
    store.addCredentials(domain, c)
}

def newUserPasswordCredentials(credentials) {
    Credentials c = (Credentials) new UsernamePasswordCredentialsImpl(CredentialsScope.GLOBAL,
        credentials.id, credentials.description, credentials.user, credentials.password)

    return c
}

def newSSHCredentials(credentials) {
    def key = new BasicSSHUserPrivateKey.DirectEntryPrivateKeySource(credential.key)
    Credentials c = (Credentials) new BasicSSHUserPrivateKey(CredentialsScope.GLOBAL, credential.id,
        credential.user, key, credential.passphrase, description)

    return c
}

// Main
def jsonSlurper = new JsonSlurper()
def credentials = jsonSlurper.parseText("{{ jenkins_credentials | to_json }}")

def jenkins = Jenkins.getInstance()

def domain = Domain.global()
def store = jenkins.getExtensionList(credentialsExt)[0].getStore()

for (def c : credentials) {
    setCredentials(c)
}

jenkins.save()
