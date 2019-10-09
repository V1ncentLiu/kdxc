package com.kuaidao.manageweb.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DownFile {
	public void downFile(String sPath, String fileName, HttpServletRequest request, HttpServletResponse response) throws IOException {

		File file = new File(sPath);// path是根据日志路径和文件名拼接出来的

		InputStream fis = new BufferedInputStream(new FileInputStream(sPath));
		byte[] buffer = new byte[fis.available()];
		fis.read(buffer);

		response.reset();

		// 先去掉文件名称中的空格,然后转换编码格式为utf-8,保证不出现乱码,这个文件名称用于浏览器的下载框中自动显示的文件名
		String agent = request.getHeader("user-agent");
		// resp.setContentType("application/x-download");
		if (agent.contains("Firefox")) {
			response.addHeader("Content-Disposition", "attachment;filename=" + new String(fileName.getBytes("GB2312"), "ISO-8859-1"));
		} else {
			response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
		}
		// response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "utf-8"));
		response.addHeader("Content-Length", "" + file.length());
		response.setCharacterEncoding("UTF-8");
		OutputStream os = new BufferedOutputStream(response.getOutputStream());
		response.setContentType("application/octet-stream");
		os.write(buffer);// 输出文件
		os.flush();
		fis.close();
		os.close();
	}

	public void yulanFile(String sPath, String fileName, HttpServletRequest request, HttpServletResponse response) throws IOException {

		File file = new File(sPath);// path是根据日志路径和文件名拼接出来的

		InputStream fis = new BufferedInputStream(new FileInputStream(sPath));
		byte[] buffer = new byte[fis.available()];
		fis.read(buffer);

		response.reset();

		// 先去掉文件名称中的空格,然后转换编码格式为utf-8,保证不出现乱码,这个文件名称用于浏览器的下载框中自动显示的文件名
		String agent = request.getHeader("user-agent");
		// resp.setContentType("application/x-download");
		if (agent.contains("Firefox")) {
			response.addHeader("Content-Disposition", "attachment;filename=" + new String(fileName.getBytes("GB2312"), "ISO-8859-1"));
		} else {
			response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
		}
		// response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "utf-8"));
//		response.addHeader("Content-Length", "" + file.length());
//		response.setCharacterEncoding("UTF-8");
		OutputStream os = new BufferedOutputStream(response.getOutputStream());
//		response.setContentType("application/octet-stream");
		os.write(buffer);// 输出文件
		os.flush();
		fis.close();
		os.close();
		
	}
	
	/** 
     * 从网络Url中下载文件 
     * @param urlStr 
     * @param fileName 
     * @param savePath 
     * @throws IOException 
     */  
    public void  downLoadFromUrl(String urlStr,String fileName,String savePath,HttpServletRequest request, HttpServletResponse response) throws IOException{  
        URL url = new URL(urlStr);    
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();    
                //设置超时间为3秒  
        conn.setConnectTimeout(3*1000);  
        //防止屏蔽程序抓取而返回403错误  
        conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");  
  
        //得到输入流  
        InputStream inputStream = conn.getInputStream();    
        //获取自己数组  
        byte[] getData = readInputStream(inputStream);      
  
        //文件保存位置  
        if(null != savePath && !savePath.equals("")){
        	File saveDir = new File(savePath);  
            if(!saveDir.exists()){  
                saveDir.mkdir();  
            }  
            File file = new File(saveDir+File.separator+fileName);      
            FileOutputStream fos = new FileOutputStream(file);       
            fos.write(getData);   
            if(fos!=null){  
                fos.close();    
            }  
            if(inputStream!=null){  
                inputStream.close();  
            } 
        }else{
        	// 先去掉文件名称中的空格,然后转换编码格式为utf-8,保证不出现乱码,这个文件名称用于浏览器的下载框中自动显示的文件名
     		String agent = request.getHeader("user-agent");
     		// resp.setContentType("application/x-download");
     		if (agent.contains("Firefox")) {
     			response.addHeader("Content-Disposition", "attachment;filename=" + new String(fileName.getBytes("GB2312"), "ISO-8859-1"));
     		} else {
     			response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
     		}
     		// response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "utf-8"));
     		//response.addHeader("Content-Length", "" + file.length());
     		response.setCharacterEncoding("UTF-8");
     		OutputStream os = new BufferedOutputStream(response.getOutputStream());
     		response.setContentType("application/octet-stream");
     		os.write(getData);// 输出文件
     		os.flush();
            if(inputStream!=null){  
                inputStream.close();  
            } 
     		os.close();
        }
  
    }  
    
    /** 
     * 从输入流中获取字节数组 
     * @param inputStream 
     * @return 
     * @throws IOException 
     */  
    public static  byte[] readInputStream(InputStream inputStream) throws IOException {    
        byte[] buffer = new byte[1024];    
        int len = 0;    
        ByteArrayOutputStream bos = new ByteArrayOutputStream();    
        while((len = inputStream.read(buffer)) != -1) {    
            bos.write(buffer, 0, len);    
        }    
        bos.close();    
        return bos.toByteArray();    
    }    
  

}
