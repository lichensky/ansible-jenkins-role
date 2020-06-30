import groovy.json.JsonSlurper;
import hudson.slaves.EnvironmentVariablesNodeProperty;
import jenkins.model.Jenkins;

def jenkins = Jenkins.getInstance();

def jsonSlurper = new JsonSlurper()
def env = jsonSlurper.parseText('{{ jenkins_environment | to_json }}')

def globalNodeProperties = jenkins.getGlobalNodeProperties();
def envVarsNodePropertyList = globalNodeProperties.getAll(EnvironmentVariablesNodeProperty.class);

def newEnvVarsNodeProperty = null;
def envVars = null;

if ( envVarsNodePropertyList == null || envVarsNodePropertyList.size() == 0 ) {
    newEnvVarsNodeProperty = new EnvironmentVariablesNodeProperty();
    globalNodeProperties.add(newEnvVarsNodeProperty);
    envVars = newEnvVarsNodeProperty.getEnvVars();
} else {
    envVars = envVarsNodePropertyList.get(0).getEnvVars();
}

env.each { var ->
    envVars.put(var['key'], var['value'])
}

jenkins.save()
