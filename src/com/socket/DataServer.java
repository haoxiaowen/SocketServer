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
import java.util.Date;
import java.util.Properties;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

public class DataServer implements Runnable {
	
	private String serverIp = "0.0.0.0";
	private int serverPort = 10001;
	private ServerSocket server;
	private BufferedReader reader;
	private BufferedWriter writer;
    
	public DataServer(){
	}
	
	public void startServer(){
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
			serverPort = Integer.parseInt(p.get("SOCKET_DATA_PORT").toString());
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
	    	System.out.println("data,Create server port:" + serverPort + "--------------------------");
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
		      
		      String inStr = reader.readLine();
		      System.out.println("data,Received: "+ inStr + ",from IP:" + client.getInetAddress()+new Date().toLocaleString());
		      
		      Document doc = DocumentHelper.parseText(inStr); // 将字符串转为XML
		      Element root = doc.getRootElement();
		      String templateid = root.elementTextTrim("pktcode");
		      String itemCode = root.elementTextTrim("itemcode");
		      
		      StringBuilder outSb = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		      outSb.append("<root>");
//		      outSb.append("<dataitem name=\"field0_RSS\">as3中常量通常是使用的字符串，比如说TextFieldType.INPUT、StageAlign.TOP_LEFT，有些时候这样使用没什么大碍，而有些时候如果随意传入一个字符串就可能出现运行异常</dataitem>");
		     
//		      outSb.append("<dataitem name=\"field0_TopTime\">8:30</dataitem>");
//		      outSb.append("<dataitem name=\"field1_TopText\">央视新闻</dataitem>");
//		      outSb.append("<dataitem name=\"field2_MidTime\">9:10</dataitem>");
//		      outSb.append("<dataitem name=\"field3_MidText\">奔跑吧兄弟</dataitem>");
//		      outSb.append("<dataitem name=\"field4_BotTime\">10:00</dataitem>");
//		      outSb.append("<dataitem name=\"field5_BotText\">老梁故事汇</dataitem>");
		     
		      if(itemCode==null || itemCode.isEmpty()){//获取条目Id
		    	  outSb.append("<dataitem name=\"itemcode\">abdcefg</dataitem>");
		      }else{
			      if("Template01_TheatreRightBarTrailer".endsWith(templateid)){//剧场-右侧条形预告
				      outSb.append("<dataitem name=\"field0_TopText\">老梁故事汇</dataitem>");
				      outSb.append("<dataitem name=\"field1_MidTime\">9:00-9:35</dataitem>");
				      outSb.append("<dataitem name=\"field2_BotText\">每周五播出</dataitem>");
			      }else if("Template01_TheatreRightSquareTrailer".endsWith(templateid)){//剧场-右侧方形预告
				      outSb.append("<dataitem name=\"field0_TopText\">老梁故事汇</dataitem>");
				      outSb.append("<dataitem name=\"field1_MidTime\">9:00-9:35</dataitem>");
				      outSb.append("<dataitem name=\"field2_BotText\">每周五播出</dataitem>");
			      }else if("Template01_guideOne".endsWith(templateid)){//导视一
				      outSb.append("<dataitem name=\"field0_videoTime_a\">8:30</dataitem>");
				      outSb.append("<dataitem name=\"field1_videoName_a\">央视新闻</dataitem>");
				      outSb.append("<dataitem name=\"field2_videoTime_b\">9:10</dataitem>");
				      outSb.append("<dataitem name=\"field3_videoName_b\">奔跑吧兄弟</dataitem>");
				      outSb.append("<dataitem name=\"field4_videoTime_c\">10:00</dataitem>");
				      outSb.append("<dataitem name=\"field5_videoName_c\">老梁故事汇</dataitem>");
			      }
		      }
		      outSb.append("</root>");
		      String outStr = outSb.toString();
	          writer.write(outStr + "\0");
	          writer.flush();
	          System.out.println("data,Send: "+outStr);
	          
		      client.close();
		   } catch (Exception e) {
		      e.printStackTrace();
		      try {
		          //发现异常关闭连接
		         if (client != null) {
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
	
	private boolean isNumeric(String str){
	   for(int i=str.length();--i>=0;){
	      int chr=str.charAt(i);
	      if(chr<48 || chr>57)
	         return false;
	   }
	   return true;
	}
	
    public static void main(String[] args)   
    {
    	PolicyServer policy = new PolicyServer();
    	policy.startPolicy();
    	
    	DataServer serv = new DataServer();
    	serv.startServer();
    }
}
