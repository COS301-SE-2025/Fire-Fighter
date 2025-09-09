import jenkins.model.*
import hudson.model.*
import hudson.tools.*
import hudson.util.DescribableList
import hudson.plugins.git.*
import hudson.tasks.Maven
import hudson.tasks.Maven.MavenInstallation
import jenkins.plugins.nodejs.tools.NodeJSInstallation
import hudson.tools.InstallSourceProperty
import hudson.tools.ToolProperty
import hudson.tools.ToolPropertyDescriptor

def instance = Jenkins.getInstance()

println "Configuring global tools..."

// Configure JDK
def jdkDescriptor = instance.getDescriptorByType(JDK.DescriptorImpl.class)
def jdkInstallations = [
    new JDK("jdk-17", "/usr/lib/jvm/java-17-openjdk-amd64", [
        new InstallSourceProperty([
            new JDKInstaller("17.0.2+8", true)
        ])
    ])
] as JDK[]
jdkDescriptor.setInstallations(jdkInstallations)
jdkDescriptor.save()

// Configure Maven
def mavenDescriptor = instance.getDescriptorByType(Maven.DescriptorImpl.class)
def mavenInstaller = new Maven.MavenInstaller("3.9.5")
def mavenInstallSourceProperty = new InstallSourceProperty([mavenInstaller])
def mavenInstallation = new MavenInstallation("maven-3.9", "", [mavenInstallSourceProperty])
mavenDescriptor.setInstallations(mavenInstallation)
mavenDescriptor.save()

// Configure NodeJS
def nodejsDescriptor = instance.getDescriptorByType(NodeJSInstallation.DescriptorImpl.class)
if (nodejsDescriptor != null) {
    def nodejsInstaller = new jenkins.plugins.nodejs.tools.NodeJSInstaller("18.18.2", "", 72)
    def nodejsInstallSourceProperty = new InstallSourceProperty([nodejsInstaller])
    def nodejsInstallation = new NodeJSInstallation("nodejs-18", "", [nodejsInstallSourceProperty])
    nodejsDescriptor.setInstallations(nodejsInstallation)
    nodejsDescriptor.save()
}

// Configure Git
def gitDescriptor = instance.getDescriptorByType(GitTool.DescriptorImpl.class)
def gitInstallation = new GitTool("Default", "/usr/bin/git", [])
gitDescriptor.setInstallations(gitInstallation)
gitDescriptor.save()

instance.save()
println "Global tools configuration completed!"
