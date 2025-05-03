package com.unilife.service.impl;
import com.unilife.service.IPLocationService;
import jakarta.servlet.http.HttpServletRequest;
import org.lionsoul.ip2region.xdb.Searcher;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;


import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;

@Service
public class IPLocationServicelmpl implements IPLocationService {

    private Searcher searcher;

    @Override
    public String getIPLocation(String ip) {
        if("127.0.0.1".equals(ip) || ip.startsWith("192.168")){
            return "局域网 IP";
        }
        if(searcher == null){
            try{
                File file = ResourceUtils.getFile("classpath:ipdb/ip2region.xdb");
                String dbPath = file.getPath();
                //System.out.println(dbPath);
                searcher = Searcher.newWithFileOnly(dbPath);
            }
            catch(FileNotFoundException e){
                e.printStackTrace();
                return "IP地址库文件未找到";
            }
            catch (IOException e){
                e.printStackTrace();
                return "IP地址库初始化失败";
            }
        }
        String region = null;
        String errormessage = null;
        try{
            region = searcher.search(ip);
        }
        catch (Exception e){
            errormessage = e.getMessage();
            if(errormessage != null && errormessage.length() > 256){
                errormessage = errormessage.substring(0, 256);
            }
            e.printStackTrace();
            return "IP归属地查找失败！" + errormessage;
        }
        return (region == null) ? "未知归属地" : region;
    }

    @Override
    public String getClientIP(HttpServletRequest request) {
        if("127.0.0.1".equals(request.getRemoteAddr())){
            return "14.21.80.0";
        }//实际开发的时候删掉！
        String ip = request.getHeader("X-forwarded-for");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return (ip != null) ? ip.split(",")[0] : null;
    }
}
