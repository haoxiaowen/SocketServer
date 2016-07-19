package com.socket;

public class StartServer {
	
	public StartServer(){
	}
	
    public static void main(String[] args)   
    {
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
