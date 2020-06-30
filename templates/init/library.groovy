import hudson.scm.SCM
import jenkins.model.Jenkins
import jenkins.plugins.git.GitSCMSource
import org.jenkinsci.plugins.workflow.libs.*
import org.jenkinsci.plugins.workflow.libs.LibraryConfiguration
import org.jenkinsci.plugins.workflow.libs.SCMSourceRetriever

def libDescriptor = "org.jenkinsci.plugins.workflow.libs.GlobalLibraries"

Jenkins jenkins = Jenkins.getInstance()

def jsonSlurper = new JsonSlurper()
def libraries = jsonSlurper.parseText('{{ jenkins_libraries | to_json }}')

libraryConfigurations = []

for (def library : libraries) {
  def libraryParameters = [
    repo_id:              library.git_source_id,
    version:              library.version
    credentialId:         library.credentials
    implicit:             library.implicit
    name:                 library.name
    repository:           library.git_repo
  ]

  GitSCMSource gitSCMSource = new GitSCMSource(
    libraryParameters.repo_id,
    libraryParameters.repository,
    libraryParameters.credentialId,
    "*",
    "",
    false
  )

  SCMSourceRetriever sCMSourceRetriever = new SCMSourceRetriever(gitSCMSource)

  def globalLibraries = jenkins.getDescriptor(libDescriptor)

  LibraryConfiguration libraryConfiguration = new LibraryConfiguration(
      libraryParameters.name, sCMSourceRetriever)
  libraryConfiguration.setDefaultVersion(libraryParameters.version)
  libraryConfiguration.setImplicit(libraryParameters.implicit)

  libraryConfigurations.add(libraryConfiguration)
}

globalLibraries.get().setLibraries(libraryConfigurations)

jenkins.save()
