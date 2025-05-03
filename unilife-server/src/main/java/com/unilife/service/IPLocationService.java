package com.unilife.service;
import jakarta.servlet.http.HttpServletRequest;


public interface IPLocationService {
    public String getIPLocation(String ip);
    public String getClientIP(HttpServletRequest request);
}
