/**
 * 
 */
package com.alten.mercato.jbpm.jpdl.internal.repository;

/**
 * @author Huage Chen
 *
 */

import java.io.InputStream;
import java.util.List;

import org.jbpm.ProcessDefinition;
import org.jbpm.ProcessDefinitionQuery;
import org.jbpm.env.Environment;
import org.jbpm.internal.log.Log;
import org.jbpm.jpdl.internal.model.JpdlProcessDefinition;
import org.jbpm.jpdl.internal.xml.JpdlParser;
import org.jbpm.pvm.internal.model.ProcessDefinitionImpl;
import org.jbpm.pvm.internal.repository.Deployer;
import org.jbpm.pvm.internal.repository.DeploymentImpl;
import org.jbpm.pvm.internal.xml.Parse;
import org.jbpm.session.RepositorySession;


/**
 * @author Tom Baeyens
 */
public class CustomJpdlDeployer implements Deployer {
  
  private static Log log = Log.getLog(CustomJpdlDeployer.class.getName());
  
  public static final String KEY_ID = "id";
  public static final String KEY_KEY = "key";
  public static final String KEY_VERSION = "version";
  
  static JpdlParser jpdlParser = new JpdlParser();

  public void deploy(DeploymentImpl deployment) {
    
    for (String resourceName: deployment.getResourceNames()) {
      
      if (resourceName.endsWith(".jpdl.xml")) {
        InputStream inputStream = deployment.getResourceAsStream(resourceName);
        Parse parse = jpdlParser.createParse();
        parse.setProblems(deployment.getProblems());
        parse.setInputStream(inputStream);
        parse.execute();
        JpdlProcessDefinition processDefinition = (JpdlProcessDefinition) parse.getDocumentObject();
        if ((processDefinition != null) && (processDefinition.getName() != null)) {
          String processDefinitionName = processDefinition.getName();

          processDefinition.setDeploymentDbid(deployment.getDbid());

          if (deployment.hasObjectProperties(processDefinitionName)) {
            String key = (String) deployment.getObjectProperty(processDefinitionName, KEY_KEY);
            String id = (String) deployment.getObjectProperty(processDefinitionName, KEY_ID);
            String version = (String) deployment.getObjectProperty(processDefinitionName, KEY_VERSION).toString();
            processDefinition.setId(id);
            processDefinition.setKey(key);
            if (version != null) {
            	processDefinition.setVersion(Integer.parseInt(version));
            } else {
            	processDefinition.setVersion(ProcessDefinitionImpl.UNASSIGNED_VERSION);
            }

          } else {
            checkKey(processDefinition, deployment);
            checkVersion(processDefinition, deployment);
            checkId(processDefinition, deployment);

            deployment.addObjectProperty(processDefinitionName, KEY_KEY, processDefinition.getKey());
            deployment.addObjectProperty(processDefinitionName, KEY_VERSION, new Long(processDefinition.getVersion()));
            deployment.addObjectProperty(processDefinitionName, KEY_ID, processDefinition.getId());
          }

          deployment.addObject(processDefinitionName, processDefinition);
        }
      }
    }
  }
  
  protected void checkKey(ProcessDefinitionImpl processDefinition, DeploymentImpl deployment) {
    String processDefinitionName = processDefinition.getName();
    String processDefinitionKey = processDefinition.getKey();

    // if no key was specified in the jpdl process file
    if (processDefinitionKey==null) {
      // derive the key from the name
      // replace any non-word character with an underscore
      processDefinitionKey = processDefinitionName.replaceAll("\\W", "_");
      processDefinition.setKey(processDefinitionKey);
    }
    
    RepositorySession repositorySession = Environment.getFromCurrent(RepositorySession.class);

    List<ProcessDefinition> existingProcesses = repositorySession.createProcessDefinitionQuery()
        .name(processDefinitionName)
        .execute();
    
    for (ProcessDefinition existingProcess: existingProcesses) {
      if (!processDefinitionKey.equals(existingProcess.getKey())) {
        deployment.addProblem("invalid key '"+processDefinitionKey+"' in process "+processDefinition.getName()+".  Existing process has name '"+processDefinitionName+"' and key '"+processDefinitionKey+"'");
      }
    }

    existingProcesses = repositorySession.createProcessDefinitionQuery()
        .key(processDefinitionKey)
        .execute();
    
    for (ProcessDefinition existingProcess: existingProcesses) {
      if (!processDefinitionName.equals(existingProcess.getName())) {
        deployment.addProblem("invalid name '"+processDefinitionName+"' in process "+processDefinition.getName()+".  Existing process has name '"+processDefinitionName+"' and key '"+processDefinitionKey+"'");
      }
    }
  }

  protected void checkId(ProcessDefinitionImpl processDefinition, DeploymentImpl deployment) {
    String id = processDefinition.getId();
    if (id==null) {
      id = processDefinition.getKey()+"-"+processDefinition.getVersion();
      if (log.isTraceEnabled()) log.trace("created id '"+id+"' for "+processDefinition);
      processDefinition.setId(id);
    }
    
    RepositorySession repositorySession = Environment.getFromCurrent(RepositorySession.class);
    ProcessDefinition existingProcessDefinition = repositorySession.createProcessDefinitionQuery()
        .id(id)
        .uniqueResult();
    if (existingProcessDefinition != null) {
      deployment.addProblem("process '" + id + "' already exists");
    }
  }
  
  protected void checkVersion(ProcessDefinitionImpl processDefinition, DeploymentImpl deployment) {
    int version = processDefinition.getVersion();
    String key = processDefinition.getKey();
    if (version==ProcessDefinitionImpl.UNASSIGNED_VERSION) {
      RepositorySession repositorySession = Environment.getFromCurrent(RepositorySession.class);
      
      ProcessDefinition latestDeployedVersion = repositorySession
          .createProcessDefinitionQuery()
          .key(key)
          .orderDesc(ProcessDefinitionQuery.PROPERTY_VERSION)
          .page(0, 1)
          .uniqueResult();

      if (latestDeployedVersion!=null) {
        version = latestDeployedVersion.getVersion() + 1;
      } else {
        version = 1;
      }
      if (log.isTraceEnabled()) log.trace("assigning version "+version+" to process definition "+key);
      processDefinition.setVersion(version);
    }
  }
}
