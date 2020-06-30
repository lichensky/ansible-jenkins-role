import groovy.json.JsonSlurper
import jenkins.model.*
import java.util.logging.Logger;

def logger = Logger.getLogger("InitPlugins")

def jsonSlurper = new JsonSlurper()
def plugins = jsonSlurper.parseText('{{ jenkins_plugins | to_json }}')

def pluginInstallJobs = []

def instance = Jenkins.getInstance()
def pluginManager = instance.getPluginManager()
def updateCenter = instance.getUpdateCenter()

plugins.each {
  logger.info("Checking UpdateCenter for: " + it)
  def plugin = updateCenter.getPlugin(it)
  if (plugin) {
    logger.info("Installing ${it}: ${plugin.version}.")
    pluginInstallJobs.add(plugin.deploy())
  }
}

// Wait for all plugins to be installed
waitUntilComplete(pluginInstallJobs)

// Update existing plugins
def pluginsToUpdate = instance.pluginManager.activePlugins.findAll {
  it -> it.hasUpdate()
}.collect {
  it -> it.getShortName()
}

def pluginUpdateJobs = instance.pluginManager.install(pluginsToUpdate, false)
waitUntilComplete(pluginUpdateJobs)

// Save configuration
instance.save()

def waitUntilComplete(jobs, heartbeat=1000) {
  // Wait for all jobs to be completed
  for (job in jobs) {
    while(!job.isDone()) {
      Thread.sleep(heartbeat)
    }
  }
}