package org.tosca4iot.webinterface;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;

@Service
public class SSH {
	private static final Logger logger = LoggerFactory.getLogger(SSH.class);
	String host;
	String user;
	String password;
	public SSH(String host, String user, String password){
		this.host=host;
		this.user=user;
		this.password=password;
	}
	
	public void execCommand(String cmd) throws JSchException, IOException{
		JSch jsch = new JSch();
		
		Session session = jsch.getSession(user,host,22);
		session.setConfig("StrictHostKeyChecking", "no");
		UserInfo ui = new SimpleUserInfo(password);
		
		session.setUserInfo(ui);
		session.setTimeout(0);
		session.connect();
		
		
		Channel channel = session.openChannel("exec");
		((ChannelExec)channel).setCommand(cmd);
		
	      ((ChannelExec)channel).setErrStream(System.err);

	      InputStream in=channel.getInputStream();

	      channel.connect();

	      byte[] tmp=new byte[1024];
	      while(true){
	        while(in.available()>0){
	          int i=in.read(tmp, 0, 1024);
	          if(i<0)break;
	          System.out.print(new String(tmp, 0, i));
	          logger.info(new String(tmp, 0, i));
	        }
	        if(channel.isClosed()){
	          if(in.available()>0) continue; 
	          System.out.println("exit-status: "+channel.getExitStatus());
	          break;
	        }
	        try{Thread.sleep(100);}catch(Exception ee){}
	      }
	      channel.disconnect();
	      session.disconnect();	
	}
	
	public void fileTransfer(String originPath, String targetPath) {
	    FileInputStream fis=null;
	    try{

	      String lfile=originPath;
	   
	      String rfile=targetPath;

	      JSch jsch=new JSch();
	      Session session=jsch.getSession(user, host, 22);
	      session.setConfig("StrictHostKeyChecking", "no");

	      UserInfo ui=new SimpleUserInfo(password);
	      session.setUserInfo(ui);
	      session.connect();

	      boolean ptimestamp = true;

	      // exec 'scp -t rfile' remotely
	      String command="scp " + (ptimestamp ? "-p" :"") +" -t "+rfile;
	      Channel channel=session.openChannel("exec");
	      ((ChannelExec)channel).setCommand(command);

	      // get I/O streams for remote scp
	      OutputStream out=channel.getOutputStream();
	      InputStream in=channel.getInputStream();

	      channel.connect();

	      if(checkAck(in)!=0){
		System.exit(0);
	      }

	      File _lfile = new File(lfile);

	      if(ptimestamp){
	        command="T"+(_lfile.lastModified()/1000)+" 0";
	        // The access time should be sent here,
	        // but it is not accessible with JavaAPI ;-<
	        command+=(" "+(_lfile.lastModified()/1000)+" 0\n"); 
	        out.write(command.getBytes()); out.flush();
	        if(checkAck(in)!=0){
	  	  System.exit(0);
	        }
	      }

	      // send "C0644 filesize filename", where filename should not include '/'
	      long filesize=_lfile.length();
	      command="C0644 "+filesize+" ";
	      if(lfile.lastIndexOf('/')>0){
	        command+=lfile.substring(lfile.lastIndexOf('/')+1);
	      }
	      else{
	        command+=lfile;
	      }
	      command+="\n";
	      out.write(command.getBytes()); out.flush();
	      if(checkAck(in)!=0){
		System.exit(0);
	      }

	      // send a content of lfile
	      fis=new FileInputStream(lfile);
	      byte[] buf=new byte[1024];
	      while(true){
	        int len=fis.read(buf, 0, buf.length);
		if(len<=0) break;
	        out.write(buf, 0, len); //out.flush();
	      }
	      fis.close();
	      fis=null;
	      // send '\0'
	      buf[0]=0; out.write(buf, 0, 1); out.flush();
	      if(checkAck(in)!=0){
		System.exit(0);
	      }
	      out.close();

	      channel.disconnect();
	      session.disconnect();

	      System.exit(0);
	    }
	    catch(Exception e){
	      System.out.println(e);
	      try{if(fis!=null)fis.close();}catch(Exception ee){}
	    }
	  }

	  static int checkAck(InputStream in) throws IOException{
	    int b=in.read();
	    // b may be 0 for success,
	    //          1 for error,
	    //          2 for fatal error,
	    //          -1
	    if(b==0) return b;
	    if(b==-1) return b;

	    if(b==1 || b==2){
	      StringBuffer sb=new StringBuffer();
	      int c;
	      do {
		c=in.read();
		sb.append((char)c);
	      }
	      while(c!='\n');
	      if(b==1){ // error
		System.out.print(sb.toString());
	      }
	      if(b==2){ // fatal error
		System.out.print(sb.toString());
	      }
	    }
	    return b;
	  }
	
	//UserInfo for simply define the ssh password
	public static class SimpleUserInfo implements UserInfo{
		String password;
		public SimpleUserInfo(String password){
			this.password=password;
		}
		@Override
		public void showMessage(String message) {
			System.out.println(message);
		}
		
		@Override
		public boolean promptYesNo(String arg0) {
			// TODO Auto-generated method stub
			return false;
		}
		
		@Override
		public boolean promptPassword(String arg0) {
			return true;
		}
		
		@Override
		public boolean promptPassphrase(String arg0) {
			return false;
		}
		
		@Override
		public String getPassword() {
			return password;
		}
		
		@Override
		public String getPassphrase() {
			return null;
		}
		
	}
}
