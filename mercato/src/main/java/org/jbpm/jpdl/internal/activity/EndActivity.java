/*
 * JBoss, Home of Professional Open Source
 * Copyright 2005, JBoss Inc., and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jbpm.jpdl.internal.activity;

import org.jbpm.activity.ActivityExecution;
import org.jbpm.model.OpenExecution;


/**
 * @author Tom Baeyens
 */
public class EndActivity extends JpdlActivity {

  private static final long serialVersionUID = 1L;
  
  protected boolean endProcessInstance = true;
  protected String state = null;

  public void execute(ActivityExecution execution) {
    OpenExecution executionToEnd = null;
    if (endProcessInstance) {
      executionToEnd = execution.getProcessInstance();
    } else {
      executionToEnd = execution;
    }
    
    if (state==null) {
      execution.end(executionToEnd);
    } else {
      execution.end(executionToEnd, state);
    }
  }
  
  public void setEndProcessInstance(boolean endProcessInstance) {
    this.endProcessInstance = endProcessInstance;
  }
  public void setState(String state) {
    this.state = state;
  }
}
