package com.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import com.socket.DataServer;
import com.socket.ItemServer;
import com.socket.PolicyServer;

public class StartServlet extends HttpServlet{
	
	private static final long serialVersionUID = 6102474622102762517L;
	
	@Override  
    public void init() throws ServletException {
    	//策略服务
    	PolicyServer policy = new PolicyServer();
    	policy.startPolicy();
    	
    	//获取条目ID
    	ItemServer serv1 = new ItemServer();
    	serv1.startServer();
    	
    	//获取条目数据
    	DataServer serv2 = new DataServer();
    	serv2.startServer();
	}
}
