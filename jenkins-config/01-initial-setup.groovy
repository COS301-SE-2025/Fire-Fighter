import jenkins.model.*
import hudson.security.*
import hudson.security.csrf.DefaultCrumbIssuer
import jenkins.security.s2m.AdminWhitelistRule
import hudson.markup.RawHtmlMarkupFormatter

def instance = Jenkins.getInstance()

// Disable setup wizard
if (!instance.getInstallState().isSetupComplete()) {
    println "Setting up Jenkins initial configuration..."
    
    // Create admin user
    def hudsonRealm = new HudsonPrivateSecurityRealm(false)
    hudsonRealm.createAccount("admin", "admin123")
    instance.setSecurityRealm(hudsonRealm)
    
    // Set authorization strategy
    def strategy = new FullControlOnceLoggedInAuthorizationStrategy()
    strategy.setAllowAnonymousRead(false)
    instance.setAuthorizationStrategy(strategy)
    
    // Enable CSRF protection
    instance.setCrumbIssuer(new DefaultCrumbIssuer(true))
    
    // Set markup formatter to allow HTML
    instance.setMarkupFormatter(new RawHtmlMarkupFormatter(false))
    
    // Disable CLI over remoting
    instance.getDescriptor("jenkins.CLI").get().setEnabled(false)
    
    // Enable agent to master security
    instance.getInjector().getInstance(AdminWhitelistRule.class).setMasterKillSwitch(false)
    
    // Set number of executors
    instance.setNumExecutors(2)
    
    // Save configuration
    instance.save()
    
    println "Jenkins initial setup completed!"
    println "Default admin credentials: admin/admin123"
    println "Please change the admin password after first login!"
}
