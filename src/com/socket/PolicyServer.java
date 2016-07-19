package com.socket;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;

public class PolicyServer implements Runnable {
	
	private String serverIp = "0.0.0.0";
	private int serverPort = 843;
	private ServerSocket server;
	private BufferedReader reader;
	private BufferedWriter writer;
	private String xml;
    
	public PolicyServer(){
		xml="<cross-domain-policy>";
		xml+="<allow-access-from domain=\"*\" to-ports=\"*\"/>";
		xml+="</cross-domain-policy> ";
	}
	
	public void startPolicy(){
		//获取配置数据
		getServerInfo();
	}
	
	//获取IP,port
	private void getServerInfo(){
		InputStream in;
		try {
			String path = this.getClass().getResource("/").toString();
			//System.out.println(path.substring(6));
			in = new BufferedInputStream(new FileInputStream(new File(path.substring(6)+"config.properties")));
			Properties p = new Properties();
			p.load(in);
			serverIp = p.get("SOCKET_SERVER_IP").toString();
			serverPort = Integer.parseInt(p.get("SOCKET_POLICY_PORT").toString());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
		    //启动843端口
		    createServerSocket();
		    new Thread(this).start();
		}
	}
	
	//启动服务器
	private void createServerSocket()
	{
	    try {
	    	InetAddress ip = InetAddress.getByName(serverIp);
	    	//server = new ServerSocket(serverPort,50,ip);
	    	server = new ServerSocket(serverPort);
	    	System.out.println("policy,Create server port:" + serverPort + "--------------------------");
	    } catch (IOException e) {
	    	System.exit(1);
	    }
	}
	
	//启动服务器线程
	public void run()
	{
	   while (true) {
		   Socket client = null;
		   try {
		       //接收客户端的连接
		      client = server.accept();
	
		      InputStreamReader input = new InputStreamReader(client.getInputStream(), "UTF-8");
		      reader = new BufferedReader(input);
		      OutputStreamWriter output = new OutputStreamWriter(client.getOutputStream(), "UTF-8");
		      writer = new BufferedWriter(output);
	
		      String inStr = "";//reader.readLine();
		      String outStr = "";
		      char[] ch=new char[22];
		      reader.read(ch, 0, ch.length);
		      StringBuffer sb=new StringBuffer();
		      for(int i=0;i< ch.length;i++){
		    	  sb.append(ch[i]);
		      }
		      inStr=sb.toString();
		      System.out.println("policy,Received: "+ inStr + ",from IP:" + client.getInetAddress());
		      
		      if(inStr.indexOf("<policy-file-request/>")!=-1){
		    	  outStr = xml;
		      }else{
		    	  outStr = "Input para Error！";
		      }
	          writer.write(outStr + "\0");
	          writer.flush();
	          System.out.println("policy,Send: "+outStr);
	          
		      client.close();
		   } catch (Exception e) {
		      e.printStackTrace();
		      try {
		          //发现异常关闭连接
		         if(client != null) {
		        	 client.close();
		        	 client = null;
		         }
		      } catch (IOException ex) {
		          ex.printStackTrace();
		      } finally {
		          //调用垃圾收集方法
		          System.gc();
		      }
		   }
	    }
	}
	
    public static void main(String[] args)   
    {
        PolicyServer policy = new PolicyServer();
        policy.startPolicy();
    }
}
