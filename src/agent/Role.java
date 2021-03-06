package agent;

import java.io.*;
import utilities.Gui; 
import java.util.*;
import java.util.concurrent.*;

import person.interfaces.*;

/**
* Base class for simple agents
*/
public abstract class Role {
        
		protected String roleName = "No Active Role";
	
        public void switchPerson(Person p){
                this.person = p;
        }
        
        public Person getPerson(){
                return person;
        }
        
        private boolean isActive;
        public Person person;
        
        public void msgEndOfDay(){
        	
        }
        
    protected Role(Person person) {
            isActive = false;
            this.person = person;
    }

    public boolean isActive() {
            
            return isActive;
    }
    public void setActive(boolean active){
            
            this.isActive = active;
    }

   /**
* This should be called whenever state has changed that might cause
* the agent to do something.
*/
    protected void stateChanged() {
        //This will call the agent's stateChanged function
            this.person.setStateChanged();
    }

    /**
* Agents must implement this scheduler to perform any actions appropriate for the
* current state. Will be called whenever a state change has occurred,
* and will be called repeated as long as it returns true.
*
* @return true iff some action was executed that might have changed the
* state.
*/
    public abstract boolean pickAndExecuteAnAction();

    /**
* Return agent name for messages. Default is to return java instance
* name.
*/
    public String getName() {
        return StringUtil.shortName(this);
    }

    /**
* The simulated action code
*/
    protected void Do(String msg) {
        print(msg, null);
    }

    /**
* Print message
*/
    protected void print(String msg) {
        print(msg, null);
    }

    /**
* Print message with exception stack trace
*/
    protected void print(String msg, Throwable e) {
        StringBuffer sb = new StringBuffer();
        sb.append(getName());
        sb.append(": ");
        sb.append(msg);
        sb.append("\n");
        if (e != null) {
            sb.append(StringUtil.stackTraceString(e));
        }
        System.out.print(sb.toString());
    }
    
    public abstract String getRoleName();


	public abstract Gui getGui();
	
}