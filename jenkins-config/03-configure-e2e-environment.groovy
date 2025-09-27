// Configure Jenkins environment for E2E testing
import jenkins.model.*
import hudson.model.*
import hudson.tools.*

def instance = Jenkins.getInstance()

// Configure global environment variables for E2E testing
def globalNodeProperties = instance.getGlobalNodeProperties()
def envVarsNodePropertyList = globalNodeProperties.getAll(hudson.slaves.EnvironmentVariablesNodeProperty.class)

def newEnvVarsNodeProperty = null
def envVars = null

if (envVarsNodePropertyList == null || envVarsNodePropertyList.size() == 0) {
    newEnvVarsNodeProperty = new hudson.slaves.EnvironmentVariablesNodeProperty()
    globalNodeProperties.add(newEnvVarsNodeProperty)
    envVars = newEnvVarsNodeProperty.getEnvVars()
} else {
    envVars = envVarsNodePropertyList.get(0).getEnvVars()
}

// Set E2E testing environment variables
envVars.put("CYPRESS_CACHE_FOLDER", "/var/jenkins_home/.cache/Cypress")
envVars.put("DISPLAY", ":99")
envVars.put("CHROME_BIN", "/usr/bin/google-chrome")
envVars.put("CYPRESS_RUN_BINARY", "/var/jenkins_home/.cache/Cypress/*/Cypress/Cypress")

// Configure Chrome options for headless mode
envVars.put("CYPRESS_chromeWebSecurity", "false")
envVars.put("CYPRESS_video", "false")
envVars.put("CYPRESS_screenshotOnRunFailure", "true")

instance.save()

println "E2E testing environment configured successfully!"
println "Environment variables set:"
println "- CYPRESS_CACHE_FOLDER: ${envVars.get('CYPRESS_CACHE_FOLDER')}"
println "- DISPLAY: ${envVars.get('DISPLAY')}"
println "- CHROME_BIN: ${envVars.get('CHROME_BIN')}"
println "- CYPRESS_chromeWebSecurity: ${envVars.get('CYPRESS_chromeWebSecurity')}"
println "- CYPRESS_video: ${envVars.get('CYPRESS_video')}"
println "- CYPRESS_screenshotOnRunFailure: ${envVars.get('CYPRESS_screenshotOnRunFailure')}"
